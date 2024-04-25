package org.users.core.model.dto;

public record GetAddressDTO(
        String street,
        String city,
        String country,
        Long zipCode
) {
}
