package de.gupta.commons.utility.collection;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("SetUtility Tests")
final class SetUtilityTest
{
	private record RemoveBlankStringsTestCase(
			Set<String> input,
			Set<String> expected,
			String description)
	{
	}

	@Nested
	@DisplayName("removeBlankStrings Normal Cases")
	class RemoveBlankStringsNormalCasesTests
	{
		@ParameterizedTest(name = "{2}")
		@MethodSource("removeBlankStringsNormalCasesProvider")
		@DisplayName("Test removeBlankStrings with normal cases")
		void removeBlankStrings_normalCases(RemoveBlankStringsTestCase testCase)
		{
			Set<String> result = SetUtility.removeBlankStrings(testCase.input());
			assertThat(result)
					.as(testCase.description())
					.containsExactlyInAnyOrderElementsOf(testCase.expected());
		}

		private static Stream<RemoveBlankStringsTestCase> removeBlankStringsNormalCasesProvider()
		{
			return Stream.of(
					new RemoveBlankStringsTestCase(
							Set.of("apple", "banana", "cherry"),
							Set.of("apple", "banana", "cherry"),
							"Set with no blank strings should remain unchanged"),

					new RemoveBlankStringsTestCase(
							Set.of("apple", "", "cherry"),
							Set.of("apple", "cherry"),
							"Empty strings should be removed"),

					new RemoveBlankStringsTestCase(
							Set.of("apple", " ", "cherry"),
							Set.of("apple", "cherry"),
							"Strings with only spaces should be removed"),

					new RemoveBlankStringsTestCase(
							Set.of("apple", "\t", "cherry"),
							Set.of("apple", "cherry"),
							"Strings with only tabs should be removed"),

					new RemoveBlankStringsTestCase(
							Set.of("apple", "\n", "cherry"),
							Set.of("apple", "cherry"),
							"Strings with only newlines should be removed"),

					new RemoveBlankStringsTestCase(
							Set.of("apple", " \t\n\r", "cherry"),
							Set.of("apple", "cherry"),
							"Strings with mixed whitespace should be removed"),

					new RemoveBlankStringsTestCase(
							Set.of("apple", "  banana  ", "cherry"),
							Set.of("apple", "  banana  ", "cherry"),
							"Strings with leading/trailing spaces but non-blank content should be preserved")
			);
		}
	}

	@Nested
	@DisplayName("removeBlankStrings Edge Cases")
	class RemoveBlankStringsEdgeCasesTests
	{
		@ParameterizedTest(name = "{2}")
		@MethodSource("removeBlankStringsEdgeCasesProvider")
		@DisplayName("Test removeBlankStrings with edge cases")
		void removeBlankStrings_edgeCases(RemoveBlankStringsTestCase testCase)
		{
			Set<String> result = SetUtility.removeBlankStrings(testCase.input());
			assertThat(result)
					.as(testCase.description())
					.containsExactlyInAnyOrderElementsOf(testCase.expected());
		}

		private static Stream<RemoveBlankStringsTestCase> removeBlankStringsEdgeCasesProvider()
		{
			return Stream.of(
					new RemoveBlankStringsTestCase(
							Set.of(),
							Set.of(),
							"Empty set should return empty set"),

					new RemoveBlankStringsTestCase(
							Set.of("", " ", "\t", "\n"),
							Set.of(),
							"Set with only blank strings should return empty set"),

					new RemoveBlankStringsTestCase(
							Set.of("\u2000", "\u2001", "\u2002"),  // Unicode whitespace characters
							Set.of(),
							"Unicode whitespace characters should be recognized as blank"),

					new RemoveBlankStringsTestCase(
							Set.of("a", "a ", " a", " a "),
							Set.of("a", "a ", " a", " a "),
							"Similar strings with different whitespace should all be preserved")
			);
		}
	}

	@Nested
	@DisplayName("removeBlankStrings Error Cases")
	class RemoveBlankStringsErrorCasesTests
	{
		@ParameterizedTest(name = "Should throw exception with message: {1}")
		@MethodSource("removeBlankStringsErrorCasesProvider")
		@DisplayName("Test removeBlankStrings with error cases")
		void removeBlankStrings_errorCases(Set<String> input, String expectedErrorMessage)
		{
			assertThatThrownBy(() -> SetUtility.removeBlankStrings(input))
					.as("Method should throw IllegalArgumentException with appropriate message")
					.isInstanceOf(IllegalArgumentException.class)
					.hasMessage(expectedErrorMessage);
		}

		private static Stream<Arguments> removeBlankStringsErrorCasesProvider()
		{
			return Stream.of(Arguments.of(null, "Input set cannot be null")
			);
		}
	}
}