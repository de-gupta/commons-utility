package de.gupta.commons.utility.string;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("StringSanitizationUtility Tests")
final class StringSanitizationUtilityTest
{
	@ParameterizedTest(name = "{3}")
	@MethodSource("stringEmptinessProvider")
	@DisplayName("Test isStringEmpty method")
	void isStringEmpty(String input, boolean expectedEmpty, boolean expectedNonEmpty, String testDescription)
	{
		boolean result = StringSanitizationUtility.isStringEmpty(input);
		assertThat(result).as(testDescription).isEqualTo(expectedEmpty);
	}

	@ParameterizedTest(name = "{3}")
	@MethodSource("stringEmptinessProvider")
	@DisplayName("Test isStringNonEmpty method")
	void isStringNonEmpty(String input, boolean expectedEmpty, boolean expectedNonEmpty, String testDescription)
	{
		boolean result = StringSanitizationUtility.isStringNonEmpty(input);
		assertThat(result).as(testDescription).isEqualTo(expectedNonEmpty);
	}

	@ParameterizedTest(name = "{3}")
	@MethodSource("stringBlanknessProvider")
	@DisplayName("Test isStringBlank method")
	void isStringBlank(String input, boolean expectedBlank, String testDescription)
	{
		boolean result = StringSanitizationUtility.isStringBlank(input);
		assertThat(result).as(testDescription).isEqualTo(expectedBlank);
	}

	@ParameterizedTest(name = "{3}")
	@MethodSource("stringBlanknessProvider")
	@DisplayName("Test isStringNonBlank method")
	void isStringNonBlank(String input, boolean expectedBlank, String testDescription)
	{
		boolean result = StringSanitizationUtility.isStringNonBlank(input);
		assertThat(result).as(testDescription).isEqualTo(!expectedBlank);
	}

	@ParameterizedTest(name = "{2}")
	@MethodSource("breakIntoLinesDefaultProvider")
	@DisplayName("Test breakIntoLines method with default delimiter")
	void breakIntoLinesDefault(String input, String[] expected, String testDescription)
	{
		String[] result = StringSanitizationUtility.breakIntoLines(input);
		assertThat(result).as(testDescription).isEqualTo(expected);
	}

	@ParameterizedTest(name = "{3}")
		@MethodSource("breakIntoLinesCustomProvider")
		@DisplayName("Test breakIntoLines method with custom delimiter")
		void breakIntoLinesCustom(String input, String delimiter, String[] expected, String testDescription)
		{
			String[] result = StringSanitizationUtility.breakIntoLines(input, delimiter);
			assertThat(result).as(testDescription).isEqualTo(expected);
		}

		@ParameterizedTest(name = "{2}")
		@MethodSource("stringTrimmednessProvider")
		@DisplayName("Test isStringTrimmed method")
		void isStringTrimmed(String input, boolean expectedTrimmed, String testDescription)
		{
			boolean result = StringSanitizationUtility.isStringTrimmed(input);
			assertThat(result).as(testDescription).isEqualTo(expectedTrimmed);
		}

		private static Stream<Arguments> stringEmptinessProvider()
		{
		return Stream.of(
				Arguments.of(null, true, false, "Null string should be considered empty"),
				Arguments.of("", true, false, "Empty string should be considered empty"),
				Arguments.of(" ", false, true, "String with only space should not be considered empty"),
				Arguments.of("\t", false, true, "String with only tab should not be considered empty"),
				Arguments.of("\n", false, true, "String with only newline should not be considered empty"),
				Arguments.of("hello", false, true, "Non-empty string should not be considered empty"),
				Arguments.of("   hello   ", false, true,
						"String with leading/trailing spaces should not be considered empty"),
				Arguments.of("123", false, true, "String with only numbers should not be considered empty"),
				Arguments.of("!@#", false, true, "String with only special characters should not be considered empty")
		);
	}

	private static Stream<Arguments> stringBlanknessProvider()
	{
		return Stream.of(
				Arguments.of(null, true, "Null string should be considered blank"),
				Arguments.of("", true, "Empty string should be considered blank"),
				Arguments.of(" ", true, "String with only space should be considered blank"),
				Arguments.of("\t", true, "String with only tab should be considered blank"),
				Arguments.of("\n", true, "String with only newline should be considered blank"),
				Arguments.of("   ", true, "String with multiple spaces should be considered blank"),
				Arguments.of("\t\n ", true, "String with mixed whitespace should be considered blank"),
				Arguments.of("hello", false, "Non-blank string should not be considered blank"),
				Arguments.of("   hello   ", false,
						"String with content and whitespace should not be considered blank"),
				Arguments.of("123", false, "String with only numbers should not be considered blank"),
				Arguments.of("!@#", false, "String with only special characters should not be considered blank")
		);
	}

	private static Stream<Arguments> breakIntoLinesDefaultProvider()
	{
		return Stream.of(
				Arguments.of("line1\nline2", new String[]{"line1", "line2"},
						"String with Unix newlines should be split correctly"),
				Arguments.of("line1\r\nline2", new String[]{"line1", "line2"},
						"String with Windows newlines should be split correctly"),
				Arguments.of("single line", new String[]{"single line"},
						"String without newlines should remain as a single line"),
				Arguments.of("line1\nline2\nline3", new String[]{"line1", "line2", "line3"},
						"String with multiple lines should be split correctly"),
				Arguments.of("\nline1\nline2", new String[]{"", "line1", "line2"},
						"String starting with newline should include empty first line"),
				Arguments.of("line1\nline2\n", new String[]{"line1", "line2", ""},
						"String ending with newline should include empty last line"),
				Arguments.of("\n\n\n", new String[]{"", "", "", ""},
						"String with only newlines should split into empty lines"),
				Arguments.of("", new String[]{""}, "Empty string should result in array with one empty string")
		);
	}

	private static Stream<Arguments> breakIntoLinesCustomProvider()
	{
		return Stream.of(
				Arguments.of("item1,item2,item3", ",", new String[]{"item1", "item2", "item3"},
					"String with comma delimiter should split correctly"),
				Arguments.of("key=value", "=", new String[]{"key", "value"},
					"String with equals delimiter should split correctly"),
				Arguments.of("word1 word2 word3", " ", new String[]{"word1", "word2", "word3"},
					"String with space delimiter should split correctly"),
				Arguments.of("a|b|c", "\\|", new String[]{"a", "b", "c"},
					"String with pipe delimiter (regex special char) should split correctly"),
				Arguments.of("no-delimiter-here", ";", new String[]{"no-delimiter-here"},
					"String without the delimiter should remain as a single item"),
				Arguments.of("prefix;;suffix", ";", new String[]{"prefix", "", "suffix"},
					"String with consecutive delimiters should include empty items"),
				Arguments.of(";item", ";", new String[]{"", "item"},
					"String starting with delimiter should include empty first item"),
				Arguments.of("item;", ";", new String[]{"item", ""},
					"String ending with delimiter should include empty last item"),
				Arguments.of("", ";", new String[]{""}, "Empty string should result in array with one empty string"),
				Arguments.of("a.b.c", "\\.", new String[]{"a", "b", "c"},
					"String with dot delimiter (regex special char) should split correctly")
		);
	}

	private static Stream<Arguments> stringTrimmednessProvider()
	{
		final String NBSP = "\u00A0"; // non-breaking space
		final String EM_SPACE = "\u2003"; // em space
		return Stream.of(
				Arguments.of(null, false, "Null string should not be considered trimmed"),
				Arguments.of("", true, "Empty string should be considered trimmed"),
				Arguments.of(" ", false, "String with only ASCII space is not trimmed"),
				Arguments.of("\t", false, "String with only tab is not trimmed"),
				Arguments.of("\n", false, "String with only newline is not trimmed"),
				Arguments.of("a", true, "Single-character string without surrounding whitespace is trimmed"),
				Arguments.of("a b", true, "Internal spaces do not affect trimmed status"),
				Arguments.of(" a", false, "Leading ASCII space should make string not trimmed"),
				Arguments.of("a ", false, "Trailing ASCII space should make string not trimmed"),
				Arguments.of("\ta", false, "Leading tab should make string not trimmed"),
				Arguments.of("a\t", false, "Trailing tab should make string not trimmed"),
				Arguments.of("\na", false, "Leading newline should make string not trimmed"),
				Arguments.of("a\n", false, "Trailing newline should make string not trimmed"),
				Arguments.of("  a  ", false, "Leading and trailing spaces should make string not trimmed"),
				Arguments.of("\t a\t", false, "Mixed leading/trailing whitespace should make string not trimmed"),
				Arguments.of(NBSP, true, "NBSP alone is not removed by trim(), so it is considered trimmed"),
				Arguments.of(NBSP + "abc" + NBSP, true, "NBSP around content is considered trimmed by trim()"),
				Arguments.of(EM_SPACE, true, "Unicode em space alone is considered trimmed by trim()"),
				Arguments.of(" " + NBSP, false, "Leading ASCII space is removed, changing string => not trimmed"),
				Arguments.of("\u0000a\u0000", false, "Leading/trailing NUL (<= U+0020) are removed by trim() => not trimmed"),
				Arguments.of("\u0000", false, "Single NUL char becomes empty after trim() => not trimmed")
		);
	}
}