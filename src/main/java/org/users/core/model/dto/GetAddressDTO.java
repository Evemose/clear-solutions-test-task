package org.users.core.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "The address of a user",
        example = """
                {
                    "street": "Main St",
                    "city": "Springfield",
                    "country": "USA",
                    "zipCode": 33000
                }
                """)
public record GetAddressDTO(
        String street,
        String city,
        String country,
        Long zipCode
) {
}
