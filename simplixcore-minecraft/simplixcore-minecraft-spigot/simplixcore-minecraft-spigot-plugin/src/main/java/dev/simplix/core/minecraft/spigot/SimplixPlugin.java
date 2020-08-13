package dev.simplix.core.minecraft.spigot;

import dev.simplix.core.common.aop.SimplixApplication;
import dev.simplix.core.common.inject.SimplixInstaller;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

@SimplixApplication(name = "SimplixCore", version = "1.0", authors = {
    "Exceptionflug",
    "JavaFactoryDev"}, workingDirectory = "plugins/SimplixCore")
public final class SimplixPlugin extends JavaPlugin {

  @Override
  public void onEnable() {
    SimplixInstaller.instance().register(SimplixPlugin.class);

    Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
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
    });
    SimplixInstaller.instance().install();
  }

  @Nullable
  private String waitForRegistration() {
    for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
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
