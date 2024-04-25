package org.users.core.model;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.users.core.validation.AdultBirthday;

import java.time.LocalDate;

@Entity(name = "users")
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Email
    @NonNull
    @NotNull
    @Column(unique = true)
    String email;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z-]+$")
    @NonNull
    String firstName;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z-]+$")
    @NonNull
    String lastName;

    @AdultBirthday
    @NonNull
    LocalDate birthDate;

    @Valid
    @Nullable
    Address address;

    @Nullable
    @Column(unique = true)
    String phoneNumber;

}
