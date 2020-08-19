package dev.simplix.core.common.deploader;

import lombok.Data;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@ToString
@Accessors(fluent = true)
public class Repository {

  private String id;
  private String url;

}
