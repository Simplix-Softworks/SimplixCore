package dev.simplix.core.minecraft.bungeecord.plugin.deploader;

import com.google.common.base.Preconditions;
import com.google.common.io.Files;
import dev.simplix.core.common.deploader.Dependency;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.function.BiConsumer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import lombok.extern.slf4j.Slf4j;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.PluginDescription;
import net.md_5.bungee.api.plugin.PluginManager;
import org.yaml.snakeyaml.Yaml;

@Slf4j
public class PluginTypeHandler implements BiConsumer<Dependency, File> {

  private final Yaml yaml = new Yaml();

  @Override
  public void accept(Dependency dependency, File file) {
    File target = new File("plugins", file.getName());
    try {
      Files.copy(file, target);
      Method enable = PluginManager.class.getDeclaredMethod(
          "enablePlugin",
          Map.class,
          Stack.class,
          PluginDescription.class);
      enable.setAccessible(true);
      PluginDescription pluginDescription = loadPluginYml(target);
      if (pluginDescription == null) {
        return;
      }
      if (ProxyServer.getInstance().getPluginManager().getPlugin(pluginDescription.getName())
          != null) {
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

  private PluginDescription loadPluginYml(File target) {
    try (JarFile jar = new JarFile(target)) {
      JarEntry pdf = jar.getJarEntry("bungee.yml");
      if (pdf == null) {
        pdf = jar.getJarEntry("plugin.yml");
      }
      Preconditions.checkNotNull(pdf, "Plugin must have a plugin.yml or bungee.yml");

      try (InputStream in = jar.getInputStream(pdf)) {
        PluginDescription desc = yaml.loadAs(in, PluginDescription.class);
        Preconditions.checkNotNull(desc.getName(), "Plugin from %s has no name", target);
        Preconditions.checkNotNull(desc.getMain(), "Plugin from %s has no main", target);

        desc.setFile(target);
        return desc;
      }
    } catch (Exception ex) {
      ProxyServer
          .getInstance()
          .getLogger()
          .log(Level.WARNING, "Could not load plugin from file " + target, ex);
    }
    return null;
  }

}
