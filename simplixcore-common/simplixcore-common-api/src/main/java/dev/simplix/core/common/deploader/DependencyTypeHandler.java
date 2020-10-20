package dev.simplix.core.common.deploader;

import java.io.File;

public interface DependencyTypeHandler {

  void handle(Dependency dependency, File file);

  boolean shouldInstall(Dependency dependency);

}
