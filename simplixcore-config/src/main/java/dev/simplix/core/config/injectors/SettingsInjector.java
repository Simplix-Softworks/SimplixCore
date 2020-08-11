package dev.simplix.core.config.injectors;

import de.leonhard.storage.Yaml;
import dev.simplix.core.config.annotations.Setting;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.NonNull;
import dev.simplix.core.config.injector.AbstractSettingsInjector;

public final class SettingsInjector extends AbstractSettingsInjector<Setting, Setting> {

  @Inject
  public SettingsInjector(@NonNull @Named("settings") final Yaml config) {
    super(Setting.class, Setting.class, config);
  }

  @Override
  public String pathFromAnnotation(Setting fieldAnnotation) {
    return fieldAnnotation.value();
  }
}
