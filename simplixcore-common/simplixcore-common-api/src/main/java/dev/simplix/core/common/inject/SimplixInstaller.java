package dev.simplix.core.common.inject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.inject.*;
import com.google.inject.spi.ElementSource;
import dev.simplix.core.common.ApplicationInfo;
import dev.simplix.core.common.aop.*;
import dev.simplix.core.common.deploader.Dependencies;
import dev.simplix.core.common.deploader.Dependency;
import dev.simplix.core.common.deploader.DependencyLoader;
import dev.simplix.core.common.deploader.Repository;
import dev.simplix.core.common.libloader.LibraryLoader;
import dev.simplix.core.common.updater.Updater;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Supplier;
import java.util.logging.Logger;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.reflections.Reflections;

/**
 * This class is used to install the Guice injectors for all registered {@link SimplixApplication}s.
 * A {@link SimplixApplication} needs to be registered using {@link SimplixInstaller#register(Class,
 * Module...)} in order to use dependency injection in your software project.
 */
@Slf4j
public class SimplixInstaller {

  private static final String SIMPLIX_BOOTSTRAP = "[Simplix | Bootstrap] ";
  private static final SimplixInstaller INSTANCE = new SimplixInstaller();
  private final Map<Class<?>, Injector> injectorMap = new HashMap<>();
  private final Map<String, InstallationContext> toInstall = new HashMap<>();
  private final Gson gson = new GsonBuilder().create();
  private Injector bossInjector;
  private DependencyLoader dependencyLoader;
  private LibraryLoader libraryLoader;
  private Updater updater;

  /**
   * This will register a class annotated with {@link SimplixApplication}.
   *
   * @param owner   The main class of the application
   * @param modules Pre-constructed modules which shall be available in the injection context
   * @throws IllegalArgumentException when the owner class is not annotated with {@link
   *                                  SimplixApplication}
   */
  public void register(@NonNull Class<?> owner, @NonNull Module... modules) {
    if (!owner.isAnnotationPresent(SimplixApplication.class)) {
      throw new IllegalArgumentException("Owner class must be annotated with @SimplixApplication");
    }
    SimplixApplication application = owner.getAnnotation(SimplixApplication.class);
    if (this.toInstall.containsKey(application.name())) {
      log.warn(SIMPLIX_BOOTSTRAP + application.name() + " is already registered. Please check " +
               "for unnecessary double registration of your application. Callstack:");
      return;
    }
    Set<String> basePackages = determineBasePackages(owner);
    ApplicationInfo info = ApplicationInfo.builder()
        .name(application.name())
        .version(application.version())
        .authors(application.authors())
        .workingDirectory(new File(application.workingDirectory()))
        .dependencies(application.dependencies())
        .build();
    this.toInstall.put(
        application.name(),
        new InstallationContext(owner, new Reflections(basePackages, owner.getClassLoader()),
            info, detectReferencedModules(owner, modules, application.name())));
    processRemoteDependencies(owner, info);
    if (this.bossInjector != null) {
      // YOU ARE TOO LATE
      log.info(SIMPLIX_BOOTSTRAP + "Late install " + application.name() + "...");
      installApplication(this.toInstall.get(application.name()), new Stack<>());
    }
  }

  private Module[] detectReferencedModules(Class<?> owner, Module[] modules, String appName) {
    if (!owner.isAnnotationPresent(RequireModules.class)) {
      return modules;
    }
    RequireModules requireModules = owner.getAnnotation(RequireModules.class);
    Set<Module> out = new HashSet<>(Arrays.asList(modules));
    try {
      Supplier<AbstractSimplixModule[]> supplier = requireModules.value().newInstance();
      out.addAll(Arrays.asList(supplier.get()));
    } catch (InstantiationException | IllegalAccessException e) {
      log.error(SIMPLIX_BOOTSTRAP
                + appName
                + ": Cannot construct module supplier. Please make sure the default constructor is accessible. "
                + requireModules.value().getName(), e);
    }
    return out.toArray(new Module[0]);
  }

  /**
   * Checks if a specified application by its name is registered for installation.
   *
   * @param appName The application name
   * @return true if there is an application registered with that name
   */
  public boolean registered(@NonNull String appName) {
    return this.toInstall.containsKey(appName);
  }

  /**
   * Returns the application injector of an application by its main class
   *
   * @param owner The main class of the application
   * @return The injector of that application
   */
  public Injector injector(@NonNull Class<?> owner) {
    return this.injectorMap.get(owner);
  }

  /**
   * @return The dependency loader
   */
  public DependencyLoader dependencyLoader() {
    if (this.dependencyLoader == null) {
      initDependencyLoader();
    }
    return this.dependencyLoader;
  }

  /**
   * @return The updater instance
   */
  public Updater updater() {
    if (this.updater == null) {
      initUpdater();
    }
    return this.updater;
  }

  private void initUpdater() {
    try {
      String libLoaderClass = System.getProperty(
          "dev.simplix.core.common.updater.Updater",
          "dev.simplix.core.common.updater.ArtifactUpdater");

      Class<?> clazz = Class.forName(libLoaderClass);
      this.updater = (Updater) clazz.newInstance();
    } catch (Exception exception) {
      throw new RuntimeException("Unable to initialize updater", exception);
    }
  }

  public LibraryLoader libraryLoader() {
    if (this.libraryLoader == null) {
      initLibraryLoader();
    }
    return this.libraryLoader;
  }

  private void initLibraryLoader() {
    try {
      String libLoaderClass = System.getProperty(
          "dev.simplix.core.common.libloader.LibraryLoader",
          "dev.simplix.core.common.libloader.SimpleLibraryLoader");

      Class<?> clazz = Class.forName(libLoaderClass);
      this.libraryLoader = (LibraryLoader) clazz.newInstance();
    } catch (Exception exception) {
      throw new RuntimeException("Unable to initialize library loader", exception);
    }
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

  /**
   * This starts installation of all registered applications. This will do:
   * <ul>
   *     <li>Create a boss {@link Injector}</li>
   *     <li>Install all applications in correct order:</li>
   *         <li>Bind the {@link ApplicationInfo} to a new anonymous {@link Module}</li>
   *         <li>Create an anonymous {@link Module} which links bindings from dependent modules to the future app injector</li>
   *         <li>Scanning for {@link Module}s in all defined base packages</li>
   *         <li>Scanning for {@link Component}s in all defined base packages</li>
   *         <li>Create the actual application child {@link Injector}</li>
   *         <li>Processing {@link ComponentInterceptor}s for all previously detected components</li>
   *         <li>Process {@link AlwaysConstruct} for detected components</li>
   * </ul>
   * It is still possible to register new applications after installation, however multiple depending applications
   * need to be registered in correct order since every registered application will be installed right away and will not
   * wait for any registration of dependencies.
   *
   * @throws IllegalStateException If installation was already done
   */
  public void install() {
    if (this.bossInjector != null) {
      throw new IllegalStateException("Already installed");
    }
    this.bossInjector = Guice.createInjector();

    for (String name : this.toInstall.keySet()) {
      InstallationContext context = this.toInstall.get(name);
      if (this.injectorMap.containsKey(context.owner)) {
        continue;
      }
      if (!installApplication(context, new Stack<>())) {
        log.warn(SIMPLIX_BOOTSTRAP + "Failed to load application "
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
        log.warn(SIMPLIX_BOOTSTRAP + "Dependency "
                 + dependency
                 + " of application "
                 + context.applicationInfo.name()
                 + " not found.");
        return false;
      }
      if (infoStack.contains(depContext.applicationInfo)) {
        StringBuilder builder = new StringBuilder();
        for (ApplicationInfo info : infoStack) {
          builder.append(info.name()).append(" -> ");
        }
        builder.append(context.applicationInfo.name()).append(" -> ").append(dependency);
        log.warn(SIMPLIX_BOOTSTRAP + "Circular dependencies detected: " + builder);
        return false;
      }
      infoStack.push(context.applicationInfo);
      boolean dependStatus = installApplication(depContext, infoStack);
      infoStack.pop();
      if (!dependStatus) {
        log.warn(SIMPLIX_BOOTSTRAP + "Dependency " + dependency + " failed to load");
        return false;
      }
    }
    createAppInjector(context);
    return true;
  }

  private void processRemoteDependencies(Class<?> appOwner, ApplicationInfo info) {
    InputStream inputStream = null;
    InputStreamReader reader = null;
    try {
      inputStream = appOwner.getResourceAsStream("/dependencies.json");
      if (inputStream == null) {
        return;
      }

      reader = new InputStreamReader(
          inputStream,
          StandardCharsets.UTF_8);
      Dependencies dependencies = this.gson.fromJson(reader, Dependencies.class);
      if (this.dependencyLoader == null) {
        initDependencyLoader();
      }
      List<Repository> repositories = Arrays.asList(dependencies.repositories());
      for (Dependency dependency : dependencies.dependencies()) {
        log.info(SIMPLIX_BOOTSTRAP
                 + info.name()
                 + ": Load dependency "
                 + dependency
                 + " from repository...");
        dependency.applicationName(info.name());
        dependency.applicationClass(appOwner);
        if (!this.dependencyLoader.load(dependency, repositories)) {
          log.error(SIMPLIX_BOOTSTRAP
                    + info.name() + ": Unable to load dependency " + dependency);
        }
      }
    } catch (JsonParseException exception) {
      log.error(SIMPLIX_BOOTSTRAP
                + info.name() + ": Cannot parse dependencies.json", exception);
    } finally {
      if (inputStream != null) {
        try {
          inputStream.close();
        } catch (IOException ignored) {
          // Ignored
        }
      }

      if (reader != null) {
        try {
          reader.close();
        } catch (IOException ignored) {
          // Ignored
        }
      }
    }
  }

  private void initDependencyLoader() {
    try {
      String depLoaderClass = System.getProperty(
          "dev.simplix.core.common.deploader.DependencyLoader",
          "dev.simplix.core.common.deploader.ArtifactDependencyLoader");

      Class<?> clazz = Class.forName(depLoaderClass);
      this.dependencyLoader = (DependencyLoader) clazz.newInstance();
    } catch (Exception exception) {
      throw new RuntimeException("Unable to initialize dependency loader", exception);
    }
  }

  private void createAppInjector(@NonNull InstallationContext context) {
    Set<Module> modules = new HashSet<>();
    modules.add(binder -> binder.bind(ApplicationInfo.class).toInstance(context.applicationInfo));
    modules.add(createModuleBridge(context));
    modules.addAll(Arrays.asList(context.modules));
    detectModules(modules, context);
    detectComponents(modules, context);
    try {
      Injector injector = this.bossInjector.createChildInjector(modules);
      this.injectorMap.put(context.owner, injector);
      modules.forEach(module -> {
        if (module instanceof AbstractSimplixModule) {
          ((AbstractSimplixModule) module).intercept(injector);
        }
      });
      processAlwaysConstruct(modules, context, injector);
      log.info(SIMPLIX_BOOTSTRAP + "Installed application "
               + context.applicationInfo.name()
               + " "
               + context.applicationInfo.version()
               + " by "
               + Arrays
                   .toString(context.applicationInfo.authors()));
    } catch (CreationException exception) {
      log.error(SIMPLIX_BOOTSTRAP + "Cannot create injector for application "
                + context.applicationInfo.name(), exception);
    }
  }

  private void processAlwaysConstruct(
      @NonNull Set<Module> modules,
      @NonNull InstallationContext context,
      @NonNull Injector injector) {
    for (Class<?> componentClass : context.reflections.getTypesAnnotatedWith(AlwaysConstruct.class)) {
      if (!componentClass.isAnnotationPresent(Component.class)) {
        log.warn(
            SIMPLIX_BOOTSTRAP + "@AlwaysConstruct can only be used in combination with @Component: "
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
      Set<Key<?>> bound = new HashSet<>();
      for (String dependency : context.applicationInfo.dependencies()) {
        Injector injector = this.injectorMap.get(this.toInstall.get(dependency).owner);
        Map<Key<?>, Binding<?>> bindings = injector.getBindings();
        for (Key key : bindings.keySet()) {
          Class<?> rawType = key.getTypeLiteral().getRawType();
          if (rawType.equals(ApplicationInfo.class) || rawType.equals(Stage.class)
              || rawType.equals(Logger.class) || rawType.equals(Injector.class)
              || rawType.isAnnotationPresent(Private.class) || bound.contains(key)
              || (
                  key.getAnnotationType() != null && key
                      .getAnnotationType()
                      .equals(Private.class))) {
            continue;
          }
          Binding<?> binding = bindings.get(key);
          log.debug(SIMPLIX_BOOTSTRAP
                    + context.applicationInfo.name()
                    + ": Bound "
                    + key
                    + " to element source "
                    + ((ElementSource) binding.getSource())
                        .getDeclaringSource()
                    + " inherited from "
                    + dependency);
          bound.add(key);
          Provider<?> provider = binding.getProvider();
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
//          log.warn(SIMPLIX_BOOTSTRAP
//                   + context.applicationInfo.name()
//                   + ": Component "
//                   + componentClass.getName()
//                   + " referenced module "
//                   + component.value().getName()
//                   + " which is not available in this context.");
//          log.warn(SIMPLIX_BOOTSTRAP
//                   + context.applicationInfo.name()
//                   + ": Available modules in this context: "
//                   + modules);
          continue;
        }
        simplixModule.components().put(componentClass, component);
        log.info(SIMPLIX_BOOTSTRAP
                 + context.applicationInfo.name()
                 + ": Detected "
                 + componentClass.getName());
      } catch (Throwable t) {
        if (t instanceof TypeNotPresentException) {
          /* Suppress some dependency problems here since some endusers would go crazy when they see
              exceptions like this in normal production... */
          log.debug(SIMPLIX_BOOTSTRAP
                    + context.applicationInfo.name()
                    + ": Cannot register "
                    + componentClass.getName(), t);
          continue;
        }
        log.warn(SIMPLIX_BOOTSTRAP
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
      if (module.getClass().equals(clazz)) {
        return (AbstractSimplixModule) module;
      }
    }
    return null;
  }

  private void detectModules(@NonNull Set<Module> modules, @NonNull InstallationContext ctx) {
    for (Class<?> moduleClass : ctx.reflections.getTypesAnnotatedWith(ApplicationModule.class)) {
      try {
        ApplicationModule module = moduleClass.getAnnotation(ApplicationModule.class);
        if (!module.value().equals(ctx.applicationInfo.name())) {
          continue;
        }
        Object instance = moduleClass.newInstance();
        modules.add((Module) instance);
        log.info(SIMPLIX_BOOTSTRAP
                 + ctx.applicationInfo.name()
                 + ": Registered module "
                 + moduleClass.getSimpleName());
      } catch (Throwable throwable) {
        log.warn(SIMPLIX_BOOTSTRAP + "Unable to create module "
                 + moduleClass.getName()
                 + " for application "
                 + ctx.applicationInfo.name(), throwable);
      }
    }
  }

  public static SimplixInstaller instance() {
    return INSTANCE;
  }

  public Class<?> applicationClass(String name) {
    return this.toInstall.get(name).owner;
  }

  @AllArgsConstructor
  final static class InstallationContext {

    private final Class<?> owner;
    private final Reflections reflections;
    private final ApplicationInfo applicationInfo;
    private final Module[] modules;

  }

}
