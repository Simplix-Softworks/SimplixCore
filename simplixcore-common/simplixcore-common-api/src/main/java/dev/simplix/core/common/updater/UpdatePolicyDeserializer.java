package dev.simplix.core.common.updater;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public final class UpdatePolicyDeserializer implements JsonDeserializer<UpdatePolicy> {

  @Override
  public UpdatePolicy deserialize(
      JsonElement jsonElement,
      Type type,
      JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
    JsonObject object = jsonElement.getAsJsonObject();
    try {
      AtomicReference<Exception> atomicException = new AtomicReference<>();
      AtomicReference<VersionFetcher> atomicFetcher = new AtomicReference<>();
      AtomicReference<UpdateDownloader> atomicDownloader = new AtomicReference<>();

      optional(object, "versionFetcher").ifPresent(fetcherElement -> {
        try {
          Class<?> fetcherClass = Class.forName(object.get("versionFetcherClass").getAsString());
          if (!VersionFetcher.class.isAssignableFrom(fetcherClass)) {
            throw new IllegalStateException("The class "
                                            + fetcherClass.getName()
                                            + " must implement "
                                            + VersionFetcher.class.getName());
          }
          atomicFetcher.set(jsonDeserializationContext.deserialize(fetcherElement, fetcherClass));
        } catch (Exception exception) {
          atomicException.set(exception);
        }
      });
      optional(object, "downloader").ifPresent(downloaderElement -> {
        try {
          Class<?> downloaderClass = Class.forName(object.get("downloaderClass").getAsString());
          if (!UpdateDownloader.class.isAssignableFrom(downloaderClass)) {
            throw new IllegalStateException("The class "
                                            + downloaderClass.getName()
                                            + " must implement "
                                            + UpdateDownloader.class.getName());
          }
          atomicDownloader.set(jsonDeserializationContext.deserialize(downloaderElement, downloaderClass));
        } catch (Exception exception) {
          atomicException.set(exception);
        }
      });
      if(atomicException.get() != null) {
        throw atomicException.get();
      }


      return new UpdatePolicy(
          object.get("versionPattern").getAsString(), atomicDownloader.get(), atomicFetcher.get());
    } catch (Exception exception) {
      throw new JsonParseException(exception);
    }
  }

  private Optional<JsonElement> optional(JsonObject object, String name) {
    return Optional.ofNullable(object.get(name));
  }

}
