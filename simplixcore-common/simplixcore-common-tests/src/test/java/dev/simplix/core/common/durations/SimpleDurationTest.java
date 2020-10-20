package dev.simplix.core.common.durations;

import dev.simplix.core.common.duration.Duration;
import dev.simplix.core.common.duration.Durations;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SimpleDurationTest {

  @Test
  public void moreThan() {
    Assertions.assertTrue(
        Durations
            .of(11L, TimeUnit.MINUTES)
            .moreThan(Durations.of(10L, TimeUnit.MINUTES)));
  }

  @Test
  public void lessThan() {
    Assertions.assertTrue(
        Durations
            .of(10L, TimeUnit.MINUTES)
            .lessThan(Durations.of(11L, TimeUnit.MINUTES)));
  }

  @Test
  public void isEmpty() {
    Assertions.assertTrue(Durations.empty().isEmpty());
    Assertions.assertTrue(Durations.of(Long.MIN_VALUE).isEmpty());
  }

  @Test
  public void isPermanent() {
    Assertions.assertTrue(Durations.permanent().isPermanent());
    Assertions.assertTrue(Durations.of(-1).isPermanent());
    Assertions.assertTrue(Durations.of(-1, TimeUnit.DAYS).isPermanent());
  }

  @Test
  public void testToString() {
    // Permanent-case
    Assertions.assertEquals("Permanent", Durations.permanent().toString());
    // Empty
    Assertions.assertEquals("Empty", Durations.empty().toString());

    // Other
//    Assertions.assertEquals("864000000", Durations.of("10 days").toString());
  }

  @Test
  public void toMs() {
    final Duration testDuration = Durations.of(10L, TimeUnit.MILLISECONDS);
    Assertions.assertEquals(testDuration.toMs(), 10);
  }
}