package dev.simplix.core.common.libloader;

import com.google.common.io.Files;
import dev.simplix.core.common.deploader.Dependency;
import java.io.File;
import java.io.IOException;
import java.util.function.BiConsumer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LibraryTypeHandler implements BiConsumer<Dependency, File> {

  private final LibraryLoader libraryLoader = new LibraryLoader();

  @Override
  public void accept(Dependency dependency, File file) {
    File target = new File("libraries", file.getName());
    try {
      Files.copy(file, target);
      libraryLoader.loadLibrary(target);
    } catch (Exception e) {
      log.error("[SimplixCore | DependencyLoader] Cannot install "+dependency+" as library!");
    }
  }

}
