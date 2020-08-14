package dev.simplix.core.minecraft.api.events;

import dev.simplix.core.common.event.AbstractEvent;
import java.net.InetAddress;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class JoinEvent extends AbstractEvent {

  private final UUID targetUUID;
  private final String name;
  private final InetAddress targetAddress;

  public static JoinEvent create(
      @NonNull final UUID targetUUID,
      @NonNull final String name,
      @NonNull final InetAddress targetInetAddress) {
    return new JoinEvent(targetUUID, name, targetInetAddress);
  }
}
