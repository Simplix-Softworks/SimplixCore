package dev.simplix.core.common.aop;

import com.google.inject.Module;
import dev.simplix.core.common.inject.SimplixInstaller;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class for automatic {@link Module} detection during module scanning. The annotated module
 * needs to have an accessible default constructor. Otherwise please register the module using
 * {@link RequireModules} or using {@link SimplixInstaller#register(Class, Module...)} in your
 * application class.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApplicationModule {

  /**
   * @return The application name of the {@link SimplixApplication} where this module belongs to.
   */
  String value();

}
