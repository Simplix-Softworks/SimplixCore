package dev.simplix.core.minecraft.spigot.plugin.util;

import com.google.common.base.Preconditions;
import java.io.File;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;

public final class PluginDescriptionUtil {

  private PluginDescriptionUtil() {}

  public static PluginDescriptionFile loadPluginYml(File target) {
    try (JarFile jar = new JarFile(target)) {
      JarEntry pdf = jar.getJarEntry("plugin.yml");
      Preconditions.checkNotNull(pdf, "Plugin must have a plugin.yml");

      try (InputStream in = jar.getInputStream(pdf)) {
        PluginDescriptionFile desc = new PluginDescriptionFile(in);
        Preconditions.checkNotNull(desc.getName(), "Plugin from %s has no name", target);
        Preconditions.checkNotNull(desc.getMain(), "Plugin from %s has no main", target);
        return desc;
      }
    } catch (Exception ex) {
      Bukkit
          .getLogger()
          .log(Level.WARNING, "Could not load plugin from file " + target, ex);
    }
    return null;
  }

}
