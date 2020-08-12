package dev.simplix.core.common.inject;

import com.google.inject.*;
import dev.simplix.core.common.ApplicationInfo;
import dev.simplix.core.common.aop.*;
import dev.simplix.core.common.libloader.LibraryLoader;
import java.io.File;
import java.util.*;
import java.util.logging.Logger;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.reflections.Reflections;

@Slf4j
public class SimplixInstaller {

  private static final SimplixInstaller INSTANCE = new SimplixInstaller();
  private final Map<Class<?>, Injector> injectorMap = new HashMap<>();
  private final Map<String, InstallationContext> toInstall = new HashMap<>();
  private final LibraryLoader libraryLoader = new LibraryLoader();
  private Injector bossInjector;

  public void register(@NonNull Class<?> owner, @NonNull Module... modules) {
    if (!owner.isAnnotationPresent(SimplixApplication.class)) {
      throw new IllegalArgumentException("Owner class must be annotated with @SimplixApplication");
    }
    SimplixApplication application = owner.getAnnotation(SimplixApplication.class);
    Set<String> basePackages = determineBasePackages(owner);
    this.toInstall.put(
        application.name(),
        new InstallationContext(owner, new Reflections(basePackages, owner.getClassLoader()),
            ApplicationInfo.builder()
                .name(application.name())
                .version(application.version())
                .authors(application.authors())
                .workingDirectory(new File(application.workingDirectory()))
                .dependencies(application.dependencies())
                .build(), modules));
  }

  public boolean registered(@NonNull String appName) {
    return this.toInstall.containsKey(appName);
  }

  public Injector injector(@NonNull Class<?> owner) {
    return this.injectorMap.get(owner);
  }

  private Set<String> determineBasePackages(Class<?> owner) {
    Set<String> basePackages = new HashSet<>();
    basePackages.add(owner.getPackage().getName());
    if (owner.isAnnotationPresent(ScanComponents.class)) {
      ScanComponents components = owner.getAnnotation(ScanComponents.class);
      basePackages.addAll(Arrays.asList(components.value()));
    }
    return basePackages;
  }

  public void install() {
    if (this.bossInjector != null) {
      throw new IllegalStateException("Already installed");
    }
    this.bossInjector = Guice.createInjector();

    this.libraryLoader.loadLibraries(new File("libraries"));

    for (String name : this.toInstall.keySet()) {
      InstallationContext context = this.toInstall.get(name);
      if (this.injectorMap.containsKey(context.owner)) {
        continue;
      }
      if (!installApplication(context, new Stack<>())) {
        log.warn("[Simplix | Bootstrap] Failed to load application "
                 + context.applicationInfo.name());
      }
    }
  }

  private boolean installApplication(
      @NonNull InstallationContext context,
      @NonNull Stack<ApplicationInfo> infoStack) {
    if (this.injectorMap.containsKey(context.owner)) {
      return true; // Application already installed
    }
    for (String dependency : context.applicationInfo.dependencies()) {
      InstallationContext depContext = this.toInstall.get(dependency);
      if (depContext == null) {
        log.warn("[Simplix | Bootstrap] Dependency "
                 + dependency
                 + " of application "
                 + context.applicationInfo.name()
                 + " not found.");
        downloadDependency(dependency);
        return false;
      }
      if (infoStack.contains(depContext.applicationInfo)) {
        StringBuilder builder = new StringBuilder();
        for (ApplicationInfo info : infoStack) {
          builder.append(info.name()).append(" -> ");
        }
        builder.append(context.applicationInfo.name()).append(" -> ").append(dependency);
        log.warn("[Simplix | Bootstrap] Circular dependencies detected: " + builder);
        return false;
      }
      infoStack.push(context.applicationInfo);
      boolean dependStatus = installApplication(depContext, infoStack);
      infoStack.pop();
      if (!dependStatus) {
        log.warn("[Simplix | Bootstrap] Dependency " + dependency + " failed to load");
        return false;
      }
    }
    createAppInjector(context);
    return true;
  }

  private void downloadDependency(@NonNull String dependency) {
    // TODO
  }

  private void createAppInjector(@NonNull InstallationContext context) {
    Set<Module> modules = new HashSet<>();
    modules.add(binder -> binder.bind(ApplicationInfo.class).toInstance(context.applicationInfo));
    modules.add(createModuleBridge(context));
    modules.addAll(Arrays.asList(context.modules));
    detectModules(modules, context);
    detectComponents(modules, context);
    Injector injector = this.bossInjector.createChildInjector(modules);
    this.injectorMap.put(context.owner, injector);
    modules.forEach(module -> {
      if (module instanceof AbstractSimplixModule) {
        ((AbstractSimplixModule) module).intercept(injector);
      }
    });
    processAlwaysConstruct(modules, context, injector);
  }

  private void processAlwaysConstruct(
      @NonNull Set<Module> modules,
      @NonNull InstallationContext context,
      @NonNull Injector injector) {
    for (Class<?> componentClass : context.reflections.getTypesAnnotatedWith(AlwaysConstruct.class)) {
      if (!componentClass.isAnnotationPresent(Component.class)) {
        log.warn(
            "[Simplix | Bootstrap] @AlwaysConstruct can only be used in combination with @Component: "
            + componentClass.getName());
        return;
      }
      Component component = componentClass.getAnnotation(Component.class);
      if (modules.stream().anyMatch(module -> module.getClass().equals(component.value()))) {
        injector.getInstance(componentClass);
      }
    }
  }

  private Module createModuleBridge(@NonNull InstallationContext context) {
    return binder -> {
      for (String dependency : context.applicationInfo.dependencies()) {
        Injector injector = this.injectorMap.get(this.toInstall.get(dependency).owner);
        Map<Key<?>, Binding<?>> bindings = injector.getAllBindings();
        for (Key key : bindings.keySet()) {
          Class rawType = key.getTypeLiteral().getRawType();
          if (rawType.equals(ApplicationInfo.class) || rawType.equals(Stage.class)
              || rawType.equals(Logger.class) || rawType.equals(Injector.class)) {
            continue;
          }
          Provider<?> provider = bindings.get(key).getProvider();
          binder.bind(key).toProvider(provider);
        }
      }
    };
  }

  private void detectComponents(
      @NonNull Set<Module> modules,
      @NonNull InstallationContext context) {
    for (Class<?> componentClass : context.reflections.getTypesAnnotatedWith(Component.class)) {
      try {
        Component component = componentClass.getAnnotation(Component.class);
        AbstractSimplixModule simplixModule = findAbstractSimplixModule(modules, component.value());
        if (simplixModule == null) {
          log.warn("[Simplix | Bootstrap] "
                   + context.applicationInfo.name()
                   + ": Component "
                   + componentClass.getName()
                   + " referenced module "
                   + component.value().getName()
                   + " which is not available in this context.");
          log.warn("[Simplix | Bootstrap] "
                   + context.applicationInfo.name()
                   + ": Available modules in this context: "
                   + modules);
          continue;
        }
        simplixModule.components().put(component, componentClass);
        log.info("[Simplix | Bootstrap] "
                 + context.applicationInfo.name()
                 + ": Detected "
                 + componentClass.getName());
      } catch (Throwable t) {
        if (t instanceof TypeNotPresentException) {
          log.debug("[Simplix | Bootstrap] "
                    + context.applicationInfo.name()
                    + ": Cannot register "
                    + componentClass.getName(), t);
          continue;
        }
        log.warn("[Simplix | Bootstrap] "
                 + context.applicationInfo.name()
                 + ": Cannot register "
                 + componentClass.getName(), t);
      }
    }
  }

  @Nullable
  private AbstractSimplixModule findAbstractSimplixModule(
      @NonNull Set<Module> modules,
      @NonNull Class<? extends AbstractSimplixModule> clazz) {
    for (Module module : modules) {
      if (module.getClass().getName().equals(clazz.getName())) {
        return (AbstractSimplixModule) module;
      }
    }
    return null;
  }

  private void detectModules(@NonNull Set<Module> modules, @NonNull InstallationContext ctx) {
    for (Class<?> moduleClass : ctx.reflections.getTypesAnnotatedWith(InjectorModule.class)) {
      try {
        InjectorModule module = moduleClass.getAnnotation(InjectorModule.class);
        if (!module.value().equals(ctx.applicationInfo.name())) {
          continue;
        }
        Object instance = moduleClass.newInstance();
        modules.add((Module) instance);
        log.info("[Simplix | Bootstrap] "
                 + ctx.applicationInfo.name()
                 + ": Registered module "
                 + moduleClass.getSimpleName());
      } catch (Throwable throwable) {
        log.warn("[Simplix | Bootstrap] Unable to create module "
                 + moduleClass.getName()
                 + " for application "
                 + ctx.applicationInfo.name(), throwable);
      }
    }
  }

  public static SimplixInstaller instance() {
    return INSTANCE;
  }

  @AllArgsConstructor
  final static class InstallationContext {

    private final Class<?> owner;
    private final Reflections reflections;
    private final ApplicationInfo applicationInfo;
    private final Module[] modules;

  }

}
