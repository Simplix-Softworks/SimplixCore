package dev.simplix.core.common;

import de.leonhard.storage.util.Valid;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;
import lombok.experimental.UtilityClass;

/**
 * Utility class for calculating time from ticks and back.
 */
@UtilityClass
public class TimeUtil {

  /**
   * The date format in dd.MM.yyy HH:mm:ss
   */
  private final DateFormat DATE_FORMAT = new SimpleDateFormat(
      "dd.MM.yyyy HH:mm:ss");
  /**
   * The date format in dd.MM.yyy HH:mm
   */
  private final DateFormat DATE_FORMAT_SHORT = new SimpleDateFormat(
      "dd.MM.yyyy HH:mm");

  /**
   * Return the current date formatted as DAY.MONTH.YEAR HOUR:MINUTES:SECONDS
   *
   * @return
   */
  public String formattedDate() {
    return formattedDate(System.currentTimeMillis());
  }

  /**
   * Return the given date in millis formatted as DAY.MONTH.YEAR HOUR:MINUTES:SECONDS
   *
   * @param time
   * @return
   */
  public String formattedDate(final long time) {
    return DATE_FORMAT.format(time);
  }

  /**
   * Return the current date formatted as DAY.MONTH.YEAR HOUR:MINUTES
   *
   * @return
   */
  public String formattedDateShort() {
    return DATE_FORMAT_SHORT.format(System.currentTimeMillis());
  }

  /**
   * Return the given date in millis formatted as DAY.MONTH.YEAR HOUR:MINUTES
   *
   * @param time
   * @return
   */
  public String formattedDateShort(final long time) {
    return DATE_FORMAT_SHORT.format(time);
  }

  // ------------------------------------------------------------------------------------------------------------
  // Converting
  // ------------------------------------------------------------------------------------------------------------

  /**
   * Converts the time from a human readable format like "10 minutes" to seconds.
   *
   * @param humanReadableTime the human readable time format: {time} {period} example: 5 seconds, 10
   *                          ticks, 7 minutes, 12 hours etc..
   * @return the converted human time to seconds
   */
  public long toTicks(final String humanReadableTime) {
    Valid.notNull(humanReadableTime, "Time is null");

    long seconds = 0L;

    final String[] split = humanReadableTime.split(" ");

    for (int i = 1; i < split.length; i++) {
      final String sub = split[i];
      int multiplier = 0; // e.g 2 hours = 2
      long unit = 0; // e.g hours = 3600
      boolean isTicks = false;

      try {
        multiplier = Integer.parseInt(split[i - 1]);
      } catch (final NumberFormatException e) {
        continue;
      }

      // attempt to match the unit time
      if (sub.startsWith("tick"))
        isTicks = true;
      else if (sub.toLowerCase().startsWith("second") || sub.startsWith("s"))
        unit = 1;
      else if (sub.toLowerCase().startsWith("minute") || sub.startsWith("m"))
        unit = 60;
      else if (sub.toLowerCase().startsWith("hour") || sub.startsWith("H"))
        unit = 3600;
      else if (sub.toLowerCase().startsWith("day") || sub.startsWith("d"))
        unit = 86400;
      else if (sub.toLowerCase().startsWith("week"))
        unit = 604800;
      else if (sub.toLowerCase().startsWith("month") || sub.startsWith("M"))
        unit = 2629743;
      else //Invalid-format
        if (sub.toLowerCase().startsWith("year") || sub.startsWith("y"))
          unit = 31556926;
        else
          return Long.MIN_VALUE;

      seconds += multiplier * (isTicks ? 1 : unit * 20);
    }

    return seconds;
  }

  /**
   * Formats the given time from seconds into the following format:
   *
   * <p>"1 hour 50 minutes 10 seconds" or similar, or less
   *
   * @param seconds
   * @return
   */
  public String formatTimeGeneric(final long seconds) {
    final long second = seconds % 60;
    long minute = seconds / 60;
    String hourMsg = "";

    if (minute >= 60) {
      final long hour = seconds / 60;
      minute %= 60;

      hourMsg = (hour == 1 ? "hour" : "hours") + " ";
    }

    return hourMsg
           + minute
           + (minute > 0 ? (minute == 1 ? " minute" : " minutes") + " " : "")
           + second
           + (second == 1 ? " second" : " seconds");
  }

  /**
   * Format time in "X days Y hours Z minutes Å seconds"
   *
   * @param seconds
   * @return
   */
  public String formatTimeDays(final long seconds) {
    final long minutes = seconds / 60;
    final long hours = minutes / 60;
    final long days = hours / 24;

    return days
           + " days "
           + hours % 24
           + " hours "
           + minutes % 60
           + " minutes "
           + seconds % 60
           + " seconds";
  }

  /**
   * Format the time in seconds, for example: 10d 5h 10m 20s or just 5m 10s If the seconds are zero,
   * an output 0s is given
   *
   * @param seconds
   * @return
   */
  public String formatTimeShort(long seconds) {
    long minutes = seconds / 60;
    long hours = minutes / 60;
    final long days = hours / 24;

    hours = hours % 24;
    minutes = minutes % 60;
    seconds = seconds % 60;

    return (days > 0 ? days + "d " : "")
           + (hours > 0 ? hours + "h " : "")
           + (minutes > 0 ? minutes + "m " : "")
           + seconds
           + "s";
  }

  public String formatMenuDate(long duration) {
    if (duration == -1 || duration == 0)
      return "&cPermanent";

    long years = TimeUnit.MILLISECONDS.toDays(duration) / 365;
    duration -= TimeUnit.DAYS.toMillis(years) * 365;
    long month = TimeUnit.MILLISECONDS.toDays(duration) / 30;
    duration -= TimeUnit.DAYS.toMillis(month) * 30;
    long days = TimeUnit.MILLISECONDS.toDays(duration);
    duration -= TimeUnit.DAYS.toMillis(days);
    long hours = TimeUnit.MILLISECONDS.toHours(duration);

    if (years < 0)
      years = 0;

    if (month < 0)
      month = 0;

    if (days < 0)
      days = 0;

    if (hours < 0)
      hours = 0;

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
    if (years != 0 && hours != 0)
      preResult = preResult.replace(hours + " hours", "");

    return preResult.startsWith(" ") ? preResult.substring(1) : preResult;
  }
}
