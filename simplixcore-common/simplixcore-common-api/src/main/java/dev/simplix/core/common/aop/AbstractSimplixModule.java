package dev.simplix.core.common.aop;

import com.google.inject.Binder;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Scopes;
import java.util.HashMap;
import java.util.Map;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public abstract class AbstractSimplixModule implements Module {

  private final Map<Component, Class<?>> components = new HashMap<>();
  private final Map<Class<?>, ComponentInterceptor> interceptorMap = new HashMap<>();

  @Override
  public void configure(Binder binder) {
    components.keySet().forEach(compDef -> {
      Class clazz = components.get(compDef);
      if (!compDef.parent().equals(Object.class)) {
        binder.bind(compDef.parent()).to(clazz).in(Scopes.SINGLETON);
      } else {
        binder.bind(clazz).in(Scopes.SINGLETON);
      }
    });
  }

  public void intercept(Injector injector) {
    components.keySet().forEach(compDef -> {
      Class<?> clazz = components.get(compDef);
      ComponentInterceptor componentInterceptor = findAssignable(clazz);
      if (componentInterceptor != null) {
        if (compDef.parent().equals(Object.class)) {
          componentInterceptor.intercept(injector.getInstance(clazz));
        } else {
          componentInterceptor.intercept(injector.getInstance(compDef.parent()));
        }
      }
    });
  }

  private ComponentInterceptor findAssignable(Class<?> clazz) {
    for (Class<?> c : interceptorMap.keySet()) {
      if (c.isAssignableFrom(clazz)) {
        return interceptorMap.get(c);
      }
    }
    return null;
  }

  public Map<Component, Class<?>> components() {
    return components;
  }

  public <T> void registerComponentInterceptor(
      Class<T> clazz,
      ComponentInterceptor<T> interceptor) {
    interceptorMap.put(clazz, interceptor);
  }
}
