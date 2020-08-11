package dev.simplix.core.minecraft.bungeecord.plugin;

import dev.simplix.core.common.aop.Component;
import dev.simplix.core.common.providers.PluginManager;
import dev.simplix.core.minecraft.bungeecord.BungeeCordSimplixModule;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;

@Component(value = BungeeCordSimplixModule.class, parent = PluginManager.class)
public final class BungeeCordPluginManager implements PluginManager {

  @Override
  public void enablePlugin(@NonNull File jarFile) {

  }

  @Override
  public List<String> enabledPlugins() {
    return new ArrayList<>();
  }
}
