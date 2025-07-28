package de.gupta.commons.utility.comparison;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Comparator;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ComparisonUtility Tests")
class ComparisonUtilityTest
{
	@Nested
	@DisplayName("doesThisValueSatisfyTheComparison Tests")
	class DoesThisValueSatisfyTheComparisonTests
	{
		@ParameterizedTest(name = "{4}")
		@MethodSource("integerComparisonProvider")
		@DisplayName("Test integer comparisons")
		void testIntegerComparisons(Integer value, Integer reference, ComparisonType comparisonType, boolean expected,
									String description)
		{
			boolean result = ComparisonUtility.doesThisValueSatisfyTheComparison(value, reference, comparisonType,
					Integer::compare);

			assertThat(result).as(description).isEqualTo(expected);
		}

		@ParameterizedTest(name = "{4}")
		@MethodSource("stringComparisonProvider")
		@DisplayName("Test string comparisons")
		void testStringComparisons(String value, String reference, ComparisonType comparisonType, boolean expected,
								   String description)
		{
			boolean result = ComparisonUtility.doesThisValueSatisfyTheComparison(value, reference, comparisonType,
					String::compareTo);

			assertThat(result).as(description).isEqualTo(expected);
		}

		@ParameterizedTest(name = "{4}")
		@MethodSource("customComparatorProvider")
		@DisplayName("Test with custom string comparator")
		void testCustomComparator(String value, String reference, ComparisonType comparisonType, boolean expected,
								  String description)
		{
			Comparator<String> caseInsensitiveComparator = String::compareToIgnoreCase;

			boolean result = ComparisonUtility.doesThisValueSatisfyTheComparison(value, reference, comparisonType,
					caseInsensitiveComparator);

			assertThat(result).as(description).isEqualTo(expected);
		}

		@ParameterizedTest(name = "{5}")
		@MethodSource("customIntegerComparatorProvider")
		@DisplayName("Test with custom integer comparator")
		void testCustomIntegerComparator(Integer value, Integer reference, ComparisonType comparisonType,
										 Comparator<Integer> comparator, boolean expected, String description)
		{
			boolean result =
					ComparisonUtility.doesThisValueSatisfyTheComparison(value, reference, comparisonType, comparator);

			assertThat(result).as(description).isEqualTo(expected);
		}

		private static Stream<Arguments> integerComparisonProvider()
		{
			return Stream.of(
								 // EQUAL comparison tests
								 IntegerComparisonTestCase.of(5, 5, ComparisonType.EQUAL, true,
										 "Equal integers should satisfy EQUAL comparison"),
								 IntegerComparisonTestCase.of(10, 5, ComparisonType.EQUAL, false,
										 "Different integers should not satisfy EQUAL comparison"),
								 IntegerComparisonTestCase.of(null, null, ComparisonType.EQUAL, false,
										 "Null values should not satisfy EQUAL comparison"),
								 IntegerComparisonTestCase.of(null, null, ComparisonType.GREATER_THAN, false,
										 "Null values should not satisfy GREATER_THAN comparison"),

								 // GREATER_THAN comparison tests
								 IntegerComparisonTestCase.of(10, 5, ComparisonType.GREATER_THAN, true,
										 "Larger value should satisfy GREATER_THAN comparison"),
								 IntegerComparisonTestCase.of(5, 5, ComparisonType.GREATER_THAN, false,
										 "Equal values should not satisfy GREATER_THAN comparison"),
								 IntegerComparisonTestCase.of(3, 5, ComparisonType.GREATER_THAN, false,
										 "Smaller value should not satisfy GREATER_THAN comparison"),

								 // GREATER_THAN_OR_EQUAL comparison tests
								 IntegerComparisonTestCase.of(10, 5, ComparisonType.GREATER_THAN_OR_EQUAL, true,
										 "Larger value should satisfy GREATER_THAN_OR_EQUAL comparison"),
								 IntegerComparisonTestCase.of(5, 5, ComparisonType.GREATER_THAN_OR_EQUAL, true,
										 "Equal values should satisfy GREATER_THAN_OR_EQUAL comparison"),
								 IntegerComparisonTestCase.of(3, 5, ComparisonType.GREATER_THAN_OR_EQUAL, false,
										 "Smaller value should not satisfy GREATER_THAN_OR_EQUAL comparison"),

								 // LESS_THAN comparison tests
								 IntegerComparisonTestCase.of(3, 5, ComparisonType.LESS_THAN, true,
										 "Smaller value should satisfy LESS_THAN comparison"),
								 IntegerComparisonTestCase.of(5, 5, ComparisonType.LESS_THAN, false,
										 "Equal values should not satisfy LESS_THAN comparison"),
								 IntegerComparisonTestCase.of(10, 5, ComparisonType.LESS_THAN, false,
										 "Larger value should not satisfy LESS_THAN comparison"),

								 // LESS_THAN_OR_EQUAL comparison tests
								 IntegerComparisonTestCase.of(3, 5, ComparisonType.LESS_THAN_OR_EQUAL, true,
										 "Smaller value should satisfy LESS_THAN_OR_EQUAL comparison"),
								 IntegerComparisonTestCase.of(5, 5, ComparisonType.LESS_THAN_OR_EQUAL, true,
										 "Equal values should satisfy LESS_THAN_OR_EQUAL comparison"),
								 IntegerComparisonTestCase.of(10, 5, ComparisonType.LESS_THAN_OR_EQUAL, false,
										 "Larger value should not satisfy LESS_THAN_OR_EQUAL comparison"),

								 // NOT_EQUAL comparison tests
								 IntegerComparisonTestCase.of(3, 5, ComparisonType.NOT_EQUAL, true,
										 "Different integers should satisfy NOT_EQUAL comparison"),
								 IntegerComparisonTestCase.of(5, 5, ComparisonType.NOT_EQUAL, false,
										 "Equal integers should not satisfy NOT_EQUAL comparison"))
						 .map(tc -> Arguments.of(tc.value(), tc.reference(), tc.comparisonType(), tc.expected(),
								 tc.description()));
		}

		private static Stream<Arguments> stringComparisonProvider()
		{
			return Stream.of(
								 // EQUAL comparison tests
								 StringComparisonTestCase.of("apple", "apple", ComparisonType.EQUAL, true,
										 "Identical strings should satisfy EQUAL comparison"),
								 StringComparisonTestCase.of("apple", "banana", ComparisonType.EQUAL, false,
										 "Different strings should not satisfy EQUAL comparison"),
								 StringComparisonTestCase.of("", "", ComparisonType.EQUAL, true,
										 "Empty strings should satisfy EQUAL comparison"),
								 StringComparisonTestCase.of(null, null, ComparisonType.EQUAL, false,
										 "Null strings should not satisfy EQUAL comparison"),
								 StringComparisonTestCase.of(null, null, ComparisonType.LESS_THAN_OR_EQUAL, false,
										 "Null strings should not satisfy LESS_THAN_OR_EQUAL comparison"),

								 // GREATER_THAN comparison tests
								 StringComparisonTestCase.of("banana", "apple", ComparisonType.GREATER_THAN, true,
										 "Lexicographically greater string should satisfy GREATER_THAN comparison"),
								 StringComparisonTestCase.of("apple", "apple", ComparisonType.GREATER_THAN, false,
										 "Identical strings should not satisfy GREATER_THAN comparison"),
								 StringComparisonTestCase.of("apple", "banana", ComparisonType.GREATER_THAN, false,
										 "Lexicographically smaller string should not satisfy GREATER_THAN comparison"),

								 // GREATER_THAN_OR_EQUAL comparison tests
								 StringComparisonTestCase.of("banana", "apple", ComparisonType.GREATER_THAN_OR_EQUAL, true,
										 "Lexicographically greater string should satisfy GREATER_THAN_OR_EQUAL comparison"),
								 StringComparisonTestCase.of("apple", "apple", ComparisonType.GREATER_THAN_OR_EQUAL, true,
										 "Identical strings should satisfy GREATER_THAN_OR_EQUAL comparison"),
								 StringComparisonTestCase.of("apple", "banana", ComparisonType.GREATER_THAN_OR_EQUAL, false,
										 "Lexicographically smaller string should not satisfy GREATER_THAN_OR_EQUAL comparison"),

								 // LESS_THAN comparison tests
								 StringComparisonTestCase.of("apple", "banana", ComparisonType.LESS_THAN, true,
										 "Lexicographically smaller string should satisfy LESS_THAN comparison"),
								 StringComparisonTestCase.of("apple", "apple", ComparisonType.LESS_THAN, false,
										 "Identical strings should not satisfy LESS_THAN comparison"),
								 StringComparisonTestCase.of("banana", "apple", ComparisonType.LESS_THAN, false,
										 "Lexicographically greater string should not satisfy LESS_THAN comparison"),

								 // LESS_THAN_OR_EQUAL comparison tests
								 StringComparisonTestCase.of("apple", "banana", ComparisonType.LESS_THAN_OR_EQUAL, true,
										 "Lexicographically smaller string should satisfy LESS_THAN_OR_EQUAL comparison"),
								 StringComparisonTestCase.of("apple", "apple", ComparisonType.LESS_THAN_OR_EQUAL, true,
										 "Identical strings should satisfy LESS_THAN_OR_EQUAL comparison"),
								 StringComparisonTestCase.of("banana", "apple", ComparisonType.LESS_THAN_OR_EQUAL, false,
										 "Lexicographically greater string should not satisfy LESS_THAN_OR_EQUAL comparison"),

								 // NOT_EQUAL comparison tests
								 StringComparisonTestCase.of("apple", "banana", ComparisonType.NOT_EQUAL, true,
										 "Different strings should satisfy NOT_EQUAL comparison"),
								 StringComparisonTestCase.of("apple", "apple", ComparisonType.NOT_EQUAL, false,
										 "Identical strings should not satisfy NOT_EQUAL comparison"))
						 .map(tc -> Arguments.of(tc.value(), tc.reference(), tc.comparisonType(), tc.expected(),
								 tc.description()));
		}

		private static Stream<Arguments> customComparatorProvider()
		{
			return Stream.of(StringComparisonTestCase.of("APPLE", "apple", ComparisonType.EQUAL, true,
										 "Case-insensitive comparison should consider different case strings equal"),
								 StringComparisonTestCase.of("BANANA", "apple", ComparisonType.GREATER_THAN, true,
										 "Case-insensitive comparison should respect lexicographical order regardless of case"),
								 StringComparisonTestCase.of("APPLE", "banana", ComparisonType.LESS_THAN, true,
										 "Case-insensitive comparison should respect lexicographical order regardless of case"),
								 StringComparisonTestCase.of("APPLE", "apple", ComparisonType.NOT_EQUAL, false,
										 "Case-insensitive comparison should not consider different case strings as not equal"))
						 .map(tc -> Arguments.of(tc.value(), tc.reference(), tc.comparisonType(), tc.expected(),
								 tc.description()));
		}

		private static Stream<Arguments> customIntegerComparatorProvider()
		{
			// Absolute value comparator - compares integers based on their absolute values
			Comparator<Integer> absoluteValueComparator = (a, b) -> Integer.compare(Math.abs(a), Math.abs(b));

			// Reverse comparator - reverses the natural ordering
			Comparator<Integer> reverseComparator = (a, b) -> Integer.compare(b, a);

			// Even-first comparator - even numbers come before odd numbers, then natural ordering
			Comparator<Integer> evenFirstComparator = (a, b) ->
			{
				boolean aIsEven = a % 2 == 0;
				boolean bIsEven = b % 2 == 0;

				if (aIsEven && !bIsEven)
				{
					return -1;
				}
				else if (!aIsEven && bIsEven)
				{
					return 1;
				}
				else
				{
					return Integer.compare(a, b);
				}
			};

			return Stream.of(
								 // Absolute value comparator tests
								 CustomIntegerComparisonTestCase.of(5, 5, ComparisonType.EQUAL, absoluteValueComparator, true,
										 "Equal absolute values should satisfy EQUAL comparison"),
								 CustomIntegerComparisonTestCase.of(-5, 5, ComparisonType.EQUAL, absoluteValueComparator, true,
										 "Different sign but same absolute value should satisfy EQUAL comparison"),
								 CustomIntegerComparisonTestCase.of(10, 5, ComparisonType.GREATER_THAN, absoluteValueComparator,
										 true, "Larger absolute value should satisfy GREATER_THAN comparison"),
								 CustomIntegerComparisonTestCase.of(-10, 5, ComparisonType.GREATER_THAN, absoluteValueComparator,
										 true, "Negative with larger absolute value should satisfy GREATER_THAN comparison"),
								 CustomIntegerComparisonTestCase.of(3, 5, ComparisonType.LESS_THAN, absoluteValueComparator, true,
										 "Smaller absolute value should satisfy LESS_THAN comparison"),

								 // Reverse comparator tests
								 CustomIntegerComparisonTestCase.of(5, 5, ComparisonType.EQUAL, reverseComparator, true,
										 "Equal values should satisfy EQUAL comparison with reverse comparator"),
								 CustomIntegerComparisonTestCase.of(3, 5, ComparisonType.GREATER_THAN, reverseComparator, true,
										 "Smaller value should satisfy GREATER_THAN comparison with reverse comparator"),
								 CustomIntegerComparisonTestCase.of(10, 5, ComparisonType.LESS_THAN, reverseComparator, true,
										 "Larger value should satisfy LESS_THAN comparison with reverse comparator"),

								 // Even-first comparator tests
								 CustomIntegerComparisonTestCase.of(4, 6, ComparisonType.LESS_THAN, evenFirstComparator, true,
										 "Smaller even number should satisfy LESS_THAN comparison with even-first comparator"),
								 CustomIntegerComparisonTestCase.of(4, 5, ComparisonType.LESS_THAN, evenFirstComparator, true,
										 "Even number should satisfy LESS_THAN comparison with odd number in even-first comparator"),
								 CustomIntegerComparisonTestCase.of(7, 6, ComparisonType.LESS_THAN_OR_EQUAL,
										 evenFirstComparator, false,
										 "Odd number should not satisfy LESS_THAN_OR_EQUAL comparison with even " +
												 "number in even-first comparator"))
						 .map(tc -> Arguments.of(tc.value(), tc.reference(), tc.comparisonType(), tc.comparator(),
								 tc.expected(), tc.description()));
		}

		private record CustomIntegerComparisonTestCase(Integer value, Integer reference, ComparisonType comparisonType,
													   Comparator<Integer> comparator, boolean expected,
													   String description)
		{
			static CustomIntegerComparisonTestCase of(Integer value, Integer reference, ComparisonType comparisonType,
													  Comparator<Integer> comparator, boolean expected,
													  String description)
			{
				return new CustomIntegerComparisonTestCase(value, reference, comparisonType, comparator, expected,
						description);
			}
		}

		private record IntegerComparisonTestCase(Integer value, Integer reference, ComparisonType comparisonType,
												 boolean expected, String description)
		{
			static IntegerComparisonTestCase of(Integer value, Integer reference, ComparisonType comparisonType,
												boolean expected, String description)
			{
				return new IntegerComparisonTestCase(value, reference, comparisonType, expected, description);
			}
		}

		private record StringComparisonTestCase(String value, String reference, ComparisonType comparisonType,
												boolean expected, String description)
		{
			static StringComparisonTestCase of(String value, String reference, ComparisonType comparisonType,
											   boolean expected, String description)
			{
				return new StringComparisonTestCase(value, reference, comparisonType, expected, description);
			}
		}
	}
}