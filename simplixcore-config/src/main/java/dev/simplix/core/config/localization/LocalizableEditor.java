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
    return this.localizable.rawValue() instanceof Collection<?>
           || this.localizable.rawValue() instanceof String[]
           || this.localizable.rawValue() instanceof Replacer;
  }

  public void set(final int index, @NonNull final String string) {
    this.value.set(index, string);
  }

  public void add(@NonNull final String element) {
    Valid.checkBoolean(
        !canMultiline(),
        "Can't add line on non-multi line Localizable");
    this.value.add(element);
  }

  public void save() {
    try {
      save0();
    } catch (final Throwable throwable) {
      this.exceptionHandler.saveError(
          throwable,
          "LocalizableEditor.save(): Exception while saving Localizable",
          "Value: " + this.localizable.value(),
          "Field: '" + this.localizable.field().getName() + "'",
          "Class: '" + this.localizable.clazz().getName() + "'");
    }
  }

  private void save0() throws IllegalAccessException {

    final Object rawValue = this.localizable.rawValue();

    if (rawValue == null) {
      return;
    }

    if (rawValue instanceof String) {
      this.localizable.rawValue(this.value.get(0));
      this.localizable.field().set(null, this.value.get(0));
      this.localization.set(this.localizable.path(), this.value.get(0));
    } else if (rawValue instanceof Collection<?>) {
      this.localizable.rawValue(this.value);
      this.localizable.field().set(null, this.value);
      this.localization.set(this.localizable.path(), this.value);
    } else if (rawValue instanceof String[]) {
      final String[] rawValueAsArray = this.value.toArray(new String[0]);
      this.localizable.rawValue(rawValueAsArray);
      this.localizable.field().set(null, rawValueAsArray);
      this.localization.set(this.localizable.path(), this.value);
    } else if (rawValue instanceof Replacer) {
      final Replacer replacer = Replacer.of(this.value);
      this.localizable.rawValue(replacer);
      this.localizable.field().set(null, replacer);
      this.localization.set(this.localizable.path(), this.value);
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
      return new LocalizableEditor(this.localization, this.exceptionHandler, localizable);
    }
  }
}
