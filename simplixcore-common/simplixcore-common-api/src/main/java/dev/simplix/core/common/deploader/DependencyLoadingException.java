package dev.simplix.core.common.deploader;

import lombok.Data;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

@Data
public class DependencyLoadingException extends RuntimeException {

  private final Dependency dependency;

  public DependencyLoadingException(@NonNull Dependency dependency, @NonNull final String message) {
    super(message);
    this.dependency = dependency;
  }

  public DependencyLoadingException(@NonNull Dependency dependency, @Nullable Throwable cause) {
    super("Unable to load dependency " + dependency.toString(), cause);
    this.dependency = dependency;
  }

  public DependencyLoadingException(@NonNull Dependency dependency) {
    super("Unable to load dependency " + dependency.toString());
    this.dependency = dependency;
  }
}
