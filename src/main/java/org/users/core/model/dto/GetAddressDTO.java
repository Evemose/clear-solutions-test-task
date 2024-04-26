package org.users.core.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.With;

@Schema(description = "The address of a user",
        example = """
                {
                    "houseNumber": 123,
                    "street": "Main St",
                    "city": "Springfield",
                    "country": "USA",
                    "zipCode": 33000
                }
                """)
@With
public record GetAddressDTO(
        Integer houseNumber,
        String street,
        String city,
        String country,
        String zipCode
) {
}
