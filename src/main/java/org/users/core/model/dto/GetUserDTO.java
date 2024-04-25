package org.users.core.model.dto;

import java.time.LocalDate;

public record GetUserDTO(
        Long id,
        String email,
        String firstName,
        String lastName,
        LocalDate birthDate,
        GetAddressDTO address,
        String phoneNumber
)  {
}
