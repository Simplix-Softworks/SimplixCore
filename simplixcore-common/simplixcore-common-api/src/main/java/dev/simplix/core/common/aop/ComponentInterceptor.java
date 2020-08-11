package dev.simplix.core.common.aop;

/**
 * A component interceptor intercepts specific components of a given subtype after component
 * scanning.
 *
 * @param <T> The type of the component
 */
public interface ComponentInterceptor<T> {

  void intercept(T obj);

}
