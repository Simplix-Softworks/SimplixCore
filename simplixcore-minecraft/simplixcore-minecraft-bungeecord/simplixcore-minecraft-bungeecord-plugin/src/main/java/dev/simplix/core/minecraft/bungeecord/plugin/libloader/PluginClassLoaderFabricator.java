package dev.simplix.core.minecraft.bungeecord.plugin.libloader;

import dev.simplix.core.common.libloader.SimplixClassLoader;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.PluginDescription;

@Slf4j
public class PluginClassLoaderFabricator implements Function<File, ClassLoader> {

  private static ClassLoader result = null;
  private static List<String> WHITELIST = Arrays.asList(
      "de.leonhard",
      "de.exceptionflug",
      "dev.simplix",
      "net.querz"
  );

//  private void unfinalize(@NonNull Field loadersField)
//      throws NoSuchFieldException, IllegalAccessException {
//    Field modifiersField = Field.class.getDeclaredField("modifiers");
//    modifiersField.setAccessible(true);
//    modifiersField.set(loadersField, loadersField.getModifiers() & ~Modifier.FINAL);
//  }

  @Override
  public ClassLoader apply(@NonNull File file) {

    if (result != null) {
      return result;
    }

    try {
      Class<?> classLoaderClass = Class.forName("net.md_5.bungee.api.plugin.PluginClassloader",
          false, ClassLoader.getSystemClassLoader());
      Constructor<?> constructor = classLoaderClass.getDeclaredConstructor(
          ProxyServer.class,
          PluginDescription.class,
          File.class,
          ClassLoader.class
      );
      constructor.setAccessible(true);

      PluginDescription pluginDescription = new PluginDescription();
      pluginDescription.setName("SimplixBridge");

      final ClassLoader simplixPluginClassLoader = ProxyServer
          .getInstance()
          .getPluginManager()
          .getPlugin("SimplixCore")
          .getClass()
          .getClassLoader();

      final Method loadClass0 = simplixPluginClassLoader
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

          boolean checkOthers = false;

          for (String whitelisted : WHITELIST) {
            if (name.startsWith(whitelisted)) {
              checkOthers = true;
              break;
            }
          }

          try {
            return (Class<?>) loadClass0.invoke(simplixPluginClassLoader, name, false, checkOthers, false);
          } catch (IllegalAccessException | InvocationTargetException reflectiveOperationException) {
            throw new ClassNotFoundException(name);
          }
        }
      };

      final SimplixClassLoader simplixClassLoader = new SimplixClassLoader(
          new URL[0],
          parentLoader);
      Object loader = constructor.newInstance(
          ProxyServer.getInstance(),
          pluginDescription,
          file,
          simplixClassLoader
      );

      Field loadersField = classLoaderClass.getDeclaredField("allLoaders");
      loadersField.setAccessible(true);

      Set<Object> loaders = (Set<Object>) loadersField.get(null);
      loaders.add(loader);
//      loadersField.set(null, loaders);

//      ProxyServer.getInstance().getPluginManager().
      result = simplixClassLoader;
      return simplixClassLoader;
    } catch (Exception exception) {
      log.error("[Simplix | LibLoader] Cannot fabricate PluginClassLoader", exception);
    }
    return null;
  }

}
