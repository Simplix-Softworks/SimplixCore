package dev.simplix.core.minecraft.bungeecord.dynamiclisteners;

import dev.simplix.core.common.aop.AbstractSimplixModule;
import lombok.NonNull;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;

public class DynamicListenersSimplixModule extends AbstractSimplixModule {

  private final Plugin plugin;

  public DynamicListenersSimplixModule(@NonNull Plugin plugin) {
    this.plugin = plugin;
    registerComponentInterceptor(
        Listener.class,
        listener -> ProxyServer
            .getInstance()
            .getPluginManager()
            .registerListener(plugin, listener));
  }

}
