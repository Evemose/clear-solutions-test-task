package org.users.core.model.entities;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.NonNull;
import org.hibernate.validator.constraints.Range;

@Embeddable
public record Address(
        @Positive @NotNull @NonNull Integer houseNumber,
        @NotBlank @NonNull String street,
        @NotBlank @NonNull String city,
        @NotBlank @NonNull String country,
        @Range(min = 10000, max = 99999) @NotNull @NonNull Integer zipCode
) {
}
