package dev.simplix.core.minecraft.bungeecord.plugin;

import dev.simplix.core.common.aop.ScanComponents;
import dev.simplix.core.common.aop.SimplixApplication;
import dev.simplix.core.common.deploader.ArtifactDependencyLoader;
import dev.simplix.core.common.inject.SimplixInstaller;
import dev.simplix.core.minecraft.bungeecord.plugin.deploader.PluginTypeHandler;
import java.io.File;
import java.util.logging.Level;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

@SimplixApplication(name = "SimplixCore", version = "1.0", authors = {
    "Exceptionflug",
    "JavaFactoryDev"}, workingDirectory = "plugins/SimplixCore")
@ScanComponents("dev.simplix.core")
public final class SimplixPlugin extends Plugin {

  @Override
  public void onLoad() {
    try {
      SimplixInstaller.instance().updater().installCachedUpdates();
    } catch (Exception exception) {
      getLogger().log(
          Level.WARNING,
          "[Simplix | Updater] Cannot install cached updates",
          exception);
    }
    System.setProperty(
        "dev.simplix.core.libloader.ClassLoaderFabricator",
        "dev.simplix.core.minecraft.bungeecord.plugin.libloader.PluginClassLoaderFabricator");
    ArtifactDependencyLoader.registerTypeHandler("plugin", new PluginTypeHandler());
    SimplixInstaller.instance().libraryLoader().loadLibraries(new File("libraries"));
  }

  @Override
  public void onEnable() {
    ProxyServer.getInstance().getPluginManager().registerListener(this, new BungeeCordListenerImpl());

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
        } catch (InterruptedException interruptedException) {
          Thread.currentThread().interrupt();
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
