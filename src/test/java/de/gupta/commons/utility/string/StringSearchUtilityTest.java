package de.gupta.commons.utility.string;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("StringSearchUtility Tests")
class StringSearchUtilityTest
{
    record AfterSearchStringTestCase(String input, String searchString, String expected, String description) {}

    @Nested
    @DisplayName("afterSearchString - Normal Cases")
    class AfterSearchStringNormalCasesTests
    {
        @ParameterizedTest(name = "{3}")
        @MethodSource("afterSearchStringNormalCasesProvider")
        @DisplayName("Should return substring after search string when found")
        void afterSearchString_normalCases(String input, String searchString, String expected, String description)
        {
            String result = StringSearchUtility.afterSearchString(input, searchString);

            assertThat(result)
                .as("Result for input '%s' with search string '%s'", input, searchString)
                .isEqualTo(expected);
        }

        private static Stream<Arguments> afterSearchStringNormalCasesProvider()
        {
            return Stream.of(
                new AfterSearchStringTestCase("Hello World", "Hello ", "World", "Basic case - search string at beginning"),
                new AfterSearchStringTestCase("Hello World", "o ", "World", "Search string in middle"),
                new AfterSearchStringTestCase("Hello World Hello", "World ", "Hello", "Multiple occurrences - returns after first match"),
                new AfterSearchStringTestCase("prefix-content-suffix", "prefix-", "content-suffix", "Search string with hyphen"),
                new AfterSearchStringTestCase("one,two,three", ",", "two,three", "First delimiter in CSV"),
                new AfterSearchStringTestCase("one,two,three", "two,", "three", "Middle delimiter in CSV"),
                new AfterSearchStringTestCase("  leading whitespace", "  ", "leading whitespace", "Search string with whitespace"),
                new AfterSearchStringTestCase("camelCaseText", "camel", "CaseText", "CamelCase text"),
                new AfterSearchStringTestCase("snake_case_text", "snake_", "case_text", "Snake case text"),
                new AfterSearchStringTestCase("Text with (parentheses)", "(", "parentheses)", "Text with parentheses"),
                new AfterSearchStringTestCase("Text with multiple   spaces", "  ", " spaces", "Multiple spaces in search string")
            ).map(testCase -> Arguments.of(testCase.input(), testCase.searchString(), testCase.expected(), testCase.description()));
        }
    }

    @Nested
    @DisplayName("afterSearchString - Edge Cases")
    class AfterSearchStringEdgeCasesTests
    {
        @ParameterizedTest(name = "{3}")
        @MethodSource("afterSearchStringEdgeCasesProvider")
        @DisplayName("Should handle edge cases appropriately")
        void afterSearchString_edgeCases(String input, String searchString, String expected, String description)
        {
            String result = StringSearchUtility.afterSearchString(input, searchString);

            assertThat(result)
                .as("Result for input '%s' with search string '%s'", input, searchString)
                .isEqualTo(expected);
        }

        private static Stream<Arguments> afterSearchStringEdgeCasesProvider()
        {
            return Stream.of(
                new AfterSearchStringTestCase("Hello World", "NotFound", "Hello World", "Search string not found - returns original input"),
                new AfterSearchStringTestCase("Hello World", "", "Hello World", "Empty search string - returns original input"),
                new AfterSearchStringTestCase("", "Hello", "", "Empty input string - returns empty string"),
                new AfterSearchStringTestCase("", "", "", "Both input and search strings empty - returns empty string"),
                new AfterSearchStringTestCase("Hello", "Hello", "", "Search string equals input - returns empty string"),
                new AfterSearchStringTestCase("Hello World", "World", "", "Search string at end - returns empty string"),
                new AfterSearchStringTestCase("Hello World", "Hello World", "", "Search string equals entire input - returns empty string"),
                new AfterSearchStringTestCase("aaa", "a", "aa", "Single character search string with multiple occurrences"),
                new AfterSearchStringTestCase("Hello World", "o W", "orld", "Search string with space"),
                new AfterSearchStringTestCase("Hello\nWorld", "\n", "World", "Search string is newline character"),
                new AfterSearchStringTestCase("abcdefg", "abc", "defg", "Search string at beginning of input"),
                new AfterSearchStringTestCase("abcdefgabc", "abc", "defgabc", "Search string appears multiple times - first occurrence used")
            ).map(testCase -> Arguments.of(testCase.input(), testCase.searchString(), testCase.expected(), testCase.description()));
        }
    }

    @Nested
    @DisplayName("afterSearchString - Special Character Cases")
    class AfterSearchStringSpecialCharacterCasesTests
    {
        @ParameterizedTest(name = "{3}")
        @MethodSource("afterSearchStringSpecialCharacterCasesProvider")
        @DisplayName("Should handle special characters correctly")
        void afterSearchString_specialCharacterCases(String input, String searchString, String expected, String description)
        {
            String result = StringSearchUtility.afterSearchString(input, searchString);

            assertThat(result)
                .as("Result for input '%s' with search string '%s'", input, searchString)
                .isEqualTo(expected);
        }

        private static Stream<Arguments> afterSearchStringSpecialCharacterCasesProvider()
        {
            return Stream.of(
                new AfterSearchStringTestCase("Text with \ttab", "\t", "tab", "Tab character in search string"),
                new AfterSearchStringTestCase("Line1\nLine2", "\n", "Line2", "Newline character in search string"),
                new AfterSearchStringTestCase("Text with \r\n Windows newline", "\r\n", " Windows newline", "Windows newline in search string"),
                new AfterSearchStringTestCase("Text with \\backslash", "\\", "backslash", "Backslash in search string"),
                new AfterSearchStringTestCase("Text with \"quotes\"", "\"", "quotes\"", "Double quote in search string"),
                new AfterSearchStringTestCase("Text with 'quotes'", "'", "quotes'", "Single quote in search string"),
                new AfterSearchStringTestCase("Text with $pecial characters", "$", "pecial characters", "Dollar sign in search string"),
                new AfterSearchStringTestCase("Text with regex chars: .*+?^${}()|[]", "regex chars: ", ".*+?^${}()|[]", "Regex special characters after search string"),
                new AfterSearchStringTestCase("Unicode: こんにちは世界", "こんにちは", "世界", "Unicode characters in search string"),
                new AfterSearchStringTestCase("Emoji: 😀😃😄", "😀", "😃😄", "Emoji in search string"),
                new AfterSearchStringTestCase("HTML: <div>content</div>", "<div>", "content</div>", "HTML tags in search string"),
                new AfterSearchStringTestCase("XML: <tag attr=\"value\"/>", "attr=\"", "value\"/>", "XML attributes in search string"),
                new AfterSearchStringTestCase("Path: C:\\Program Files\\App", "C:\\", "Program Files\\App", "Windows path with backslashes")
            ).map(testCase -> Arguments.of(testCase.input(), testCase.searchString(), testCase.expected(), testCase.description()));
        }
    }

    @Nested
    @DisplayName("afterSearchString - Case Sensitivity Tests")
    class AfterSearchStringCaseSensitivityTests
    {
        @ParameterizedTest(name = "{3}")
        @MethodSource("afterSearchStringCaseSensitivityProvider")
        @DisplayName("Should respect case sensitivity")
        void afterSearchString_caseSensitivity(String input, String searchString, String expected, String description)
        {
            String result = StringSearchUtility.afterSearchString(input, searchString);

            assertThat(result)
                .as("Result for input '%s' with search string '%s'", input, searchString)
                .isEqualTo(expected);
        }

        private static Stream<Arguments> afterSearchStringCaseSensitivityProvider()
        {
            return Stream.of(
                new AfterSearchStringTestCase("Hello World", "hello", "Hello World", "Lowercase search not found in mixed case input"),
                new AfterSearchStringTestCase("Hello World", "HELLO", "Hello World", "Uppercase search not found in mixed case input"),
                new AfterSearchStringTestCase("HELLO WORLD", "hello", "HELLO WORLD", "Lowercase search not found in uppercase input"),
                new AfterSearchStringTestCase("hello world", "Hello", "hello world", "Mixed case search not found in lowercase input"),
                new AfterSearchStringTestCase("Hello World", "Hello", " World", "Exact case match found"),
                new AfterSearchStringTestCase("camelCaseExample", "camelCase", "Example", "CamelCase exact match"),
                new AfterSearchStringTestCase("camelCaseExample", "CamelCase", "camelCaseExample", "CamelCase wrong case - not found")
            ).map(testCase -> Arguments.of(testCase.input(), testCase.searchString(), testCase.expected(), testCase.description()));
        }
    }

    @Nested
    @DisplayName("afterSearchString - Null Handling Tests")
    class AfterSearchStringNullHandlingTests
    {
        @ParameterizedTest(name = "When input is null")
        @NullSource
        @DisplayName("Should throw NullPointerException when input is null")
        void afterSearchString_nullInput(String input)
        {
            assertThatThrownBy(() -> StringSearchUtility.afterSearchString(input, "search"))
                .as("Should throw NullPointerException when input is null")
                .isInstanceOf(NullPointerException.class);
        }

        @ParameterizedTest(name = "When search string is null")
        @NullSource
        @DisplayName("Should throw NullPointerException when search string is null")
        void afterSearchString_nullSearchString(String searchString)
        {
            assertThatThrownBy(() -> StringSearchUtility.afterSearchString("input", searchString))
                .as("Should throw NullPointerException when search string is null")
                .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("afterSearchString - Performance Edge Cases")
    class AfterSearchStringPerformanceEdgeCasesTests
    {
        @ParameterizedTest(name = "{3}")
        @MethodSource("afterSearchStringPerformanceEdgeCasesProvider")
        @DisplayName("Should handle performance edge cases correctly")
        void afterSearchString_performanceEdgeCases(String input, String searchString, String expected, String description)
        {
            String result = StringSearchUtility.afterSearchString(input, searchString);

            assertThat(result)
                .as("Result for performance edge case: %s", description)
                .isEqualTo(expected);
        }

        private static Stream<Arguments> afterSearchStringPerformanceEdgeCasesProvider()
        {
            // Create some larger strings for performance testing
            String longInput = "a".repeat(1000) + "MARKER" + "b".repeat(1000);
            String longSearchString = "a".repeat(1000) + "MARKER";
            String longRepeatedChar = "a".repeat(2000);
            String searchInLongRepeated = "a".repeat(1000);
            String veryLongInput = "x".repeat(10000) + "NEEDLE" + "y".repeat(10000);
            String longPrefix = "prefix".repeat(1000);
            String longSuffix = "suffix".repeat(1000);
            String complexInput = longPrefix + "TARGET" + longSuffix;

            return Stream.of(
                new AfterSearchStringTestCase(longInput, "MARKER", "b".repeat(1000), "Long input with marker in middle"),
                new AfterSearchStringTestCase(longInput, longSearchString, "b".repeat(1000), "Long search string"),
                new AfterSearchStringTestCase(longRepeatedChar, searchInLongRepeated, "a".repeat(1000), "Search in long repeated characters"),
                new AfterSearchStringTestCase(longInput, "NOTFOUND", longInput, "Long input with search string not found"),
                new AfterSearchStringTestCase(veryLongInput, "NEEDLE", "y".repeat(10000), "Very long input (20K+ chars)"),
                new AfterSearchStringTestCase(complexInput, "TARGET", "suffix".repeat(1000), "Complex input with long prefix and suffix")
            ).map(testCase -> Arguments.of(testCase.input(), testCase.searchString(), testCase.expected(), testCase.description()));
        }
    }
}