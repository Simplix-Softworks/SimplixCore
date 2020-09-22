package dev.simplix.core.common.updater;

import java.io.File;
import java.io.IOException;
import lombok.NonNull;

public interface UpdateDownloader {

  void download(@NonNull File target, @NonNull Version latest) throws IOException;

}
