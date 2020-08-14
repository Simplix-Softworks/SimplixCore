package dev.simplix.core.common.durations;

import dev.simplix.core.common.duration.Duration;
import dev.simplix.core.common.duration.Durations;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SimpleDurationTest {

  @Test
  void moreThan() {
    Assertions.assertTrue(
        Durations
            .of(11L, TimeUnit.MINUTES)
            .moreThan(Durations.of(10L, TimeUnit.MINUTES)));
  }

  @Test
  void lessThan() {
    Assertions.assertTrue(
        Durations
            .of(10L, TimeUnit.MINUTES)
            .lessThan(Durations.of(11L, TimeUnit.MINUTES)));
  }

  @Test
  void isEmpty() {
    Assertions.assertTrue(Durations.empty().isEmpty());
    Assertions.assertTrue(Durations.of(Long.MIN_VALUE).isEmpty());
  }

  @Test
  void isPermanent() {
    Assertions.assertTrue(Durations.permanent().isPermanent());
    Assertions.assertTrue(Durations.of(-1).isPermanent());
    Assertions.assertTrue(Durations.of(-1, TimeUnit.DAYS).isPermanent());
  }

  @Test
  void testToString() {
    // Permanent-case
    Assertions.assertEquals("Permanent", Durations.permanent().toString());
    // Empty
    Assertions.assertEquals("Empty", Durations.empty().toString());

    // Other
    Assertions.assertEquals("10 day(s)", Durations.of("10 days").toString());
  }

  @Test
  void toMs() {
    final Duration testDuration = Durations.of(10L, TimeUnit.MILLISECONDS);
    Assertions.assertEquals(testDuration.toMs(), 10);
  }
}