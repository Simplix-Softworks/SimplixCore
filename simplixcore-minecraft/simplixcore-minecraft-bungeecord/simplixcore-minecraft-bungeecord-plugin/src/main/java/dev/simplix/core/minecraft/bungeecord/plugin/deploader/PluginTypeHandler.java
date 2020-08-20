package dev.simplix.core.minecraft.bungeecord.plugin.deploader;

import com.google.common.io.Files;
import dev.simplix.core.common.deploader.Dependency;
import lombok.extern.slf4j.Slf4j;
import net.md_5.bungee.api.ProxyServer;

import java.io.File;
import java.util.function.BiConsumer;

@Slf4j
public class PluginTypeHandler implements BiConsumer<Dependency, File> {

  @Override
  public void accept(Dependency dependency, File file) {
    File target = new File("plugins", file.getName());
    try {
      Files.copy(file, target);
      // TODO
    } catch (Exception e) {
      log.error("[Simplix | DependencyLoader] Unable to load plugin "+file.getName(), e);
    }
  }

}
