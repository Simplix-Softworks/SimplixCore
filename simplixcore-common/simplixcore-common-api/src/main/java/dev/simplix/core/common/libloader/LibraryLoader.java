package dev.simplix.core.common.libloader;

import java.io.File;
import java.util.Set;
import lombok.NonNull;

public interface LibraryLoader {

  /**
   * Loads all .jar libraries in an given directory
   *
   * @param directory
   */
  void loadLibraries(@NonNull File directory);

  void loadLibrary(@NonNull File file);

  void loadLibraryEncapsulated(@NonNull File file, @NonNull Class<?> owner);

  /**
   * Returns which jar files have been loaded as library by the given application
   */
  Set<File> loadedLibraries();

}
