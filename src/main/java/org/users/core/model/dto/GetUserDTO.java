package org.users.core.model.dto;

public record GetUserDTO(
        Long id,
        String email,
        String firstName,
        String lastName,
        String birthDate,
        GetAddressDTO address,
        String phoneNumber
)  {
}
