package dev.simplix.core.minecraft.velocity.plugin.listeners;

import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.PluginDescription;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.simplix.core.common.ApplicationInfo;
import dev.simplix.core.common.events.ApplicationPreInstallEvent;
import dev.simplix.core.common.listener.Listener;
import dev.simplix.core.common.listener.Listeners;
import java.net.URISyntaxException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class ApplicationPreInstallListener implements Listener<ApplicationPreInstallEvent> {

  private final ProxyServer proxyServer;

  public ApplicationPreInstallListener(@NonNull ProxyServer proxyServer) {
    this.proxyServer = proxyServer;
    Listeners.register(this);
  }

  @Override
  public Class<ApplicationPreInstallEvent> type() {
    return ApplicationPreInstallEvent.class;
  }

  @Override
  public void handleEvent(@NonNull ApplicationPreInstallEvent event) {
    if (event.applicationInfo().version().equals("<auto>")) {
      PluginDescription pluginDescription = obtainPluginDescription(event.applicationClass());
      if (pluginDescription == null) {
        log.warn("[Simplix] Cannot fill plugin version to application info of " + event
            .applicationInfo()
            .name() + ": No plugin description found");
        return;
      }
      event.applicationInfo(ApplicationInfo.builder()
          .name(event.applicationInfo().name())
          .version(pluginDescription.getVersion().orElse("<UNKNOWN>"))
          .authors(event.applicationInfo().authors())
          .dependencies(event.applicationInfo().dependencies())
          .workingDirectory(event.applicationInfo().workingDirectory())
          .build());
    }
  }

  private PluginDescription obtainPluginDescription(@NonNull Class<?> applicationClass) {
    for (PluginContainer pluginContainer : proxyServer.getPluginManager().getPlugins()) {
      if(pluginContainer.getInstance().isPresent()) {
        if(pluginContainer.getInstance().get().getClass().equals(applicationClass)) {
          return pluginContainer.getDescription();
        }
      }
    }
    return null;
  }

}
