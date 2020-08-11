package dev.simplix.core.common.duration;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractDuration implements Duration {

  protected final long ms;
}
