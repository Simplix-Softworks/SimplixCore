package dev.simplix.core.common.permission;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PermissionsTest {

  private static Permission EXAMPLE;

  @BeforeEach
  void setUp() {
    EXAMPLE = Permission.of("Example.Permission");
  }

  @Test
  void register() {
    Permissions.register(EXAMPLE);
    Assertions.assertTrue(Permissions.registeredPermissions().contains(EXAMPLE));
  }

  @Test
  void registerAll() {
    final List<Permission> permissionList = new ArrayList<>();

    // Generating permissions
    IntStream
        .range(0, 10)
        .forEach(integer -> permissionList.add(Permission.of("Example.AddAll." + integer)));

    Permissions.registerAll(permissionList);

    permissionList.forEach(permission -> Assertions.assertTrue(Permissions
        .registeredPermissions()
        .contains(permission)));
  }

  @Test
  void addFromClass() {
    Permissions.addFromClass(ClassWithPermissions.class);
    Assertions.assertTrue(
        Permissions
            .registeredPermissions()
            .contains(ClassWithPermissions.EXAMPLE_PERMISSION_1),
        "Example Permission 1 must be contained");
    Assertions.assertTrue(
        Permissions
            .registeredPermissions()
            .contains(ClassWithPermissions.EXAMPLE_PERMISSION_2),
        "Example Permission 1 must be contained");
  }

  static class ClassWithPermissions {

    private static final Permission EXAMPLE_PERMISSION_1 = Permission
        .of("Example.Permission.1");

    private static final Permission EXAMPLE_PERMISSION_2 = Permission
        .of("Example.Permission.2");

  }

}