package dev.simplix.core.common.listener;

import dev.simplix.core.common.event.Event;

public interface Listener<T extends Event> {

  Class<T> type();

  void handleEvent(T event);
}
