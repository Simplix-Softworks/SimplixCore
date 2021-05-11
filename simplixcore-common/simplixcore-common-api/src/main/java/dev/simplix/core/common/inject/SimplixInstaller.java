package dev.simplix.core.common.inject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.inject.*;
import com.google.inject.spi.ElementSource;
import dev.simplix.core.common.ApplicationInfo;
import dev.simplix.core.common.aop.SuppressWarnings;
import dev.simplix.core.common.aop.*;
import dev.simplix.core.common.deploader.*;
import dev.simplix.core.common.event.Events;
import dev.simplix.core.common.events.ApplicationPreInstallEvent;
import dev.simplix.core.common.libloader.LibraryLoader;
import dev.simplix.core.common.platform.Platform;
import dev.simplix.core.common.platform.PlatformDependent;
import dev.simplix.core.common.updater.UpdatePolicy;
import dev.simplix.core.common.updater.UpdatePolicyDeserializer;
import dev.simplix.core.common.updater.Updater;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Logger;
import lombok.AllArgsConstructor;
import lombok.Cleanup;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.jetbrains.annotations.Nullable;
import org.reflections.Reflections;

/**
 * This class is used to install the Guice injectors for all registered {@link SimplixApplication}s.
 * A {@link SimplixApplication} needs to be registered using {@link SimplixInstaller#register(Class,
 * Module...)} in order to use dependency injection in your software project.
 */
public class SimplixInstaller {

  private static final String SIMPLIX_BOOTSTRAP = "[Simplix | Bootstrap] ";
  private static SimplixInstaller INSTANCE = null;
  private final Map<Class<?>, Injector> injectorMap = new HashMap<>();
  private final Map<Class<?>, DependencyManifest> dependenciesMap = new HashMap<>();

  private final Map<String, InstallationContext> toInstall = new ConcurrentHashMap<>();
  private final Map<String, UpdatePolicy> updatePolicyMap = new HashMap<>();
  private final Gson gson = new GsonBuilder()
      .registerTypeAdapter(UpdatePolicy.class, new UpdatePolicyDeserializer())
      .create();
  private final Timer updateTimer = new Timer("UpdateTimer");
  private Platform platform;
  private Injector bossInjector;
  private DependencyLoader dependencyLoader;
  private LibraryLoader libraryLoader;
  private Updater updater;
  private final org.slf4j.Logger log;

  public SimplixInstaller(org.slf4j.Logger logger) {
    this.log = logger;
  }

  public static void init(org.slf4j.Logger logger) {
    if (INSTANCE == null) {
      INSTANCE = new SimplixInstaller(logger);
    }
  }

  public static SimplixInstaller instance() {
    if (INSTANCE == null) {
      throw new IllegalStateException("Instance not yet set");
    }
    return INSTANCE;
  }

  /**
   * This will register a class annotated with {@link SimplixApplication}.
   *
   * @param owner   The main class of the application
   * @param modules Pre-constructed modules which shall be available in the injection context
   * @throws IllegalArgumentException when the owner class is not annotated with {@link
   *                                  SimplixApplication}
   */
  public Optional<DependencyLoadingException> register(
      @NonNull Class<?> owner,
      @NonNull Module... modules) {
    return register(owner, e -> {
    }, modules);
  }

  /**
   * This will register a class annotated with {@link SimplixApplication}.
   *
   * @param owner       The main class of the application
   * @param onException Will accept the exception if some exception occurs while installing
   * @param modules     Pre-constructed modules which shall be available in the injection context
   * @throws IllegalArgumentException when the owner class is not annotated with {@link
   *                                  SimplixApplication}
   */
  public Optional<DependencyLoadingException> register(
      @NonNull Class<?> owner,
      @NonNull Consumer<Exception> onException,
      @NonNull Module... modules) {
    if (!owner.isAnnotationPresent(SimplixApplication.class)) {
      throw new IllegalArgumentException("Owner class must be annotated with @SimplixApplication");
    }
    SimplixApplication application = owner.getAnnotation(SimplixApplication.class);
    if (this.toInstall.containsKey(application.name())) {
      log.warn(SIMPLIX_BOOTSTRAP + application.name() + " is already registered. Please check " +
               "for unnecessary double registration of your application.");
      return Optional.empty();
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
            info, detectReferencedModules(owner, modules, application.name()), onException));
    final Optional<DependencyLoadingException> optionalDependencyLoadingException = processRemoteDependencies(
        owner,
        info);
    if (this.bossInjector != null) {
      // YOU ARE TOO LATE
      log.info(SIMPLIX_BOOTSTRAP + "Late install " + application.name() + "...");
      installApplication(this.toInstall.get(application.name()), new Stack<>());
    }
    return optionalDependencyLoadingException;
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
  @Nullable
  public Injector injector(@NonNull Class<?> owner) {
    return this.injectorMap.get(owner);
  }

  public Optional<Injector> findInjector(@NonNull Class<?> owner) {
    return Optional.ofNullable(this.injectorMap.get(owner));
  }

  public Optional<DependencyManifest> findDependencies(@NonNull Class<?> owner) {
    return Optional.ofNullable(this.dependenciesMap.get(owner));
  }

  /**
   * @return The current platform.
   */
  public Platform platform() {
    return this.platform;
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
          "dev.simplix.core.common.updater.SimpleUpdater");

      Class<?> clazz = Class.forName(libLoaderClass);
      this.updater = (Updater) clazz.newInstance();
    } catch (Exception exception) {
      // Do nothing
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
  public void install(Platform platform) {
    if (this.bossInjector != null) {
      throw new IllegalStateException("Already installed");
    }
    log.debug(SIMPLIX_BOOTSTRAP + "Installing application on platform " + platform.name());
    this.platform = platform;
    this.bossInjector = Guice.createInjector(Stage.PRODUCTION);

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

    startUpdateTimer();
  }

  private void startUpdateTimer() {
    this.updateTimer.schedule(new TimerTask() {
      @Override
      public void run() {
        for (String appName : SimplixInstaller.this.updatePolicyMap.keySet()) {
          ApplicationInfo applicationInfo = SimplixInstaller.this.toInstall.get(appName).applicationInfo;
          if (System.getProperty("simplix.disableupdate." + appName) != null) {
            continue;
          }
          updater().checkForUpdates(
              applicationInfo,
              SimplixInstaller.this.updatePolicyMap.get(appName));
        }
      }
    }, 1000, TimeUnit.MINUTES.toMillis(5));
  }

  private boolean installApplication(
      @NonNull InstallationContext context,
      @NonNull Stack<ApplicationInfo> infoStack) {
    if (this.injectorMap.containsKey(context.owner)) {
      return true; // Application already installed
    }
    ApplicationPreInstallEvent event = new ApplicationPreInstallEvent(
        context.applicationInfo,
        context.owner);
    Events.call(event);
    context.applicationInfo = event.applicationInfo();
    context.owner = event.applicationClass();
    for (String dependency : context.applicationInfo.dependencies()) {
      InstallationContext depContext = this.toInstall.get(dependency);
      if (depContext == null) {
        log.warn(SIMPLIX_BOOTSTRAP + "Dependency "
                 + dependency
                 + " of application "
                 + context.applicationInfo.name()
                 + " not found.");
        context.onException.accept(new RuntimeException("Dependency "
                                                        + dependency
                                                        + " not found."));
        return false;
      }
      if (infoStack.contains(depContext.applicationInfo)) {
        StringBuilder builder = new StringBuilder();
        for (ApplicationInfo info : infoStack) {
          builder.append(info.name()).append(" -> ");
        }
        builder.append(context.applicationInfo.name()).append(" -> ").append(dependency);
        log.warn(SIMPLIX_BOOTSTRAP + "Circular dependencies detected: " + builder);
        context.onException.accept(new RuntimeException("Circular dependencies detected: "
                                                        + builder));
        return false;
      }
      infoStack.push(context.applicationInfo);
      boolean dependStatus = installApplication(depContext, infoStack);
      infoStack.pop();
      if (!dependStatus) {
        log.warn(SIMPLIX_BOOTSTRAP + "Dependency " + dependency + " failed to load");
        context.onException.accept(new RuntimeException("Dependency "
                                                        + dependency
                                                        + " failed to load"));
        return false;
      }
    }
    try {
      createAppInjector(context);
    } catch (Exception exception) {
      log.error(
          SIMPLIX_BOOTSTRAP + " Exception occurred while creating application injector",
          exception);
      context.onException.accept(exception);
      return false;
    }
    try {
      loadUpdatePolicy(context);
    } catch (Exception exception) {
      log.warn(SIMPLIX_BOOTSTRAP
               + "Cannot load updatepolicy.json of "
               + context.applicationInfo.name(), exception);
    }
    return true;
  }

  /**
   * @param appOwner Class the app should be loaded from
   * @return Optional if no dependencies.json could be found for the appOwner
   * @throws JsonParseException If the dependencies.json file is formatted invalidly
   */
  @SneakyThrows
  private Optional<DependencyManifest> loadDependencies(@NonNull Class<?> appOwner)
      throws JsonParseException {
    @Cleanup
    InputStream inputStream = appOwner.getResourceAsStream("/dependencies.json");

    if (inputStream == null) {
      return Optional.empty();
    }
    @Cleanup
    InputStreamReader reader = new InputStreamReader(
        inputStream,
        StandardCharsets.UTF_8);

    return Optional.ofNullable(new GsonBuilder()
        .create()
        .fromJson(reader, DependencyManifest.class));
  }

  private void loadUpdatePolicy(InstallationContext context) throws IOException {
    InputStream stream = context.owner.getClassLoader().getResourceAsStream("updatepolicy.json");
    if (stream == null) {
      return;
    }
    try (
        InputStreamReader inputStreamReader = new InputStreamReader(
            stream,
            StandardCharsets.UTF_8)) {
      UpdatePolicy policy = this.gson.fromJson(
          inputStreamReader,
          UpdatePolicy.class);
      this.updatePolicyMap.put(context.applicationInfo.name(), policy);
    }
  }

  public Optional<DependencyLoadingException> earlyLoadDependencies(Class<?> appClass) {
    ApplicationInfo tempInfo = new ApplicationInfo(appClass.getSimpleName(),
        "1.0", new String[0], new File("."), new String[0]);
    return processRemoteDependencies(appClass, tempInfo);
  }

  private Optional<DependencyLoadingException> processRemoteDependencies(
      @NonNull Class<?> appOwner,
      @NonNull ApplicationInfo info) {
    if (this.dependencyLoader == null) {
      initDependencyLoader();
    }
    if (this.dependenciesMap.containsKey(appOwner)) {
      return Optional.empty();
    }

    Optional<DependencyManifest> optionalDependencies;

    try {
      optionalDependencies = loadDependencies(appOwner);
    } catch (JsonParseException jsonParseException) {
      log.error(
          SIMPLIX_BOOTSTRAP + info.name() + ": Cannot parse dependencies.json",
          jsonParseException);
      optionalDependencies = Optional.empty();
    }

    if (!optionalDependencies.isPresent()) {
      return Optional.empty();
    }

    DependencyManifest dependencyManifest = optionalDependencies.get();
    this.dependenciesMap.put(appOwner, dependencyManifest);

    List<Repository> repositories = Arrays.asList(dependencyManifest.repositories());
    for (Dependency dependency : dependencyManifest.dependencies()) {
      if (dependency.platform() != null && dependency.platform() != this.platform) {
        continue;
      }
      log.info(SIMPLIX_BOOTSTRAP
               + info.name()
               + ": Load dependency "
               + dependency
               + " from repository...");
      dependency.applicationName(info.name());
      dependency.applicationClass(appOwner);
      final Optional<DependencyLoadingException> load = this.dependencyLoader.load(
          dependency,
          repositories);
      if (load.isPresent()) {
        log.error(SIMPLIX_BOOTSTRAP
                  + info.name() + ": Unable to load dependency " + dependency);
        return load;
      }
    }
    return Optional.empty();
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
    filterPlatformDependentModules(modules, context.applicationInfo.name());
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
      context.onException.accept(exception);
    }
  }

  private void filterPlatformDependentModules(Set<Module> modules, String appName) {
    Iterator<Module> moduleIterator = modules.iterator();
    while (moduleIterator.hasNext()) {
      Module module = moduleIterator.next();
      if (module.getClass().isAnnotationPresent(PlatformDependent.class)) {
        PlatformDependent platform = module.getClass().getAnnotation(PlatformDependent.class);
        if (platform.value() != this.platform) {
          log.debug(SIMPLIX_BOOTSTRAP
                    + " "
                    + appName
                    + ": Filter module "
                    + module
                        .getClass()
                        .getName()
                    + " from binding. Expected platform "
                    + platform.value()
                    + " but we are running on "
                    + this.platform);
          moduleIterator.remove();
        }
      }
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
        AbstractSimplixModule simplixModule;
        Component component;
        try {
          component = componentClass.getAnnotation(Component.class);
          simplixModule = findAbstractSimplixModule(modules, component.value());
        } catch (Throwable throwable) {
          component = null;
          simplixModule = null;
        }

        if (simplixModule == null) {
          if (!suppressWarning(componentClass, "moduleNotAvailable")
              || !suppressWarning(context.owner, "moduleNotAvailable")) {
            log.warn(SIMPLIX_BOOTSTRAP
                     + context.applicationInfo.name()
                     + ": Component "
                     + componentClass.getName()
                     + " referenced module "
                     + component.value().getName()
                     + " which is not available in this context.");
            log.warn(SIMPLIX_BOOTSTRAP
                     + context.applicationInfo.name()
                     + ": Available modules in this context: "
                     + modules);
          }
          continue;
        } else {
          if (simplixModule.getClass().isAnnotationPresent(PlatformDependent.class)) {
            PlatformDependent platformDependent = simplixModule
                .getClass()
                .getAnnotation(PlatformDependent.class);
            if (platformDependent.value() != this.platform) {
              continue;
            }
          }
        }
        simplixModule.components().put(componentClass, component);
        log.info(SIMPLIX_BOOTSTRAP
                 + context.applicationInfo.name()
                 + ": Detected "
                 + componentClass.getName());
      } catch (Throwable throwable) {
        if (suppressWarning(componentClass, "exception:*")
            || suppressWarning(
            componentClass,
            "exception:" + throwable.getClass().getSimpleName())) {
          continue;
        }
        log.warn(SIMPLIX_BOOTSTRAP
                 + context.applicationInfo.name()
                 + ": Cannot register "
                 + componentClass.getName(), throwable);
      }
    }
  }

  private boolean suppressWarning(Class<?> clazz, String warning) {
    if (!clazz.isAnnotationPresent(SuppressWarnings.class)) {
      return false;
    }
    return arrayContains(clazz.getAnnotation(SuppressWarnings.class).value(), warning);
  }

  private <T> boolean arrayContains(T[] value, T obj) {
    for (T t : value) {
      if (t.equals(obj)) {
        return true;
      }
    }
    return false;
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
        if (suppressWarning(moduleClass, "exception:*")
            || suppressWarning(moduleClass, "exception:" + throwable.getClass().getSimpleName())) {
          continue;
        }
        log.warn(SIMPLIX_BOOTSTRAP + "Unable to create module "
                 + moduleClass.getName()
                 + " for application "
                 + ctx.applicationInfo.name(), throwable);
      }
    }
  }

  public Class<?> applicationClass(String name) {
    return this.toInstall.get(name).owner;
  }

  @AllArgsConstructor
  static final class InstallationContext {

    private Class<?> owner;
    private final Reflections reflections;
    private ApplicationInfo applicationInfo;
    private final Module[] modules;
    private final Consumer<Exception> onException;

  }

}
