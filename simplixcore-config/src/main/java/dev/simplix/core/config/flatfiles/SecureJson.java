package dev.simplix.core.config.flatfiles;

import de.leonhard.storage.Json;
import de.leonhard.storage.internal.exceptions.LightningValidationException;
import de.leonhard.storage.internal.settings.ReloadSettings;
import de.leonhard.storage.shaded.jetbrains.annotations.Nullable;
import de.leonhard.storage.util.FileUtils;
import dev.simplix.core.common.providers.ExceptionHandler;
import java.io.File;
import java.io.InputStream;
import lombok.NonNull;

/**
 * A class designed to make it as easy as possible to handle exceptions while handling JSON-Files
 */
public abstract class SecureJson extends Json {

  private final boolean canWrite;

  protected SecureJson(@NonNull final Json json) {
    super(json);
    this.canWrite = this.file.canWrite();
  }

  protected SecureJson(@NonNull final String name, @NonNull final String path) {
    super(name, path);
    this.canWrite = this.file.canWrite();
  }

  protected SecureJson(
      @NonNull final String name,
      @NonNull final String path,
      @Nullable final InputStream inputStream) {
    super(name, path, inputStream);
    this.canWrite = this.file.canWrite();
  }

  protected SecureJson(
      @NonNull final String name,
      @Nullable final String path,
      @Nullable final InputStream inputStream,
      @Nullable final ReloadSettings reloadSettings) {
    super(name, path, inputStream, reloadSettings);
    this.canWrite = this.file.canWrite();
  }

  protected SecureJson(@NonNull final File file) {
    super(file);
    this.canWrite = file.canWrite();
  }

  //Used because not all of our subclasses may access Dependency-Injection
  abstract ExceptionHandler exceptionHandler();

  // ----------------------------------------------------------------------------------------------------
  // Overridden methods for best usability & security
  // ----------------------------------------------------------------------------------------------------

  @Override
  public final <T> T getOrDefault(@NonNull final String key, @NonNull final T def) {
    try {
      return super.getOrDefault(key, def);
    } catch (final Throwable throwable) {
      final LightningValidationException lightningValidationException = new LightningValidationException(
          throwable,
          "Exception while getting '" + def.getClass().getSimpleName()
          + "' from '" + this.file.getName() + "'",
          "Directory: '" + FileUtils.getParentDirPath(this.file) + "'",
          "Have you altered the data?",
          "Additional data: searched: " + def.getClass().getSimpleName()
      );
      exceptionHandler().saveError(lightningValidationException);
      throw lightningValidationException;
    }
  }

  @Override
  public final <T> T getOrSetDefault(@NonNull final String key, @NonNull final T def) {
    try {
      return super.getOrSetDefault(key, def);
    } catch (final Throwable throwable) {
      exceptionHandler().saveError(
          throwable,
          "Exception while trying searching: '" + key + "' in '" + this.file
              .getName() + "'",
          "Have you altered the data?",
          "Additional data: searched: " + def.getClass().getSimpleName());
    }
    return def;
  }

  @Override
  public final void set(@NonNull final String key, @NonNull final Object value) {
    try {
      super.set(key, value);
    } catch (final Throwable throwable) {
      exceptionHandler().saveError(
          throwable,
          "Exception while writing to '" + this.file.getName() + "'",
          "Directory: '" + FileUtils.getParentDirPath(this.file) + "'",
          "Path: '" + key + "'",
          "Value-Type '" + value.getClass().getSimpleName() + "'",
          "Value: '" + value.toString() + "'");
    }
  }
}
