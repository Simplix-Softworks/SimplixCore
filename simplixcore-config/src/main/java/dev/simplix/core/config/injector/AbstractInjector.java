package dev.simplix.core.config.injector;

import de.leonhard.storage.util.Valid;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractInjector<C extends Annotation, F extends Annotation>
    implements Injector<C, F> {

  protected final Class<C> classAnnotationClass;
  protected final Class<F> fieldAnnotationClass;

  @Override
  public final void startInjecting(@NonNull final List<Class<?>> classes) {
    for (Class<?> clazz : classes) {
      if (isAnnotationPresent(clazz, this.classAnnotationClass)) {
        for (final Field field : clazz.getDeclaredFields()) {

          // We only inject static fields
          if (!Modifier.isStatic(field.getModifiers())) {
            continue;
          }

          if (!field.isAnnotationPresent(this.fieldAnnotationClass)) {
            continue;
          }

          Valid.checkBoolean(
              !Modifier.isFinal(field.getModifiers()),
              "Can't inject final field '" + field.getName() + "'",
              "Class: " + clazz.getName(),
              "Field is final!");

          final F fieldAnnotation = field.getAnnotation(this.fieldAnnotationClass);
          final String path = pathFromAnnotation(fieldAnnotation);
          Valid.checkBoolean(
              !path.isEmpty(),
              "Path mustn't be empty",
              "Class: " + clazz.getName(),
              "Field: " + field.getName());
          try {
            field.setAccessible(true);
            final Object raw = field.get(null);
            final Object valueByPath = getValueByPath(path, raw);
            field.set(null, valueByPath);
            onInjected(clazz, field, path, valueByPath);
          } catch (final Throwable throwable) {
            System.err.println("Exception while injecting!");
            System.err.println("Path:  '" + path + "'");
            System.err.println("Class: '" + clazz.getSimpleName() + "'");
            throwable.printStackTrace();
          }
        }
      }
    }
  }

  private boolean isAnnotationPresent(
      @NonNull final Class<?> clazz,
      @NonNull final Class<? extends Annotation> annotationClass) {
    try {
      return clazz.isAnnotationPresent(annotationClass);
    } catch (final Throwable throwable) {
      return false;
    }
  }

  protected void onInjected(
      final Class<?> clazz,
      final Field field,
      final String path,
      final Object value) {
  }
}
