package dev.simplix.core.common.utils;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class FileUtils {

  private FileUtils(){}

  public static FileContext context(File file) {
    return new FileContext(file);
  }

  public static class FileContext {

    private final File file;

    private FileContext(File file) {
      this.file = file;
    }

    public FileContext whenDirectory(Consumer<FileContext> fileConsumer) {
      if(file.isDirectory()) {
        fileConsumer.accept(this);
      }
      return this;
    }

    public FileContext whenFile(Consumer<FileContext> fileConsumer) {
      if(file.isFile()) {
        fileConsumer.accept(this);
      }
      return this;
    }

    public FileContext whenExists(Consumer<FileContext> fileConsumer) {
      if(file.exists()) {
        fileConsumer.accept(this);
      }
      return this;
    }

    public FileContext whenNotExists(Consumer<FileContext> fileConsumer) {
      if(!file.exists()) {
        fileConsumer.accept(this);
      }
      return this;
    }

    public FileContext createNewFile(boolean override) {
      Consumer<FileContext> consumer = fileContext -> {
        try {
          fileContext.file().createNewFile();
        } catch (IOException e) {
          throw new FileContextException(e);
        }
      };
      if(!override) {
        return whenNotExists(consumer);
      }
      consumer.accept(this);
      return this;
    }

    public FileContext subFiles(Consumer<FileContext> fileConsumer) {
      File[] files = file.listFiles();
      if(files == null) {
        return this;
      }
      for (File file : files) {
        fileConsumer.accept(new FileContext(file));
      }
      return this;
    }

    public FileContext makeParentDirectories() {
      File parentFile = file.getParentFile();
      if(parentFile != null) {
        parentFile.mkdirs();
      }
      return this;
    }

    public FileContext subFile(String relPath) {
      return new FileContext(new File(file, relPath));
    }

    public File file() {
      return file;
    }

  }

  public static class FileContextException extends RuntimeException {

    public FileContextException(IOException cause) {
      super(cause);
    }

  }

}
