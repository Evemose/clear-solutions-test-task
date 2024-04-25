package org.users.core.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.users.core.validation.AdultBirthday;
import org.users.core.validation.Post;

import java.time.LocalDate;

@Schema(description = "The user to create",
        example = """
                {
                    "email": "johndoe@example.com",
                    "firstName": "John",
                    "lastName": "Doe",
                    "birthDate": "2000-01-01",
                    "address": {
                        "houseNumber": "123",
                        "street": "Elm St",
                        "city": "Springfield",
                        "country": "USA",
                        "zipCode": "62701"
                    },
                    "phoneNumber": "555-555-5555"
                }
                """)
public record PostUserDTO(
        @Email @NotNull(groups = Post.class) String email,
        @NotBlank(groups = Post.class) String firstName,
        @NotBlank(groups = Post.class) String lastName,
        @AdultBirthday LocalDate birthDate,
        @Valid PostAddressDTO address,
        @NotBlank(groups = Post.class) String phoneNumber
) {
}
