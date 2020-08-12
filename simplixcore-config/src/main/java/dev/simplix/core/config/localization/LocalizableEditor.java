package dev.simplix.core.config.localization;

import de.leonhard.storage.Yaml;
import de.leonhard.storage.util.Valid;
import dev.simplix.core.common.Replacer;
import dev.simplix.core.common.providers.ExceptionHandler;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
public final class LocalizableEditor {

  private final Yaml localization;
  private final ExceptionHandler exceptionHandler;
  @Getter
  private final Localizable localizable;
  @Getter
  private final List<String> value;

  public LocalizableEditor(
      Yaml localization,
      ExceptionHandler exceptionHandler,
      Localizable localizable) {
    this.localization = localization;
    this.exceptionHandler = exceptionHandler;
    this.localizable = localizable;
    this.value = new ArrayList<>(localizable.value());
  }

  public boolean canMultiline() {
    return localizable.rawValue() instanceof Collection<?>
           || localizable.rawValue() instanceof String[]
           || localizable.rawValue() instanceof Replacer;
  }

  public void set(final int index, final String string) {
    value.set(index, string);
  }

  public void add(final String element) {
    Valid.checkBoolean(
        !canMultiline(),
        "Can't add line on non-multi line Localizable");
    value.add(element);
  }

  public void save() {
    try {
      save0();
    } catch (final Throwable throwable) {
      exceptionHandler.saveError(
          throwable,
          "LocalizableEditor.save(): Exception while saving Localizable",
          "Value: " + localizable.value(),
          "Field: '" + localizable.field().getName() + "'",
          "Class: '" + localizable.clazz().getName() + "'");
    }
  }

  private void save0() throws IllegalAccessException {

    final Object rawValue = localizable.rawValue();

    if (rawValue == null) {
      return;
    }

    if (rawValue instanceof String) {
      localizable.rawValue(value.get(0));
      localizable.field().set(null, value.get(0));
      localization.set(localizable.path(), value.get(0));
    } else if (rawValue instanceof Collection<?>) {
      localizable.rawValue(value);
      localizable.field().set(null, value);
      localization.set(localizable.path(), value);
    } else if (rawValue instanceof String[]) {
      final String[] rawValueAsArray = this.value.toArray(new String[0]);
      localizable.rawValue(rawValueAsArray);
      localizable.field().set(null, rawValueAsArray);
      localization.set(localizable.path(), value);
    } else if (rawValue instanceof Replacer) {
      final Replacer replacer = Replacer.of(value);
      localizable.rawValue(replacer);
      localizable.field().set(null, replacer);
      localization.set(localizable.path(), value);
    }
  }

  public static final class Builder {

    private final Yaml localization;
    private final ExceptionHandler exceptionHandler;

    @Inject
    public Builder(
        @Named("localization") @NonNull final Yaml localization,
        @NonNull final ExceptionHandler exceptionHandler) {
      this.localization = localization;
      this.exceptionHandler = exceptionHandler;
    }

    public LocalizableEditor build(@NonNull final Localizable localizable) {
      return new LocalizableEditor(localization, exceptionHandler, localizable);
    }
  }
}
