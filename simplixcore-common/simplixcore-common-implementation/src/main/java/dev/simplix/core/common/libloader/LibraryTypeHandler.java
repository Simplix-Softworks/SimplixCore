package dev.simplix.core.common.libloader;

import com.google.common.io.Files;
import dev.simplix.core.common.deploader.Dependency;
import java.io.File;
import java.util.function.BiConsumer;

import dev.simplix.core.common.inject.SimplixInstaller;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LibraryTypeHandler implements BiConsumer<Dependency, File> {

  @Override
  public void accept(Dependency dependency, File file) {
    File target = new File("libraries", file.getName());
    try {
      Files.copy(file, target);
      SimplixInstaller.instance().libraryLoader().loadLibrary(target);
    } catch (Exception e) {
      log.error("[SimplixCore | DependencyLoader] Cannot install "+dependency+" as library!");
    }
  }

}
