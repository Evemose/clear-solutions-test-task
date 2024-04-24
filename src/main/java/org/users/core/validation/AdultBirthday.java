package org.users.core.validation;

import jakarta.validation.Constraint;
import jakarta.validation.constraints.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * The annotated element must be a date in the past.
 * Suitable for {@link java.time.LocalDate}.
 * Null values are considered invalid.
 */
@Constraint(validatedBy = BeforeTodayValidator.class)
@Retention(RUNTIME)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@NotNull
public @interface AdultBirthday {
    String message() default "Must be a date in the past";

    Class<?>[] groups() default {};

    Class<?>[] payload() default {};
}
