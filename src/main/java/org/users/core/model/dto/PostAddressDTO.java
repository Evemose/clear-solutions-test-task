package org.users.core.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.users.core.validation.Post;

@Schema(description = "The address of a user",
        example = """
                {
                    "houseNumber": 123,
                    "street": "Main St",
                    "city": "Springfield",
                    "country": "USA",
                    "zipCode": "33000"
                }
                """)
public record PostAddressDTO(
        @NotNull(groups = Post.class) @Min(1) Integer houseNumber,
        @NotBlank(groups = Post.class) String street,
        @NotBlank(groups = Post.class) String city,
        @NotBlank(groups = Post.class) String country,
        @NotNull(groups = Post.class) @Min(10000) @Max(99999) Integer zipCode // 5-digit zip code,
        // validation could be replaced with @Range(min = 10000, max = 99999), but swagger does not support it
) {
}
