package dev.simplix.core.common.aop;

import dev.simplix.core.common.inject.SimplixInstaller;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Only usable in combination with {@link SimplixApplication}. This tells the {@link SimplixInstaller} the possible
 * locations for components or modules.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ScanComponents {

  /**
   * @return An array containing the base packages for components or modules. The packages will be scanned recursively.
   */
  String[] value();

}
