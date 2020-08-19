package dev.simplix.core.minecraft.api.events;

import dev.simplix.core.common.event.AbstractEvent;
import java.net.InetAddress;
import java.util.UUID;
import lombok.*;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(fluent = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ChatEvent extends AbstractEvent {

  private final UUID targetUUID;
  private final InetAddress targetAddress;
  @NonNull
  private String message;

  public static ChatEvent create(
      @NonNull final UUID targetUUID,
      @NonNull final InetAddress targetAddress,
      @NonNull final String message) {
    return new ChatEvent(targetUUID, targetAddress, message);
  }
}
