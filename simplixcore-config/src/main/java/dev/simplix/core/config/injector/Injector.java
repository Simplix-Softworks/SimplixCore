package dev.simplix.core.config.injector;

import java.lang.annotation.Annotation;
import java.util.List;
import lombok.NonNull;

public interface Injector<C extends Annotation, F extends Annotation> {

  String pathFromAnnotation(@NonNull F fieldAnnotation);

  Class<C> classAnnotationClass();

  Class<F> fieldAnnotationClass();

  void startInjecting(@NonNull List<Class<?>> classes);

  <T> T getValueByPath(@NonNull String path, @NonNull T def);
}
