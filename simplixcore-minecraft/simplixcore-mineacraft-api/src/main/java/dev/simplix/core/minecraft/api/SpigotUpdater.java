package dev.simplix.core.minecraft.api;

import de.leonhard.storage.util.Valid;
import dev.simplix.core.common.ApplicationInfo;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import lombok.Getter;

/**
 * A simple class performing an update check for Spigot free and premium resources
 */
public class SpigotUpdater implements Runnable {

  /**
   * The Spigot resource ID for your plugin
   */
  @Getter
  private final int resourceId;
  private final boolean download;
  private final ApplicationInfo applicationInfo;
  private final File updateFolder;
  /**
   * Did we found that a newer version is available?
   */
  @Getter
  private boolean newVersionAvailable = false;
  /**
   * The newer version
   */
  @Getter
  private String newVersion = "";

  /**
   * Initializes the new instance to check but not to download updates
   *
   * @param resourceId the id of the plugin at Spigot's page
   */

  public SpigotUpdater(
      ApplicationInfo applicationInfo,
      File updateFolder,
      final int resourceId) {
    this(applicationInfo, updateFolder, resourceId, false);
  }

  /**
   * Initializes a new instance.
   *
   * @param resourceId the id of the plugin at Spigot's page.
   * @param download   should we attempt to download new versions automatically? PLEASE NOTE YOU CAN
   *                   ONLY DOWNLOAD FREE RESOURCES FROM SPIGOT NOT PREMIUM
   */
  public SpigotUpdater(
      ApplicationInfo applicationInfo,
      File updateFolder,
      final int resourceId,
      final boolean download) {
    this.resourceId = resourceId;
    this.download = download;
    this.applicationInfo = applicationInfo;
    this.updateFolder = updateFolder;
  }

  /**
   * The main method in which the update check takes place.
   */
  @Override
  public void run() {
    if (resourceId == -1) {
      return;
    }

    final String currentVersion = applicationInfo.version();

    if (!canUpdateFrom(currentVersion)) {
      return;
    }

    try {
      HttpURLConnection connection = (HttpURLConnection) new URL(
          "https://api.spigotmc.org/legacy/update.php?resource=" + resourceId).openConnection();
      connection.setRequestMethod("GET");

      try (final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
        final String line = reader.readLine();

        newVersion = line;
      }

      if (newVersion.isEmpty()) {
        return;
      }

      if (isNewerVersion(currentVersion, newVersion) && canUpdateTo(newVersion)) {
        newVersionAvailable = true;

        if (download) {
          final ReadableByteChannel channel;

          connection = (HttpURLConnection) new URL("https://api.spiget.org/v2/resources/"
                                                   + resourceId
                                                   + "/download").openConnection();
          connection.setRequestProperty("User-Agent", applicationInfo.name());
          Valid.checkBoolean(
              connection.getResponseCode() == 200,
              "Downloading update for "
              + applicationInfo.name()
              + " returned "
              + connection.getResponseCode()
              + ", aborting.");

          channel = Channels.newChannel(connection.getInputStream());

          final File destination = new File(
              updateFolder,
              applicationInfo.name() + "-" + newVersion + ".jar");
          final FileOutputStream output = new FileOutputStream(destination);

          output.getChannel().transferFrom(channel, 0, Long.MAX_VALUE);
          output.flush();
          output.close();

          System.out.println(getDownloadMessage());
        } else {
          System.out.println(getNotifyMessage());
        }
      }

    } catch (final UnknownHostException ex) {
      System.out.println("Could not check for update from " + ex.getMessage() + ".");

    } catch (final IOException ex) {
//      if (ex.getMessage().startsWith("Server returned HTTP response code: 403")) {
//        // no permission
//      } else if (ex.getMessage().startsWith("Server returned HTTP response code:"))
//        Common.log(
//            "Could not check for update, SpigotMC site appears to be down (or unaccessible): "
//            + ex.getMessage());
//      else
//        Common.error(
//            ex,
//            "IOException performing update from SpigotMC.org check for " + SimplePlugin.getNamed());
//
//    } catch (final Exception ex) {
//      Common.error(
//          ex,
//          "Unknown error performing update from SpigotMC.org check for " + SimplePlugin.getNamed());
    }
  }

  /**
   * Return if the current version is suitable for upgrading from.
   * <p>
   * By default we do not update if it contains SNAPSHOT or DEV.
   *
   * @param currentVersion
   * @return
   */
  protected boolean canUpdateFrom(final String currentVersion) {
    return !currentVersion.contains("SNAPSHOT") && !currentVersion.contains("DEV");
  }

  /**
   * Return if the new version is suitable for upgrading to.
   * <p>
   * By default we do not update if it contains SNAPSHOT or DEV.
   *
   * @param newVersion
   * @return
   */
  protected boolean canUpdateTo(final String newVersion) {
    return !newVersion.contains("SNAPSHOT") && !newVersion.contains("DEV");
  }

  /**
   * Returns true if the remote version is greater than the current version
   *
   * @param current
   * @param remote
   * @return
   */
  private boolean isNewerVersion(final String current, final String remote) {
    if (remote.contains("-LEGACY")) {
      return false;
    }

    String[] currParts = removeTagsInNumber(current).split("\\.");
    String[] remoteParts = removeTagsInNumber(remote).split("\\.");

    if (currParts.length != remoteParts.length) {
      final boolean olderIsLonger = currParts.length > remoteParts.length;
      final String[] modifiedParts = new String[olderIsLonger
          ? currParts.length
          : remoteParts.length];

      for (int i = 0; i < (olderIsLonger ? currParts.length : remoteParts.length); i++) {
        modifiedParts[i] = olderIsLonger
            ? remoteParts.length > i ? remoteParts[i] : "0"
            : currParts.length > i ? currParts[i] : "0";
      }

      if (olderIsLonger) {
        remoteParts = modifiedParts;
      } else {
        currParts = modifiedParts;
      }
    }

    for (int i = 0; i < currParts.length; i++) {
      if (Integer.parseInt(currParts[i]) > Integer.parseInt(remoteParts[i])) {
        return false;
      }

      if (Integer.parseInt(remoteParts[i]) > Integer.parseInt(currParts[i])) {
        return true;
      }
    }

    return false;
  }

  /**
   * Removes some tags such as -BETA in the current/new version number by splitting the version by
   * "-" and then returning the first part
   *
   * @param raw
   * @return
   */
  protected String removeTagsInNumber(final String raw) {
    return raw.split("\\-")[0];
  }

  public final String getNotifyMessage() {
    return "New plugin version is available!";
  }

  public final String getDownloadMessage() {
    return "Plugin downloaded plugin!";
  }

  /**
   * Used to replace variables in the update log/notify/downloaded message such as {new} {current}
   * and {plugin.name}
   *
   * @param message
   * @return
   */
  protected String replaceVariables(final String message) {
    return message
        .replace("{resourceId}", resourceId + "") // DEPRECATED
        .replace("{resource_id}", resourceId + "")
        .replace("{plugin.name}", applicationInfo.name()) // DEPRECATED
        .replace("{plugin_name}", applicationInfo.name())
        .replace("{new}", newVersion)
        .replace("{curr}", applicationInfo.version()) // DEPRECATED
        .replace("{current}", applicationInfo.name())
        .replace("{user_id}", "%%__USER__%%");
  }
}

