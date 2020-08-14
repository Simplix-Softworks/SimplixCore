package dev.simplix.core.common.i18n;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import lombok.NonNull;

public class SimpleLocalizationManager implements LocalizationManager {

  private final Map<Locale, Map<String, String>> translations;
  private Locale fallbackLocale = Locale.ENGLISH;
  private String fallbackString = "N/A";

  SimpleLocalizationManager(@NonNull Map<Locale, Map<String, String>> translations) {
    this.translations = translations;
  }

  @Override
  public void fallbackString(@NonNull String string) {
    this.fallbackString = string;
  }

  @Override
  public String fallbackString() {
    return this.fallbackString;
  }

  @Override
  public void fallbackLocale(@NonNull Locale locale) {
    this.fallbackLocale = locale;
  }

  @Override
  public Locale fallbackLocale() {
    return this.fallbackLocale;
  }

  @Override
  public String localized(@NonNull String key, @NonNull Locale locale) {
    Map<String, String> trans = this.translations.get(locale);
    if (trans == null) {
      trans = this.translations.get(fallbackLocale());
      if (trans == null) {
        return fallbackString();
      }
    }
    String out = trans.get(key);
    if (out == null) {
      out = this.translations.getOrDefault(fallbackLocale(), Collections.emptyMap()).get(key);
      if (out == null) {
        return fallbackString();
      }
    }
    return out;
  }

  @Override
  public Set<String> keys(@NonNull Locale locale) {
    return Collections.unmodifiableSet(this.translations
        .getOrDefault(locale, Collections.emptyMap())
        .keySet());
  }

  @Override
  public Set<Locale> locales() {
    return Collections.unmodifiableSet(this.translations.keySet());
  }

}
