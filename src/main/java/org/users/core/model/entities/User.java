package org.users.core.model.entities;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.users.core.validation.AdultBirthday;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity(name = "users")
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor
@EntityListeners(AuditingEntityListener.class)
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

    @CreatedDate
    @Column(updatable = false)
    // this field could be null on save as it is ignored on update after creation
    LocalDateTime createdAt;

    @LastModifiedDate
    @NotNull
    LocalDateTime updatedAt;

}
