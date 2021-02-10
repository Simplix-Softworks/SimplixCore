package dev.simplix.core.minecraft.velocity.quickstart;

import com.google.common.io.ByteStreams;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

@Slf4j
public final class SimplixCommand implements SimpleCommand {

  private final String downloadAddress;

  public SimplixCommand(@NonNull String downloadAddress) {
    this.downloadAddress = downloadAddress;
  }

  @Override
  public void execute(
      CommandSource source,
      String @org.checkerframework.checker.nullness.qual.NonNull [] args) {

  }

  private void download(@NonNull URL url, @NonNull File file) throws IOException {
    URLConnection urlConnection = url.openConnection();
    if (!(urlConnection instanceof HttpURLConnection)) {
      throw new IllegalArgumentException("Unsupported protocol " + url.getProtocol());
    }

    HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
    if (file.exists()) {
      return;
    }
    if (!file.createNewFile()) {
      throw new IOException("File could not be overridden");
    }
    try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
      fileOutputStream.write(ByteStreams.toByteArray(httpURLConnection.getInputStream()));
      fileOutputStream.flush();
    }
  }

  @Override
  public void execute(Invocation invocation) {
    CommandSource commandSender = invocation.source();
    String[] strings = invocation.arguments();
    if (!commandSender.hasPermission("simplix.install")) {
      return;
    }
    if (strings.length == 0) {
      commandSender.sendMessage(LegacyComponentSerializer
          .legacySection()
          .deserialize("Use command \"simplix install\" to install SimplixCore."));
    } else {
      if (strings[0].equalsIgnoreCase("install")) {
        commandSender.sendMessage(LegacyComponentSerializer
            .legacySection()
            .deserialize("Going to install SimplixCore to your Velocity server!"));
        try {
          download(new URL(this.downloadAddress), new File("./plugins/SimplixCore-Velocity.jar"));
          commandSender.sendMessage(LegacyComponentSerializer
              .legacySection()
              .deserialize("SimplixCore was installed successfully."));
        } catch (Exception exception) {
          commandSender.sendMessage(LegacyComponentSerializer.legacySection().deserialize(
              "§cException while downloading SimplixCore! Check console for details."));
          log
              .error("Exception while downloading SimplixCore", exception);
        }
      }
    }
  }

}
