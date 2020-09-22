package dev.simplix.core.common.events;

import dev.simplix.core.common.ApplicationInfo;
import dev.simplix.core.common.event.AbstractEvent;
import lombok.NonNull;

public final class ApplicationPreInstallEvent extends AbstractEvent {

  private Class<?> applicationClass;
  private ApplicationInfo applicationInfo;

  public ApplicationPreInstallEvent(
      @NonNull ApplicationInfo applicationInfo,
      @NonNull Class<?> applicationClass) {
    this.applicationInfo = applicationInfo;
    this.applicationClass = applicationClass;
  }

  public Class<?> applicationClass() {
    return applicationClass;
  }

  public void applicationClass(@NonNull Class<?> applicationClass) {
    this.applicationClass = applicationClass;
  }

  public ApplicationInfo applicationInfo() {
    return applicationInfo;
  }

  public void applicationInfo(@NonNull ApplicationInfo applicationInfo) {
    this.applicationInfo = applicationInfo;
  }

}
