package dev.simplix.core.minecraft.bungeecord.tests;

import dev.simplix.core.minecraft.bungeecord.plugin.BungeeCordListenerImpl;
import dev.simplix.core.minecraft.bungeecord.tests.stub.StubProxiedPlayer;
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
    try {
      bungeeCordListenerImpl.login(new PostLoginEvent(new StubProxiedPlayer()));
    } catch (Throwable throwable) {
      Assertions.fail(throwable);
    }
  }

  @Test
  void chat() {
    try {
      bungeeCordListenerImpl.chat(new ChatEvent(
          new StubProxiedPlayer(),
          new StubProxiedPlayer(),
          "Example message"));
    } catch (Throwable throwable) {
      Assertions.fail(throwable);
    }
  }

  @Test
  void quit() {
    try {
      bungeeCordListenerImpl.quit(new PlayerDisconnectEvent(new StubProxiedPlayer()));
    } catch (Throwable throwable) {
      Assertions.fail(throwable);
    }
  }
}