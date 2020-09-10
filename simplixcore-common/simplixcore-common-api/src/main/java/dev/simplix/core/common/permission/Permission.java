package dev.simplix.core.common.permission;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true, chain = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Permission {

  private final String permissionString;
  private final String[] description;

  public static Permission of(
      @NonNull final String permissionString,
      @NonNull final String... desc) {
    return new Permission(permissionString, desc);
  }
}
