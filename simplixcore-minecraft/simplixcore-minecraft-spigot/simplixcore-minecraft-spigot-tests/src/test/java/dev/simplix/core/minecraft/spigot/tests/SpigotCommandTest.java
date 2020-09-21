package dev.simplix.core.minecraft.spigot.tests;

import dev.simplix.core.minecraft.spigot.quickstart.SimplixCommand;
import dev.simplix.core.minecraft.spigot.quickstart.SimplixQuickStart;
import dev.simplix.core.minecraft.spigot.tests.stub.StubPlayer;
import org.junit.jupiter.api.Test;

public class SpigotCommandTest {

  @Test
  void test() {
    final SimplixCommand simplixCommand = new SimplixCommand(SimplixQuickStart.SIMPLIX_DOWNLOAD_URL);
    simplixCommand.execute(new StubPlayer(), "simplix", new String[0]);
  }
}
