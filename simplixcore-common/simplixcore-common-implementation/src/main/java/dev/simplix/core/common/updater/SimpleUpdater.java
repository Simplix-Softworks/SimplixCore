package dev.simplix.core.common.updater;

import dev.simplix.core.common.ApplicationInfo;
import dev.simplix.core.common.inject.SimplixInstaller;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class SimpleUpdater implements Updater {

  private final File cacheDirectory = new File(".updateCache");

  {
    cacheDirectory.mkdir();
  }

  @Override
  public void installCachedUpdates() {
    for (File file : cacheDirectory.listFiles()) {
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
      } else {
        log.info("[Simplix | Updater] Installed update for " + file.getName());
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
        if (latest.newerThen(currentVersion)) {
          log.info("[Simplix | Updater] "
                   + applicationInfo.name()
                   + ": A new version is available: "
                   + latest
                   + " (currently installed: " + currentVersion + ")");
          if (updatePolicy.updateDownloader() != null) {
            try {
              updatePolicy
                  .updateDownloader()
                  .download(new File(cacheDirectory, toReplace.getName() + ".update"), latest);
              log.info("[Simplix | Updater] "
                       + applicationInfo.name()
                       + ": Update downloaded. To install the update you have to restart your server.");
            } catch (Exception exception) {
              log.warn(
                  "[Simplix | Updater] " + applicationInfo.name() + ": Cannot download update",
                  exception);
            }
          }
        }
      } catch (Exception exception) {
        log.warn(
            "[Simplix | Updater] " + applicationInfo.name() + ": Could not check for updates",
            exception);
      }
    } catch (URISyntaxException exception) {
      // will never happen
    }
  }

}
