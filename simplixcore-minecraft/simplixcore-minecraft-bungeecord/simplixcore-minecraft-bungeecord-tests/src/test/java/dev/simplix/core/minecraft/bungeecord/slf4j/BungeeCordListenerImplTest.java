package dev.simplix.core.minecraft.bungeecord.slf4j;

import dev.simplix.core.minecraft.bungeecord.plugin.BungeeCordListenerImpl;
import dev.simplix.core.minecraft.bungeecord.slf4j.mock.MockProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class BungeeCordListenerImplTest {

  private static BungeeCordListenerImpl bungeeCordListenerImpl;

  @BeforeAll
  static void setUp() {
    bungeeCordListenerImpl = new BungeeCordListenerImpl();
    Assertions.assertEquals(
        "dev.simplix.core.minecraft.bungeecord.plugin.ProxyListenerImpl",
        bungeeCordListenerImpl.getClass().getName());
  }

  @Test
  void login() {
    bungeeCordListenerImpl.login(new PostLoginEvent(new MockProxiedPlayer()));
  }

  @Test
  void chat() {
    bungeeCordListenerImpl.chat(new ChatEvent(
        new MockProxiedPlayer(),
        new MockProxiedPlayer(),
        "Example message"));
  }

  @Test
  void quit() {
    bungeeCordListenerImpl.quit(new PlayerDisconnectEvent(new MockProxiedPlayer()));
  }
}