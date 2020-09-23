package dev.simplix.core.minecraft.spigot.tests;

import be.seeseemelk.mockbukkit.MockBukkit;
import dev.simplix.core.minecraft.spigot.plugin.SimplixPlugin;
import dev.simplix.core.minecraft.spigot.quickstart.SimplixCommand;
import dev.simplix.core.minecraft.spigot.quickstart.SimplixQuickStart;
import dev.simplix.core.minecraft.spigot.tests.stub.StubPlayer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SpigotCommandTest {

  private static SimplixCommand SIMPLIX_COMMAND;

  @BeforeAll
  @Test
  static void setUp() {
    SIMPLIX_COMMAND = new SimplixCommand(SimplixQuickStart.SIMPLIX_DOWNLOAD_URL);
    if(!MockBukkit.isMocked()) {
      MockBukkit.mock();
    }
    MockBukkit.load(SimplixPlugin.class);
  }

  @Test
  @BeforeEach
  void testNoArgs() {
    SIMPLIX_COMMAND.execute(new StubPlayer(), "simplix", new String[0]);
  }

  @Test
  void testInstallCommand() {
    SIMPLIX_COMMAND.execute(new StubPlayer(), "simplix", new String[]{"install"});
  }
}
