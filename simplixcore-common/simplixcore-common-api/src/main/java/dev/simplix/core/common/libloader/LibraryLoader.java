package dev.simplix.core.common.libloader;

import java.io.File;
import java.util.Set;

public interface LibraryLoader {

  void loadLibraries(File directory);

  void loadLibrary(File file);

  Set<File> loadedLibraries();

}
