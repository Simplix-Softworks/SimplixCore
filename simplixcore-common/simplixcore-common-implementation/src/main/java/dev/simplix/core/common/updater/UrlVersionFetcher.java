package dev.simplix.core.common.updater;

import dev.simplix.core.common.ApplicationInfo;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

@AllArgsConstructor
@NoArgsConstructor
public final class UrlVersionFetcher implements VersionFetcher {

  private String url;

  @Override
  public Version fetchLatestVersion(ApplicationInfo applicationInfo, UpdatePolicy updatePolicy)
      throws IOException {
    URL url = new URL(this.url.replace("{name}", applicationInfo.name()));
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
