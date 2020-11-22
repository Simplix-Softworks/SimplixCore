package dev.simplix.core.minecraft.spigot.plugin.listeners;

import dev.simplix.core.common.ApplicationInfo;
import dev.simplix.core.common.events.ApplicationPreInstallEvent;
import dev.simplix.core.common.listener.Listener;
import dev.simplix.core.common.listener.Listeners;
import dev.simplix.core.minecraft.spigot.plugin.util.PluginDescriptionUtil;
import java.io.File;
import java.net.URISyntaxException;
import lombok.NonNull;
import org.bukkit.plugin.PluginDescriptionFile;

public final class ApplicationPreInstallListener implements Listener<ApplicationPreInstallEvent> {

  public ApplicationPreInstallListener() {
    Listeners.register(this);
  }

  @Override
  public Class<ApplicationPreInstallEvent> type() {
    return ApplicationPreInstallEvent.class;
  }

  @Override
  public void handleEvent(@NonNull ApplicationPreInstallEvent event) {
    if (event.applicationInfo().version().equals("<auto>")) {
      try {
        PluginDescriptionFile pluginDescription = PluginDescriptionUtil.loadPluginYml(new File(event
            .applicationClass()
            .getProtectionDomain()
            .getCodeSource()
            .getLocation()
            .toURI()));
        if (pluginDescription == null) {
          return;
        }
        event.applicationInfo(ApplicationInfo.builder()
            .name(event.applicationInfo().name())
            .version(pluginDescription.getVersion())
            .authors(event.applicationInfo().authors())
            .dependencies(event.applicationInfo().dependencies())
            .workingDirectory(event.applicationInfo().workingDirectory())
            .build());
      } catch (URISyntaxException exception) {
        // Will never happen
      }
    }
  }

}
