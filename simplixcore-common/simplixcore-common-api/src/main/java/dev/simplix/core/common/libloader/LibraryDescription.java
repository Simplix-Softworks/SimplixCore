package dev.simplix.core.common.libloader;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
public class LibraryDescription {

  private String name;
  private String version;
  private String[] authors;
  private String mainClass;

}
