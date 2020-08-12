package dev.simplix.core.common.listener;

import java.util.ArrayList;
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
@SuppressWarnings({"rawtypes"})
public class Listeners {

  private final List<Listener> registeredListeners = new ArrayList<>();

  public void register(@NonNull final Listener listener) {
    registeredListeners.add(listener);
  }

  public List<Listener> registeredListeners() {
    return Collections.unmodifiableList(registeredListeners);
  }
}
