package dev.simplix.core.common.providers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import lombok.NonNull;

public interface DependencyDownloader {

  PluginManager pluginManager();

  void download(@NonNull final URL url, @NonNull File targetFile) throws IOException;

  void downloadAndEnable(@NonNull final URL url, @NonNull File targetFile) throws IOException;
}
