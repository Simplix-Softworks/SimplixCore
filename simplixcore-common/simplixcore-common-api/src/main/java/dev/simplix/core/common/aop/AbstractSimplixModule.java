package dev.simplix.core.common.aop;

import com.google.inject.Binder;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Scopes;
import com.google.inject.binder.AnnotatedBindingBuilder;
import java.util.HashMap;
import java.util.Map;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * A {@link Module} which allows the registration of {@link ComponentInterceptor}s.
 */
@NoArgsConstructor
@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class AbstractSimplixModule implements Module {

  private final Map<Class, Component> components = new HashMap<>();
  private final Map<Class<?>, ComponentInterceptor> interceptorMap = new HashMap<>();

  @Override
  public void configure(Binder binder) {
    this.components.keySet().forEach(clazz -> {
      Component component = this.components.get(clazz);
      if (!component.parent().equals(Object.class)) {
        AnnotatedBindingBuilder<?> bindingBuilder = binder.bind(component.parent());
        if(isPrivate()) {
          bindingBuilder.annotatedWith(getClass().getAnnotation(Private.class));
        }
        bindingBuilder.to(clazz).in(Scopes.SINGLETON);
      } else {
        AnnotatedBindingBuilder bindingBuilder = binder.bind(clazz);
        if(isPrivate()) {
          bindingBuilder.annotatedWith(getClass().getAnnotation(Private.class));
        }
        bindingBuilder.in(Scopes.SINGLETON);
      }
    });
  }

  public void intercept(@NonNull Injector injector) {
    this.components.keySet().forEach(clazz -> {
      Component component = this.components.get(clazz);
      ComponentInterceptor componentInterceptor = findAssignable(clazz);
      if (componentInterceptor != null) {
        if (component.parent().equals(Object.class)) {
          componentInterceptor.intercept(injector.getInstance(clazz));
        } else {
          componentInterceptor.intercept(injector.getInstance(component.parent()));
        }
      }
    });
  }

  private ComponentInterceptor findAssignable(@NonNull Class<?> clazz) {
    for (Class<?> c : this.interceptorMap.keySet()) {
      if (c.isAssignableFrom(clazz)) {
        return this.interceptorMap.get(c);
      }
    }
    return null;
  }

  public Map<Class, Component> components() {
    return this.components;
  }

  public <T> void registerComponentInterceptor(
      Class<T> clazz,
      ComponentInterceptor<T> interceptor) {
    this.interceptorMap.put(clazz, interceptor);
  }

  public boolean isPrivate() {
    return getClass().isAnnotationPresent(Private.class);
  }

}
