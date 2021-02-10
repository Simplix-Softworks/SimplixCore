package dev.simplix.core.minecraft.velocity.plugin;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent.ChatResult;
import com.velocitypowered.api.proxy.Player;
import dev.simplix.core.common.event.Events;
import dev.simplix.core.minecraft.api.events.ChatEvent;
import dev.simplix.core.minecraft.api.events.JoinEvent;
import dev.simplix.core.minecraft.api.events.QuitEvent;
import lombok.NonNull;
import net.kyori.adventure.text.Component;

public class VelocityListenerImpl {

  public static VelocityListenerImpl create() {
    return new VelocityListenerImpl();
  }

  private VelocityListenerImpl() {
  }

  @Subscribe
  public void login(@NonNull PostLoginEvent playerPostLoginEvent) {
    final JoinEvent joinEvent = Events.call(
        JoinEvent.create(
            playerPostLoginEvent.getPlayer().getUniqueId(),
            playerPostLoginEvent.getPlayer().getUsername(),
            playerPostLoginEvent.getPlayer().getRemoteAddress().getAddress()));

    if (!joinEvent.canceled()) {
      return;
    }

    if (joinEvent.canceled()) {
      playerPostLoginEvent.getPlayer()
          .disconnect(Component.text(joinEvent.cancelReason().replace("&", "§")));
    }
  }

  @Subscribe
  public void chat(@NonNull PlayerChatEvent playerChatEvent) {
    final Player sender = playerChatEvent.getPlayer();
    final ChatEvent chatEvent = Events.call(
        ChatEvent.create(
            sender.getUniqueId(),
            sender.getRemoteAddress().getAddress(),
            playerChatEvent.getMessage())
    );

    playerChatEvent.setResult(chatEvent.canceled()
        ? ChatResult.denied() : ChatResult.message(chatEvent.message()));
  }

  @Subscribe
  public void quit(@NonNull DisconnectEvent playerDisconnectEvent) {
    final QuitEvent quitEvent =
        Events.call(
            QuitEvent.create(playerDisconnectEvent.getPlayer().getUniqueId())
        );
  }
}