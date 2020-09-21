package dev.simplix.core.common;

import java.io.File;
import java.util.Arrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ApplicationInfoTest {

  private static final String NAME = "Name";
  private static final String VERSION = "1.0.0";
  private static final String[] AUTHORS = new String[0];
  private static final String[] DEPENDENCIES = new String[0];
  private static final File WORKING_DIRECTORY = new File(".");
  private static ApplicationInfo applicationInfo;

  @BeforeAll
  static void setUp() {
    applicationInfo = new ApplicationInfo(
        NAME,
        VERSION,
        AUTHORS,
        WORKING_DIRECTORY,
        DEPENDENCIES);
  }

  @Test
  void name() {
    Assertions.assertEquals(applicationInfo.name(), NAME);
  }

  @Test
  void version() {
    Assertions.assertEquals(applicationInfo.version(), VERSION);
  }

  @Test
  void authors() {
    Assertions.assertTrue(Arrays.equals(applicationInfo.authors(), AUTHORS));
  }

  @Test
  void workingDirectory() {
    Assertions.assertEquals(applicationInfo.workingDirectory(), WORKING_DIRECTORY);
  }

  @Test
  void dependencies() {
    Assertions.assertTrue(Arrays.equals(applicationInfo.dependencies(), DEPENDENCIES));
  }
}