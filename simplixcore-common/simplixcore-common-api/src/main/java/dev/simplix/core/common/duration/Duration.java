package dev.simplix.core.common.duration;

import lombok.NonNull;

public interface Duration {

  /**
   * Compares the length of this and another specified duration.
   *
   * @param duration Duration to compare to
   */
  boolean moreThan(@NonNull Duration duration);

  /**
   * Convenience method for the opposite of {@link #moreThan(Duration)}
   *
   * @param duration Duration to compare to
   */
  boolean lessThan(@NonNull Duration duration);

  /**
   * Compares the length of this and another specified duration for equality
   *
   * @param duration Duration to compare to
   */
  boolean sameAs(@NonNull Duration duration);

  /**
   * An Duration can also have no value. This is usually the case if the duration in milliseconds is
   * equal to {@link Long#MIN_VALUE}
   */
  boolean isEmpty();

  /**
   * A duration can be indefinite. If a Duration is indefinite it is called permanent. Usually this
   * happens when the duration in milliseconds is equal to -1
   */
  boolean isPermanent();

  /**
   * Returns an formatted time string
   */
  @Override
  String toString();

  /**
   * Returns the duration in milliseconds
   */
  long toMs();
}
