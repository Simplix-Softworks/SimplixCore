package dev.simplix.core.common.aop;

import com.google.inject.BindingAnnotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This will set the privacy level of an injection client. A private client cannot be accessed from other
 * SimplixApplications.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@BindingAnnotation
public @interface Privacy {

  Level value() default Level.PUBLIC;

  enum Level {

    PRIVATE, PUBLIC

  }

}
