package dev.simplix.core.common.libloader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.simplix.core.common.aop.SimplixApplication;
import dev.simplix.core.common.inject.SimplixInstaller;
import dev.simplix.core.common.updater.Version;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

public class SimpleLibraryLoader implements LibraryLoader {

  private final Version javaVersion;
  private final Logger log;
  private final Gson gson = new GsonBuilder().create();
  private final Set<File> files = new HashSet<>();
  private Method addMethod;

  public SimpleLibraryLoader(@NonNull Logger log) {
    this.log = log;
    javaVersion = Version.parse(System.getProperty("java.version"));

  }

  @Override
  public void loadLibraries(@NonNull File directory) {
    if (!directory.exists()) {
      directory.mkdirs();
    }

    for (File file : directory.listFiles((dir, name) -> name.endsWith(".jar"))) {
      loadLibrary(file);
    }
  }

  @Override
  public void loadLibrary(File file) {
    try {
      if (files.contains(file)) {
        return;
      }
      ClassLoader classLoader = createClassLoader(file);
      if (classLoader == null) {
        return;
      }
      log.info("[Simplix | LibLoader] Loaded library " + file.getName());
      addUrlToClassLoader((URLClassLoader) classLoader, file);
      checkAndLoadSimplixApplication(file, classLoader);
    } catch (Exception ex) {
      log.info("[Simplix | LibLoader] Unable to load library " + file.getName(), ex);
    }
  }

  @Override
  public void loadLibraryEncapsulated(@NonNull File file, @NotNull Class<?> owner) {
    try {
      if (files.contains(file)) {
        return;
      }
      ClassLoader classLoader;

      if (javaVersion.olderThen(Version.parse("16.0.0"))) {
        classLoader = owner.getClassLoader();
      } else {
        classLoader = createClassLoader(file);
      }

      if (!(classLoader instanceof URLClassLoader)) {
        log.warn("[Simplix | LibLoader] with classloader: " + classLoader.getClass().getName()
                 + owner.getSimpleName()
                 + " is not supporting library encapsulation. Loading as shared library...");
        loadLibrary(file);
        return;
      }

      addUrlToClassLoader((URLClassLoader) classLoader, file);
      checkAndLoadSimplixApplication(file, classLoader);

      log.info("[Simplix | LibLoader] Loaded encapsulated library " + file.getName() +
               " for application " + owner.getSimpleName());

    } catch (Exception ex) {
      log.info("[Simplix | LibLoader] Unable to load encapsulated library " + file.getName() +
               " for application " + owner.getSimpleName(), ex);

    }
  }

  private void checkAndLoadSimplixApplication(@NonNull File file, ClassLoader classLoader)
      throws IOException {
    files.add(file);
    InputStream stream = classLoader.getResourceAsStream("library.json");
    if (stream == null) {
      return;
    }
    try (
        InputStreamReader inputStreamReader = new InputStreamReader(
            stream,
            StandardCharsets.UTF_8)) {
      LibraryDescription libraryDescription = this.gson.fromJson(
          inputStreamReader,
          LibraryDescription.class);
      try {
        Class<?> mainClass = classLoader.loadClass(libraryDescription.mainClass());
        if (mainClass.isAnnotationPresent(SimplixApplication.class)) {
          SimplixInstaller.instance().register(mainClass, exception -> {
            log.error("Could not install library: "
                      + mainClass.getSimpleName()
                      + " due to "
                      + exception.getMessage());
          }, true);
          log.debug("[Simplix | LibLoader] "
                    + libraryDescription.name()
                    + " was registered as a SimplixApplication");
          log.debug("[Simplix | LibLoader] Application class = "
                    + mainClass.getName()
                    + " hashCode = "
                    + mainClass.hashCode());
        }
      } catch (ClassNotFoundException exception) {
        throw new RuntimeException("Cannot find library main class", exception);
      }
    }
  }

  private void addUrlToClassLoader(URLClassLoader classLoader, File file)
      throws ReflectiveOperationException, MalformedURLException {

    if (classLoader instanceof SimplixClassLoader) {
      ((SimplixClassLoader) classLoader).addURL(file.toURI().toURL());
      return;
    }

    if (addMethod == null) {
      addMethod = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
      addMethod.setAccessible(true);
    }
    addMethod.invoke(classLoader, file.toURI().toURL());
  }

  @Override
  public Set<File> loadedLibraries() {
    return files;
  }

  private ClassLoader createClassLoader(File file) throws ReflectiveOperationException {
    String classLoaderFabricator = System.getProperty(
        "dev.simplix.core.libloader.ClassLoaderFabricator",
        "dev.simplix.core.common.libloader.StandaloneClassLoaderFabricator");
    Class<? extends Function<File, ClassLoader>> clazz = (Class<? extends Function<File, ClassLoader>>) Class
        .forName(classLoaderFabricator);
    final ClassLoader out =  clazz.newInstance().apply(file);
    if (out == null) {
      throw new IllegalStateException("Created Classloader can not be null: " + classLoaderFabricator);
    }
    return out;
  }

}
