package dev.simplix.core.common.libloader;

import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;
import java.util.Optional;
import lombok.ToString;

@ToString()
public class SimplixClassLoader extends URLClassLoader {

  private final ClassLoader parentLoader;

  public SimplixClassLoader(URL[] urls, ClassLoader parent) {
    super(urls);
    this.parentLoader = parent;
  }

  public SimplixClassLoader(URL[] urls) {
    super(urls);
    this.parentLoader = null;
  }

  /**
   * @param urls         the URLs from which to load classes and resources
   * @param parentLoader Parent classloader: Not passed on to the super constructor!
   * @param factory      the URLStreamHandlerFactory to use when creating URLs
   */
  public SimplixClassLoader(
      URL[] urls,
      ClassLoader parentLoader,
      URLStreamHandlerFactory factory) {
    super(urls, null, factory);
    this.parentLoader = parentLoader;
  }

  public Optional<ClassLoader> parent() {
    return Optional.ofNullable(parentLoader);
  }

  @Override
  public void addURL(URL url) {
    super.addURL(url);
  }

  @Override
  public Class<?> loadClass(String name) throws ClassNotFoundException {
    return loadClass(name, false);
  }

  @Override
  protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
    return loadClass(name, resolve, true);
  }

  public Class<?> loadClass(String name, boolean resolve, boolean useParent)
      throws ClassNotFoundException {
    try {
      return super.loadClass(name, resolve);
    } catch (Throwable ignored) {
      if (parentLoader != null && useParent) {
        try {
          return parentLoader.loadClass(name);
        } catch (Throwable ignored1) {
        }
      }
    }
    throw new ClassNotFoundException(name);
  }
}