package dev.simplix.core.common.i18n;

import dev.simplix.core.common.CommonSimplixModule;
import dev.simplix.core.common.aop.Component;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Component(value = CommonSimplixModule.class, parent = LocalizationManagerFactory.class)
@Slf4j
public class SimpleLocalizationManagerFactory implements LocalizationManagerFactory {

  private static final String SIMPLIX = "[Simplix]";

  @Override
  public LocalizationManager create(File translationDirectory) {
    Map<Locale, Properties> propertiesMap = new HashMap<>();
    if (!translationDirectory.exists()) {
      throw new IllegalArgumentException("Directory does not exist");
    }
    if (!translationDirectory.isDirectory()) {
      throw new IllegalArgumentException("File is not a directory");
    }
    for (File file : translationDirectory.listFiles((dir, name) -> name.endsWith(".properties"))) {
      if (file.isDirectory()) {
        continue;
      }
      Optional<Locale> optionalLocale = findLocale(file.getName());
      if (!optionalLocale.isPresent()) {
        log.warn(SIMPLIX + " '" + file.getName() + "' is not a valid localization file name!");
        continue;
      }
      Locale locale = optionalLocale.get();
      try {
        Properties properties = loadPropertiesFromFile(file);
        propertiesMap.put(locale, properties);
      } catch (IOException ex) {
        log.warn(SIMPLIX + " Unable to load language file " + file.getAbsolutePath() + ": ", ex);
      }
    }
    return create0(propertiesMap);
  }

  @Override
  public LocalizationManager create(
      @NonNull String translationResourcesDirectory,
      @NonNull Class<?> classRef) {
    Map<Locale, Properties> propertiesMap = new HashMap<>();
    try {
      File codeSource = new File(classRef
          .getProtectionDomain()
          .getCodeSource()
          .getLocation()
          .toURI());
      if (!codeSource.getName().endsWith(".jar")) {
        throw new IllegalStateException("Reference class must be contained in a jar file!");
      }
      URI uri;
      if (codeSource.getAbsolutePath().startsWith("/")) { // UNIX
        uri = URI.create("jar:file:" + codeSource.getAbsolutePath());
      } else { // Windows
        uri = URI.create("jar:file:/" + codeSource.getAbsolutePath()
            .replace("\\", "/")
            .replace(" ", "%20"));
      }

      try (
          FileSystem fileSystem = FileSystems.newFileSystem(
              uri,
              Collections.emptyMap())) {
        try (Stream<Path> listFiles = Files.list(fileSystem.getPath(translationResourcesDirectory))) {
          listFiles.forEach(path -> {

            String fileName = path.getFileName().toString().substring(
                0,
                path.getFileName().toString().length() - 11);
            Optional<Locale> optionalLocale = findLocale(fileName);

            if (!optionalLocale.isPresent()) {
              log.warn(SIMPLIX + " '" + fileName + "' is not a valid localization file name!");
              return;
            }

            Locale locale = optionalLocale.get();

            try {
              Properties properties = loadPropertiesFromReader(Files.newBufferedReader(
                  path,
                  StandardCharsets.UTF_8));
              propertiesMap.put(locale, properties);
            } catch (IOException ex) {
              log.warn(SIMPLIX + " Cannot load language file " + path + " from resource: ", ex);
            }
          });
        }
      } catch (IOException ioException) {
        log.warn(SIMPLIX + " Cannot load language files from resource: ", ioException);
      }
    } catch (URISyntaxException uriSyntaxException) {
      // bug??
    }
    return create0(propertiesMap);
  }

  private Properties loadPropertiesFromFile(@NonNull File file) throws IOException {
    try (
        InputStreamReader streamReader = new InputStreamReader(new BufferedInputStream(
            new FileInputStream(file)), StandardCharsets.UTF_8)) {
      Properties properties = new Properties();
      properties.load(streamReader);
      return properties;
    }
  }

  private Properties loadPropertiesFromReader(@NonNull BufferedReader bufferedReader)
      throws IOException {
    Properties properties = new Properties();
    properties.load(bufferedReader);
    bufferedReader.close();
    return properties;
  }

  private LocalizationManager create0(@NonNull Map<Locale, Properties> propertiesMap) {
    Map<Locale, Map<String, String>> translations = new HashMap<>();
    for (Locale locale : propertiesMap.keySet()) {
      Properties properties = propertiesMap.get(locale);
      Enumeration<Object> keys = properties.keys();
      Map<String, String> localized = new HashMap<>();
      while (keys.hasMoreElements()) {
        Object key = keys.nextElement();
        if (!(key instanceof String)) {
          continue;
        }
        localized.put((String) key, properties.getProperty((String) key));
      }
      translations.put(locale, localized);
    }
    return new SimpleLocalizationManager(translations);
  }

  private Optional<Locale> findLocale(@NonNull String fileName) {
    try {
      if (fileName.endsWith(".properties")) {
        return Optional.of(new Locale(fileName.substring(0, fileName.length() - 11)));
      } else {
        return Optional.of(new Locale(fileName));
      }
    } catch (Exception exception) {
      return Optional.empty();
    }
  }

}
