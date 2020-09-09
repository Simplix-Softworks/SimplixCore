package dev.simplix.core.common.updater;

import java.io.File;
import java.io.IOException;

public interface UpdateDownloader {

  void download(File target) throws IOException;

}
