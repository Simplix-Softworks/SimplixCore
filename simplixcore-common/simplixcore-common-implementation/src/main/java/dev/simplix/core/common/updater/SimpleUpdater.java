package dev.simplix.core.common.updater;

import dev.simplix.core.common.ApplicationInfo;
import dev.simplix.core.common.inject.SimplixInstaller;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
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
    if (!new File(".").getAbsolutePath().startsWith("/")) {
      // Not UNIX. Windows file locks make updating impossible.
      log.warn(SIMPLIX_UPDATER
               + " Running on Microsoft Windows. Automatic updating is not supported on this platform."
               + " Please be sure you are using an unix system.");
      return;
    }
    try {
      moveDir(cacheDirectory, new File("."));
    } catch (IOException exception) {
      log.warn(SIMPLIX_UPDATER + " Cannot install update: ", exception);
    }
  }

  private void moveDir(File file, File target) throws IOException {
    for (File f : file.listFiles()) {
      File t = new File(target, f.getName());
      if (f.isDirectory()) {
        moveDir(f, t);
      } else {
        f.getParentFile().mkdirs();
        Files.move(
            f.toPath(),
            t.toPath(),
            StandardCopyOption.REPLACE_EXISTING);
        log.debug(SIMPLIX_UPDATER + " Moved " + f.getAbsolutePath() + " to " + t.getAbsolutePath());
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
          File target = new File(
              cacheDirectory,
              new File(".").toURI().relativize(toReplace.toURI()).getPath());
          if (target.exists()) {
            return;
          }
          log.info(SIMPLIX_UPDATER + " "
                   + applicationInfo.name()
                   + ": A new version is available: "
                   + latest
                   + " (currently installed: " + currentVersion + ")");
          if (!new File(".").getAbsolutePath().startsWith("/")) {
            // Not UNIX. Windows file locks make updating impossible.
            log.warn(SIMPLIX_UPDATER
                     + " Running on Microsoft Windows. Automatic updating is not supported on this platform."
                     + " Please be sure you are using an unix system. Please update "
                     + applicationInfo.name()
                     + " manually.");
            return;
          }
          if (updatePolicy.updateDownloader() != null) {
            try {
              updatePolicy
                  .updateDownloader()
                  .download(target, latest);
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
          log.debug(SIMPLIX_UPDATER + " " + applicationInfo.name() + " is up-to-date.");
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
