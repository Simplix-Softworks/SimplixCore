package dev.simplix.core.common.libloader;

import com.google.common.io.Files;
import dev.simplix.core.common.deploader.Dependency;
import dev.simplix.core.common.deploader.DependencyTypeHandler;
import dev.simplix.core.common.inject.SimplixInstaller;
import dev.simplix.core.common.utils.FileUtils;
import dev.simplix.core.common.utils.FileUtils.FileContext;
import java.io.File;
import java.util.concurrent.atomic.AtomicReference;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SharedLibraryTypeHandler implements DependencyTypeHandler {

  private static final FileContext LIBRARIES_CONTEXT = FileUtils.context(new File("libraries"));

  @Override
  public void handle(@NonNull Dependency dependency, @NonNull File file) {
    File target = LIBRARIES_CONTEXT
        .subFile(file.getName())
        .makeParentDirectories()
        .file();
    try {
      Files.copy(file, target);
      SimplixInstaller
          .instance()
          .libraryLoader()
          .loadLibrary(target);
    } catch (Exception e) {
      log.error("[SimplixCore | DependencyLoader] Cannot install "
                + dependency
                + " as shared library!");
    }
  }

  @Override
  public boolean shouldInstall(@NonNull Dependency dependency) {
    AtomicReference<File> atomicReference = new AtomicReference<>();
    LIBRARIES_CONTEXT.whenExists(fileContext -> fileContext.subFiles(subFile -> {
      File file = fileContext.file();
      if (file.getName().startsWith(dependency.artifactId() + "-")
          && !file.getName().equals(dependency.artifactId() + "-" + dependency.version())) {
        atomicReference.set(file);
      }
    }));
    if (atomicReference.get() != null) {
      log.warn("[Simplix | DependencyLoader] "
               + dependency.applicationName()
               + ": Version conflict of shared library "
               + dependency.toString());
      log.warn("[Simplix | DependencyLoader] "
               + dependency.applicationName()
               + ": This file seems to contain another version of this dependency: "
               + atomicReference.get().getAbsolutePath());
      log.warn("[Simplix | DependencyLoader] "
               + dependency.applicationName()
               + ": SimplixCore will not load multiple versions of the same dependency. "
               + "Please resolve this issue or consider using encapsulated libraries instead.");
      return false;
    }
    return true;
  }

}
