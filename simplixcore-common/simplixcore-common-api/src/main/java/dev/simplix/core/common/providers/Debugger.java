package dev.simplix.core.common.providers;

import lombok.NonNull;

public interface Debugger {

  boolean isDebugged(@NonNull String section);

  boolean isWarned(@NonNull String section);

  void debug(@NonNull String section, @NonNull String... messages);

  void warn(@NonNull String section, @NonNull String... messages);

}
