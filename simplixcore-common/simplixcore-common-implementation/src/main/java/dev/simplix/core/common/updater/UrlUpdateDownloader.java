package dev.simplix.core.common.updater;

import com.google.common.io.ByteStreams;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import lombok.NonNull;

public class UrlUpdateDownloader implements UpdateDownloader {

  private String url;

  @Override
  public void download(@NonNull File target,@NonNull  Version latest) throws IOException {
    URL url = new URL(this.url.replace("{latest}", latest.toString()));

    URLConnection urlConnection = url.openConnection();
    try (BufferedInputStream bufferedInputStream = new BufferedInputStream(urlConnection.getInputStream())) {
      try (FileOutputStream fileOutputStream = new FileOutputStream(target)) {
        fileOutputStream.write(ByteStreams.toByteArray(bufferedInputStream));
        fileOutputStream.flush();
      }
    }
  }

}
