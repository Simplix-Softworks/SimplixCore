package dev.simplix.core.common.i18n;

import dev.simplix.core.common.CommonSimplixModule;
import dev.simplix.core.common.aop.Component;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.*;
import lombok.extern.slf4j.Slf4j;

@Component(value = CommonSimplixModule.class, parent = LocalizationManagerFactory.class)
@Slf4j
public class SimpleLocalizationManagerFactory implements LocalizationManagerFactory {

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
      Locale locale = new Locale(file.getName().substring(0, file.getName().length() - 11));
      try {
        Properties properties = loadPropertiesFromFile(file);
        propertiesMap.put(locale, properties);
      } catch (IOException ex) {
        log.warn("[Simplix] Unable to load language file " + file.getAbsolutePath() + ": ", ex);
      }
    }
    return create0(propertiesMap);
  }

  @Override
  public LocalizationManager create(String translationResourcesDirectory, Class<?> classRef) {
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
      try (
          FileSystem fileSystem = FileSystems.newFileSystem(
              codeSource.toURI(),
              Collections.emptyMap())) {
        Files.list(fileSystem.getPath(translationResourcesDirectory))
            .filter(path -> path.getFileName().endsWith(".properties"))
            .forEach(path -> {
              Locale locale = new Locale(path.getFileName().toString().substring(
                  0,
                  path.getFileName().toString().length() - 11));
              try {
                Properties properties = loadPropertiesFromReader(Files.newBufferedReader(
                    path,
                    StandardCharsets.UTF_8));
                propertiesMap.put(locale, properties);
              } catch (IOException ex) {
                log.warn("[Simplix] Cannot load language file " + path + " from resource: ", ex);
              }
            });
      } catch (IOException ex) {
        log.warn("[Simplix] Cannot load language files from resource: ", ex);
      }
    } catch (URISyntaxException ex) {
      // bug??
    }
    return create0(propertiesMap);
  }

  private Properties loadPropertiesFromFile(File file) throws IOException {
    try (
        InputStreamReader streamReader = new InputStreamReader(new BufferedInputStream(
            new FileInputStream(file)), StandardCharsets.UTF_8)) {
      Properties properties = new Properties();
      properties.load(streamReader);
      return properties;
    }
  }

  private Properties loadPropertiesFromReader(BufferedReader bufferedReader) throws IOException {
    Properties properties = new Properties();
    properties.load(bufferedReader);
    bufferedReader.close();
    return properties;
  }

  private LocalizationManager create0(Map<Locale, Properties> propertiesMap) {
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

}
