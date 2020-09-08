package dev.simplix.core.common.event;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true, chain = false)
public abstract class AbstractEvent implements Event {

  private boolean canceled;
  private String cancelReason;

  protected AbstractEvent() {
    Events.add(this);
  }

  @Override
  public void canceled(boolean canceled) {
    this.canceled = canceled;
  }

  @Override
  public void cancelReason(@NonNull String cancelReason) {
    this.cancelReason = cancelReason;
  }
}
