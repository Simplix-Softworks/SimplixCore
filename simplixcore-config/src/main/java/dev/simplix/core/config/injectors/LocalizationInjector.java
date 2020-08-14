package dev.simplix.core.config.injectors;

import de.leonhard.storage.Yaml;
import dev.simplix.core.config.annotations.Localizable;
import dev.simplix.core.config.injector.AbstractSettingsInjector;
import dev.simplix.core.config.localization.Localizables;
import java.lang.reflect.Field;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.NonNull;

public final class LocalizationInjector extends AbstractSettingsInjector<Localizable, Localizable> {

  @Inject
  public LocalizationInjector(@NonNull @Named("localization") final Yaml config) {
    super(Localizable.class, Localizable.class, config);
  }

  @Override
  protected void onInjected(
      @NonNull final Class<?> clazz,
      @NonNull final Field field,
      @NonNull final String path,
      @NonNull final Object raw) {
    Localizables.register(
        dev.simplix.core.config.localization.Localizable
            .builder()
            .clazz(clazz)
            .field(field)
            .path(path)
            .rawValue(raw)
            .build()
    );
  }

  @Override
  public String pathFromAnnotation(@NonNull final Localizable fieldAnnotation) {
    return fieldAnnotation.value();
  }
}


