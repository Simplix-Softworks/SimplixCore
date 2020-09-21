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
    spigotListener.login(new AsyncPlayerPreLoginEvent(
        "KotlinFactory",
        InetAddress.getLocalHost(),
        UUID.randomUUID()));
  }

  @Test
  void chat() {
    spigotListener.chat(new AsyncPlayerChatEvent(
        false,
        new StubPlayer(),
        "Example message",
        Sets.newHashSet()));
  }

  @Test
  void chat2() {
    spigotListener.chat2(new PlayerCommandPreprocessEvent(
        new StubPlayer(),
        "/example",
        // Recipients must be given or the test will fail due to the MockPlayer#getServer returning null
        Sets.newHashSet()));
  }

  @Test
  void quit() {
    spigotListener.quit(new PlayerQuitEvent(new StubPlayer(), "Quitted"));
  }
}

