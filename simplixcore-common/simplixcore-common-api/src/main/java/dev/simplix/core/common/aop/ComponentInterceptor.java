package dev.simplix.core.common.aop;

import lombok.NonNull;

/**
 * A component interceptor intercepts specific components of a given subtype after component
 * scanning.
 *
 * @param <T> The type of the component
 */
public interface ComponentInterceptor<T> {

  void intercept(@NonNull T obj);

}
