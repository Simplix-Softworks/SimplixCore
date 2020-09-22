package dev.simplix.core.common.event;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class EventsTest {

  @Test
  void registeredEvents() {
    Assertions.assertTrue(Events.registeredEvents().contains(new TestEvent()));
  }

  @Test
  void call() {
    TestEvent call = Events.call(new TestEvent());
    Assertions.assertFalse(call.canceled());
  }

  private static class TestEvent extends AbstractEvent {

  }

}