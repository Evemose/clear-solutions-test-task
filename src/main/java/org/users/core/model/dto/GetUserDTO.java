package org.users.core.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "The user to get",
        example = """
                {
                    "id": 1,
                    "email": "johndoe@example.com",
                    "firstName": "John",
                    "lastName": "Doe",
                    "birthDate": "2000-01-01",
                    "address": {
                        "houseNumber": 123,
                        "street": "Main St",
                        "city": "Springfield",
                        "country": "USA",
                        "zipCode": "33000"
                    },
                    "phoneNumber": "555-555-5555",
                    "createdAt": "2021-01-01",
                    "updatedAt": "2021-01-01"
                }
                """
)
        public record GetUserDTO(
        Long id,
        String email,
        String firstName,
        String lastName,
        LocalDate birthDate,
        GetAddressDTO address,
        String phoneNumber,
        LocalDate createdAt,
        LocalDate updatedAt
){ }
