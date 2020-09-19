package dev.simplix.core.common.deploader;

public class DependencyLoadingException extends RuntimeException {

  public DependencyLoadingException(Dependency dependency) {
    super("Unable to load dependency "+dependency.toString());
  }

}
