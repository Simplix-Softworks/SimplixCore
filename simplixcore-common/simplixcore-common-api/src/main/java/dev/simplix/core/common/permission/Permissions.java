package dev.simplix.core.common.permission;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

/**
 * Central utility class to handle {@link Permission}'s
 * <p>
 * Within this class you can store and register permissions
 */
@UtilityClass
public class Permissions {

  private final List<Permission> registeredPermissions = new ArrayList<>();

  /**
   * Returns all registered {@link Permission}'s as an unmodifiable collection. It is made
   * unmodifiable since we don't want listeners to be removed from it
   */
  public List<Permission> registeredPermissions() {
    return Collections.unmodifiableList(Permissions.registeredPermissions);
  }

  /**
   * Add an {@link Permission} to the known permissions
   */
  public void register(@NonNull Permission permission) {
    Permissions.registeredPermissions.add(permission);
  }

  /**
   * Add a collection of {@link Permission} to the known permissions
   */
  public void registerAll(@NonNull Collection<Permission> permissions) {
    Permissions.registeredPermissions.addAll(permissions);
  }

  /**
   * Method to register permissions from an class that contains them as static fields
   */
  @SneakyThrows
  public <T> void addFromClass(final Class<?> clazz) {
    for (final Field field : clazz.getFields()) {
      if (field.getType() != Permission.class) {
        continue;
      }

      final Permission perm = (Permission) field.get(null);
      if (perm == null) {
        continue;
      }
      Permissions.register(perm);
    }
  }
}
