package dev.simplix.core.common.listener;

import dev.simplix.core.common.event.AbstractEvent;
import dev.simplix.core.common.event.Events;
import lombok.NonNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ListenersTest {

  private static final ExampleListener LISTENER = new ExampleListener();

  @BeforeEach
  void setUp() {
    Listeners.register(LISTENER);
  }

  @Test
  void registeredListeners() {
    Assertions.assertTrue(Listeners.registeredListeners().contains(LISTENER));
  }

  @Test
  void callEvent() {
    ExampleEvent call = Events.call(new ExampleEvent());
    Assertions.assertTrue(call.canceled(), "Event has been canceled");
    Assertions.assertEquals(call.cancelReason(), "Example cancel reason");
  }

  static class ExampleEvent extends AbstractEvent {

  }

  static class ExampleListener implements Listener<ExampleEvent> {

    @Override
    public Class<ExampleEvent> type() {
      return ExampleEvent.class;
    }

    @Override
    public void handleEvent(@NonNull ExampleEvent event) {
      event.canceled(true);
      event.cancelReason("Example cancel reason");
    }
  }
}