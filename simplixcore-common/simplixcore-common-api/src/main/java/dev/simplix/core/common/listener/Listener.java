package dev.simplix.core.common.listener;

import dev.simplix.core.common.event.Event;
import lombok.NonNull;

public interface Listener<T extends Event> {

  Class<T> type();

  void handleEvent(@NonNull T event);
}
