package dev.simplix.core.minecraft.velocity.plugin;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.simplix.core.common.aop.ScanComponents;
import dev.simplix.core.common.aop.SimplixApplication;
import dev.simplix.core.common.deploader.ArtifactDependencyLoader;
import dev.simplix.core.common.inject.SimplixInstaller;
import dev.simplix.core.common.platform.Platform;
import dev.simplix.core.minecraft.velocity.plugin.deploader.PluginTypeHandler;
import dev.simplix.core.minecraft.velocity.plugin.listeners.ApplicationPreInstallListener;
import lombok.NonNull;
import org.slf4j.Logger;
import javax.inject.Inject;
import java.io.File;

@SimplixApplication(name = "SimplixCore", authors = "Simplix Softworks",
    workingDirectory = "plugins/SimplixCore")
@ScanComponents("dev.simplix.core")
public class SimplixPlugin {

  private final ProxyServer proxyServer;
  private final Logger logger;

  @Inject
  public SimplixPlugin(
      @NonNull Logger logger,
      @NonNull ProxyServer proxyServer) {
    this.proxyServer = proxyServer;
    this.logger = logger;
    new ApplicationPreInstallListener(proxyServer);
    try {
      SimplixInstaller.instance().updater().installCachedUpdates();
    } catch (Exception exception) {
      logger.warn("[Simplix | Updater] Cannot install cached updates", exception);
    }
    System.setProperty(
        "dev.simplix.core.libloader.ClassLoaderFabricator",
        "dev.simplix.core.minecraft.velocity.plugin.libloader.PluginClassLoaderFabricator");
    ArtifactDependencyLoader.registerTypeHandler("plugin", PluginTypeHandler.create(proxyServer));
    SimplixInstaller.instance().libraryLoader().loadLibraries(new File("libraries"));
  }

  @Subscribe
  public void onProxyInitialization(@NonNull ProxyInitializeEvent event) {
    logger.warn(
        "[Simplix] You are running a development build of SimplixCore. Please be aware that thing may break!");
    proxyServer.getEventManager().register(this, VelocityListenerImpl.create());
    SimplixInstaller.instance().register(SimplixPlugin.class);
    proxyServer.getScheduler().buildTask(this, () -> {
      long started = System.currentTimeMillis();
      String blockingApp;
      while ((blockingApp = waitForRegistration()) != null) {
        if (System.currentTimeMillis() - started > 5000) {
          logger.error(
              "[Simplix] The following plugin takes to long for application registration: "
              + blockingApp);
          logger.error(
              "[Simplix] Don't forget to call SimplixInstaller#register(owner: Class<?>): void in your ProxyInitializeEvent handler.");
          logger.error(
              "[Simplix] SimplixCore will not wait any longer. Begin with installation...");
          break;
        }

        try {
          Thread.sleep(10);
        } catch (InterruptedException interruptedException) {
          Thread.currentThread().interrupt();
        }
      }
      SimplixInstaller.instance().install(Platform.VELOCITY);
    }).schedule();
  }

  private String waitForRegistration() {
    for (PluginContainer plugin : proxyServer.getPluginManager().getPlugins()) {
      if (plugin.getInstance().isPresent()) {
        if (plugin.getInstance().get().getClass().isAnnotationPresent(SimplixApplication.class)) {
          SimplixApplication simplixApplication = plugin.getInstance().get()
              .getClass()
              .getAnnotation(SimplixApplication.class);
          if (!SimplixInstaller.instance().registered(simplixApplication.name())) {
            return plugin.getDescription().getName().orElse("<UNKNOWN>") + " " + plugin
                .getDescription()
                .getVersion()
                .orElse("<UNKNOWN>");
          }
        }
      }
    }
    return null;
  }

}
