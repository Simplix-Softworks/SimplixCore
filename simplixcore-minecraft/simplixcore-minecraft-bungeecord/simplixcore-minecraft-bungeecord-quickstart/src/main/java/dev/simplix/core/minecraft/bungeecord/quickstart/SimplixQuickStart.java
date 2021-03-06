package dev.simplix.core.minecraft.bungeecord.quickstart;

import lombok.NonNull;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

/**
 * This class is used to quickly populate non-simplix environments with a fresh copy of
 * SimplixCore.
 */
public final class SimplixQuickStart {

  public static final String SIMPLIX_DOWNLOAD_URL
      = "https://ci.exceptionflug.de/job/SimplixCore/lastSuccessfulBuild/artifact/simplixcore-minecraft/simplixcore-minecraft-bungeecord/simplixcore-minecraft-bungeecord-plugin/target/SimplixCore-BungeeCord.jar";

  /**
   * Ensures that SimplixCore is installed on this server.
   *
   * @param plugin The plugin main class of the caller plugin
   * @return true if SimplixCore is installed or false otherwise
   */
  public static boolean ensureSimplixCore(@NonNull Plugin plugin) {
    if (ProxyServer.getInstance().getPluginManager().getPlugin("SimplixCore") == null) {
      registerInstaller(plugin);
      return false;
    }
    return true;
  }

  private static void registerInstaller(Plugin plugin) {
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
    ProxyServer
        .getInstance()
        .getPluginManager()
        .registerCommand(plugin, new SimplixCommand(SIMPLIX_DOWNLOAD_URL));
  }

  private static void log(@NonNull String message) {
    ProxyServer.getInstance().getLogger().severe(message);
  }

}
