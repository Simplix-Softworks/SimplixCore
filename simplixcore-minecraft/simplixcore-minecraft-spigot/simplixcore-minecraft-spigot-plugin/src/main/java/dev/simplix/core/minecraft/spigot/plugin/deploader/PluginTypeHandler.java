package dev.simplix.core.minecraft.spigot.plugin.deploader;

import com.google.common.io.Files;
import dev.simplix.core.common.deploader.Dependency;
import dev.simplix.core.common.deploader.DependencyTypeHandler;
import dev.simplix.core.common.utils.FileUtils;
import java.io.File;
import java.util.concurrent.atomic.AtomicReference;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.Bukkit;

@Slf4j
public class PluginTypeHandler implements DependencyTypeHandler {

  @Override
  public void handle(Dependency dependency, File file) {
    File target = new File("plugins", file.getName());
    try {
      Files.copy(file, target);
      Bukkit.getPluginManager().loadPlugin(target);
    } catch (Exception exception) {
      log.error("[Simplix | DependencyLoader] Unable to load plugin " + file.getName(), exception);
    }
  }

  @Override
  public boolean shouldInstall(Dependency dependency) {
    AtomicReference<File> atomicReference = new AtomicReference<>();
    FileUtils
        .context(new File("plugins"))
        .whenExists(fileContext -> fileContext.subFiles(fc -> fc.whenFile(subFile -> {
          File file = subFile.file();
          if (file.getName().startsWith(dependency.artifactId())
              && !file.getName().equals(dependency.artifactId() + "-" + dependency.version())) {
            atomicReference.set(file);
          }
        })));
    if (atomicReference.get() != null) {
      log.warn("[Simplix | DependencyLoader] "
               + dependency.applicationName()
               + ": Version conflict of plugin "
               + dependency);
      log.warn("[Simplix | DependencyLoader] "
               + dependency.applicationName()
               + ": This file seems to contain another version of this dependency: "
               + atomicReference.get().getAbsolutePath());
      log.warn("[Simplix | DependencyLoader] "
               + dependency.applicationName()
               + ": SimplixCore will not load multiple versions of the same dependency. "
               + "Please resolve this issue.");
      return false;
    }
    return true;
  }

}
