package dev.simplix.core.minecraft.spigot.quickstart;

import java.lang.reflect.Field;
import java.util.logging.Level;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * This class is used to quickly populate non-simplix environments with a fresh copy of
 * SimplixCore.
 *
 * <B>Please note that you have to add the command "simplix" in your plugin.yml</B>
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

  private static void registerInstaller(@NonNull JavaPlugin plugin) {
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

    registerCommand(new SimplixCommand(SIMPLIX_DOWNLOAD_URL));
  }

  private static void log(@NonNull String string) {
    Bukkit.getLogger().severe(string);
  }

  public static void registerCommand(@NonNull final Command command) {
    try {
      final Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
      commandMapField.setAccessible(true);

      final CommandMap commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());
      commandMap.register(command.getLabel(), command);
    } catch (final Throwable throwable) {
      Bukkit.getLogger().log(Level.SEVERE, "Unable to register command", throwable);
    }
  }

}
