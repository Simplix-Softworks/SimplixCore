package dev.simplix.core.minecraft.bungeecord.plugin;

import dev.simplix.core.common.aop.Component;
import dev.simplix.core.common.providers.PluginManager;
import dev.simplix.core.minecraft.bungeecord.BungeeCordSimplixModule;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import net.md_5.bungee.api.ProxyServer;

@Component(value = BungeeCordSimplixModule.class, parent = PluginManager.class)
public final class BungeeCordPluginManager implements PluginManager {

  @Override
  public void enablePlugin(@NonNull File jarFile) {
    final File parentFile = jarFile.getParentFile();
    if (parentFile != null) {
      ProxyServer.getInstance().getPluginManager().detectPlugins(parentFile);
    }
  }

  @Override
  public List<String> enabledPlugins() {
    return new ArrayList<>();
  }
}
