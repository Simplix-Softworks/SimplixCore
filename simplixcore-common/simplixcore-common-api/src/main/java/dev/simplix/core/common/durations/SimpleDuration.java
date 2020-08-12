package dev.simplix.core.common.durations;

import dev.simplix.core.common.TimeUtil;
import dev.simplix.core.common.duration.AbstractDuration;
import dev.simplix.core.common.duration.Duration;
import lombok.NonNull;

/**
 * Class to be able
 * <p>
 * TODO: do not obfuscate!
 */
public final class SimpleDuration extends AbstractDuration {

  public SimpleDuration(long ms) {
    super(ms);
  }

  // ----------------------------------------------------------------------------------------------------
  // Convenience methods here
  // ----------------------------------------------------------------------------------------------------

  @Override
  public boolean moreThan(@NonNull final Duration punishDuration) {
    if (isPermanent()) {
      return true;
    }

    if (punishDuration.isPermanent()) {
      return false;
    }

    return toMs() > punishDuration.toMs();
  }

  @Override
  public boolean lessThan(@NonNull final Duration duration) {
    return !moreThan(duration);
  }

  @Override
  public boolean isEmpty() {
    return toMs() == Long.MIN_VALUE;
  }

  @Override
  public boolean isPermanent() {
    return this.ms == -1L || this.ms == 0;
  }

  @Override
  public String toString() {
    if (isPermanent()) {
      return "Permanent";
    }
    if (isEmpty()) {
      return "Empty";
    }
    return TimeUtil.formatMenuDate(this.ms);
  }

  @Override
  public long toMs() {
    return this.ms;
  }
}
