package dev.simplix.core.config.injector;

import de.leonhard.storage.internal.DataStorage;
import de.leonhard.storage.internal.FlatFile;
import de.leonhard.storage.internal.serialize.LightningSerializable;
import de.leonhard.storage.internal.serialize.LightningSerializer;
import java.lang.annotation.Annotation;
import lombok.NonNull;

public abstract class AbstractSettingsInjector<C extends Annotation, F extends Annotation> extends
    AbstractInjector<C, F> {

  private final DataStorage dataStorage;

  protected AbstractSettingsInjector(
      Class<C> classAnnotationClass,
      Class<F> fieldAnnotationClass,
      DataStorage dataStorage) {
    super(classAnnotationClass, fieldAnnotationClass);
    this.dataStorage = dataStorage;
  }

  public final DataStorage dataStorage() {
    return dataStorage;
  }

  @Override
  public final <T> T getValueByPath(@NonNull final String path, @NonNull final T def) {

    if (dataStorage instanceof FlatFile)
      ((FlatFile) dataStorage).setPathPrefix(null);

    final LightningSerializable serializable =
        LightningSerializer.findSerializable(def.getClass());

    if (serializable == null)
      return dataStorage.getOrSetDefault(path, def);

    if (dataStorage.contains(path))
      return (T) dataStorage.getSerializable(path, def.getClass());

    final Object serialized = serializable.serialize(def);

    dataStorage.set(path, serialized);
    return (T) serializable.deserialize(serialized);
  }
}
