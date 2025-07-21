package de.gupta.commons.utility.collection;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("StreamUtility Tests")
final class StreamUtilityTest
{
	private static final Function<Stream<?>, Collection<?>> DEFAULT_COLLECTOR = Stream::toList;

	@SuppressWarnings("unchecked")
	public static <T> Collection<T> toCollection(Stream<T> stream)
	{
		return (Collection<T>) DEFAULT_COLLECTOR.apply(stream);
	}

	private record SplitTestCase(String input, String delimiter, Stream<String> expected, String description)
	{
	}

	@Nested
	@DisplayName("splitToStream Normal Cases")
	class SplitToStreamNormalCasesTests
	{
		private static Stream<SplitTestCase> splitToStreamNormalCasesProvider()
		{
			return Stream.of(
					new SplitTestCase("a,b,c", ",", Stream.of("a", "b", "c"),
							"Simple comma-separated stream should split correctly"),
					new SplitTestCase("apple,banana,cherry", ",", Stream.of("apple", "banana", "cherry"),
							"Words with comma delimiter should split correctly"),

					new SplitTestCase("a, b, c", ",", Stream.of("a", " b", " c"),
							"Values with spaces after delimiter are preserved"),
					new SplitTestCase(" a,b ,c ", ",", Stream.of(" a", "b ", "c "),
							"Values with spaces before and after are preserved"),

					new SplitTestCase("a|b|c", "|", Stream.of("a", "b", "c"),
							"Pipe delimiter should split correctly"),
					new SplitTestCase("a b c", " ", Stream.of("a", "b", "c"),
							"Space delimiter should split correctly"),
					new SplitTestCase("a.b.c", "\\.", Stream.of("a", "b", "c"),
							"Dot delimiter (regex special char) should split correctly"),
					new SplitTestCase("a.b.c", ".", Stream.of("a", "b", "c"),
							"Dot delimiter (regex special char without escaping) should split correctly"),
					new SplitTestCase("a/b/c", "/", Stream.of("a", "b", "c"),
							"/ delimiter should split correctly"),
					new SplitTestCase("a;b;c", ";", Stream.of("a", "b", "c"),
							"Semicolon delimiter should split correctly")
			);
		}

		@ParameterizedTest(name = "{0}")
		@MethodSource("splitToStreamNormalCasesProvider")
		@DisplayName("Test splitToStream with normal cases")
		void splitToStream_normalCases(SplitTestCase testCase)
		{
			Stream<String> result = StreamUtility.splitToStream(testCase.input(), testCase.delimiter());
			Collection<String> resultCollection = toCollection(result);
			Collection<String> expectedCollection = toCollection(testCase.expected);
			assertThat(resultCollection).as(testCase.description()).isEqualTo(expectedCollection);
		}
	}

	@Nested
	@DisplayName("splitToStream Edge Cases")
	class SplitToStreamEdgeCasesTests
	{
		private static Stream<SplitTestCase> splitToStreamEdgeCasesProvider()
		{
			return Stream.of(
					new SplitTestCase("", ",", Stream.of(""),
							"Empty string should result in a stream with one empty element"),

					new SplitTestCase("single", ",", Stream.of("single"),
							"String without delimiter should result in a single element stream"),

					new SplitTestCase("a,,c", ",", Stream.of("a", "", "c"),
							"Consecutive delimiters should include empty elements"),
					new SplitTestCase(",b,c", ",", Stream.of("", "b", "c"),
							"String starting with delimiter should include empty first element"),
					new SplitTestCase("a,b,", ",", Stream.of("a", "b", ""),
							"String ending with delimiter should include empty last element"),
					new SplitTestCase(",,,", ",", Stream.of("", "", "", ""),
							"String with only delimiters should split into empty elements"),

					new SplitTestCase("  ", ",", Stream.of("  "),
							"String with only spaces is preserved"),
					new SplitTestCase(" , , ", ",", Stream.of(" ", " ", " "),
							"Spaces around delimiters are preserved"),

					new SplitTestCase("a*b*c", "\\*", Stream.of("a", "b", "c"),
							"Asterisk delimiter (regex special char) should split correctly"),
					new SplitTestCase("a+b+c", "\\+", Stream.of("a", "b", "c"),
							"Plus delimiter (regex special char) should split correctly")
			);
		}

		@ParameterizedTest(name = "{0}")
		@MethodSource("splitToStreamEdgeCasesProvider")
		@DisplayName("Test splitToStream with edge cases")
		void splitToStream_edgeCases(SplitTestCase testCase)
		{
			Stream<String> result = StreamUtility.splitToStream(testCase.input(), testCase.delimiter());
			Collection<String> resultCollection = toCollection(result);
			Collection<String> expectedCollection = toCollection(testCase.expected());
			assertThat(resultCollection).as(testCase.description()).isEqualTo(expectedCollection);
		}
	}

	@Nested
	@DisplayName("splitToStream Error Cases")
	class SplitToStreamErrorCasesTests
	{
		private static Stream<Arguments> splitToStreamErrorCasesProvider()
		{
			return Stream.of(
					Arguments.of(null, ",", "Input string cannot be null")
			);
		}

		@ParameterizedTest(name = "Should throw exception with message: {2}")
		@MethodSource("splitToStreamErrorCasesProvider")
		@DisplayName("Test splitToStream with error cases")
		void splitToStream_errorCases(String input, String delimiter, String expectedErrorMessage)
		{
			assertThatThrownBy(() -> StreamUtility.splitToStream(input, delimiter))
					.as("Method should throw IllegalArgumentException with appropriate message")
					.isInstanceOf(IllegalArgumentException.class)
					.hasMessage(expectedErrorMessage);
		}
	}
}