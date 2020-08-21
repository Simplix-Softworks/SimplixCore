package dev.simplix.core.common.deploader;

import lombok.Data;
import lombok.NonNull;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
@NonNull
public class Dependencies {

  private Repository[] repositories;
  private Dependency[] dependencies;

}
