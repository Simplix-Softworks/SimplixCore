package dev.simplix.core.common.event;

import dev.simplix.core.common.listener.Listener;
import dev.simplix.core.common.listener.Listeners;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

/**
 * Central utility class for handling events
 */
@UtilityClass
public class Events {

  private final List<Event> knownEvents = new ArrayList<>();

  void add(@NonNull Event event) {
    knownEvents.add(event);
  }

  public List<Event> registeredEvents() {
    return Collections.unmodifiableList(knownEvents);
  }

  /**
   * Calls an {@link Event} and the {@link Listener#handleEvent(Event)} method of all {@link
   * Listeners} registered using {@link Listeners#register(Listener)} that are listening for the
   * given {@link Event}
   *
   * @return Returns the event
   */
  public <T extends Event> T call(@NonNull T event) {
    for (final Listener<Event> listener : Listeners.registeredListeners()) {
      if (listener.type() != event.getClass()) {
        continue;
      }

      listener.handleEvent(event);
      // Don't call further Listeners if the event was already canceled
      if (event.canceled()) {
        return event;
      }
    }

    return event;
  }
}
