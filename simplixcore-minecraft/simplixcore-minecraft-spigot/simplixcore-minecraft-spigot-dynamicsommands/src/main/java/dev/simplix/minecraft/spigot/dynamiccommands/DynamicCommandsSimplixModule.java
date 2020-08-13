package dev.simplix.minecraft.spigot.dynamiccommands;

import com.google.inject.Binder;
import dev.simplix.core.common.aop.AbstractSimplixModule;
import dev.simplix.core.common.aop.InjectorModule;
import java.lang.reflect.Field;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.Plugin;

@InjectorModule("SimplixCore")
public class DynamicCommandsSimplixModule extends AbstractSimplixModule {

  private Field pluginField;

  {
    registerComponentInterceptor(
        Command.class,
        this::registerCommand);
  }

  public void registerCommand(@NonNull final Command command) {
    try {
      final Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
      commandMapField.setAccessible(true);

      final CommandMap commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());
      commandMap.register(command.getLabel(), command);
    } catch (final Throwable throwable) {
      throwable.printStackTrace();
    }
  }

  public Plugin loaderPlugin() {
    ClassLoader classLoader = getClass().getClassLoader();
    if (classLoader.getClass().getName().startsWith("org.craftbukkit")) {
      try {
        if (this.pluginField == null) {
          this.pluginField = classLoader.getClass().getDeclaredField("plugin");
          this.pluginField.setAccessible(true);
        }
        return (Plugin) this.pluginField.get(classLoader);
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
