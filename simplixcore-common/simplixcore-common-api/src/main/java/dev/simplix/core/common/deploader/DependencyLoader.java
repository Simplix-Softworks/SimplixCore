package dev.simplix.core.common.deploader;

import java.util.Optional;
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
  Optional<DependencyLoadingException> load(
      @NonNull Dependency dependency,
      @NonNull Iterable<Repository> repositories);

}
