package dev.simplix.core.minecraft.spigot.dynamiclisteners;

import dev.simplix.core.common.aop.AbstractSimplixModule;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class DynamicListenersSimplixModule extends AbstractSimplixModule {

    private final Plugin plugin;

    public DynamicListenersSimplixModule(Plugin plugin) {
        if (plugin == null) {
            this.plugin = Bukkit
                    .getPluginManager()
                    .getPlugins()[0];
        } else {
            this.plugin = plugin;
        }

        registerComponentInterceptor(
                Listener.class,
                listener -> Bukkit
                        .getPluginManager()
                        .registerEvents(listener, plugin));
    }

}
