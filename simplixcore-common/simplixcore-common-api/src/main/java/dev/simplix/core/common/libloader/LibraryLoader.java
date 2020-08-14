package dev.simplix.core.common.libloader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.simplix.core.common.aop.SimplixApplication;
import dev.simplix.core.common.inject.SimplixInstaller;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LibraryLoader {

  private final Gson gson = new GsonBuilder().create();

  public void loadLibraries(@NonNull File directory) {
    if (!directory.exists()) {
      directory.mkdirs();
    }

    for (File file : directory.listFiles((dir, name) -> name.endsWith(".jar"))) {
      try {
        LibraryClassLoader classLoader = new LibraryClassLoader(file.toURI().toURL());
        InputStream stream = classLoader.getResourceAsStream("/library.json");
        if (stream == null) {
          log.warn("[Simplix | LibLoader] "
                   + file.getName()
                   + " does not contain a library.json file!");
          continue;
        }

        try (
            InputStreamReader inputStreamReader = new InputStreamReader(
                stream,
                StandardCharsets.UTF_8)) {
          LibraryDescription libraryDescription = this.gson.fromJson(
              inputStreamReader,
              LibraryDescription.class);
          Class<?> mainClass = classLoader.loadClass(libraryDescription.mainClass());
          if (mainClass.isAnnotationPresent(SimplixApplication.class)) {
            SimplixInstaller.instance().register(mainClass);
          }
        }
      } catch (Exception ex) {
        log.info("[Simplix | LibLoader] Unable to load library " + file.getName());
      }
    }
  }

}
