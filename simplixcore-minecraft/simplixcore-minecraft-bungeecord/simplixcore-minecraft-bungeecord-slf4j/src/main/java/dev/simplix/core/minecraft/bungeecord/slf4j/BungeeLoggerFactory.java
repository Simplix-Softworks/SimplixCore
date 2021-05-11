package dev.simplix.core.minecraft.bungeecord.slf4j;

import java.lang.reflect.Constructor;
import lombok.NonNull;
import net.md_5.bungee.api.ProxyServer;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import dev.simplix.slf4j.impl.JDK14LoggerAdapter;

public final class BungeeLoggerFactory implements ILoggerFactory {

  private final JDK14LoggerAdapter bungeeLoggerAdapter;

  public BungeeLoggerFactory() {
    this.bungeeLoggerAdapter = createLoggerAdapter(ProxyServer.getInstance().getLogger());
    ProxyServer
        .getInstance()
        .getLogger()
        .info("[Simplix] Slf4j logging configured for BungeeCord");
  }

  private JDK14LoggerAdapter createLoggerAdapter(@NonNull java.util.logging.Logger logger) {
    try {
      Constructor<JDK14LoggerAdapter> constructor = JDK14LoggerAdapter.class.getDeclaredConstructor(
          java.util.logging.Logger.class);
      constructor.setAccessible(true);
      return constructor.newInstance(logger);
    } catch (ReflectiveOperationException e) {
      ProxyServer
          .getInstance()
          .getLogger()
          .severe("[Simplix] Slf4j BungeeCord JUL bridge not working!");
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public Logger getLogger(@NonNull String string) {
    return this.bungeeLoggerAdapter;
  }

}
