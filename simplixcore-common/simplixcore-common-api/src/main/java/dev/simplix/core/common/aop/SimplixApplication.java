package dev.simplix.core.common.aop;

import dev.simplix.core.common.inject.SimplixInstaller;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This marks a class as the main class of a SimplixCore compliant application.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SimplixApplication {

  /**
   * @return The name of the application
   */
  String name();

  /**
   * @return The version of the application
   */
  String version();

  /**
   * @return The authors of the application
   */
  String[] authors();

  /**
   * Tells the {@link SimplixInstaller} to install dependent applications before this application.
   * @return The dependencies of the application
   */
  String[] dependencies() default {};

  /**
   * @return A path for the working directory
   */
  String workingDirectory() default ".";

}
