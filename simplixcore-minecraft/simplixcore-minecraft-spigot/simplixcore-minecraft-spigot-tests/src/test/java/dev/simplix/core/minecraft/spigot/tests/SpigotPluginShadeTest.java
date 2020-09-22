package dev.simplix.core.minecraft.spigot.tests;

import dev.simplix.core.common.ApplicationInfo;
import lombok.NonNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test to shade whether everything was shaded correctly
 */
public class SpigotPluginShadeTest {

  @Test
  void testClassPresence() {
    //Common-Api
    assertPresent("dev.simplix.core.common.ApplicationInfo");
    // Common-Implementation
    assertPresent("dev.simplix.core.common.CommonSimplixModule");
    // Database-SQL
    assertPresent("dev.simplix.core.database.sql.HikariDataSourceCreator");

    assertPackageEquals(ApplicationInfo.class, "dev.simplix.core.common");
  }

  private void assertPackageEquals(Class<?> classRef, String packageName) {
    assertPackageEquals(
        classRef,
        packageName,
        classRef.getSimpleName() + " should not be relocated");

  }

  // This method will work since the maven relocation won't run over strings in the tests directory
  public void assertPackageEquals(
      @NonNull Class<?> classRef,
      @NonNull String packageName,
      @NonNull String notEqualsMessage) {
    Assertions.assertTrue(doesPackageEquals(classRef, packageName), notEqualsMessage);
  }

  public boolean doesPackageEquals(@NonNull Class<?> classRef, @NonNull String packageName) {
    return classRef.getPackage().getName().equals(packageName);
  }

  private void assertPresent(@NonNull String className) {
    assertPresent(className, className + " must be present");
  }

  private void assertPresent(@NonNull String className, @NonNull String notPresentMessage) {
    Assertions.assertTrue(isPresent(className), notPresentMessage);
  }

  private boolean isPresent(@NonNull String className) {
    try {
      Class.forName(className);
      return true;
    } catch (final Throwable throwable) {
      return false;
    }
  }

}
