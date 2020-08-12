package dev.simplix.core.common.i18n;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class SimpleLocalizationManager implements LocalizationManager {

  private final Map<Locale, Map<String, String>> translations;
  private Locale fallbackLocale = Locale.ENGLISH;
  private String fallbackString = "N/A";

  SimpleLocalizationManager(Map<Locale, Map<String, String>> translations) {
    this.translations = translations;
  }

  @Override
  public void fallbackString(String string) {
    this.fallbackString = string;
  }

  @Override
  public String fallbackString() {
    return this.fallbackString;
  }

  @Override
  public void fallbackLocale(Locale locale) {
    this.fallbackLocale = locale;
  }

  @Override
  public Locale fallbackLocale() {
    return this.fallbackLocale;
  }

  @Override
  public String localized(String key, Locale locale) {
    Map<String, String> trans = translations.get(locale);
    if (trans == null) {
      trans = translations.get(fallbackLocale());
      if (trans == null) {
        return fallbackString();
      }
    }
    String out = trans.get(key);
    if (out == null) {
      out = translations.getOrDefault(fallbackLocale(), Collections.emptyMap()).get(key);
      if (out == null) {
        return fallbackString();
      }
    }
    return out;
  }

  @Override
  public Set<String> keys(Locale locale) {
    return Collections.unmodifiableSet(translations
        .getOrDefault(locale, Collections.emptyMap())
        .keySet());
  }

  @Override
  public Set<Locale> locales() {
    return Collections.unmodifiableSet(translations.keySet());
  }

}
