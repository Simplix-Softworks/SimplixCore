package dev.simplix.core.common.scanner;

/**
 * Used in our class scanner to show that this class knows that it is using imports that might not
 * be available at runtime and handles this gracefully.
 */
public @interface FailureAware {

}
