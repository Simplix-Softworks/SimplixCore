package dev.simplix.core.common.deploader;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode
@Accessors(fluent = true)
public class Dependency {

  private String groupId;
  private String artifactId;
  private String version;
  private String type = "library";

  @Override
  public String toString() {
    return groupId + ':' + artifactId + ':' + version;
  }
}
