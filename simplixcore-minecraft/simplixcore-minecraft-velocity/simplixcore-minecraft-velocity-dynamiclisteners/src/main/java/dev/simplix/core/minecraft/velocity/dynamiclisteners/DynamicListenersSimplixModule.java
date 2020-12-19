package dev.simplix.core.minecraft.velocity.dynamiclisteners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.simplix.core.common.aop.AbstractSimplixModule;
import lombok.extern.slf4j.Slf4j;
import java.lang.reflect.Method;

@Slf4j
public class DynamicListenersSimplixModule extends AbstractSimplixModule {

  public DynamicListenersSimplixModule(ProxyServer proxyServer, Object plugin) {
    registerComponentInterceptor(Object.class, obj -> {
      if (containsAnyListener(obj.getClass())) {
        proxyServer.getEventManager().register(plugin, obj);
      } else {
        log.debug("[Simplix] "
                  + obj.getClass().getName()
                  + " is bound to dynamic listeners module but is not containing any listener.");
      }
    });
  }

  private boolean containsAnyListener(Class<?> c) {
    for (Method method : c.getMethods()) {
      if (method.isAnnotationPresent(Subscribe.class)) {
        return true;
      }
    }
    return false;
  }

}
