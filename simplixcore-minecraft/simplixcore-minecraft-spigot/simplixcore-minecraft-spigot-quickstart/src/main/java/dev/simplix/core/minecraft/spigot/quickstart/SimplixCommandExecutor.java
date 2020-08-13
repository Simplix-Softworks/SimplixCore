package dev.simplix.core.minecraft.spigot.quickstart;

import com.google.common.io.ByteStreams;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import lombok.NonNull;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

final class SimplixCommandExecutor implements CommandExecutor {

  private final String downloadAddress;

  public SimplixCommandExecutor(String downloadAddress) {
    this.downloadAddress = downloadAddress;
  }

  @Override
  public boolean onCommand(
      CommandSender commandSender, Command command, String s, String[] strings) {
    if(commandSender.hasPermission("simplix.install")) {
      if(strings.length == 0) {
        commandSender.sendMessage("Use command \"simplix install\" to install SimplixCore.");
      } else {
        if(strings[0].equalsIgnoreCase("install")) {
          commandSender.sendMessage("Going to install SimplixCore to your Spigot server!");
          try {
            download(new URL(downloadAddress), new File("./plugins/SimplixCore-Spigot.jar"));
            commandSender.sendMessage("Installation done. Please restart your Spigot server.");
          } catch (Exception exception) {
            commandSender.sendMessage("§cError while downloading SimplixCore! Check console for details.");
            exception.printStackTrace();
          }
        }
      }
    }
    return false;
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
    file.createNewFile();
    try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
      fileOutputStream.write(ByteStreams.toByteArray(httpURLConnection.getInputStream()));
      fileOutputStream.flush();
    }
  }

}
