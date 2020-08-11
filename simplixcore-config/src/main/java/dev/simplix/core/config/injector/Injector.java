package dev.simplix.core.config.injector;

import java.lang.annotation.Annotation;
import java.util.List;
import lombok.NonNull;

public interface Injector<C extends Annotation, F extends Annotation> {

  String pathFromAnnotation(F fieldAnnotation);

  Class<C> classAnnotationClass();

  Class<F> fieldAnnotationClass();

  void startInjecting(List<Class<?>> classes);

  <T> T getValueByPath(@NonNull String path, @NonNull T def);
}
