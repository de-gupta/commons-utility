package de.gupta.commons.utility.string;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("StringUtility Tests")
final class StringFormatUtilityTest
{
	private static Stream<Arguments> startsWithUppercaseProvider()
	{
		return Stream.of(
				Arguments.of("Hello", true, "String starting with uppercase letter should return true"),
				Arguments.of("hello", false, "String starting with lowercase letter should return false"),
				Arguments.of("123ABC", false, "String starting with a number should return false"),
				Arguments.of("UPPERCASE", true, "String with all uppercase letters should return true"),
				Arguments.of("Camel", true, "String in camel case should return true"),
				Arguments.of("", false, "Empty string should return false"),
				Arguments.of("$pecial", false, "String starting with special character should return false"),
				Arguments.of("Ñoño", true, "String starting with non-ASCII uppercase letter should return true"),
				Arguments.of("ñoño", false, "String starting with non-ASCII lowercase letter should return false"),
				Arguments.of("Ümlaut", true, "String starting with uppercase umlaut should return true"),
				Arguments.of("ümlaut", false, "String starting with lowercase umlaut should return false"),
				Arguments.of("A", true, "Single uppercase letter should return true"),
				Arguments.of("a", false, "Single lowercase letter should return false")
		);
	}

	@ParameterizedTest(name = "{2}")
	@MethodSource("startsWithUppercaseProvider")
	@DisplayName("Test if string starts with uppercase letter")
	void startsWithUppercase(String input, boolean expected, String testDescription)
	{
		boolean result = StringFormatUtility.startsWithUppercase(input);
		assertThat(result).as(testDescription).isEqualTo(expected);
	}
}