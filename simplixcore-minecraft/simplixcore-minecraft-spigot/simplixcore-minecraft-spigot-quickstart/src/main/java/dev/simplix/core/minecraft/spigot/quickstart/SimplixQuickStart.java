package dev.simplix.core.minecraft.spigot.quickstart;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * This class is used to quickly populate non-simplix environments with a fresh copy of
 * SimplixCore.
 *
 * <B>Please note that you have to add the command "simplix" in your plugin.yml</B>
 *
 */
public final class SimplixQuickStart {

  private static final String SIMPLIX_DOWNLOAD_URL
      = "https://github.com/Simplix-Softworks/SimplixCore/releases/latest/SimplixCore-Spigot.jar";

  /**
   * Ensures that SimplixCore is installed on this server.
   *
   * @param plugin The plugin main class of the caller plugin
   * @return true if SimplixCore is installed or false otherwise
   */
  public static boolean ensureSimplixCore(JavaPlugin plugin) {
    if (Bukkit.getPluginManager().getPlugin("SimplixCore") == null) {
      registerInstaller(plugin);
      return false;
    }
    return true;
  }

  private static void registerInstaller(JavaPlugin plugin) {
    log("["
        + plugin.getDescription().getName()
        + "] This plugin needs the SimplixCore in order to work "
        + "properly.");
    log("["
        + plugin.getDescription().getName()
        + "] If you wish to automatically install SimplixCore "
        + "type the following command: simplix install");
    log("["
        + plugin.getDescription().getName()
        + "] "
        + plugin.getDescription().getName()
        + " will now halt.");
    plugin.getCommand("simplix").setExecutor(new SimplixCommandExecutor(SIMPLIX_DOWNLOAD_URL));
  }

  private static void log(String s) {
    Bukkit.getLogger().severe(s);
  }

}
