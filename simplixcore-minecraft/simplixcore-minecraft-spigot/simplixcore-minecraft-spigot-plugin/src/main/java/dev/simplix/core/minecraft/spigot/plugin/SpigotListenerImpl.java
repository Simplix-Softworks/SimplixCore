package dev.simplix.core.minecraft.spigot.plugin;

import dev.simplix.core.common.event.Events;
import dev.simplix.core.minecraft.api.events.ChatEvent;
import dev.simplix.core.minecraft.api.events.JoinEvent;
import dev.simplix.core.minecraft.api.events.QuitEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class SpigotListenerImpl implements Listener {

  @EventHandler(priority = EventPriority.LOW)
  public void login(AsyncPlayerPreLoginEvent playerPreLoginEvent) {

    JoinEvent joinEvent = Events.call(
        JoinEvent
            .create(playerPreLoginEvent.getUniqueId(), playerPreLoginEvent.getName(),
                playerPreLoginEvent.getAddress()));

    if (!joinEvent.canceled()) {
      return;
    }

    playerPreLoginEvent.setLoginResult(Result.KICK_BANNED);
    playerPreLoginEvent.setKickMessage(joinEvent.cancelReason().replace("&", "§"));
  }

  @EventHandler(priority = EventPriority.LOW)
  public void chat(AsyncPlayerChatEvent asyncPlayerChatEvent) {
    if (asyncPlayerChatEvent.isCancelled()) {
      return;
    }
    ChatEvent chatEvent = Events.call(ChatEvent
        .create(
            asyncPlayerChatEvent.getPlayer().getUniqueId(),
            asyncPlayerChatEvent.getPlayer().getAddress().getAddress(),
            asyncPlayerChatEvent.getMessage()));

    if (chatEvent.canceled()) {
      asyncPlayerChatEvent.setCancelled(true);
    }
    asyncPlayerChatEvent.setMessage(chatEvent.message());
  }

  @EventHandler(priority = EventPriority.LOW)
  public void chat2(PlayerCommandPreprocessEvent playerCommandPreprocessEvent) {
    if (playerCommandPreprocessEvent.isCancelled()) {
      return;
    }
    ChatEvent chatEvent = Events.call(ChatEvent
        .create(
            playerCommandPreprocessEvent.getPlayer().getUniqueId(),
            playerCommandPreprocessEvent.getPlayer().getAddress().getAddress(),
            playerCommandPreprocessEvent.getMessage()));

    if (!chatEvent.cancelReason().isEmpty()) {
      playerCommandPreprocessEvent.getPlayer()
          .sendMessage(chatEvent.cancelReason().replace("&", "§"));
    }

    if (chatEvent.canceled()) {
      playerCommandPreprocessEvent.setCancelled(true);
    }
    
    if (!chatEvent.message().isEmpty()) {
      playerCommandPreprocessEvent.setMessage(chatEvent.message());
    }
  }

  @EventHandler(priority = EventPriority.LOW)
  public void quit(PlayerQuitEvent playerQuitEvent) {
    QuitEvent quitEvent = Events.call(QuitEvent.create(playerQuitEvent.getPlayer().getUniqueId()));
  }
}