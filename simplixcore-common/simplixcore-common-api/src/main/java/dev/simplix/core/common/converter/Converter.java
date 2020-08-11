package dev.simplix.core.common.converter;

public interface Converter<Source, Target> {

  Target convert(Source src);

}
