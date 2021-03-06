package dev.simplix.core.common.updater;

import dev.simplix.core.common.ApplicationInfo;
import java.io.IOException;
import lombok.NonNull;

public final class SpigotMcResourceVersionFetcher implements VersionFetcher {

  private String resourceId;

  @Override
  public Version fetchLatestVersion(
      @NonNull ApplicationInfo applicationInfo,
      @NonNull UpdatePolicy updatePolicy)
      throws IOException {
    return new UrlVersionFetcher("https://api.spigotmc.org/legacy/update.php?resource="
                                 + resourceId)
        .fetchLatestVersion(applicationInfo, updatePolicy);
  }

}
