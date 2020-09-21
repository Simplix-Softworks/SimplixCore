package dev.simplix.core.common;

import lombok.NonNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test to shade whether everything was shaded correctly
 */
public class ShadeTest {

  @Test
  void testClassPresence() {
    //Common-Api
    assertPresent("dev.simplix.core.common.ApplicationInfo");
    // Common-Implementation
    assertPresent("dev.simplix.core.common.CommonSimplixModule");
    // Database-SQL
    assertPresent("dev.simplix.core.database.sql.HikariDataSourceCreator");
  }

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
