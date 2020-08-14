package dev.simplix.core.minecraft.spigot.plugin;

import dev.simplix.core.common.aop.Component;
import dev.simplix.core.common.providers.PluginManager;
import dev.simplix.core.minecraft.spigot.SpigotSimplixModule;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;

@Component(value = SpigotSimplixModule.class, parent = PluginManager.class)
public final class SpigotPluginManager implements PluginManager {

//  private final org.bukkit.plugin.PluginManager handle =

  @Override
  public void enablePlugin(@NonNull File jarFile) {
    try {
      final org.bukkit.plugin.PluginManager handle = Bukkit.getPluginManager();
      handle.enablePlugin(handle.loadPlugin(jarFile));
    } catch (InvalidPluginException | InvalidDescriptionException exception) {
      exception.printStackTrace();
    }
  }

  @Override
  public List<String> enabledPlugins() {
    final org.bukkit.plugin.PluginManager handle = Bukkit.getPluginManager();
    return Arrays
        .stream(handle.getPlugins())
        .map(Plugin::getName)
        .collect(Collectors.toList());
  }
}
