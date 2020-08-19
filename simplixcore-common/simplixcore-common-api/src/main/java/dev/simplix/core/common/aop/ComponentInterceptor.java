package dev.simplix.core.common.aop;

import lombok.NonNull;

/**
 * A component interceptor intercepts specific {@link Component}s of a given subtype after component
 * scanning.
 *
 * @param <T> The type of the component
 */
public interface ComponentInterceptor<T> {

  /**
   * Passes an instance of an intercepted component to this interceptor for further processing.
   * @param obj An instance of component
   */
  void intercept(@NonNull T obj);

}
