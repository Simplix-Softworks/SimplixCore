package dev.simplix.core.common.deploader;

import lombok.NonNull;

/**
 * The dependency loader is used to download dependencies from remote repositories.
 */
public interface DependencyLoader {

  /**
   * Download a dependency from the given repositories.
   *
   * @param dependency   The dependency to download
   * @param repositories The repositories to search
   */
  boolean load(@NonNull Dependency dependency, @NonNull Iterable<Repository> repositories);

}
