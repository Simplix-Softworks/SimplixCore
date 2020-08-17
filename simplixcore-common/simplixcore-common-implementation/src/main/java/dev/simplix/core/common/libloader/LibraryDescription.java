package dev.simplix.core.common.libloader;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
@ToString
public class LibraryDescription {

  private String name;
  private String version;
  private String[] authors;
  private String mainClass;

}
