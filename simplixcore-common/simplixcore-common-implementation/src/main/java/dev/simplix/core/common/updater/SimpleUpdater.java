package dev.simplix.core.common.updater;

import dev.simplix.core.common.ApplicationInfo;
import dev.simplix.core.common.inject.SimplixInstaller;
import java.io.File;
import java.net.URISyntaxException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class SimpleUpdater implements Updater {

  private static final String SIMPLIX_UPDATER = "[Simplix | Updater]";
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

  private void installUpdates(@NonNull File directory, @NonNull String path) {
    for (File file : directory.listFiles()) {
      if (file.isDirectory()) {
        continue;
      }
      if (!file.renameTo(new File(
          path,
          file.getName().substring(0, file.getName().length() - 7)))) {
        log.warn(SIMPLIX_UPDATER + " Cannot update " + file.getName());
      } else {
        log.info(SIMPLIX_UPDATER + " Installed update for " + file.getName());
      }
    }
  }

  @Override
  public void checkForUpdates(
      @NonNull ApplicationInfo applicationInfo,
      @NonNull UpdatePolicy updatePolicy) {
    Class<?> appClass = SimplixInstaller.instance().applicationClass(applicationInfo.name());
    try {
      File toReplace = new File(appClass
          .getProtectionDomain()
          .getCodeSource()
          .getLocation()
          .toURI());
      Version currentVersion;
      if (updatePolicy.versionPattern() != null) {
        currentVersion = Version.parse(updatePolicy.versionPattern(), applicationInfo.version());
      } else {
        currentVersion = Version.parse(applicationInfo.version());
      }

      try {
        Version latest = updatePolicy
            .versionFetcher()
            .fetchLatestVersion(applicationInfo, updatePolicy);
        log.debug(SIMPLIX_UPDATER + " "
                  + applicationInfo.name()
                  + ": Latest version is "
                  + latest
                  + " and installed version is "
                  + applicationInfo.version());
        if (latest.newerThen(currentVersion)) {
          log.info(SIMPLIX_UPDATER + " "
                   + applicationInfo.name()
                   + ": A new version is available: "
                   + latest
                   + " (currently installed: " + currentVersion + ")");
          if (updatePolicy.updateDownloader() != null) {
            try {
              updatePolicy
                  .updateDownloader()
                  .download(new File(this.cacheDirectory, toReplace.getName() + ".update"), latest);
              log.info(SIMPLIX_UPDATER + " "
                       + applicationInfo.name()
                       + ": Update downloaded. To install the update you have to restart your server.");
            } catch (Exception exception) {
              log.warn(
                  SIMPLIX_UPDATER + " " + applicationInfo.name() + ": Cannot download update",
                  exception);
            }
          } else {
            log.info(SIMPLIX_UPDATER + " You need to install this update manually.");
          }
        } else {
          log.debug(SIMPLIX_UPDATER+" "+applicationInfo.name()+" is up-to-date.");
        }
      } catch (Exception exception) {
        log.warn(
            SIMPLIX_UPDATER + " " + applicationInfo.name() + ": Could not check for updates",
            exception);
      }
    } catch (URISyntaxException exception) {
      // will never happen
    }
  }

}
