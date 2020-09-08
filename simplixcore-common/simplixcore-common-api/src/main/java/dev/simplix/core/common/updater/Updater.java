package dev.simplix.core.common.updater;

import dev.simplix.core.common.ApplicationInfo;
import lombok.NonNull;

/**
 * The updater handles all update relevant tasks. Checking for updates, downloading and install them
 * during startup.
 */
public interface Updater {

  /**
   * This will install all previously downloaded updates
   */
  void installCachedUpdates();

  /**
   * This checks for available updates.
   *
   * @param applicationInfo The application to be checked for
   * @param updatePolicy    The defined update policy of this application
   */
  void checkForUpdates(
      @NonNull ApplicationInfo applicationInfo,
      @NonNull UpdatePolicy updatePolicy);

}
