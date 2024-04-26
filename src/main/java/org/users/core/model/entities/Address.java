package org.users.core.model.entities;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.NonNull;
import lombok.With;
import org.hibernate.validator.constraints.Range;

import java.io.Serializable;

@Embeddable
@With
public record Address(
        @Positive @NotNull @NonNull Integer houseNumber,
        @NotBlank @NonNull String street,
        @NotBlank @NonNull String city,
        @NotBlank @NonNull String country,
        @Pattern(regexp = "\\d{5}") @NotNull @NonNull String zipCode
) implements Serializable {
}
