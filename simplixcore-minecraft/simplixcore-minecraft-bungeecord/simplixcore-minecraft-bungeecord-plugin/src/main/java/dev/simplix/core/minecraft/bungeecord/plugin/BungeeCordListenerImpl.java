package dev.simplix.core.minecraft.bungeecord.plugin;

import dev.simplix.core.common.event.Events;
import dev.simplix.core.minecraft.api.events.ChatEvent;
import dev.simplix.core.minecraft.api.events.JoinEvent;
import dev.simplix.core.minecraft.api.events.QuitEvent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class BungeeCordListenerImpl implements Listener {

  public static BungeeCordListenerImpl create() {
    return new BungeeCordListenerImpl();
  }

  @EventHandler
  public void login(final PostLoginEvent playerPostLoginEvent) {
    final JoinEvent joinEvent = Events.call(
        JoinEvent.create(
            playerPostLoginEvent.getPlayer().getUniqueId(),
            playerPostLoginEvent.getPlayer().getName(),
            playerPostLoginEvent.getPlayer().getAddress().getAddress()));

    if (!joinEvent.canceled()) {
      return;
    }

    if (joinEvent.canceled()) {
      playerPostLoginEvent.getPlayer()
          .disconnect(joinEvent.cancelReason().replace("&", "§"));
    }
  }

  @EventHandler
  public void chat(final net.md_5.bungee.api.event.ChatEvent playerChatEvent) {
    final ProxiedPlayer sender = (ProxiedPlayer) playerChatEvent.getSender();
    final ChatEvent chatEvent = Events.call(
        ChatEvent.create(
            sender.getUniqueId(),
            sender.getAddress().getAddress(),
            playerChatEvent.getMessage())
    );

    playerChatEvent.setCancelled(chatEvent.canceled());
    playerChatEvent.setMessage(chatEvent.message());
  }

  @EventHandler
  public void quit(final PlayerDisconnectEvent playerDisconnectEvent) {
    final QuitEvent quitEvent =
        Events.call(
            QuitEvent.create(playerDisconnectEvent.getPlayer().getUniqueId())
        );
  }
}