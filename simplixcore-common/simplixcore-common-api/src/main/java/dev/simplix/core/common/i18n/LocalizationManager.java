package dev.simplix.core.common.i18n;

import java.util.Locale;
import java.util.Set;

public interface LocalizationManager {

  /**
   * The string used when some string isn't localized in any language.
   * @param string The fallback string
   */
  void fallbackString(String string);

  /**
   * The string used when some string isn't localized in any language.
   * @return The fallback string
   */
  String fallbackString();

  /**
   * The locale used when some string isn't localized in a specific language.
   * @param locale The locale
   */
  void fallbackLocale(Locale locale);

  /**
   * The locale used when some string isn't localized in a specific language.
   * @return The locale
   */
  Locale fallbackLocale();

  /**
   * Translates a string identified by its key.
   *
   * @param key    The string key
   * @param locale The locale to use
   * @return The localized string or if not present the fallback string
   */
  String localized(String key, Locale locale);

  /**
   * Returns all supported keys
   *
   * @param locale The locale
   * @return Supported keys for that locale
   */
  Set<String> keys(Locale locale);

  /**
   * Returns all supported locales
   * @return Supported locales
   */
  Set<Locale> locales();

}
