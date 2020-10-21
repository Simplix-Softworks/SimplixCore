package dev.simplix.core.minecraft.bungeecord.plugin.deploader;

import com.google.common.io.Files;
import dev.simplix.core.common.deploader.Dependency;
import dev.simplix.core.common.deploader.DependencyTypeHandler;
import dev.simplix.core.common.utils.FileUtils;
import dev.simplix.core.minecraft.bungeecord.plugin.util.PluginDescriptionUtil;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicReference;
import lombok.extern.slf4j.Slf4j;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.PluginDescription;
import net.md_5.bungee.api.plugin.PluginManager;

@Slf4j
public class PluginTypeHandler implements DependencyTypeHandler {

  private Field toLoadField;
  private Method enable;

  public PluginTypeHandler() {
    try {
      toLoadField = PluginManager.class.getDeclaredField("toLoad");
      toLoadField.setAccessible(true);
      enable = PluginManager.class.getDeclaredMethod(
          "enablePlugin",
          Map.class,
          Stack.class,
          PluginDescription.class);
      enable.setAccessible(true);
    } catch (ReflectiveOperationException ignored) {
    }
  }

  private boolean willBeAutomaticallyLoaded(String name)
      throws ReflectiveOperationException {
    Map<String, PluginDescription> toLoad = (Map<String, PluginDescription>) toLoadField.get(
        ProxyServer.getInstance().getPluginManager());
    if (toLoad == null) {
      log.warn("[Simplix | DependencyLoader] Cannot check if "
               + name
               + " will be loaded by default plugin loader. This may occur when using outdated or unofficial BungeeCord versions.");
      return false;
    }
    log.debug("[Simplix] Checking if "
              + name
              + " will be automatically enabled. Automatically enabled will be: "
              + toLoad.keySet());
    return toLoad.containsKey(name);
  }

  @Override
  public void handle(Dependency dependency, File file) {
    File target = new File("plugins", file.getName());
    try {
      Files.copy(file, target);
      PluginDescription pluginDescription = PluginDescriptionUtil.loadPluginYml(target);
      if (pluginDescription == null) {
        return;
      }
      if (willBeAutomaticallyLoaded(pluginDescription.getName())) {
        return;
      }
      enable.invoke(
          ProxyServer.getInstance().getPluginManager(),
          new HashMap<>(),
          new Stack<>(),
          pluginDescription);
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
