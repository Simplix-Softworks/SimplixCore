package dev.simplix.core.common.providers.impl;

import com.google.inject.Inject;
import de.leonhard.storage.util.FileUtils;
import dev.simplix.core.common.ApplicationInfo;
import dev.simplix.core.common.CommonSimplixModule;
import dev.simplix.core.common.TimeUtil;
import dev.simplix.core.common.aop.Component;
import dev.simplix.core.common.providers.ExceptionHandler;
import dev.simplix.core.common.providers.PluginManager;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.ClosedByInterruptException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

@Component(value = CommonSimplixModule.class, parent = ExceptionHandler.class)
public final class FileBasedExceptionHandler implements ExceptionHandler {

  private final ApplicationInfo applicationInfo;
  private final File logFile;
  private final PluginManager pluginManager;

  @Inject
  public FileBasedExceptionHandler(
      @NonNull ApplicationInfo applicationInfo,
      @NonNull PluginManager pluginManager) {
    this.applicationInfo = applicationInfo;
    // Ensures the file & the parent directory are always existent
    logFile = new File(applicationInfo.workingDirectory(), "debug.log");
    this.pluginManager = pluginManager;
    try {
      FileUtils.getAndMake(logFile);
    } catch (final Throwable throwable) {
      System.err.println("Exception while creating debug file!");
      throwable.printStackTrace();
    }
  }

  private static void fill(List<String> list, String... messages) {
    list.addAll(Arrays.asList(messages));
  }

  private static void write(File file, Collection<String> lines) {
    write(file, lines, StandardOpenOption.APPEND);
  }

  /**
   * Write the given lines to file
   *
   * @param to
   * @param lines
   * @param options
   */
  private static void write(File to, Collection<String> lines, StandardOpenOption... options) {
    try {
      final Path path = Paths.get(to.toURI());

      try {
        Files.write(path, lines, StandardCharsets.UTF_8, options);

      } catch (final ClosedByInterruptException ex) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(to, true))) {
          for (final String line : lines) {
            bw.append(System.lineSeparator() + line);
          }

        } catch (final IOException e) {
          e.printStackTrace();
        }
      }

    } catch (final Exception ex) {
      System.out.println("Failed to write to " + to);

      ex.printStackTrace(); // do not throw our exception since it would cause an infinite loop if there is a problem due to error writing
    }
  }

  // ----------------------------------------------------------------------------------------------------
  // Overridden methods
  // ----------------------------------------------------------------------------------------------------

  @Override
  public void saveError(@Nullable Throwable throwable, @NonNull String... messages) {
    // Something went wrong on startup
    if (!logFile.exists()) {
      return;
    }
    if (throwable == null) {
      return;
    }

    final List<String> lines = new ArrayList<>();
    final String header = applicationInfo.name()
                          + " "
                          + applicationInfo.version()
                          + " encountered "
                          + "a(n)" + (throwable.getClass().getSimpleName());

    // Write out header and server info
    fill(
        lines,
        "------------------------------------[ "
        + TimeUtil.formattedDate()
        + " ]-----------------------------------",
        header,
        "Running Java " + System.getProperty("java.version"),
        "Plugins: " + pluginManager.enabledPlugins(),
        "----------------------------------------------------------------------------------------------");

    // Write additional data
    if (messages != null && String.join("", messages).isEmpty()) {
      fill(lines, "\nMore Information: ");
      fill(lines, messages);
    }

    { // Write the stack trace

      do {
        // Write the error header
        fill(
            lines,
            throwable.getClass().getSimpleName() + " " +
            (
                throwable.getMessage() == null
                    ? "(Unknown cause)"
                    : throwable.getMessage()));

        int count = 0;

        for (final StackTraceElement el : throwable.getStackTrace()) {
          count++;

          final String trace = el.toString();

          if (count > 6 && trace.startsWith("net.minecraft.server")) {
            break;
          }

          fill(lines, "\t at " + el.toString());
        }
      } while ((throwable = throwable.getCause()) != null);
    }

    fill(
        lines,
        "----------------------------------------------------------------------------------------------",
        System.lineSeparator());

    // Log to the console
    System.out.println(header
                       + "! Please check your error.log and report this issue with the information in that file.");

    write(logFile, lines);
  }
}
