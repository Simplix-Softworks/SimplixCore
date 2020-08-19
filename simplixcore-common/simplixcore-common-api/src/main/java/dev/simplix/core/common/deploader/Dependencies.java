package dev.simplix.core.common.deploader;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
public class Dependencies {

  private Repository[] repositories;
  private Dependency[] dependencies;

}
