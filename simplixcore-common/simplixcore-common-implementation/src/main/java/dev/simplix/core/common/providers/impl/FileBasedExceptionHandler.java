package dev.simplix.core.common.providers.impl;

import com.google.inject.Inject;
import de.leonhard.storage.util.FileUtils;
import dev.simplix.core.common.ApplicationInfo;
import dev.simplix.core.common.CommonSimplixModule;
import dev.simplix.core.common.TimeFormatUtil;
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
import java.util.*;
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
    this.logFile = new File(applicationInfo.workingDirectory(), "debug.log");
    this.pluginManager = pluginManager;
    try {
      FileUtils.getAndMake(this.logFile);
    } catch (final Throwable throwable) {
      System.err.println("Exception while creating debug file!");
      throwable.printStackTrace();
    }
  }

  private static void write(File file, Collection<String> lines) {
    try {
      final Path path = Paths.get(file.toURI());

      try {
        Files.write(path, lines, StandardCharsets.UTF_8, StandardOpenOption.APPEND);

      } catch (final ClosedByInterruptException ex) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
          for (final String line : lines) {
            bw.append(System.lineSeparator() + line);
          }

        } catch (final IOException ioException) {
          ioException.printStackTrace();
        }
      }

    } catch (final Exception exception) {
      System.out.println("Failed to write to " + file);
      exception.printStackTrace();
    }
  }

  // ----------------------------------------------------------------------------------------------------
  // Overridden methods
  // ----------------------------------------------------------------------------------------------------

  @Override
  public void saveError(@Nullable Throwable throwable, @NonNull String... messages) {
    // Something went wrong on startup
    if (!this.logFile.exists()) {
      return;
    }
    if (throwable == null) {
      return;
    }

    final String header = this.applicationInfo.name()
                          + " "
                          + this.applicationInfo.version()
                          + " encountered "
                          + "a(n)" + (throwable.getClass().getSimpleName());

    // Write out header and server info
    final List<String> lines = new ArrayList<>(Arrays.asList(
        "------------------------------------[ "
        + TimeFormatUtil.calculateCurrentDateFormatted()
        + " ]-----------------------------------",
        header,
        "Running Java " + System.getProperty("java.version"),
        "Plugins: " + this.pluginManager.enabledPlugins(),
        "----------------------------------------------------------------------------------------------"));

    // Write additional data
    if (messages != null && String.join("", messages).isEmpty()) {
      lines.addAll(Collections.singletonList("\nMore Information: "));
      lines.addAll(Arrays.asList(messages));
    }

    { // Write the stack trace

      do {
        // Write the error header
        String[] messages1 = new String[]{
            throwable.getClass().getSimpleName() + " " +
            (
                throwable.getMessage() == null
                    ? "(Unknown cause)"
                    : throwable.getMessage())};
        lines.addAll(Arrays.asList(messages1));

        int count = 0;

        for (final StackTraceElement el : throwable.getStackTrace()) {
          count++;

          final String trace = el.toString();

          if (count > 6 && trace.startsWith("net.minecraft.server")) {
            break;
          }

          lines.addAll(Collections.singletonList("\t at " + el.toString()));
        }
      } while ((throwable = throwable.getCause()) != null);
    }

    lines.addAll(Arrays.asList(
        "----------------------------------------------------------------------------------------------",
        System.lineSeparator()));

    // Log to the console
    System.out.println(header
                       + "! Please check your error.log and report this issue with the information in that file.");

    write(this.logFile, lines);
  }
}
