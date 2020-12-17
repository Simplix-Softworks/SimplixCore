package dev.simplix.core.minecraft.velocity.plugin;

import com.velocitypowered.api.proxy.ProxyServer;
import dev.simplix.core.common.aop.Component;
import dev.simplix.core.minecraft.api.providers.PluginManager;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import dev.simplix.core.minecraft.velocity.VelocitySimplixModule;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Component(value = VelocitySimplixModule.class, parent = PluginManager.class)
@Slf4j
public final class VelocityPluginManager implements PluginManager {

  @Override
  public void enablePlugin(@NonNull File jarFile) {
    log.warn("[Simplix] Plugin management on velocity not supported");
  }

  @Override
  public List<String> enabledPlugins() {
    return new ArrayList<>();
  }
}
