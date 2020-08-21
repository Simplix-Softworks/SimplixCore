package dev.simplix.core.minecraft.bungeecord.quickstart;

import com.google.common.io.ByteStreams;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import lombok.NonNull;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;

final class SimplixCommand extends Command {

  private final String downloadAddress;

  public SimplixCommand(String downloadAddress) {
    super("simplix");
    this.downloadAddress = downloadAddress;
  }

  @Override
  public void execute(CommandSender commandSender, String[] strings) {
    if (!commandSender.hasPermission("simplix.install")) {
      return;
    }
    if (strings.length == 0) {
      commandSender.sendMessage("Use command \"simplix install\" to install SimplixCore.");
    } else {
      if (strings[0].equalsIgnoreCase("install")) {
        commandSender.sendMessage("Going to install SimplixCore to your BungeeCord server!");
        try {
          download(new URL(this.downloadAddress), new File("./plugins/SimplixCore-BungeeCord.jar"));
          commandSender.sendMessage("SimplixCore was installed. In order to work correctly, " +
                                    "SimplixCore needs some BungeeCord.jar patching to enable " +
                                    "full logging.");
          commandSender.sendMessage("You can do this manually using our graphical jar patcher tool "
                                    +
                                    "or you can type the command §esimplix patch §rto do this automatically.");
        } catch (Exception exception) {
          commandSender.sendMessage(
              "§cException while downloading SimplixCore! Check console for details.");
          ProxyServer
              .getInstance()
              .getLogger()
              .log(Level.SEVERE, "Exception while downloading SimplixCore", exception);
        }
      }
    }
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
    if(!file.createNewFile()) {
      throw new IOException("File could not be overridden");
    }
    try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
      fileOutputStream.write(ByteStreams.toByteArray(httpURLConnection.getInputStream()));
      fileOutputStream.flush();
    }
  }

}
