package org.users.core.validation;

import jakarta.validation.Constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * The annotated element must be a date in the past.
 * Suitable for {@link java.time.LocalDate}.
 * Null values are considered valid.
 */
@Constraint(validatedBy = AdultValidator.class)
@Retention(RUNTIME)
@Target({ElementType.PARAMETER, ElementType.FIELD})
public @interface AdultBirthday {
    // there is no way to inject value into annotation, so a message must be formed in the validator
    String message() default "";

    Class<?>[] groups() default {};

    Class<?>[] payload() default {};
}
