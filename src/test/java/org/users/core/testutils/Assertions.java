package org.users.core.testutils;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.function.Executable;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class Assertions {
    public static void assertConstraintViolations(Executable action, ConstraintViolationInfo... violations) {
        var exception = assertThrows(ConstraintViolationException.class, action);
        assertEquals(violations.length, exception.getConstraintViolations().size());
        assertThat(exception.getConstraintViolations().stream().map(v ->
                new ConstraintViolationInfo(v.getPropertyPath().toString(), v.getMessage()))
        ).containsExactlyInAnyOrderElementsOf(Arrays.asList(violations));
    }
}
