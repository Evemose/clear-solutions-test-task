package org.users.core.validation;

import java.time.LocalDate;
import java.util.stream.Stream;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.users.core.utils.CaseAndExplanation;
import org.users.core.utils.PropertiesAwareTest;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AdultValidatorTest extends PropertiesAwareTest {

	final AdultValidator adultValidator = new AdultValidator();

	int adultAge;

	@BeforeAll
	public void setUp() throws Exception {
		adultAge = Integer.parseInt(properties.getProperty("adult.age"));
		var adultAgeField = AdultValidator.class.getDeclaredField("adultAge");
		adultAgeField.setAccessible(true);
		adultAgeField.set(adultValidator, adultAge);
	}

	@Test
	public void isValidTest_Valid() {
        assertTrue(adultValidator.isValid(LocalDate.now().minusYears(adultAge).minusDays(1), null));
		assertTrue(adultValidator.isValid(null, null));
	}

	private Stream<CaseAndExplanation<LocalDate>> invalidDates() {
		return Stream.of(
				new CaseAndExplanation<>(LocalDate.now(),
						"today is not a valid birth date, must be at least %s years ago"
								.formatted(properties.getProperty("adult.age"))),
				new CaseAndExplanation<>(LocalDate.now().minusYears(adultAge),
						"if persons birthday is today, they are not considered an adult, " +
								"for more information see AdultValidator.isValid"
				)
		);
	}

	@ParameterizedTest
	@MethodSource("invalidDates")
	public void isValidTest_Invalid(CaseAndExplanation<LocalDate> testCase) {
		assertFalse(adultValidator.isValid(testCase.input(), null), testCase.explanation());
	}
}
