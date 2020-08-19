package dev.simplix.core.common.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Normally, {@link Component}s are lazy constructed. When there is no need for a specific component to be injected,
 * there will also be no instance of this component. A component annotated with {@link AlwaysConstruct} will always
 * be constructed.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AlwaysConstruct {

}
