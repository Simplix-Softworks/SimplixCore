package dev.simplix.core.minecraft.api.events;

import dev.simplix.core.common.event.AbstractEvent;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class QuitEvent extends AbstractEvent {

  private final UUID targetUUID;

  public static QuitEvent create(@NonNull final UUID targetUUID) {
    return new QuitEvent(targetUUID);
  }
}
