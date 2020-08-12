package dev.simplix.core.minecraft.spigot.dynamiclisteners;

import dev.simplix.core.common.aop.Component;
import dev.simplix.core.common.event.Events;
import dev.simplix.core.common.providers.ExceptionHandler;
import dev.simplix.core.minecraft.api.events.ChatEvent;
import dev.simplix.core.minecraft.api.events.JoinEvent;
import dev.simplix.core.minecraft.api.events.QuitEvent;
import java.util.Objects;
import javax.inject.Inject;
import lombok.NonNull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;

//@Setting
@Component(value = DynamicListenersSimplixModule.class)
public final class SpigotListenerImpl implements Listener {

  //  @Setting("Advanced.Listen_For_Commands")
  private static boolean LISTEN_FOR_COMMANDS = true;
  private final ExceptionHandler exceptionHandler;

  @Inject
  public SpigotListenerImpl(@NonNull ExceptionHandler exceptionHandler) {
    this.exceptionHandler = exceptionHandler;
  }

  @EventHandler
  public void login(AsyncPlayerPreLoginEvent playerPreLoginEvent) {

    JoinEvent joinEvent = Events.call(
        JoinEvent
            .create(
                playerPreLoginEvent.getUniqueId(),
                playerPreLoginEvent.getName(),
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
    try {
      ChatEvent chatEvent = Events
          .call(
              ChatEvent.create(
                  asyncPlayerChatEvent.getPlayer().getUniqueId(),
                  asyncPlayerChatEvent.getPlayer().getAddress().getAddress(),
                  asyncPlayerChatEvent.getMessage()));
      asyncPlayerChatEvent.setCancelled(chatEvent.canceled());
      asyncPlayerChatEvent.setMessage(chatEvent.message());
    } catch (Throwable throwable) {
      exceptionHandler.saveError(throwable, "Exception while calling chat-event!");
    }
  }

  @EventHandler
  public void chat2(PlayerCommandPreprocessEvent playerCommandPreprocessEvent) {
    if (playerCommandPreprocessEvent.isCancelled() || !LISTEN_FOR_COMMANDS) {
      return;
    }
    try {
      ChatEvent chatEvent = Events.call(ChatEvent
          .create(
              playerCommandPreprocessEvent.getPlayer().getUniqueId(),
              Objects
                  .requireNonNull(playerCommandPreprocessEvent.getPlayer().getAddress())
                  .getAddress(),
              playerCommandPreprocessEvent.getMessage()));
      playerCommandPreprocessEvent.setCancelled(chatEvent.canceled());
      playerCommandPreprocessEvent.setMessage(chatEvent.message());
    } catch (Throwable throwable) {
      exceptionHandler.saveError(
          throwable,
          "Exception while calling command-preprocess-event!"
      );
    }
  }

  @EventHandler
  public void quit(PlayerQuitEvent playerQuitEvent) {
    QuitEvent quitEvent = Events
        .call(QuitEvent.create(playerQuitEvent.getPlayer().getUniqueId()));
  }
}
