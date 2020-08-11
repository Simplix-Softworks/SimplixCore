package dev.simplix.core.common.duration;

import dev.simplix.core.common.TimeUtil;
import dev.simplix.core.common.durations.SimpleDuration;
import java.sql.Timestamp;
import java.util.OptionalInt;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
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

    // Input: 10days Output: 10 days
    if (!humanReadableTime.contains(" ")) {
      humanReadableTime = splitHumanToHumanReadable(humanReadableTime);
    }

    // Converting to ms (1tick = 50ms)

    final long ticks = TimeUtil.toTicks(humanReadableTime);

    //Invalid format
    if (ticks == Long.MIN_VALUE) {
      return empty();
    }
    return new SimpleDuration(TimeUtil.toTicks(humanReadableTime) * 50);
  }

  // 10days becomes 10 days
  private String splitHumanToHumanReadable(@NonNull final String humanReadable) {
    // returns an OptionalInt with the value of the index of the first Letter
    final OptionalInt firstLetterIndex =
        IntStream.range(0, humanReadable.length())
            .filter(i -> Character.isLetter(humanReadable.charAt(i)))
            .findFirst();

    // Default if there is no letter, only numbers
    String numbers = humanReadable;
    String letters = "";
    // if there are letters, split the string at the first letter
    if (firstLetterIndex.isPresent()) {
      numbers = humanReadable.substring(0, firstLetterIndex.getAsInt());
      letters = humanReadable.substring(firstLetterIndex.getAsInt());
    }

    return numbers + " " + letters;
  }

  public Duration of(final long ms) {
    if (ms == -1) {
      return permanent();
    }
    return new SimpleDuration(ms);
  }

  public Duration of(final long time, final TimeUnit unit) {
    if (time == -1) {
      return permanent();
    }
    return new SimpleDuration(unit.toMillis(time));
  }

  public Duration permanent() {
    return new SimpleDuration(-1);
  }
}
