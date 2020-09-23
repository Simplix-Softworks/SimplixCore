package dev.simplix.core.minecraft.spigot.quickstart;

import com.google.common.io.ByteStreams;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

@Slf4j
public final class SimplixCommand extends Command {

  private final String downloadAddress;

  public SimplixCommand(String downloadAddress) {
    super("simplix");
    this.downloadAddress = downloadAddress;
  }

  @Override
  public boolean execute(CommandSender commandSender, String commandLabel, String[] strings) {
    if (!commandSender.hasPermission("simplix.install")) {
      return false;
    }
    if (strings.length == 0) {
      commandSender.sendMessage("Use command \"simplix install\" to install SimplixCore.");
    } else {
      if (!strings[0].equalsIgnoreCase("install")) {
        return false;
      }
      commandSender.sendMessage("Going to install SimplixCore to your Spigot server!");
      try {
        download(new URL(this.downloadAddress), new File("./plugins/SimplixCore-Spigot.jar"));
        commandSender.sendMessage("Installation done. Please restart your Spigot server.");
      } catch (Exception exception) {
        commandSender.sendMessage(
            "§cException while downloading SimplixCore! Check console for details.");
        log.error("Exception while downloading SimplixCore", exception);
      }
    }
    return false;
  }

  private void download(@NonNull URL url, @NonNull File file) throws IOException {
    URLConnection urlConnection = url.openConnection();
    if (!(urlConnection instanceof HttpURLConnection)) {
      throw new IllegalArgumentException("Unsupported protocol " + url.getProtocol());
    }
    file.getParentFile().mkdirs();
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

}
