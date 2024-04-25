package org.users.core.model.dto;

public record PostAddressDTO(
        String street,
        String city,
        String country,
        String zipCode
) {
}
