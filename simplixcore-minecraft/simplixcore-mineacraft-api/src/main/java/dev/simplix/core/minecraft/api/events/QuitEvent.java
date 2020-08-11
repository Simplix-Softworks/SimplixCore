package dev.simplix.core.minecraft.api.events;

import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import dev.simplix.core.common.event.AbstractEvent;

@RequiredArgsConstructor
public class QuitEvent extends AbstractEvent {

  public static QuitEvent create(@NonNull final UUID target) {
    return new QuitEvent(target);
  }

  private final UUID target;
}