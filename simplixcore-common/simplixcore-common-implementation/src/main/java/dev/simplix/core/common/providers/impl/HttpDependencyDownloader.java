package dev.simplix.core.common.providers.impl;

import com.google.common.io.ByteStreams;
import dev.simplix.core.common.CommonSimplixModule;
import dev.simplix.core.common.aop.Component;
import dev.simplix.core.common.providers.DependencyDownloader;
import dev.simplix.core.common.providers.PluginManager;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import javax.inject.Inject;
import lombok.NonNull;

@Component(value = CommonSimplixModule.class, parent = DependencyDownloader.class)
public final class HttpDependencyDownloader implements DependencyDownloader {

  private final PluginManager pluginManager;

  @Inject
  public HttpDependencyDownloader(@NonNull PluginManager pluginManager) {
    this.pluginManager = pluginManager;
  }

  @Override
  public PluginManager pluginManager() {
    return pluginManager;
  }

  @Override
  public void download(@NonNull URL url, @NonNull File file) throws IOException {
    URLConnection urlConnection = url.openConnection();
    if (!(urlConnection instanceof HttpURLConnection)) {
      throw new IllegalArgumentException("Unsupported protocol " + url.getProtocol());
    }

    HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
    if (file.exists()) {
      return;
    }
    file.createNewFile();
    try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
      fileOutputStream.write(ByteStreams.toByteArray(httpURLConnection.getInputStream()));
      fileOutputStream.flush();
    }
  }

  @Override
  public void downloadAndEnable(@NonNull URL url, @NonNull File file) throws IOException {
    download(url, file);
    pluginManager.enablePlugin(file);
  }
}
