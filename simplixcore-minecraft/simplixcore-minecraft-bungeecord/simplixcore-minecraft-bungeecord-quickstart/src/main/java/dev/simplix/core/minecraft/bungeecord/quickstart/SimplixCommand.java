package dev.simplix.core.minecraft.bungeecord.quickstart;

import com.google.common.io.ByteStreams;
import dev.simplix.core.minecraft.bungeecord.slf4j.ServiceProviderPatcher;
import io.netty.util.internal.PlatformDependent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
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
          exception.printStackTrace();
        }
      } else if (strings[0].equalsIgnoreCase("patch")) {
        if (PlatformDependent.isWindows()) {
          commandSender.sendMessage("§cThis feature is not supported on Windows. Please use our " +
                                    "graphical jar patcher: https://simplixsoft.com/jarpatcher");
          return;
        }
        commandSender.sendMessage("Going to patch your BungeeCord.jar!");
        try {
          File bungeeJar = new File(ProxyServer.class
              .getProtectionDomain()
              .getCodeSource()
              .getLocation()
              .toURI());
          ServiceProviderPatcher.patchJarUnix(bungeeJar);
          commandSender.sendMessage("Patching done. Please restart your BungeeCord server!");
        } catch (Exception exception) {
          commandSender.sendMessage("There was an exception while patching your jar file. Please "
                                    + "check console for details.");
          exception.printStackTrace();
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
    file.createNewFile();
    try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
      fileOutputStream.write(ByteStreams.toByteArray(httpURLConnection.getInputStream()));
      fileOutputStream.flush();
    }
  }

}
