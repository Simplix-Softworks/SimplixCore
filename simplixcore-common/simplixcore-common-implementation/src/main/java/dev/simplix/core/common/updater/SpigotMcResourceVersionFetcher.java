package dev.simplix.core.common.updater;

import dev.simplix.core.common.ApplicationInfo;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public final class SpigotMcResourceVersionFetcher implements VersionFetcher {

  private String projectId;

  @Override
  public Version fetchLatestVersion(ApplicationInfo applicationInfo, UpdatePolicy updatePolicy)
      throws IOException {
    URL url = new URL("https://api.spigotmc.org/legacy/update.php?resource="
                      + projectId);
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
