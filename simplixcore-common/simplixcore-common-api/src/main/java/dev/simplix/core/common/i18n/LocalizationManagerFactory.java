package dev.simplix.core.common.i18n;

import java.io.File;

/**
 * Used to localize strings using language files
 */
public interface LocalizationManagerFactory {

  /**
   * Constrcuts a {@link LocalizationManager} based on a directory containing all translation files.
   * @param translationDirectory The directory where the language files are in
   * @return A new instance of {@link LocalizationManager}
   */
  LocalizationManager create(File translationDirectory);

  /**
   * Constrcuts a {@link LocalizationManager} based on a resource path containing all translation files.
   * @param translationResourcesDirectory The resource path the language files are in
   * @param refClass The class reference that is used to determine the source file and the resource location
   * @return A new instance of {@link LocalizationManager}
   */
  LocalizationManager create(String translationResourcesDirectory, Class<?> refClass);

}
