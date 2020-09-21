package dev.simplix.core.common.events;

import dev.simplix.core.common.ApplicationInfo;
import dev.simplix.core.common.event.AbstractEvent;

public final class ApplicationPreInstallEvent extends AbstractEvent {

  private Class<?> applicationClass;
  private ApplicationInfo applicationInfo;

  public ApplicationPreInstallEvent(ApplicationInfo applicationInfo, Class<?> applicationClass) {
    this.applicationInfo = applicationInfo;
    this.applicationClass = applicationClass;
  }

  public Class<?> applicationClass() {
    return applicationClass;
  }

  public void applicationClass(Class<?> applicationClass) {
    this.applicationClass = applicationClass;
  }

  public ApplicationInfo applicationInfo() {
    return applicationInfo;
  }

  public void applicationInfo(ApplicationInfo applicationInfo) {
    this.applicationInfo = applicationInfo;
  }

}
