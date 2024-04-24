package org.users.core.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.NonNull;

import java.time.LocalDate;

public class BeforeTodayValidator implements ConstraintValidator<AdultBirthday, LocalDate> {

    @Override
    public boolean isValid(@NonNull LocalDate value, ConstraintValidatorContext context) {
        return value.isBefore(LocalDate.now());
    }
}
