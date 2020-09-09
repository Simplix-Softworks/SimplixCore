package dev.simplix.core.minecraft.api.providers;

import java.io.File;
import java.util.List;
import lombok.NonNull;

public interface PluginManager {

  void enablePlugin(@NonNull final File jarFile);

  List<String> enabledPlugins();
}
