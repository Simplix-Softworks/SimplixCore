package dev.simplix.core.common;

import java.io.File;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.Accessors;

@NonNull
@Accessors(fluent = true)
@Builder
@Data
public final class ApplicationInfo {

  private final String name;
  @Builder.Default()
  private final String version;
  private final String[] authors;
  @Builder.Default()
  private File workingDirectory = new File(".");
  @Builder.Default()
  private String[] dependencies = new String[0];

  public ApplicationInfo(
      @NonNull String name,
      @NonNull String version,
      @NonNull String[] authors,
      @NonNull File workingDirectory,
      @NonNull String[] dependencies) {
    this.name = name;
    this.version = version;
    this.authors = authors;
    this.workingDirectory = workingDirectory;
    this.dependencies = dependencies;
  }

}