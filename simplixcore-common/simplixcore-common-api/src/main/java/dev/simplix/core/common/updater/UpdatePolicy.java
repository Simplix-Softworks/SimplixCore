package dev.simplix.core.common.updater;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Builder
@Accessors(fluent = true)
public class UpdatePolicy {

  private String versionPattern;
  private UpdateDownloader updateDownloader;
  private VersionFetcher versionFetcher;

}
