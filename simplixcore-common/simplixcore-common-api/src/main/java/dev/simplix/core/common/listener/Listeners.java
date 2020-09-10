package dev.simplix.core.common.listener;

import dev.simplix.core.common.event.Event;
import dev.simplix.core.common.event.Events;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

/**
 * Central utility class for handling listeners
 * <p>
 * They will be called in a class called Proxy/Spigot-ListenerImpl
 */
@UtilityClass
public class Listeners {

  private final List<Listener<Event>> registeredListeners = new ArrayList<>();

  /**
   * Registers an {@link Listener}. If an {@link Listener} is registered using this method it's
   * {@link Listener#handleEvent(Event)} method will be called once an {@link Event} is called using
   * {@link Events#call(Event)} method
   */
  public void register(@NonNull final Listener<Event> listener) {
    registeredListeners.add(listener);
  }

  /**
   * Returns all registered {@link Listener}'s as an unmodifiable collection. It is made
   * unmodifiable since we don't want listeners to be removed from it
   */
  public Collection<Listener<Event>> registeredListeners() {
    return Collections.unmodifiableCollection(registeredListeners);
  }
}
