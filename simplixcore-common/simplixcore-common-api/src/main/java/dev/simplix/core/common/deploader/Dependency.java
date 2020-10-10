package dev.simplix.core.common.deploader;

import dev.simplix.core.common.platform.Platform;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;

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
  @Nullable
  private Platform platform;

  @Nullable
  public Platform platform() {
    return this.platform;
  }

  @Override
  public String toString() {
    return this.groupId + ':' + this.artifactId + ':' + this.version;
  }
}



