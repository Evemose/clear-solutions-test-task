package org.users.core.model;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.NonNull;
import lombok.With;

@Embeddable
@With
public record Address(
        @NotBlank @NonNull String street,
        @NotBlank @NonNull String city,
        @NotBlank @NonNull String country,
        @Positive @NotNull @NonNull Long zipCode
) {
}
