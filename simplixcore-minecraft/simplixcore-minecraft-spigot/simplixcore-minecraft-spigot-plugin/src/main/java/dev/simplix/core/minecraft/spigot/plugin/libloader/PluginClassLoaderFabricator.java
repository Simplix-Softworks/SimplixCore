package dev.simplix.core.minecraft.spigot.plugin.libloader;

import com.google.common.io.ByteStreams;
import dev.simplix.core.minecraft.spigot.plugin.SimplixPlugin;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URI;
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
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

@Slf4j
public final class PluginClassLoaderFabricator implements Function<File, ClassLoader> {

  private void unfinalize(@NonNull Field loadersField)
      throws NoSuchFieldException, IllegalAccessException {
    Field modifiersField = Field.class.getDeclaredField("modifiers");
    modifiersField.setAccessible(true);
    modifiersField.set(loadersField, loadersField.getModifiers() & ~Modifier.FINAL);
  }

  @Override
  public ClassLoader apply(@NonNull File file) {
    try {
      injectFakeClass(file);

      Class<?> classLoaderClass = Class.forName("org.bukkit.plugin.java.PluginClassLoader");
      Constructor<?> constructor = classLoaderClass.getDeclaredConstructor(
          JavaPluginLoader.class,
          ClassLoader.class,
          PluginDescriptionFile.class,
          File.class,
          File.class);
      constructor.setAccessible(true);

      SimplixPlugin plugin = JavaPlugin.getPlugin(SimplixPlugin.class);
      PluginDescriptionFile pluginDescriptionFile = new PluginDescriptionFile(
          SimplixPlugin.class.getResourceAsStream("/fakeplugin.yml"));
      Object loader = constructor.newInstance(
          plugin.getPluginLoader(),
          plugin.getClass().getClassLoader(),
          pluginDescriptionFile,
          plugin.getDataFolder(),
          file);

      Field loadersField = JavaPluginLoader.class.getDeclaredField("loaders");
      loadersField.setAccessible(true);
      if (Modifier.isFinal(loadersField.getModifiers())) {
        unfinalize(loadersField);
      }
      if (Map.class.isAssignableFrom(loadersField.getType())) { // Spigot 1.8 - Spigot 1.10.2
        Map<String, Object> loaders = (Map<String, Object>) loadersField.get(plugin.getPluginLoader());
        loaders.put("SimplixBridge", loader);
        loadersField.set(plugin.getPluginLoader(), loaders);
      } else {
        List<Object> loaders = (List<Object>) loadersField.get(plugin.getPluginLoader());
        loaders.add(loader);
        loadersField.set(plugin.getPluginLoader(), loaders);
      }
      return (ClassLoader) loader;
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
