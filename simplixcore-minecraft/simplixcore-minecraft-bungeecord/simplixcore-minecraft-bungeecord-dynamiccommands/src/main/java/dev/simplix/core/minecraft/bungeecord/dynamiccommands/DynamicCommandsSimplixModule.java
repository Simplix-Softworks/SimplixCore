package dev.simplix.core.minecraft.bungeecord.dynamiccommands;

import dev.simplix.core.common.aop.AbstractSimplixModule;
import lombok.extern.slf4j.Slf4j;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;

@Slf4j
public class DynamicCommandsSimplixModule extends AbstractSimplixModule {

  private final Plugin plugin;

  public DynamicCommandsSimplixModule(Plugin plugin) {
    this.plugin = plugin;
    registerComponentInterceptor(
        Command.class,
        command -> {
          log.info("[Simplix] Register command /" + command.getName());
          ProxyServer
              .getInstance()
              .getPluginManager()
              .registerCommand(plugin, command);
        });
  }

}
