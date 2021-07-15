package dev.simplix.core.minecraft.spigot.plugin.libloader;

import com.google.common.io.ByteStreams;
import dev.simplix.core.common.libloader.SimplixClassLoader;
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

  private static boolean hasClassLoaderAccess = false;
  private static ClassLoader cachedResult;

  static {
    try {
      Method addMethod = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
      addMethod.setAccessible(true);
      hasClassLoaderAccess = true;
    } catch (Throwable ignored) {
    }
  }

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

        // Check whether the fabricated classloader will actually work
        if (!hasClassLoaderAccess) {
          log.error("SimplixCore can not fabricate ClassLoaders");
          log.error("Your minecraft version is EOL & SimplixCore can't access java.base/java.net");
          log.error("-> New api is not (yet) present and legacy api is blocked");
          log.error("1) Upgrade to Minecraft 1.17 (Recommended)");
          log.error("2) Add: --add-opens java.base/java.net=ALL-UNNAMED as jvm flag");
          log.error(
              "- Example java command: java --add-opens java.base/java.net=ALL-UNNAMED -Xmx2048M -Xms2048M -jar paper-1.12.2.jar");
          log.error("3) Downgrade to Java11 ");
          throw new IllegalStateException(
              "Can not fabricate ClassLoader with outdated Minecraft and Java16");
        }

      } catch (Throwable throwable) {
        // 1.16.5
        if (hasClassLoaderAccess && !Bukkit.getBukkitVersion().startsWith("1.17")) {
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


        } else if (!hasClassLoaderAccess && Bukkit.getBukkitVersion().startsWith("1.16")) {
          log.error("SimplixCore can not fabricate ClassLoaders");
          log.error(
              "Your minecraft version contains a bug & SimplixCore can't access java.base/java.net");
          log.error("Please see: https://hub.spigotmc.org/jira/browse/SPIGOT-6502");
          log.error("To resolve this you can either:");
          log.error("1) Upgrade to Minecraft 1.17 (Recommended)");
          log.error("2) Add: --add-opens java.base/java.net=ALL-UNNAMED as jvm flag");
          log.error(
              "- Example java command: java --add-opens java.base/java.net=ALL-UNNAMED -Xmx2048M -Xms2048M -jar paper-1.16.5.jar");
          log.error("3) Downgrade to Java11");
          throw new IllegalStateException(
              "Can not fabricate ClassLoader with outdated Minecraft and Java16");
        } else {
          // Spigot 1.17 newer - Compatible with Java16
          log.info("Used Java16 fabricator");
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

          final ClassLoader simplixCoreClassLoader = plugin
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
                    false,
                    false);
                return (Class<?>) invoke;
              } catch (IllegalAccessException reflectiveOperationException) {
                throw new ClassNotFoundException(name, reflectiveOperationException);
              } catch (InvocationTargetException invocationTargetException) {
                if (invocationTargetException.getCause() instanceof ClassNotFoundException) {
                  throw (ClassNotFoundException) invocationTargetException.getCause();
                }
                throw new ClassNotFoundException(name, invocationTargetException);
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