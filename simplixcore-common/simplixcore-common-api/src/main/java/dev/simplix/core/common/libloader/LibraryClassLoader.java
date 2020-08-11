package dev.simplix.core.common.libloader;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import lombok.NonNull;

final class LibraryClassLoader extends URLClassLoader {

  private final static Set<LibraryClassLoader> LOADERS = new CopyOnWriteArraySet<>();

  static {
    ClassLoader.registerAsParallelCapable();
  }

  LibraryClassLoader(@NonNull URL... urls) {
    super(urls);
  }

  @Override
  protected Class<?> loadClass(
      @NonNull String name,
      boolean resolve) throws ClassNotFoundException {
    return loadClass(name, resolve, true);
  }

  private Class<?> loadClass(
      @NonNull String name,
      boolean resolve,
      boolean checkOther)
      throws ClassNotFoundException {
    ClassNotFoundException exception;
    try {
      return super.loadClass(name, resolve);
    } catch (ClassNotFoundException classNotFoundException) {
      exception = classNotFoundException;
    }
    if (!checkOther) {
      throw exception;
    }

    for (LibraryClassLoader loader : LOADERS) {
      if (loader == this) {
        continue;
      }
      try {
        return loader.loadClass(name, resolve, false);
        // Expected
      } catch (ClassNotFoundException ignored) {
      }
    }
    throw exception;
  }

}
