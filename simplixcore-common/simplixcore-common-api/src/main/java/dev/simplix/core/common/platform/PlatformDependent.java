package dev.simplix.core.common.platform;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a module to be platform dependent. SimplixCore will recognize this module during
 * installation but will only bind it if the specified platform matches with the platform running
 * on.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PlatformDependent {

  /**
   * @return The platform this module should be applied on.
   */
  Platform value();

}
