package dev.simplix.core.common.listener;

import dev.simplix.core.common.event.Event;
import dev.simplix.core.common.event.Events;
import lombok.NonNull;

/**
 * Listener that can listen to an {@link Event}. An Listener can be registered using the {@link
 * Listeners#register(Listener)} method
 *
 * @param <T> Event the listener should listen to.
 */
public interface Listener<T extends Event> {

  /**
   * Class of the event we want to listen to.
   */
  Class<T> type();

  /**
   * Will be executed once the event is called using {@link Events#call(Event)} method All event
   * handling is done in the implementations of this method
   */
  void handleEvent(@NonNull T event);
}
