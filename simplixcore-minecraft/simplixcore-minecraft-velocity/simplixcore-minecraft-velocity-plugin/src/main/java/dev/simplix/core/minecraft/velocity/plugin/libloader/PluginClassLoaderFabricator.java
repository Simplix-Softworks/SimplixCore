package dev.simplix.core.minecraft.velocity.plugin.libloader;

import static org.reflections.Reflections.log;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Set;
import java.util.function.Function;

public class PluginClassLoaderFabricator implements Function<File, ClassLoader> {

  @Override
  public ClassLoader apply(File file) {
    try {
      Class<?> classLoaderClass = Class.forName("com.velocitypowered.proxy.plugin.PluginClassloader",
          false, ClassLoader.getSystemClassLoader());
      Constructor<?> constructor = classLoaderClass.getDeclaredConstructor(URL[].class);

      Object loader = constructor.newInstance((Object) new URL[]{file.toURI().toURL()});

      Field loadersField = classLoaderClass.getDeclaredField("loaders");
      loadersField.setAccessible(true);
      Set<Object> loaders = (Set<Object>) loadersField.get(null);
      loaders.add(loader);
      return (ClassLoader) loader;
    } catch (Exception exception) {
      log.error("[Simplix | LibLoader] Cannot fabricate PluginClassLoader", exception);
    }
    return null;
  }

}
