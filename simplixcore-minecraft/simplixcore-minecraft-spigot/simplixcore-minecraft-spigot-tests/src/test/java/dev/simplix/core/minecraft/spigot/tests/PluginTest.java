package dev.simplix.core.minecraft.spigot.tests;

import be.seeseemelk.mockbukkit.MockBukkit;
import dev.simplix.core.minecraft.spigot.plugin.SimplixPlugin;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class PluginTest {

  private static SimplixPlugin plugin;

  @BeforeAll
  @Test
  static void setUp() {
    MockBukkit.mock();
    plugin = MockBukkit.load(SimplixPlugin.class);
  }

  @Test
  void testEnabled() {
    Assertions.assertEquals(
        "dev.simplix.core.minecraft.spigot.plugin.SimplixPlugin",
        SimplixPlugin.class.getName(),
        "Plugin was invalidly relocated");
    Assertions.assertTrue(plugin.isEnabled(), "Plugin must be enabled");
  }
}
