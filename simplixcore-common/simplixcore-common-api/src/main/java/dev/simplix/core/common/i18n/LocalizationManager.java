package dev.simplix.core.common.i18n;

import java.util.Locale;
import java.util.Set;
import lombok.NonNull;

/**
 * The localization manager handles all translation stuff for any supported language.
 */
public interface LocalizationManager {

  /**
   * The string used when some string isn't localized in any language.
   *
   * @param string The fallback string
   */
  void fallbackString(@NonNull String string);

  /**
   * The string used when some string isn't localized in any language.
   *
   * @return The fallback string
   */
  String fallbackString();

  /**
   * The locale used when some string isn't localized in a specific language.
   *
   * @param locale The locale
   */
  void fallbackLocale(@NonNull Locale locale);

  /**
   * The locale used when some string isn't localized in a specific language.
   *
   * @return The locale
   */
  Locale fallbackLocale();

  /**
   * @return The default locale
   */
  Locale defaultLocale();

  /**
   * @param locale default locale
   */
  void defaultLocale(@NonNull Locale locale);

  /**
   * Translates a string identified by its key.
   *
   * @param key The string key
   * @return The localized string or if not present the fallback string
   */
  String localized(@NonNull String key);

  /**
   * Translates a string identified by its key.
   *
   * @param key    The string key
   * @param locale The locale to use
   * @return The localized string or if not present the fallback string
   */
  String localized(@NonNull String key, @NonNull Locale locale);

  /**
   * Returns all supported keys
   *
   * @param locale The locale
   * @return Supported keys for that locale
   */
  Set<String> keys(@NonNull Locale locale);

  /**
   * Returns all supported locales
   *
   * @return Supported locales
   */
  Set<Locale> locales();

}
