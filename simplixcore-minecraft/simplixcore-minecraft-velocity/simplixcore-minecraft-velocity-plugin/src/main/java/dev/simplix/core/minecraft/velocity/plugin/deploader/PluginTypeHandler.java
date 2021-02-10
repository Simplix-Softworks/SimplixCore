package dev.simplix.core.minecraft.velocity.plugin.deploader;

import com.google.common.base.Joiner;
import com.google.common.io.Files;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.PluginDescription;
import com.velocitypowered.api.plugin.PluginManager;
import com.velocitypowered.api.plugin.meta.PluginDependency;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.proxy.plugin.VelocityPluginManager;
import com.velocitypowered.proxy.plugin.loader.VelocityPluginContainer;
import com.velocitypowered.proxy.plugin.loader.java.JavaPluginLoader;
import dev.simplix.core.common.deploader.Dependency;
import dev.simplix.core.common.deploader.DependencyTypeHandler;
import dev.simplix.core.common.utils.FileUtils;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicReference;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PluginTypeHandler implements DependencyTypeHandler {

  private final ProxyServer proxyServer;

  private Method registerPluginMethod;

  {
    try {
      registerPluginMethod = VelocityPluginManager.class.getDeclaredMethod(
          "registerPlugin",
          PluginContainer.class);
      registerPluginMethod.setAccessible(true);
    } catch (ReflectiveOperationException exception) {
      log.error("[Simplix] Unable to initialize PluginTypeHandler for velocity", exception);
    }
  }

  private PluginTypeHandler(ProxyServer proxyServer) {
    this.proxyServer = proxyServer;
  }

  public static PluginTypeHandler create(@NonNull ProxyServer proxyServer) {
    return new PluginTypeHandler(proxyServer);
  }

  private boolean willBeAutomaticallyLoaded(@NonNull String name) {
    return proxyServer.getPluginManager().getPlugin(name).isPresent();
  }

  @Override
  public void handle(Dependency dependency, File file) {
    File target = new File("plugins", file.getName());
    try {
      Files.copy(file, target);
      JavaPluginLoader pluginLoader = new JavaPluginLoader(proxyServer, Paths.get("plugins"));
      PluginDescription pluginDescription = pluginLoader.loadPluginDescription(file.toPath());
      if (pluginDescription == null) {
        return;
      }
      if (willBeAutomaticallyLoaded(pluginDescription.getName().orElse("null"))) {
        return;
      }
      for (PluginDependency pluginDependency : pluginDescription.getDependencies()) {
        if (!pluginDependency.isOptional()
            && !willBeAutomaticallyLoaded(pluginDependency.getId())) {
          log.error(
              "[Simplix | DependencyLoader] Can't load plugin {} due to missing dependency {}",
              pluginDescription.getId(),
              pluginDependency.getId());
          return;
        }
      }
      PluginDescription realPlugin = pluginLoader.loadPlugin(pluginDescription);
      VelocityPluginContainer container = new VelocityPluginContainer(realPlugin);
      AbstractModule commonModule = new AbstractModule() {
        @Override
        protected void configure() {
          bind(ProxyServer.class).toInstance(proxyServer);
          bind(PluginManager.class).toInstance(proxyServer.getPluginManager());
          bind(EventManager.class).toInstance(proxyServer.getEventManager());
          bind(CommandManager.class).toInstance(proxyServer.getCommandManager());
          for (PluginContainer container : proxyServer.getPluginManager().getPlugins()) {
            bind(PluginContainer.class)
                .annotatedWith(Names.named(container.getDescription().getId()))
                .toInstance(container);
          }
        }
      };
      pluginLoader.createPlugin(container, pluginLoader.createModule(container), commonModule);
      registerPluginMethod.invoke(proxyServer.getPluginManager(), container);
      log.info(
          "[Simplix | DependencyLoader] Loaded plugin {} {} by {}",
          pluginDescription.getId(),
          pluginDescription.getVersion()
              .orElse("<UNKNOWN>"),
          Joiner.on(", ").join(pluginDescription.getAuthors()));
    } catch (Exception exception) {
      log.error("[Simplix | DependencyLoader] Unable to load plugin " + file.getName(), exception);
    }
  }

  @Override
  public boolean shouldInstall(Dependency dependency) {
    AtomicReference<File> atomicReference = new AtomicReference<>();
    FileUtils
        .context(new File("plugins"))
        .whenExists(fileContext -> fileContext.subFiles(fc -> fc.whenFile(subFile -> {
          File file = subFile.file();
          if (file.getName().startsWith(dependency.artifactId())
              && !file.getName().equals(dependency.artifactId() + "-" + dependency.version())) {
            atomicReference.set(file);
          }
        })));
    if (atomicReference.get() != null) {
      log.warn("[Simplix | DependencyLoader] "
               + dependency.applicationName()
               + ": Version conflict of plugin "
               + dependency.toString());
      log.warn("[Simplix | DependencyLoader] "
               + dependency.applicationName()
               + ": This file seems to contain another version of this dependency: "
               + atomicReference.get().getAbsolutePath());
      log.warn("[Simplix | DependencyLoader] "
               + dependency.applicationName()
               + ": SimplixCore will not load multiple versions of the same dependency. "
               + "Please resolve this issue.");
      return false;
    }
    return true;
  }

}
