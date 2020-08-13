package dev.simplix.core.minecraft.bungeecord.plugin;

import dev.simplix.core.common.aop.ScanComponents;
import dev.simplix.core.common.aop.SimplixApplication;
import dev.simplix.core.common.inject.SimplixInstaller;
import dev.simplix.core.minecraft.bungeecord.slf4j.ServiceProviderPatcher;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

@SimplixApplication(name = "SimplixCore", version = "1.0", authors = {
    "Exceptionflug",
    "JavaFactoryDev"}, workingDirectory = "plugins/SimplixCore")
@ScanComponents("dev.simplix.core")
public final class SimplixPlugin extends Plugin {

  @Override
  public void onLoad() {
    if (ServiceProviderPatcher.needsPatching()) {
      ProxyServer.getInstance().getLogger().warning("SimplixCore needs BungeeCord jar patching or "
                                                    + "some platform independent logging stuff from "
                                                    + "simplix will not work. To enable full logging, "
                                                    + "execute our slf4j-jarpatcher to patch your "
                                                    + "BungeeCord.jar file. See https://simplixsoft.com/jarpatcher "
                                                    + "for further information.");
    }
  }

  @Override
  public void onEnable() {
    SimplixInstaller.instance().register(SimplixPlugin.class);
    ProxyServer.getInstance().getScheduler().runAsync(this, () -> {
      long started = System.currentTimeMillis();
      String blockingApp;
      while ((blockingApp = waitForRegistration()) != null) {
        if (System.currentTimeMillis() - started > 2000) {
          getLogger().severe(
              "[Simplix] The following plugin takes to long for application registration: "
              + blockingApp);
          getLogger().severe(
              "[Simplix] Don't forget to call SimplixInstaller#register(owner: Class<?>): void in your onEnable method.");
          getLogger().severe(
              "[Simplix] SimplixCore will not wait any longer. Begin with installation...");
          break;
        }
        try {
          Thread.sleep(10);
        } catch (InterruptedException e) {
        }
      }
      SimplixInstaller.instance().install();
    });
  }

  private String waitForRegistration() {
    for (Plugin plugin : ProxyServer.getInstance().getPluginManager().getPlugins()) {
      if (plugin.getClass().isAnnotationPresent(SimplixApplication.class)) {
        SimplixApplication simplixApplication = plugin
            .getClass()
            .getAnnotation(SimplixApplication.class);
        if (!SimplixInstaller.instance().registered(simplixApplication.name())) {
          return plugin.getDescription().getName() + " " + plugin.getDescription().getVersion();
        }
      }
    }
    return null;
  }

}
