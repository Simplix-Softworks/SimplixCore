package dev.simplix.core.minecraft.velocity.dynamiccommands;

import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.simplix.core.common.aop.AbstractSimplixModule;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DynamicCommandsSimplixModule extends AbstractSimplixModule {

  public DynamicCommandsSimplixModule(@NonNull ProxyServer proxyServer) {
    registerComponentInterceptor(Command.class, obj -> {
      if (obj.getClass().isAnnotationPresent(CommandMeta.class)) {
        CommandMeta meta = obj.getClass().getAnnotation(CommandMeta.class);
        com.velocitypowered.api.command.CommandMeta commandMeta = proxyServer
            .getCommandManager()
            .metaBuilder(meta.name())
            .aliases(meta.aliases())
            .build();
        proxyServer.getCommandManager().register(commandMeta, obj);
        log.info("[Simplix] Register command /" + meta.name());
      } else {
        log.warn("[Simplix] Cannot register command "
                 + obj.getClass().getName()
                 + ": Please annotate your command class with @CommandMeta");
      }
    });
  }

}
