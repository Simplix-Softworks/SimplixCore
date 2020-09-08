package dev.simplix.core.common.updater;

import dev.simplix.core.common.deploader.Dependency;
import dev.simplix.core.common.deploader.Repository;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Builder
@Accessors(fluent = true)
public class UpdatePolicy {

  private String projectId;
  private String versionPattern;

}
