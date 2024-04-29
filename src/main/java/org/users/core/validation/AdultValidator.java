package org.users.core.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;


public class AdultValidator implements ConstraintValidator<AdultBirthday, LocalDate> {

    @Value("${adult.age}")
    int adultAge;

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        // in most jurisdictions, a person is considered an adult at then next day after their birthday,
        // i.e. if the person was born on 2000-01-01, they are considered an adult on 2018-01-02,
        // because age increments on the next day after the birthday, not on the birthday itself
        // that's why we use value.isBefore(LocalDate.now().minusYears(adultAge))
        // instead of !LocalDate.now().minusYears(adultAge).isAfter(value)
        var validationResult = value == null || value.isBefore(LocalDate.now().minusYears(adultAge));
        if (!validationResult) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("must be at least %d years ago".formatted(adultAge))
                    .addConstraintViolation();
        }
        return validationResult;
    }
}
