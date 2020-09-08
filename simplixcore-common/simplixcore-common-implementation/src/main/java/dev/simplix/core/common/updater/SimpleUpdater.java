package dev.simplix.core.common.updater;

import dev.simplix.core.common.ApplicationInfo;
import dev.simplix.core.common.inject.SimplixInstaller;
import java.io.File;
import java.net.URISyntaxException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class SimpleUpdater implements Updater {

  private final File cacheDirectory = new File(".updateCache");

  {
    this.cacheDirectory.mkdir();
  }

  @Override
  public void installCachedUpdates() {
    for (File file : this.cacheDirectory.listFiles()) {
      if (file.isDirectory()) {
        installUpdates(file, file.getName());
      }
    }
  }

  private void installUpdates(File directory, String path) {
    for (File file : directory.listFiles()) {
      if (file.isDirectory()) {
        continue;
      }
      if (!file.renameTo(new File(
          path,
          file.getName().substring(0, file.getName().length() - 7)))) {
        log.warn("[Simplix | Updater] Cannot update " + file.getName());
      }
    }
  }

  @Override
  public void checkForUpdates(ApplicationInfo applicationInfo, UpdatePolicy updatePolicy) {
    Class<?> appClass = SimplixInstaller.instance().applicationClass(applicationInfo.name());
    try {
      File toReplace = new File(appClass
          .getProtectionDomain()
          .getCodeSource()
          .getLocation()
          .toURI());
    } catch (URISyntaxException e) {
      // will never happen
    }

  }

}
