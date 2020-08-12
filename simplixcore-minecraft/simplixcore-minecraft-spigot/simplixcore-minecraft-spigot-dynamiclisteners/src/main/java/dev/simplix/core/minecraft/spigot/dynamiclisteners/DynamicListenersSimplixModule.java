package dev.simplix.core.minecraft.spigot.dynamiclisteners;

import com.google.inject.Binder;
import dev.simplix.core.common.aop.AbstractSimplixModule;
import dev.simplix.core.common.aop.InjectorModule;
import java.lang.reflect.Field;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

@InjectorModule("SimplixCore")
public class DynamicListenersSimplixModule extends AbstractSimplixModule {

  private Field pluginField;

  {
    registerComponentInterceptor(
        Listener.class,
        listener -> Bukkit.getPluginManager().registerEvents(listener, loaderPlugin()));
  }

  public Plugin loaderPlugin() {
    ClassLoader classLoader = getClass().getClassLoader();
    if (classLoader.getClass().getName().startsWith("org.craftbukkit")) {
      try {
        if (pluginField == null) {
          pluginField = classLoader.getClass().getDeclaredField("plugin");
          pluginField.setAccessible(true);
        }
        return (Plugin) pluginField.get(classLoader);
      } catch (NoSuchFieldException | IllegalAccessException ignored) {
      }
    }
    return null;
  }

  @Override
  public void configure(Binder binder) {
    super.configure(binder);
    binder.bind(Plugin.class).toInstance(loaderPlugin());
  }
}
