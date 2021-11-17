package dev.simplix.core.minecraft.bungeecord.dynamiclisteners;

import dev.simplix.core.common.aop.AbstractSimplixModule;
import java.util.ArrayList;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;

public class DynamicListenersSimplixModule extends AbstractSimplixModule {

  private final Plugin plugin;

  public DynamicListenersSimplixModule(Plugin plugin) {
    if (plugin == null) {
      this.plugin = new ArrayList<>(ProxyServer
          .getInstance()
          .getPluginManager()
          .getPlugins()).get(0);
    } else {
      this.plugin = plugin;
    }

    registerComponentInterceptor(
        Listener.class,
        listener -> ProxyServer
            .getInstance()
            .getPluginManager()
            .registerListener(plugin, listener));
  }

}
