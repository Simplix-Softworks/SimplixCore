package dev.simplix.core.minecraft.spigot.plugin.deploader;

import com.google.common.io.Files;
import dev.simplix.core.common.deploader.Dependency;
import java.io.File;
import java.util.function.BiConsumer;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.Bukkit;

@Slf4j
public class PluginTypeHandler implements BiConsumer<Dependency, File> {

  @Override
  public void accept(Dependency dependency, File file) {
    File target = new File("plugins", file.getName());
    try {
      Files.copy(file, target);
      Bukkit.getPluginManager().loadPlugin(target);
    } catch (Exception exception) {
      log.error("[Simplix | DependencyLoader] Unable to load plugin " + file.getName(), exception);
    }
  }

}
