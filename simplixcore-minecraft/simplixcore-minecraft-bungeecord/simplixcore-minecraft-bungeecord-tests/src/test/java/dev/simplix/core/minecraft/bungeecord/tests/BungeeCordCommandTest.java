package dev.simplix.core.minecraft.bungeecord.tests;

import dev.simplix.core.minecraft.bungeecord.quickstart.SimplixCommand;
import dev.simplix.core.minecraft.bungeecord.quickstart.SimplixQuickStart;
import dev.simplix.core.minecraft.bungeecord.tests.stub.StubProxiedPlayer;
import org.junit.jupiter.api.Test;

public class BungeeCordCommandTest {

  @Test
  void test() {
    new SimplixCommand(SimplixQuickStart.SIMPLIX_DOWNLOAD_URL).execute(
        new StubProxiedPlayer(),
        new String[0]);
  }
}
