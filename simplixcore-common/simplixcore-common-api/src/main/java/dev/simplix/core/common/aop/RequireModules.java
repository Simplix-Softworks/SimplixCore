package dev.simplix.core.common.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.Supplier;

/**
 * Only usable in combination with {@link SimplixApplication}. This class makes it possible to
 * create module instances using specified {@link Supplier}.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireModules {

  /**
   * A supplier which will deliver initialized modules of subtype {@link AbstractSimplixModule}. The
   * supplier class needs to have an accessible default constructor.
   *
   * @return The supplier class
   */
  Class<? extends Supplier<AbstractSimplixModule[]>> value();

}
