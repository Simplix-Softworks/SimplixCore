package dev.simplix.core.minecraft.spigot.plugin.libloader;

import com.google.common.io.ByteStreams;
import dev.simplix.core.common.libloader.SimplixClassLoader;
import dev.simplix.core.common.updater.Version;
import dev.simplix.core.minecraft.spigot.plugin.SimplixPlugin;
import java.io.File;
import java.lang.reflect.*;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

@Slf4j
public final class PluginClassLoaderFabricator implements Function<File, ClassLoader> {

  private static ClassLoader cachedResult;
  private final Version javaVersion = Version.parse(System.getProperty("java.version"));

  private void unfinalize(@NonNull Field loadersField)
      throws NoSuchFieldException, IllegalAccessException {
    Field modifiersField = Field.class.getDeclaredField("modifiers");
    modifiersField.setAccessible(true);
    modifiersField.set(loadersField, loadersField.getModifiers() & ~Modifier.FINAL);
  }

  @Override
  public ClassLoader apply(@NonNull File file) {
    // We don't to create a SimplixClassLoader twice if on 1.16 & Java16
    if (cachedResult != null) {
      return cachedResult;
    }

    // Our resulting class loader. Varies from 1.8 ->  1.16 (PluginClassLoader vs SimplixClassLoader)
    ClassLoader out;
    try {
      injectFakeClass(file);

      SimplixPlugin plugin = JavaPlugin.getPlugin(SimplixPlugin.class);
      PluginDescriptionFile pluginDescriptionFile = new PluginDescriptionFile(
          SimplixPlugin.class.getResourceAsStream("/fakeplugin.yml"));

      Object pluginClassloader;

      try {
        Class<?> classLoaderClass = Class.forName("org.bukkit.plugin.java.PluginClassLoader");
        Constructor<?> constructor = classLoaderClass.getDeclaredConstructor(
            JavaPluginLoader.class,
            ClassLoader.class,
            PluginDescriptionFile.class,
            File.class,
            File.class
        );
        constructor.setAccessible(true);
        pluginClassloader = constructor.newInstance(
            plugin.getPluginLoader(),
            plugin.getClass().getClassLoader(),
            pluginDescriptionFile,
            plugin.getDataFolder(),
            file
        );
        out = (ClassLoader) pluginClassloader;
      } catch (Throwable throwable) {
        if (javaVersion.olderThen(Version.parse("16.0.0"))) {
          log.info("Used Java11 fabricator");
          // Spigot 1.16 or newer using old reflection
          Class<?> classLoaderClass = Class.forName("org.bukkit.plugin.java.PluginClassLoader");
          Constructor<?> constructor = classLoaderClass.getDeclaredConstructor(
              JavaPluginLoader.class,
              ClassLoader.class,
              PluginDescriptionFile.class,
              File.class,
              File.class,
              ClassLoader.class
          );
          constructor.setAccessible(true);
          pluginClassloader = constructor.newInstance(
              plugin.getPluginLoader(),
              plugin.getClass().getClassLoader().getParent(),
              pluginDescriptionFile,
              plugin.getDataFolder(),
              file,
              null
          );
          out = (ClassLoader) pluginClassloader;
        } else {
          log.info("Used Java16 fabricator");
          // Spigot 1.16 newer - Compatible with Java16
          Class<?> classLoaderClass = Class.forName("org.bukkit.plugin.java.PluginClassLoader");
          Constructor<?> constructor = classLoaderClass.getDeclaredConstructor(
              JavaPluginLoader.class,
              ClassLoader.class,
              PluginDescriptionFile.class,
              File.class,
              File.class,
              ClassLoader.class
          );
          constructor.setAccessible(true);

          final ClassLoader simplixCoreClassLoader = Bukkit
              .getPluginManager()
              .getPlugin("SimplixCore")
              .getClass()
              .getClassLoader();

          final Method loadClass0 = simplixCoreClassLoader
              .getClass()
              .getDeclaredMethod(
                  "loadClass0",
                  String.class,
                  boolean.class,
                  boolean.class,
                  boolean.class);

          loadClass0.setAccessible(true);

          ClassLoader parentLoader = new URLClassLoader(new URL[]{
          }) {
            @Override
            public Class<?> loadClass(String name) throws ClassNotFoundException {
              try {
                final Object invoke = loadClass0.invoke(
                    simplixCoreClassLoader,
                    name,
                    false,
                    true,
                    false);
                return (Class<?>) invoke;
              } catch (IllegalAccessException reflectiveOperationException) {
                reflectiveOperationException.printStackTrace();
                throw new ClassNotFoundException(name);
              } catch (InvocationTargetException invocationTargetException) {
                if (invocationTargetException.getCause() instanceof ClassNotFoundException) {
                  throw (ClassNotFoundException) invocationTargetException.getCause();
                }
                invocationTargetException.printStackTrace();
                throw new ClassNotFoundException(name);
              }
            }
          };

          final SimplixClassLoader simplixClassLoader = new SimplixClassLoader(
              new URL[0],
              parentLoader);

          pluginClassloader = constructor.newInstance(
              plugin.getPluginLoader(),
              plugin.getClass().getClassLoader().getParent(),
              pluginDescriptionFile,
              plugin.getDataFolder(),
              file,
              simplixClassLoader
          );
          out = simplixClassLoader;
          cachedResult = simplixClassLoader;
        }
      }

      Field loadersField = JavaPluginLoader.class.getDeclaredField("loaders");
      loadersField.setAccessible(true);
      if (Map.class.isAssignableFrom(loadersField.getType())) { // Spigot 1.8 - Spigot 1.10.2
        if (Modifier.isFinal(loadersField.getModifiers())) {
          unfinalize(loadersField);
        }
        Map<String, Object> loaders = (Map<String, Object>) loadersField.get(plugin.getPluginLoader());
        loaders.put("SimplixBridge", pluginClassloader);
        loadersField.set(plugin.getPluginLoader(), loaders);
      } else {
        List<Object> loaders = (List<Object>) loadersField.get(plugin.getPluginLoader());
        loaders.add(pluginClassloader);
      }

      return out;
    } catch (Exception exception) {
      log.error("[Simplix | LibLoader] Cannot fabricate PluginClassLoader", exception);
    }
    return null;
  }

  private void injectFakeClass(@NonNull File file) {
    URI uri;
    if (file.getAbsolutePath().startsWith("/")) { // UNIX
      uri = URI.create("jar:file:" + file.getAbsolutePath());
    } else { // Windows
      uri = URI.create("jar:file:/" + file.getAbsolutePath()
          .replace("\\", "/")
          .replace(" ", "%20"));
    }
    Map<String, String> env = new HashMap<>();
    env.put("create", "true");
    try (FileSystem system = FileSystems.newFileSystem(uri, env)) {
      Path path = system.getPath("dev/simplix/core/minecraft/spigot/fake/FakeJavaPlugin.class");
      if (Files.exists(path)) {
        return;
      }
      Files.createDirectories(path.getParent());
      Files.write(
          path,
          ByteStreams.toByteArray(SimplixPlugin.class.getResourceAsStream("/FakeJavaPlugin.class")));
    } catch (Exception e) {
      throw new RuntimeException("Cannot inject fake class", e);
    }
  }

}