package dev.simplix.core.common.duration;

import lombok.NonNull;

public interface Duration {

  boolean moreThan(@NonNull Duration punishDuration);

  boolean lessThan(@NonNull Duration punishDuration);

  boolean isEmpty();

  boolean isPermanent();

  @Override
  String toString();

  long toMs();
}
