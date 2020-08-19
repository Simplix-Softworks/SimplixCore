package dev.simplix.core.common.aop;

import com.google.inject.Binder;
import com.google.inject.Inject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A component is a class that will be automatically registered as a candidate for dependency injection. Components
 * can be used in class constructors annotated with {@link Inject}. Components are singletons in their specific
 * context which will only be constructed when there is a need for them.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Component {

  /**
   * The owner of this component. Components need to be registered at a {@link AbstractSimplixModule}.
   * @return The module class
   */
  Class<? extends AbstractSimplixModule> value();

  /**
   * The type that is being used to bind this component to a key during module configuration. If nothing is specified,
   * it will use the explicit type of this component.
   * @see com.google.inject.Module#configure(Binder)
   * @return The type that is being used for binding
   */
  Class<?> parent() default Object.class;

}
