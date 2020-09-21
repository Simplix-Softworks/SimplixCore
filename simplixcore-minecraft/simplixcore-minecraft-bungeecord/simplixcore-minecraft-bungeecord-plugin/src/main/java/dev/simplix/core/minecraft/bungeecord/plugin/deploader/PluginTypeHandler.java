package dev.simplix.core.minecraft.bungeecord.plugin.deploader;

import com.google.common.io.Files;
import dev.simplix.core.common.deploader.Dependency;
import dev.simplix.core.minecraft.bungeecord.plugin.util.PluginDescriptionUtil;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.function.BiConsumer;
import lombok.extern.slf4j.Slf4j;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.PluginDescription;
import net.md_5.bungee.api.plugin.PluginManager;

@Slf4j
public class PluginTypeHandler implements BiConsumer<Dependency, File> {

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

  @Override
  public void accept(Dependency dependency, File file) {
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
      boolean b = (boolean) enable.invoke(
          ProxyServer.getInstance().getPluginManager(),
          new HashMap<>(),
          new Stack<>(),
          pluginDescription);
      if (!b) {
        return;
      }
      ProxyServer
          .getInstance()
          .getPluginManager()
          .getPlugin(pluginDescription.getName())
          .onEnable();
    } catch (Exception e) {
      log.error("[Simplix | DependencyLoader] Unable to load plugin " + file.getName(), e);
    }
  }

  private boolean willBeAutomaticallyLoaded(String name)
      throws ReflectiveOperationException {
    Map<String, PluginDescription> toLoad = (Map<String, PluginDescription>) toLoadField.get(
        ProxyServer.getInstance().getPluginManager());
    log.debug("[Simplix] Checking if "+name+" will be automatically enabled. Automatically enabled will be: "+toLoad.keySet());
    return toLoad.containsKey(name);
  }

}
