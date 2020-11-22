package dev.simplix.core.common.libloader;

import com.google.common.io.Files;
import dev.simplix.core.common.deploader.Dependency;
import dev.simplix.core.common.deploader.DependencyTypeHandler;
import dev.simplix.core.common.inject.SimplixInstaller;
import dev.simplix.core.common.utils.FileUtils;
import java.io.File;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LibraryTypeHandler implements DependencyTypeHandler {

  @Override
  public void handle(Dependency dependency, File file) {
    File target = FileUtils.context(new File("libraries"))
        .subFile(dependency.applicationName())
        .subFile(file.getName())
        .makeParentDirectories()
        .file();
    try {
      Files.copy(file, target);
      SimplixInstaller
          .instance()
          .libraryLoader()
          .loadLibraryEncapsulated(target, dependency.applicationClass());
    } catch (Exception e) {
      log.error("[SimplixCore | DependencyLoader] Cannot install " + dependency + " as library!");
    }
  }

  @Override
  public boolean shouldInstall(@NonNull Dependency dependency) {
    return !FileUtils.context(new File("libraries"))
        .whenNotExists(fileContext -> fileContext.file().mkdirs())
        .subFile(dependency.applicationName())
        .whenNotExists(fileContext -> fileContext.file().mkdirs())
        .subFiles(fc -> fc.whenFile(fileContext -> {
          File file = fileContext.file();
          if (file.getName().startsWith(dependency.artifactId() + "-")
              && !file.getName().equals(dependency.artifactId() + "-" + dependency.version())) {
            file.delete();
          }
        }))
        .subFile(dependency.artifactId() + "-" + dependency.version())
        .file().exists();
  }

}
