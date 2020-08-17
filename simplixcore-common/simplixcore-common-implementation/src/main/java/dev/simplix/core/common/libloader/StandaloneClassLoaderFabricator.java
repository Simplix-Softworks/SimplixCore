package dev.simplix.core.common.libloader;

import java.io.File;
import java.net.MalformedURLException;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class StandaloneClassLoaderFabricator implements Function<File, ClassLoader> {

  @Override
  public ClassLoader apply(File file) {
    try {
      return new LibraryClassLoader(file, file.toURI().toURL());
    } catch (MalformedURLException e) {
      log.error("[Simplix | LibLoader] Cannot fabricate ClassLoader", e);
    }
    return null;
  }

}
