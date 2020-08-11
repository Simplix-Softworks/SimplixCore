package dev.simplix.core.common.providers;

import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

public interface ExceptionHandler {

  void saveError(@Nullable Throwable throwable, @NonNull String... messages);
}
