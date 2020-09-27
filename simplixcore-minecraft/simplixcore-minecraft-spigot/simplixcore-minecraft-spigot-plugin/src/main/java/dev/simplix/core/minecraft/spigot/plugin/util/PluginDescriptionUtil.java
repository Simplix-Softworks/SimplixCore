package dev.simplix.core.minecraft.spigot.plugin.util;

import java.io.File;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;

@UtilityClass
public final class PluginDescriptionUtil {

  public PluginDescriptionFile loadPluginYml(File target) {
    try (JarFile jar = new JarFile(target)) {
      JarEntry pdf = jar.getJarEntry("plugin.yml");

      try (InputStream in = jar.getInputStream(pdf)) {
        PluginDescriptionFile desc = new PluginDescriptionFile(in);
        return desc;
      }
    } catch (Exception exception) {
      Bukkit
          .getLogger()
          .log(Level.WARNING, "Could not load plugin from file " + target, exception);
    }
    return null;
  }

}
