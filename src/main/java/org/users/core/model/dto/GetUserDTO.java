package org.users.core.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.With;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
                    "createdAt": "2015-10-01 23:00:12",
                    "updatedAt": "2021-12-15 16:32:45"
                }
                """
)
@With
public record GetUserDTO(
        Long id,
        String email,
        String firstName,
        String lastName,
        LocalDate birthDate,
        GetAddressDTO address,
        String phoneNumber,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime createdAt,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime updatedAt
) {
}
