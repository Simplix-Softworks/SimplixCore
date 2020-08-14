package dev.simplix.core.minecraft.bungeecord.slf4j;

import com.google.common.io.ByteStreams;
import io.netty.util.internal.PlatformDependent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.md_5.bungee.api.ProxyServer;
import org.slf4j.spi.SLF4JServiceProvider;

public final class ServiceProviderPatcher {

  private static final Set<String> TO_BE_COPIED = new HashSet<>();

  static {
    TO_BE_COPIED.add("/org/slf4j/event/EventConstants.class");
    TO_BE_COPIED.add("/org/slf4j/event/EventRecodingLogger.class");
    TO_BE_COPIED.add("/org/slf4j/event/Level.class");
    TO_BE_COPIED.add("/org/slf4j/event/LoggingEvent.class");
    TO_BE_COPIED.add("/org/slf4j/event/SubstituteLoggingEvent.class");
    TO_BE_COPIED.add("/org/slf4j/helpers/BasicMarker.class");
    TO_BE_COPIED.add("/org/slf4j/helpers/BasicMarkerFactory.class");
    TO_BE_COPIED.add("/org/slf4j/helpers/BasicMDCAdapter$1.class");
    TO_BE_COPIED.add("/org/slf4j/helpers/BasicMDCAdapter.class");
    TO_BE_COPIED.add("/org/slf4j/helpers/FormattingTuple.class");
    TO_BE_COPIED.add("/org/slf4j/helpers/MarkerIgnoringBase.class");
    TO_BE_COPIED.add("/org/slf4j/helpers/MessageFormatter.class");
    TO_BE_COPIED.add("/org/slf4j/helpers/NamedLoggerBase.class");
    TO_BE_COPIED.add("/org/slf4j/helpers/NOPLogger.class");
    TO_BE_COPIED.add("/org/slf4j/helpers/NOPLoggerFactory.class");
    TO_BE_COPIED.add("/org/slf4j/helpers/NOPMDCAdapter.class");
    TO_BE_COPIED.add("/org/slf4j/helpers/NOPServiceProvider.class");
    TO_BE_COPIED.add("/org/slf4j/helpers/SubstitureServiceProvider.class");
    TO_BE_COPIED.add("/org/slf4j/helpers/SubstituteLogger.class");
    TO_BE_COPIED.add("/org/slf4j/helpers/SubstituteLoggerFactory.class");
    TO_BE_COPIED.add("/org/slf4j/helpers/Util$1.class");
    TO_BE_COPIED.add("/org/slf4j/helpers/Util$ClassContextSecurityManager.class");
    TO_BE_COPIED.add("/org/slf4j/helpers/Util.class");
    TO_BE_COPIED.add("/org/slf4j/jul/JDK14LoggerAdapter.class");
    TO_BE_COPIED.add("/org/slf4j/spi/LocationAwareLogger.class");
    TO_BE_COPIED.add("/org/slf4j/spi/LoggerFactoryBinder.class");
    TO_BE_COPIED.add("/org/slf4j/spi/MarkerFactoryBinder.class");
    TO_BE_COPIED.add("/org/slf4j/spi/MDCAdapter.class");
    TO_BE_COPIED.add("/org/slf4j/spi/SLF4JServiceProvider.class");
    TO_BE_COPIED.add("/org/slf4j/ILoggerFactory.class");
    TO_BE_COPIED.add("/org/slf4j/IMarkerFactory.class");
    TO_BE_COPIED.add("/org/slf4j/Logger.class");
    TO_BE_COPIED.add("/org/slf4j/LoggerFactory.class");
    TO_BE_COPIED.add("/org/slf4j/LoggerFactoryFriend.class");
    TO_BE_COPIED.add("/org/slf4j/Marker.class");
    TO_BE_COPIED.add("/org/slf4j/MarkerFactory.class");
    TO_BE_COPIED.add("/org/slf4j/MDC$1.class");
    TO_BE_COPIED.add("/org/slf4j/MDC$MDCCloseable.class");
    TO_BE_COPIED.add("/org/slf4j/MDC.class");
    TO_BE_COPIED.add("/dev/simplix/core/minecraft/spigot/slf4j/BungeeLoggerFactory.class");
    TO_BE_COPIED.add("/dev/simplix/core/minecraft/spigot/slf4j/BungeeLoggerServiceProvider.class");
  }

  public static void patchJarUnix(File file) {
    Map<String, String> env = new HashMap<>();
    env.put("create", "true");
    URI uri;
    if (!PlatformDependent.isWindows()) {
      uri = URI.create("jar:file:" + file.getAbsolutePath());
    } else {
      uri = URI.create("jar:file:/" + file.getAbsolutePath().replace("\\", "/"));
    }
    try (FileSystem zipfs = FileSystems.newFileSystem(uri, env)) {
      Path path = zipfs.getPath("/META-INF/services/" + SLF4JServiceProvider.class.getName());
      if (Files.exists(path)) {
        Files.delete(path);
      }
      Files.createDirectories(path.getParent());
      Files.write(
          path,
          BungeeLoggerServiceProvider.class.getName().getBytes(StandardCharsets.UTF_8));
      patchResources(zipfs);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static void patchResources(FileSystem zipfs) throws IOException {
    for (String resource : TO_BE_COPIED) {
      Path path = zipfs.getPath(resource);
      if (Files.exists(path)) {
        Files.delete(path);
      }
      Files.createDirectories(path.getParent());
      InputStream resourceStream = BungeeLoggerServiceProvider.class.getResourceAsStream(
          resource);
      if (resourceStream == null) {
        continue;
      }
      Files.createDirectories(path.getParent());
      Files.write(
          path,
          ByteStreams.toByteArray(resourceStream));
    }
  }

  public static boolean needsPatching() {
    InputStream stream = ProxyServer.class.getResourceAsStream("/META-INF/services/"
                                                               + SLF4JServiceProvider.class
                                                                   .getName());
    if (stream == null) {
      return true;
    }
    try (InputStream inputStream = stream) {
      String binding = new String(ByteStreams.toByteArray(inputStream), StandardCharsets.UTF_8);
      return !binding.equals(BungeeLoggerServiceProvider.class.getName());
    } catch (IOException e) {
      e.printStackTrace();
    }
    return false;
  }

}
