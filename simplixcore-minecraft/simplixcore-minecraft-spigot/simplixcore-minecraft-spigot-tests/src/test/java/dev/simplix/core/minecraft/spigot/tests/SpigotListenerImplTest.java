package dev.simplix.core.minecraft.spigot.tests;

import com.google.common.collect.Sets;
import dev.simplix.core.minecraft.spigot.plugin.SpigotListenerImpl;
import dev.simplix.core.minecraft.spigot.tests.stub.StubPlayer;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class SpigotListenerImplTest {

  private static SpigotListenerImpl spigotListener;

  @BeforeAll
  static void setUp() {
    spigotListener = new SpigotListenerImpl();
    Assertions.assertEquals(
        "dev.simplix.core.minecraft.spigot.plugin.SpigotListenerImpl",
        spigotListener.getClass().getName());
  }

  @Test
  void login() throws UnknownHostException {
    try {
      spigotListener.login(new AsyncPlayerPreLoginEvent(
          "KotlinFactory",
          InetAddress.getLocalHost(),
          UUID.randomUUID()));
    } catch (Throwable throwable) {
      Assertions.fail(throwable);
    }
  }

  @Test
  void chat() {
    try {
      spigotListener.chat(new AsyncPlayerChatEvent(
          false,
          new StubPlayer(),
          "Example message",
          Sets.newHashSet()));
    } catch (Throwable throwable) {
      Assertions.fail(throwable);
    }
  }

  @Test
  void chat2() {
    try {
      spigotListener.chat2(new PlayerCommandPreprocessEvent(
          new StubPlayer(),
          "/example",
          // Recipients must be given or the test will fail due to the MockPlayer#getServer returning null
          Sets.newHashSet()));
    } catch (Throwable throwable) {
      Assertions.fail(throwable);
    }
  }

  @Test
  void quit() {
    try {
      spigotListener.quit(new PlayerQuitEvent(new StubPlayer(), "Quitted"));
    } catch (Throwable throwable) {
      Assertions.fail(throwable);
    }
  }
}

