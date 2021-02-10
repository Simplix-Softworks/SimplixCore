package dev.simplix.core.minecraft.velocity.quickstart;

import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * This class is used to quickly populate non-simplix environments with a fresh copy of
 * SimplixCore.
 */
@Slf4j
public final class SimplixQuickStart {

  public static final String SIMPLIX_DOWNLOAD_URL
      = "https://ci.exceptionflug.de/job/SimplixCore/lastSuccessfulBuild/artifact/simplixcore-minecraft/simplixcore-minecraft-velocity/simplixcore-minecraft-velocity-plugin/target/SimplixCore-Velocity.jar";

  /**
   * Ensures that SimplixCore is installed on this server.
   *
   * @param plugin The plugin main class of the caller plugin
   * @return true if SimplixCore is installed or false otherwise
   */
  public static boolean ensureSimplixCore(
      @NonNull ProxyServer proxyServer,
      @NonNull PluginContainer plugin) {
    if (!proxyServer.getPluginManager().getPlugin("simplixcore").isPresent()) {
      registerInstaller(proxyServer, plugin);
      return false;
    }
    return true;
  }

  private static void registerInstaller(ProxyServer proxyServer, PluginContainer plugin) {
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
    proxyServer
        .getCommandManager()
        .register(
            proxyServer.getCommandManager().metaBuilder("simplix").build(),
            new SimplixCommand(SIMPLIX_DOWNLOAD_URL));
  }

  private static void log(@NonNull String message) {
    log.error(message);
  }

}
