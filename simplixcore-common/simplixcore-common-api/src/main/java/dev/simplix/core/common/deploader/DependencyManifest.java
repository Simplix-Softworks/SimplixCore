package dev.simplix.core.common.deploader;

import lombok.Data;
import lombok.NonNull;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
@NonNull
public class DependencyManifest {

  private Repository[] repositories;
  private Dependency[] dependencies;

}
