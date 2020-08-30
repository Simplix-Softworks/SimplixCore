package dev.simplix.core.common.deploader;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode
@Accessors(fluent = true)
@NonNull
public class Dependency {

  private String applicationName;
  private Class<?> applicationClass;

  private String groupId;
  private String artifactId;
  private String version;
  private String type = "library";

  @Override
  public String toString() {
    return this.groupId + ':' + this.artifactId + ':' + this.version;
  }
}
