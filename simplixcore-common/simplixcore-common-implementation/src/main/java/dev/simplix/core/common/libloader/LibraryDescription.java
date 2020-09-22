package dev.simplix.core.common.libloader;

import lombok.Data;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
@ToString
@NonNull
public class LibraryDescription {

  private String name;
  private String version;
  private String[] authors;
  private String mainClass;

}
