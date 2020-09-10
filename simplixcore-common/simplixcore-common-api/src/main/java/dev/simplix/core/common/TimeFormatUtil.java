package dev.simplix.core.common;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.OptionalInt;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

/**
 * Utility class to format durations.
 */
@UtilityClass
public class TimeFormatUtil {

  /**
   * The date format in dd.MM.yyy HH:mm:ss
   */
  private final DateFormat DEFAULT_DATA_FORMAT = new SimpleDateFormat(
      "dd.MM.yyyy HH:mm:ss");

  public String calculateCurrentDateFormatted() {
    return calculateDateFormatted(System.currentTimeMillis());
  }

  public String calculateDateFormatted(final long time) {
    return DEFAULT_DATA_FORMAT.format(time);
  }

  public String calculateDateFormatted(@NonNull DateFormat dateFormat, final long time) {
    return dateFormat.format(time);
  }

  /**
   * Method to parse an String like "10 days 20 hours" to an duration in milliseconds
   *
   * @return Returns the duration in milliseconds or {@link Long#MIN_VALUE} if the #humanReadable
   * had an unparsable format
   */
  public long parseToMilliseconds(@NonNull String humanReadable) {
    long ticks = 0L;

    String finalHumanReadable = splitHumanToHumanReadable(humanReadable);

    String[] split = finalHumanReadable.split(" ");

    for (int i = 1; i < split.length; i++) {
      final String sub = split[i];
      int multiplier; // e.g 2 hours = 2
      long unit = 0; // e.g hours = 3600
      boolean isTicks = false;

      try {
        multiplier = Integer.parseInt(split[i - 1]);
      } catch (NumberFormatException ignored) {
        continue;
      }

      if (sub.toLowerCase().startsWith("tick")) {
        isTicks = true;
      } else if (sub.toLowerCase().startsWith("second") || sub.startsWith("s")) {
        unit = 1;
      } else if (sub.toLowerCase().startsWith("minute") || sub.startsWith("m")) {
        unit = 60;
      } else if (sub.toLowerCase().startsWith("hour") || sub.startsWith("H")) {
        unit = 3600;
      } else if (sub.toLowerCase().startsWith("day") || sub.startsWith("d")) {
        unit = 86400;
      } else if (sub.toLowerCase().startsWith("week")) {
        unit = 604800;
      } else if (sub.toLowerCase().startsWith("month") || sub.startsWith("M")) {
        unit = 2629743;
      } else if (sub.toLowerCase().startsWith("year") || sub.startsWith("y")) {
        unit = 31556926;
      } else {
        //Invalid-format
        return Long.MIN_VALUE;
      }

      ticks += multiplier * (isTicks ? 1 : unit * 50);
    }

    return ticks * 20;
  }

  // 10days becomes 10 days
  private String splitHumanToHumanReadable(@NonNull final String humanReadable) {

    // Already properly formatted
    if (humanReadable.contains(" ")) {
      return humanReadable;
    }
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

  public String formatDuration(long duration) {
    if (duration == -1 || duration == 0) {
      return "Permanent";
    }

    if (duration == Long.MIN_VALUE) {
      return "Empty";
    }

    long years = TimeUnit.MILLISECONDS.toDays(duration) / 365;
    duration -= TimeUnit.DAYS.toMillis(years) * 365;
    long month = TimeUnit.MILLISECONDS.toDays(duration) / 30;
    duration -= TimeUnit.DAYS.toMillis(month) * 30;
    long days = TimeUnit.MILLISECONDS.toDays(duration);
    duration -= TimeUnit.DAYS.toMillis(days);
    long hours = TimeUnit.MILLISECONDS.toHours(duration);

    if (years < 0) {
      years = 0;
    }

    if (month < 0) {
      month = 0;
    }

    if (days < 0) {
      days = 0;
    }

    if (hours < 0) {
      hours = 0;
    }

    String preResult = " {years} year(s) {month} month {days} day(s) {hours} hour(s)"
        .replace("{years}", years + "")
        .replace("{month}", month + "")
        .replace("{days}", days + "")
        .replace("{hours}", hours + "");

    preResult = preResult
        .replace(" 0 year(s)", "")
        .replace(" 0 month", "")
        .replace(" 0 day(s)", "")
        .replace(" 0 hour(s)", "");

    //shortening

    //we don't need years & hours
    if (years != 0 && hours != 0) {
      preResult = preResult.replace(hours + " hours", "");
    }

    return preResult.startsWith(" ") ? preResult.substring(1) : preResult;
  }
}
