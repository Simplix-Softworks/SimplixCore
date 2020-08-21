package dev.simplix.core.common.libloader;

import java.io.File;
import java.util.Set;
import lombok.NonNull;

public interface LibraryLoader {

  void loadLibraries(@NonNull File directory);

  void loadLibrary(@NonNull File file);

  Set<File> loadedLibraries();

}
