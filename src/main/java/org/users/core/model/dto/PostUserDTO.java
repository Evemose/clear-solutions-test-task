package org.users.core.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.With;
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
@With
public record PostUserDTO(
        @Email @NotNull(groups = Post.class) String email,
        @NotBlank(groups = Post.class) String firstName,
        @NotBlank(groups = Post.class) String lastName,
        @AdultBirthday @NotNull(groups = Post.class) LocalDate birthDate,
        @Valid PostAddressDTO address,
        @Pattern(regexp = "[x0-9()-]{4,20}", groups = Post.class) String phoneNumber
        // as https://en.wikipedia.org/wiki/List_of_mobile_telephone_prefixes_by_country states,
        // the shortest valid phone number is 4 digits, and the longest is 13. We will allow 4-15 digits
        // so parentheses and hyphens can be included in DTO
        // as formatting should not be done on the client side
) {
}
