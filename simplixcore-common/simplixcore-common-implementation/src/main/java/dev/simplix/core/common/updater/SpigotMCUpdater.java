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
public final class SpigotMCUpdater implements Updater {

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
        Version latest = latestVersion(updatePolicy);
        if (latest.newerThen(currentVersion)) {
          log.info("[Simplix | Updater] "
                   + applicationInfo.name()
                   + ": A new version is available: "
                   + latest
                   + " (currently installed: " + currentVersion + ")");
          log.info("[Simplix | Updater] "
                   + applicationInfo.name()
                   + ": Download here: https://www.spigotmc.org/resources/"
                   + updatePolicy.projectId()
                   + "/");
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

  private Version latestVersion(UpdatePolicy updatePolicy) throws IOException {
    URL url = new URL("https://api.spigotmc.org/legacy/update.php?resource="
                      + updatePolicy.projectId());
    String latestVersion;
    try (
        BufferedReader r = new BufferedReader(new InputStreamReader(url
            .openConnection()
            .getInputStream()))) {
      latestVersion = r.readLine();
    }
    Version version;
    if (updatePolicy.versionPattern() != null) {
      version = Version.parse(updatePolicy.versionPattern(), latestVersion);
    } else {
      version = Version.parse(latestVersion);
    }
    return version;
  }

}
