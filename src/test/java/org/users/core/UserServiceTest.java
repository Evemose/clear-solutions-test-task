package org.users.core;


import org.antlr.v4.runtime.misc.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.users.core.model.User;
import org.users.core.testutils.ConstraintViolationInfo;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.users.core.testutils.Assertions.assertConstraintViolations;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
public class UserServiceTest {

    @Value("${users.min-age}")
    int minAge;

    @Autowired
    UserService userService;

    public record InvalidUser(User user, ConstraintViolationInfo[] errors) {}

    @Test
    public void testSave_Valid() {
        var user = new User("mockemail@mock.com", "John", "Doe",
                LocalDate.now().minusYears(minAge + 10));
        var savedUser = userService.save(user);
        Assertions.assertEquals(user, savedUser);
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
    public void testSave_Invalid(InvalidUser userAndErrors) {
        assertConstraintViolations(() -> userService.save(userAndErrors.user), userAndErrors.errors);
    }

}
