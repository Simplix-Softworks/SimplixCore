package dev.simplix.core.config.localization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

/**
 * Class to manage our localizable Strings. Can be used to display them in a menu in the different
 * platform's implementations such as Spigot or Proxy
 */
@UtilityClass
public class Localizables {

  private final List<Localizable> localizables = new ArrayList<>();

  public void register(@NonNull final Localizable localizable) {
    localizables.add(localizable);
  }

  public Optional<Localizable> find(@NonNull final String path) {
    return localizables()
        .stream()
        .filter(localizable -> localizable.path().equals(path))
        .findFirst();
  }

  public void clear() {
    localizables.clear();
  }

  public List<Localizable> localizables() {
    return Collections.unmodifiableList(localizables);
  }
}
