package dev.simplix.core.common.duration;

import dev.simplix.core.common.TimeFormatUtil;
import dev.simplix.core.common.durations.SimpleDuration;
import java.sql.Timestamp;
import java.util.concurrent.TimeUnit;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Durations {

  /**
   * Returns an empty punish-duration. Normally indicates that the String that should be parsed had
   * the wrong format.
   */
  public Duration empty() {
    return new SimpleDuration(Long.MIN_VALUE);
  }

  public Duration of(@NonNull final Timestamp timestamp) {
    return of(timestamp.getTime());
  }

  public Duration of(@NonNull String humanReadableTime) {
    if (humanReadableTime.equalsIgnoreCase("-1")) {
      return permanent();
    }

    // Converting to seconds
    final long milliseconds = TimeFormatUtil.parseToMilliseconds(humanReadableTime);

    //Invalid format
    if (milliseconds == Long.MIN_VALUE) {
      return empty();
    }
    return new SimpleDuration(milliseconds);
  }

  public Duration of(final long ms) {
    if (ms == -1) {
      return permanent();
    }
    return new SimpleDuration(ms);
  }

  public Duration of(final long time, @NonNull final TimeUnit unit) {
    if (time == -1) {
      return permanent();
    }
    return new SimpleDuration(unit.toMillis(time));
  }

  public Duration permanent() {
    return new SimpleDuration(-1);
  }
}
