package dev.simplix.core.minecraft.spigot.tests;

import dev.simplix.core.minecraft.spigot.quickstart.SimplixCommand;
import dev.simplix.core.minecraft.spigot.quickstart.SimplixQuickStart;
import dev.simplix.core.minecraft.spigot.tests.stub.StubPlayer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SpigotCommandTest {

  private static SimplixCommand SIMPLIX_COMMAND;

  @BeforeAll
  static void setUp() {
    SIMPLIX_COMMAND = new SimplixCommand(SimplixQuickStart.SIMPLIX_DOWNLOAD_URL);
  }

  @Test
  @BeforeEach
  void testNoArgs() {
    try{
      SIMPLIX_COMMAND.execute(new StubPlayer(), "simplix", new String[0]);
    }catch(Throwable throwable){
      Assertions.fail(throwable);
    }
  }

  @Test
  void testInstallCommand() {
    try{
      SIMPLIX_COMMAND.execute(new StubPlayer(), "simplix", new String[]{"install"});
    }catch(Throwable throwable){
      Assertions.fail(throwable);
    }
  }
}
