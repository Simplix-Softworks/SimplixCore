package dev.simplix.core.common;

import com.google.inject.Binder;
import dev.simplix.core.common.aop.AbstractSimplixModule;
import dev.simplix.core.common.aop.ApplicationModule;
import dev.simplix.core.common.listener.Listener;
import dev.simplix.core.common.listener.Listeners;
import lombok.NonNull;

@ApplicationModule("SimplixCore")
public final class CommonSimplixModule extends AbstractSimplixModule {

  {
    registerComponentInterceptor(Listener.class, Listeners::register);
  }

  @Override
  public void configure(@NonNull Binder binder) {
    super.configure(binder);
  }

}
