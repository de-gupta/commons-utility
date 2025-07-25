package de.gupta.commons.utility.string;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("StringTokenizeUtility Tests")
final class StringTokenizeUtilityTest
{
	private record TokenizeWithStringTestCase(String input, String delimiter, String[] expected, String description)
	{
	}

	private record TokenizeWithSetTestCase(String input, Set<String> delimiters, String[] expected, String description)
	{
	}

	@Nested
	@DisplayName("tokenize with String delimiter - Normal Cases")
	class TokenizeWithStringNormalCasesTests
	{
		@ParameterizedTest(name = "{3}")
		@MethodSource("tokenizeWithStringNormalCasesProvider")
		@DisplayName("Test tokenize with string delimiter - normal cases")
		void tokenize_withStringDelimiter_normalCases(String input, String delimiter, String[] expected, String description)
		{
			String[] result = StringTokenizeUtility.tokenize(input, delimiter);
			assertThat(result).as(description).containsExactly(expected);
		}

		private static Stream<Arguments> tokenizeWithStringNormalCasesProvider()
		{
			return Stream.of(new TokenizeWithStringTestCase("apple,banana,cherry", ",",
							new String[]{"apple", "banana", "cherry"}, "Simple comma-separated string should split correctly"),

					new TokenizeWithStringTestCase("apple banana cherry", " ",
							new String[]{"apple", "banana", "cherry"}, "Space-separated string should split correctly"),

					new TokenizeWithStringTestCase("apple-banana-cherry", "-",
							new String[]{"apple", "banana", "cherry"},
							"Hyphen-separated string should split correctly"),

					new TokenizeWithStringTestCase("key=value", "=", new String[]{"key", "value"},
							"Key-value pair should split correctly with equals delimiter"),

                    new TokenizeWithStringTestCase("2023/07/25", "/",
							new String[]{"2023", "07", "25"},
							"Date format with slash delimiter should split correctly"),

                    new TokenizeWithStringTestCase("192.168.0.1", "\\.",
							new String[]{"192", "168", "0", "1"},
							"IP address with dot delimiter should split correctly"),

                    new TokenizeWithStringTestCase("user@example.com", "@",
							new String[]{"user", "example.com"},
							"Email address with @ delimiter should split correctly"),

                    new TokenizeWithStringTestCase("firstName:John;lastName:Doe;age:30", ";",
							new String[]{"firstName:John", "lastName:Doe", "age:30"},
							"Semi-colon separated key-value pairs should split correctly"),

                    new TokenizeWithStringTestCase("one_two_three", "_",
							new String[]{"one", "two", "three"},
							"Underscore-separated string should split correctly")
			).map(tc -> Arguments.of(tc.input(), tc.delimiter(), tc.expected(), tc.description()));
		}
	}

	@Nested
	@DisplayName("tokenize with String delimiter - Edge Cases")
	class TokenizeWithStringEdgeCasesTests
	{
		@ParameterizedTest(name = "{3}")
		@MethodSource("tokenizeWithStringEdgeCasesProvider")
		@DisplayName("Test tokenize with string delimiter - edge cases")
		void tokenize_withStringDelimiter_edgeCases(String input, String delimiter, String[] expected, String description)
		{
			String[] result = StringTokenizeUtility.tokenize(input, delimiter);
			assertThat(result).as(description).containsExactly(expected);
		}

		private static Stream<Arguments> tokenizeWithStringEdgeCasesProvider()
		{
			return Stream.of(new TokenizeWithStringTestCase("", ",", new String[]{""},
							"Empty string should return array with single empty string"),

					new TokenizeWithStringTestCase("singleword", ",", new String[]{"singleword"},
							"String without delimiter should return array with single element"),

					new TokenizeWithStringTestCase(",", ",", new String[]{},
							"String with only delimiter should return an empty array"),

					new TokenizeWithStringTestCase("apple,,cherry", ",", new String[]{"apple", "", "cherry"},
							"String with consecutive delimiters should include empty elements"),

					new TokenizeWithStringTestCase(",apple,banana,cherry", ",",
							new String[]{"", "apple", "banana", "cherry"},
							"String starting with delimiter should include empty first element"),

					new TokenizeWithStringTestCase("apple,banana,cherry,", ",",
							new String[]{"apple", "banana", "cherry"},
							"String ending with delimiter should not include empty last element"),

					new TokenizeWithStringTestCase("apple.banana.cherry", "\\.",
							new String[]{"apple", "banana", "cherry"},
							"String with regex special character as delimiter should split correctly"),

					new TokenizeWithStringTestCase("apple|banana|cherry", "\\|",
							new String[]{"apple", "banana", "cherry"},
							"String with pipe character as delimiter should split correctly"),

                    new TokenizeWithStringTestCase(",apple,banana,cherry,", ",",
							new String[]{"", "apple", "banana", "cherry"},
							"String with delimiters at both start and end should handle both cases correctly"),

                    new TokenizeWithStringTestCase("a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z", ",",
							new String[]{"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"},
							"Very long string with many tokens should split correctly"),

                    new TokenizeWithStringTestCase("apple###banana###cherry", "###",
							new String[]{"apple", "banana", "cherry"},
							"Multi-character delimiter should split correctly"),

                    new TokenizeWithStringTestCase("café,résumé,naïve", ",",
							new String[]{"café", "résumé", "naïve"},
							"String with Unicode characters should split correctly")
			).map(tc -> Arguments.of(tc.input(), tc.delimiter(), tc.expected(), tc.description()));
		}
	}

	@Nested
	@DisplayName("tokenize with String delimiter - Special Cases")
	class TokenizeWithStringSpecialCasesTests
	{
		@ParameterizedTest(name = "{3}")
		@MethodSource("tokenizeWithStringSpecialCasesProvider")
		@DisplayName("Test tokenize with string delimiter - special cases")
		void tokenize_withStringDelimiter_specialCases(TokenizeWithStringTestCase testCase)
		{
			String[] result = StringTokenizeUtility.tokenize(testCase.input(), testCase.delimiter());
			assertThat(result).as(testCase.description()).containsExactly(testCase.expected());
		}

		private static Stream<TokenizeWithStringTestCase> tokenizeWithStringSpecialCasesProvider()
		{
			return Stream.of(new TokenizeWithStringTestCase("apple\nbanana\ncherry", "\n",
							new String[]{"apple", "banana", "cherry"}, "Newline-separated string should split correctly"),

					new TokenizeWithStringTestCase("apple\tbanana\tcherry", "\t",
							new String[]{"apple", "banana", "cherry"}, "Tab-separated string should split correctly"),

					new TokenizeWithStringTestCase("apple+banana+cherry", "\\+",
							new String[]{"apple", "banana", "cherry"},
							"Plus character as delimiter (regex special char) should split correctly"),

					new TokenizeWithStringTestCase("apple*banana*cherry", "\\*",
							new String[]{"apple", "banana", "cherry"},
							"Asterisk character as delimiter (regex special char) should split correctly"),

					new TokenizeWithStringTestCase("apple?banana?cherry", "\\?",
							new String[]{"apple", "banana", "cherry"},
							"Question mark as delimiter (regex special char) should split correctly"),

					new TokenizeWithStringTestCase("apple  banana  cherry", "  ",
							new String[]{"apple", "banana", "cherry"},
							"Multiple spaces as delimiter should split correctly"));
		}
	}

	@Nested
	@DisplayName("tokenize with String delimiter - Error Cases")
	class TokenizeWithStringErrorCasesTests
	{
		@ParameterizedTest(name = "{2}")
		@MethodSource("tokenizeWithStringErrorCasesProvider")
		@DisplayName("Test tokenize with string delimiter - error cases")
		void tokenize_withStringDelimiter_errorCases(String input, String delimiter, String description)
		{
			assertThatThrownBy(() -> StringTokenizeUtility.tokenize(input, delimiter)).as(description).isInstanceOf(
					NullPointerException.class);
		}

		private static Stream<Arguments> tokenizeWithStringErrorCasesProvider()
		{
			return Stream.of(Arguments.of(null, ",", "Null input should throw NullPointerException"),
					Arguments.of("apple,banana,cherry", null, "Null delimiter should throw NullPointerException"));
		}
	}

	@Nested
	@DisplayName("tokenize with Set of delimiters - Normal Cases")
	class TokenizeWithSetNormalCasesTests
	{
		@ParameterizedTest(name = "{3}")
		@MethodSource("tokenizeWithSetNormalCasesProvider")
		@DisplayName("Test tokenize with set of delimiters - normal cases")
		void tokenize_withSetOfDelimiters_normalCases(String input, Set<String> delimiters, String[] expected, String description)
		{
			String[] result = StringTokenizeUtility.tokenize(input, delimiters);
			assertThat(result).as(description).containsExactlyInAnyOrder(expected);
		}

		private static Stream<Arguments> tokenizeWithSetNormalCasesProvider()
		{
			return Stream.of(new TokenizeWithSetTestCase("apple,banana,cherry", Set.of(","),
							new String[]{"apple", "banana", "cherry"},
							"Set with single delimiter should work like string delimiter"),

					new TokenizeWithSetTestCase("apple,banana;cherry", Set.of(",", ";"),
							new String[]{"apple", "banana", "cherry"},
							"String with mixed delimiters should split on all delimiters"),

					new TokenizeWithSetTestCase("key=value,item1;item2", Set.of(",", ";", "="),
							new String[]{"key", "value", "item1", "item2"},
							"String with multiple delimiter types should split on all delimiters"),

                    new TokenizeWithSetTestCase("2023/07/25 14:30:45", Set.of("/", " ", ":"),
							new String[]{"2023", "07", "25", "14", "30", "45"},
							"Date and time with multiple delimiters should split correctly"),

                    new TokenizeWithSetTestCase("192.168.0.1:8080", Set.of(".", ":"),
							new String[]{"192", "168", "0", "1", "8080"},
							"IP address and port with multiple delimiters should split correctly"),

                    new TokenizeWithSetTestCase("firstName:John;lastName:Doe;age:30", Set.of(";", ":"),
							new String[]{"firstName", "John", "lastName", "Doe", "age", "30"},
							"Key-value pairs with multiple delimiters should split correctly"),

                    new TokenizeWithSetTestCase("one_two-three.four", Set.of("_", "-", "."),
							new String[]{"one", "two", "three", "four"},
							"String with multiple word separators should split correctly"),

                    new TokenizeWithSetTestCase("user@example.com", Set.of("@", "."),
							new String[]{"user", "example", "com"},
							"Email address with multiple delimiters should split correctly")
			).map(tc -> Arguments.of(tc.input(), tc.delimiters(), tc.expected(), tc.description()));
		}
	}

	@Nested
	@DisplayName("tokenize with Set of delimiters - Edge Cases")
	class TokenizeWithSetEdgeCasesTests
	{
		@ParameterizedTest(name = "{3}")
		@MethodSource("tokenizeWithSetEdgeCasesProvider")
		@DisplayName("Test tokenize with set of delimiters - edge cases")
		void tokenize_withSetOfDelimiters_edgeCases(String input, Set<String> delimiters, String[] expected, String description)
		{
			String[] result = StringTokenizeUtility.tokenize(input, delimiters);
			assertThat(result).as(description + "\nExpected: " + Arrays.toString(
									  expected) + "\nActual: " + Arrays.toString(result))
							  .containsExactlyInAnyOrder(expected);
		}

		private static Stream<Arguments> tokenizeWithSetEdgeCasesProvider()
		{
			return Stream.of(new TokenizeWithSetTestCase("", Set.of(",", ";"), new String[]{""},
							"Empty string should return array with single empty string regardless of delimiters"),

					new TokenizeWithSetTestCase("singleword", Set.of(",", ";"), new String[]{"singleword"},
							"String without any delimiters should return array with single element"),

					new TokenizeWithSetTestCase("apple", Set.of(), new String[]{"apple"},
							"Empty set of delimiters should return input as single element"),

					new TokenizeWithSetTestCase(",;", Set.of(",", ";"), new String[]{},
							"String with only delimiters should return an empty array"),

                    new TokenizeWithSetTestCase(",apple;banana,cherry;", Set.of(",", ";"), new String[]{"", "apple", "banana", "cherry"},
							"String with delimiters at both start and end should include first empty element but not " +
									"last"),

                    new TokenizeWithSetTestCase("apple,,banana;;cherry", Set.of(",", ";"), new String[]{"apple", "", "banana", "", "cherry"},
							"String with consecutive delimiters should include empty elements"),

                    new TokenizeWithSetTestCase("a,b;c,d;e,f;g", Set.of(",", ";"),
                            new String[]{"a", "b", "c", "d", "e", "f", "g"},
							"String with alternating delimiters should split on all delimiters"),

                    new TokenizeWithSetTestCase("café;résumé,naïve", Set.of(",", ";"),
							new String[]{"café", "résumé", "naïve"},
							"String with Unicode characters should split correctly with multiple delimiters"),

                    new TokenizeWithSetTestCase("a|b|c", Set.of("|"), new String[]{"a", "b", "c"},
							"Set with pipe character delimiter should split correctly"),

                    new TokenizeWithSetTestCase("key=value;item1,item2", Set.of("=", ";", ",", ":", "|", "/"),
							new String[]{"key", "value", "item1", "item2"},
							"Set with many delimiters should split on all specified delimiters")
			).map(tc -> Arguments.of(tc.input(), tc.delimiters(), tc.expected(), tc.description()));
		}
	}

	@Nested
	@DisplayName("tokenize with Set of delimiters - Error Cases")
	class TokenizeWithSetErrorCasesTests
	{
		@ParameterizedTest(name = "{2}")
		@MethodSource("tokenizeWithSetErrorCasesProvider")
		@DisplayName("Test tokenize with set of delimiters - error cases")
		void tokenize_withSetOfDelimiters_errorCases(String input, Set<String> delimiters, String description)
		{
			assertThatThrownBy(() -> StringTokenizeUtility.tokenize(input, delimiters)).as(description).isInstanceOf(
					NullPointerException.class);
		}

		private static Stream<Arguments> tokenizeWithSetErrorCasesProvider()
		{
			return Stream.of(Arguments.of(null, Set.of(",", ";"), "Null input should throw NullPointerException"),
					Arguments.of("apple,banana;cherry", null, "Null delimiters set should throw NullPointerException"),
					Arguments.of("apple,banana;cherry",
							new HashSet<>(Arrays.asList(null, ",")),
							"Set containing null delimiter should throw NullPointerException"));
		}
	}
}