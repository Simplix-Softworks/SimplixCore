package dev.simplix.core.common.deploader;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import lombok.Cleanup;
import lombok.Data;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
@NonNull
public class Dependencies {

  private Repository[] repositories;
  private Dependency[] dependencies;

  /**
   * @param appOwner Class the app should be loaded from
   * @return Optional if no dependencies.json could be found for the appOwner
   * @throws JsonParseException If the dependencies.json file is formatted invalidly
   */
  @SneakyThrows
  public static Optional<Dependencies> loadDependencies(@NonNull Class<?> appOwner)
      throws JsonParseException {
    @Cleanup
    InputStream inputStream = appOwner.getResourceAsStream("/dependencies.json");

    if (inputStream == null) {
      return Optional.empty();
    }
    @Cleanup
    InputStreamReader reader = new InputStreamReader(
        inputStream,
        StandardCharsets.UTF_8);

    return Optional.ofNullable(new GsonBuilder().create().fromJson(reader, Dependencies.class));
  }
}
