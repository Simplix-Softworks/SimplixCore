package dev.simplix.core.minecraft.spigot.plugin;

import dev.simplix.core.common.aop.ScanComponents;
import dev.simplix.core.common.aop.SimplixApplication;
import dev.simplix.core.common.deploader.ArtifactDependencyLoader;
import dev.simplix.core.common.inject.SimplixInstaller;
import dev.simplix.core.minecraft.spigot.dynamiclisteners.DynamicListenersSimplixModule;
import dev.simplix.core.minecraft.spigot.plugin.deploader.PluginTypeHandler;
import dev.simplix.core.minecraft.spigot.plugin.listeners.ApplicationPreInstallListener;
import java.io.File;
import java.util.logging.Level;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.jetbrains.annotations.Nullable;

@SimplixApplication(name = "SimplixCore", authors = "Simplix Softworks",
    workingDirectory = "plugins/SimplixCore")
@ScanComponents("dev.simplix.core")
@Slf4j
public final class SimplixPlugin extends JavaPlugin {

  public SimplixPlugin(
      @NonNull JavaPluginLoader loader,
      @NonNull PluginDescriptionFile description,
      @NonNull File dataFolder,
      @NonNull File file) {
    super(loader, description, dataFolder, file);
  }

  @Override
  public void onLoad() {
    new ApplicationPreInstallListener();
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
        "dev.simplix.core.minecraft.spigot.plugin.libloader.PluginClassLoaderFabricator");
    ArtifactDependencyLoader.registerTypeHandler("plugin", new PluginTypeHandler());
    SimplixInstaller.instance().libraryLoader().loadLibraries(new File("libraries"));
  }

  @Override
  public void onEnable() {
    Bukkit.getPluginManager().registerEvents(new SpigotListenerImpl(), this);
    SimplixInstaller
        .instance()
        .register(SimplixPlugin.class, new DynamicListenersSimplixModule(this));

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
          Thread.currentThread().interrupt();
        }
      }
      SimplixInstaller.instance().install();
    });
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
