package dev.simplix.core.minecraft.spigot.plugin;

import de.leonhard.storage.util.LagCatcher;
import dev.simplix.core.common.event.Events;
import dev.simplix.core.minecraft.api.events.ChatEvent;
import dev.simplix.core.minecraft.api.events.JoinEvent;
import dev.simplix.core.minecraft.api.events.QuitEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class SpigotListenerImpl implements Listener {

  @EventHandler
  public void login(AsyncPlayerPreLoginEvent playerPreLoginEvent) {

    LagCatcher.start("async-join");
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

  @EventHandler
  public void chat(AsyncPlayerChatEvent asyncPlayerChatEvent) {
    if (asyncPlayerChatEvent.isCancelled()) {
      return;
    }
    ChatEvent chatEvent = Events.call(ChatEvent
        .create(
            asyncPlayerChatEvent.getPlayer().getUniqueId(),
            asyncPlayerChatEvent.getPlayer().getAddress().getAddress(),
            asyncPlayerChatEvent.getMessage()));
    asyncPlayerChatEvent.setCancelled(chatEvent.canceled());
    asyncPlayerChatEvent.setMessage(chatEvent.message());
  }

  @EventHandler
  public void chat2(PlayerCommandPreprocessEvent playerCommandPreprocessEvent) {
    if (playerCommandPreprocessEvent.isCancelled()) {
      return;
    }
    LagCatcher.start("command-chat");
    ChatEvent chatEvent = Events.call(ChatEvent
        .create(
            playerCommandPreprocessEvent.getPlayer().getUniqueId(),
            playerCommandPreprocessEvent.getPlayer().getAddress().getAddress(),
            playerCommandPreprocessEvent.getMessage()));
    playerCommandPreprocessEvent.setCancelled(chatEvent.canceled());
    playerCommandPreprocessEvent.setMessage(chatEvent.message());
    LagCatcher.start("command-chat");
  }

  @EventHandler
  public void quit(PlayerQuitEvent playerQuitEvent) {
    QuitEvent quitEvent = Events
        .call(QuitEvent.create(playerQuitEvent.getPlayer().getUniqueId()));

  }
}