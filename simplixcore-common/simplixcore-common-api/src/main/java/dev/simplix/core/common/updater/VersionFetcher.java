package dev.simplix.core.common.updater;

import dev.simplix.core.common.ApplicationInfo;
import java.io.IOException;
import lombok.NonNull;

public interface VersionFetcher {

  Version fetchLatestVersion(
      @NonNull ApplicationInfo applicationInfo,
      @NonNull UpdatePolicy updatePolicy)
      throws IOException;

}
