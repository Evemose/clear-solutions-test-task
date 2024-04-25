package org.users.core.model.dto;

public record PostUserDTO(
        String email,
        String firstName,
        String lastName,
        String birthDate,
        PostAddressDTO address,
        String phoneNumber
) {
}
