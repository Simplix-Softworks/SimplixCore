package dev.simplix.core.common.converter;

import lombok.NonNull;

public interface Converter<Source, Target> {

  Target convert(@NonNull Source src);

}
