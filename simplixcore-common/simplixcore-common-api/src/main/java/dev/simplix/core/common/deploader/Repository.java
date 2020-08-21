package dev.simplix.core.common.deploader;

import lombok.Data;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@ToString
@Accessors(fluent = true)
@NonNull
public class Repository {

  private String id;
  private String url;

}
