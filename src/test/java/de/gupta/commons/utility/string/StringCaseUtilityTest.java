package de.gupta.commons.utility.string;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("StringCaseUtility Tests")
final class StringCaseUtilityTest
{
	private static Stream<Arguments> capitalizeFirstLetterProvider()
	{
		return Stream.of(
				Arguments.of("hello", "Hello", "Lowercase word should have its first letter capitalized"),
				Arguments.of("Hello", "Hello", "Word already starting with uppercase should remain unchanged"),
				Arguments.of("", "", "Empty string should remain empty"),
				Arguments.of(null, null, "Null input should return null"),
				Arguments.of("a", "A", "Single lowercase letter should be capitalized"),
				Arguments.of("A", "A", "Single uppercase letter should remain unchanged"),
				Arguments.of("123abc", "123abc", "String starting with numbers should remain unchanged"),
				Arguments.of("$special", "$special", "String starting with special character should remain unchanged"),
				Arguments.of("camelCase", "CamelCase", "Camel case word should have its first letter capitalized"),
				Arguments.of("UPPERCASE", "UPPERCASE", "All uppercase word should remain unchanged"),
				Arguments.of("lowercase", "Lowercase", "All lowercase word should have its first letter capitalized"),
				Arguments.of("mixed CASE", "Mixed CASE",
						"Mixed case string should have only its first letter capitalized"),
				Arguments.of("  spacePrefix", "  spacePrefix", "String with leading spaces should remain unchanged"),
				Arguments.of("ñoño", "Ñoño", "Non-ASCII lowercase first letter should be capitalized"),
				Arguments.of("Ñoño", "Ñoño", "Non-ASCII uppercase first letter should remain unchanged"),
				Arguments.of("ümlaut", "Ümlaut", "Lowercase umlaut should be capitalized"),
				Arguments.of("Ümlaut", "Ümlaut", "Uppercase umlaut should remain unchanged")
		);
	}

	@ParameterizedTest(name = "{2}")
	@MethodSource("capitalizeFirstLetterProvider")
	@DisplayName("Test capitalizing the first letter of a string")
	void capitalizeFirstLetter(String input, String expected, String testDescription)
	{
		String result = StringCaseUtility.capitalizeFirstLetter(input);
		assertThat(result).as(testDescription).isEqualTo(expected);
	}
}