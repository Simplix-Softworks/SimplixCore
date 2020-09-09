package dev.simplix.core.common.updater;

import dev.simplix.core.common.ApplicationInfo;
import java.io.IOException;
import java.net.MalformedURLException;

public interface VersionFetcher {

  Version fetchLatestVersion(ApplicationInfo applicationInfo, UpdatePolicy updatePolicy)
      throws IOException;

}
