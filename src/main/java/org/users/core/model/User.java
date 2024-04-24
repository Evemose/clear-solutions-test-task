package org.users.core.model;

import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.users.core.validation.BeforeToday;

import java.time.LocalDate;

@Entity
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z-]+$")
    String firstName;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z-]+$")
    String lastName;

    @BeforeToday
    LocalDate birthDate;

    @Valid
    @Nullable
    Address address;

    @Pattern(regexp = "^\\+[0-9]{9}$")
    @Nullable
    String phoneNumber;

}
