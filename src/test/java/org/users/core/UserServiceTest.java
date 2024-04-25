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
import org.users.core.utils.CaseAndException;
import org.users.core.utils.CaseAndExplanation;
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

    public record InvalidUser(User user, ConstraintViolationInfo[] errors) {
    }

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
    public void testSave_Null() {
        assertThrows(NullPointerException.class, () -> userService.save(null));
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
    public void testExistsById_Null() {
        assertThrows(NullPointerException.class, () -> userService.existsById(null));
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
    public void testDeleteById_Null() {
        assertThrows(NullPointerException.class, () -> userService.deleteById(null));
    }

    @Test
    @SuppressWarnings("all") // suppress isEqualTo warning about comparing different optional to user
    public void testFindById_Exists() {
        var actual = new User("joseph26@example.net", "Adam", "Brady", LocalDate.of(1934, 9, 1));
        actual.setAddress(new Address(1, "Anthony Burgs", "New Brianshire", "Iraq", "97225"));
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
    public void testFindById_Null() {
        assertThrows(NullPointerException.class, () -> userService.findById(null));
    }

    @Test
    public void testFindByBirthdateBetween() {
        var start = LocalDate.of(1993, 2, 25); // corner case to test inclusive start
        var end = LocalDate.of(2004, 11, 26); // corner case to test inclusive end
        assertThat(userService.findByBirthdateBetween(start, end))
                .map(User::getId)
                .containsExactly(11L, 17L, 20L, 24L, 25L, 28L, 29L, 38L, 47L, 50L);
    }

    public record DateRange(LocalDate start, LocalDate end) {
    }

    public Stream<CaseAndException<DateRange>> invalidDates() {
        return Stream.of(
                new CaseAndException<>(
                        new CaseAndExplanation<>(
                                new DateRange(null, LocalDate.now()),
                                "Null start date is forbidden"),
                        NullPointerException.class
                ),
                new CaseAndException<>(
                        new CaseAndExplanation<>(
                                new DateRange(LocalDate.now(), null),
                                "Null end date is forbidden"),
                        NullPointerException.class
                ),
                new CaseAndException<>(
                        new CaseAndExplanation<>(
                                new DateRange(LocalDate.now(), LocalDate.now().minusDays(1)),
                                "Start date after end date is forbidden"),
                        IllegalArgumentException.class
                )
        );
    }

    @ParameterizedTest
    @MethodSource("invalidDates")
    public void testFindByBirthdateBetween_NullStart(CaseAndException<DateRange> caseAndException) {
        var exception = caseAndException.exceptionType();
        var dateRange = caseAndException.caseAndExplanation();
        assertThrows(
                exception,
                () -> userService.findByBirthdateBetween(dateRange.input().start, dateRange.input().end),
                dateRange.explanation()
        );
    }


}
