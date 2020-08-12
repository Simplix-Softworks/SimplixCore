package dev.simplix.core.common.i18n;

import java.io.File;
import java.util.Locale;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class InternationalizationTest {

  @Test
  public void testResource() {
    LocalizationManager localizationManager = new SimpleLocalizationManagerFactory()
        .create(new File("src/test/resources/"));

    String localized = localizationManager.localized("test-string", Locale.GERMAN);
    Assertions.assertEquals("Ein Teststring", localized);

    localized = localizationManager.localized("test-string", Locale.ENGLISH);
    Assertions.assertEquals("A test string", localized);
  }

}
