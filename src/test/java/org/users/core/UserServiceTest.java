package org.users.core;


import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.users.core.model.entities.Address;
import org.users.core.model.entities.User;
import org.users.core.utils.ConstraintViolationInfo;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.users.core.utils.Assertions.assertConstraintViolations;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
public class UserServiceTest {

    @Value("${adult.age}")
    int minAge;

    @Autowired
    UserService userService;

    public record InvalidUser(User user, ConstraintViolationInfo[] errors) {}

    @Test
    @DirtiesContext
    public void testSave_Valid() {
        var user = new User("mockemail@mock.com", "John", "Doe",
                LocalDate.now().minusYears(minAge + 10));
        assertDoesNotThrow(() -> userService.save(user));
    }

    private Stream<?> invalidUsers() {
        var invalidEmailUser = new User("mockemail", "John", "Doe",
                LocalDate.now().minusYears(minAge + 10));
        var invalidFirstNameUser = new User("mockemail@mock.com", "$#", "Doe",
                LocalDate.now().minusYears(minAge + 10));
        var invalidLastNameUser = new User("mockemail@mock.com", "John", "   ",
                LocalDate.now().minusYears(minAge + 10));
        var invalidBirthDateUser = new User("mockemail@mock.com", "John", "Doe",
                LocalDate.now());


        return Stream.of(
                new InvalidUser(invalidEmailUser, new ConstraintViolationInfo[]{
                        new ConstraintViolationInfo("email", "must be a well-formed email address")
                }),
                new InvalidUser(invalidFirstNameUser, new ConstraintViolationInfo[]{
                        new ConstraintViolationInfo("firstName", "must match \"^[a-zA-Z-]+$\"")
                }),
                new InvalidUser(invalidLastNameUser, new ConstraintViolationInfo[]{
                        new ConstraintViolationInfo("lastName", "must match \"^[a-zA-Z-]+$\""),
                        new ConstraintViolationInfo("lastName", "must not be blank")
                }),
                new InvalidUser(invalidBirthDateUser, new ConstraintViolationInfo[]{
                        new ConstraintViolationInfo("birthDate", "Must be a date in the past")
                })
        );

    }

    @ParameterizedTest
    @MethodSource("invalidUsers")
    @DirtiesContext // could possibly dirty the context if test fails and invalid user is saved
    public void testSave_Invalid(InvalidUser userAndErrors) {
        assertConstraintViolations(() -> userService.save(userAndErrors.user), userAndErrors.errors);
    }

    @Test
    public void testExistsById_Exists() {
        assertTrue(userService.existsById(1L));
    }

    @Test
    public void testExistsById_NotExists() {
        assertFalse(userService.existsById(Long.MAX_VALUE));
    }

    @Test
    @DirtiesContext
    public void testDeleteById_Exists() {
        assertDoesNotThrow(() -> userService.deleteById(1L));
        assertFalse(userService.existsById(1L));
    }

    @Test
    @DirtiesContext // could possibly dirty the context if test fails and valid user is deleted
    public void testDeleteById_NotExists() {
        assertThrows(EntityNotFoundException.class, () -> userService.deleteById(Long.MAX_VALUE));
    }

    @Test
    @SuppressWarnings("all") // suppress isEqualTo warning about comparing different optional to user
    public void testFindById_Exists() {
        var actual = new User("joseph26@example.net", "Adam", "Brady", LocalDate.of(1934, 9, 1));
        actual.setAddress(new Address("Anthony Burgs", "New Brianshire", "Iraq", 97225L));
        actual.setId(1L);
        actual.setPhoneNumber("282-500-3002x343");
        assertThat(userService.findById(1L))
                .get()
                .usingRecursiveComparison()
                .ignoringFields("createdAt", "updatedAt")
                .isEqualTo(actual);
    }

    @Test
    public void testFindById_NotExists() {
        assertThat(userService.findById(Long.MAX_VALUE)).isEmpty();
    }

    @Test
    public void testFindByBirthdateBetween() {
        var start = LocalDate.of(1993, 2, 25); // corner case to test inclusive start
        var end = LocalDate.of(2004, 11, 26); // corner case to test inclusive end
        assertThat(userService.findByBirthdateBetween(start, end)).isEmpty();
    }

}
