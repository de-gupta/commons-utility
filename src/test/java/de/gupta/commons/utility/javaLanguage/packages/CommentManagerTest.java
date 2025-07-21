package de.gupta.commons.utility.javaLanguage.packages;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CommentManager Tests")
final class CommentManagerTest
{
    private static Stream<Arguments> removeBlockCommentsProvider()
    {
        return Stream.of(
                Arguments.of("/* This is a block comment */System.out.println();", "System.out.println();",
                        "Basic block comment should be removed"),
                Arguments.of("System.out.println(); /* This is a block comment */", "System.out.println(); ",
                        "Block comment at the end should be removed"),
                Arguments.of("System.out.println(); /* This is a\nmultiline\nblock comment */", "System.out.println(); ",
                        "Multiline block comment should be removed"),
                Arguments.of("/* Nested /* comment */ */System.out.println();", "System.out.println();",
                        "Nested block comments should be removed"),
                Arguments.of("/* Comment with special chars: !@#$%^&*() */code;", "code;",
                        "Block comment with special characters should be removed"),
                Arguments.of("// This is not a block comment", "// This is not a block comment",
                        "Line comments should not be affected"),
                Arguments.of("String s = \"/* This is not a comment */\";", "String s = \"/* This is not a comment */\";",
                        "Text in quotes that looks like a comment should not be removed"),
                Arguments.of("", "",
                        "Empty string should remain empty"),
                Arguments.of("/**/", "",
                        "Empty block comment should be removed"),
                Arguments.of("code with no comments", "code with no comments",
                        "String without comments should remain unchanged"),
                Arguments.of("/* Comment 1 */code/* Comment 2 */", "code",
                        "Multiple block comments should all be removed")
        );
    }

    private static Stream<Arguments> doesLineStartWithACommentProvider()
    {
        return Stream.of(
                Arguments.of("// This is a line comment", true,
                        "Line starting with // should return true"),
                Arguments.of("/* This is a block comment", true,
                        "Line starting with /* should return true"),
                Arguments.of("    // Indented comment", true,
                        "Indented line comment should return true"),
                Arguments.of("    /* Indented block comment", true,
                        "Indented block comment should return true"),
                Arguments.of("code // comment", false,
                        "Line with code before comment should return false"),
                Arguments.of("code /* comment */", false,
                        "Line with code before block comment should return false"),
                Arguments.of("", false,
                        "Empty line should return false"),
                Arguments.of("   ", false,
                        "Line with only whitespace should return false"),
                Arguments.of("\"// Not a comment\"", false,
                        "Line with quoted text that looks like a comment should return false"),
                Arguments.of("\"/* Not a comment */\"", false,
                        "Line with quoted text that looks like a block comment should return false"),
                Arguments.of("/not a comment", false,
                        "Line starting with single slash should return false"),
                Arguments.of("*/ not a comment start", false,
                        "Line starting with block comment end should return false")
        );
    }

    private static Stream<Arguments> doesLineEndWithACommentProvider()
    {
        return Stream.of(
                Arguments.of("This line ends with //", true,
                        "Line ending with // should return true"),
                Arguments.of("This line ends with a block comment */", true,
                        "Line ending with */ should return true"),
                Arguments.of("// This is just a comment", false,
                        "Line with only a line comment should return false"),
                Arguments.of("/* This is just a block comment", false,
                        "Line with only a block comment start should return false"),
                Arguments.of("Code with no comment", false,
                        "Line without any comment should return false"),
                Arguments.of("", false,
                        "Empty line should return false"),
                Arguments.of("   ", false,
                        "Line with only whitespace should return false"),
                Arguments.of("Line with \"//\" in quotes", false,
                        "Line with quoted text that looks like a comment end should return false"),
                Arguments.of("Line with \"*/\" in quotes", false,
                        "Line with quoted text that looks like a block comment end should return false"),
                Arguments.of("Line ending with / ", false,
                        "Line ending with single slash should return false"),
                Arguments.of("Line ending with * ", false,
                        "Line ending with asterisk should return false"),
                Arguments.of("Line ending with //   ", true,
                        "Line ending with // followed by whitespace should return true"),
                Arguments.of("Line ending with */   ", true,
                        "Line ending with */ followed by whitespace should return true")
        );
    }

    private static Stream<Arguments> doesLineContainACommentProvider()
    {
        return Stream.of(
                Arguments.of("// This is a line comment", true,
                        "Line starting with // should return true"),
                Arguments.of("/* This is a block comment", true,
                        "Line starting with /* should return true"),
                Arguments.of("This line ends with //", true,
                        "Line ending with // should return true"),
                Arguments.of("This line ends with a block comment */", true,
                        "Line ending with */ should return true"),
                Arguments.of("Code // with comment in the middle", true,
                        "Line with comment in the middle should return true"),
                Arguments.of("Code /* with block comment */ in the middle", true,
                        "Line with block comment in the middle should return true"),
                Arguments.of("Code with no comment", false,
                        "Line without any comment should return false"),
                Arguments.of("", false,
                        "Empty line should return false"),
                Arguments.of("   ", false,
                        "Line with only whitespace should return false"),
                Arguments.of("Line with \"//\" in quotes", false,
                        "Line with quoted text that looks like a comment should return false"),
                Arguments.of("Line with \"/*\" in quotes", false,
                        "Line with quoted text that looks like a block comment start should return false"),
                Arguments.of("Line with \"*/\" in quotes", false,
                        "Line with quoted text that looks like a block comment end should return false")
        );
    }

    private static Stream<Arguments> inverseMethodsTestProvider()
    {
        return Stream.of(
                Arguments.of("// This is a line comment",
                        "Line starting with // should have opposite results for start and not-start methods"),
                Arguments.of("/* This is a block comment",
                        "Line starting with /* should have opposite results for start and not-start methods"),
                Arguments.of("Code // with comment in the middle",
                        "Line with comment in the middle should have opposite results for start and not-start methods"),
                Arguments.of("Code with no comment",
                        "Line without any comment should have opposite results for start and not-start methods"),
                Arguments.of("",
                        "Empty line should have opposite results for start and not-start methods"),
                Arguments.of("   ",
                        "Line with only whitespace should have opposite results for start and not-start methods"),
                Arguments.of("Line with \"//\" in quotes",
                        "Line with quoted text should have opposite results for start and not-start methods")
        );
    }

    @ParameterizedTest(name = "{2}")
    @MethodSource("removeBlockCommentsProvider")
    @DisplayName("Test removing block comments from content")
    void removeBlockComments(String input, String expected, String testDescription)
    {
        String result = CommentManager.removeBlockComments(input);
        assertThat(result).as(testDescription).isEqualTo(expected);
    }

    @ParameterizedTest(name = "{2}")
    @MethodSource("doesLineStartWithACommentProvider")
    @DisplayName("Test if line starts with a comment")
    void doesLineStartWithAComment(String input, boolean expected, String testDescription)
    {
        boolean result = CommentManager.doesLineStartWithAComment(input);
        boolean negativeResult = CommentManager.doesLineNotStartWithAComment(input);
        assertThat(result).as(testDescription).isEqualTo(expected);
        assertThat(negativeResult).as(testDescription).isEqualTo(!expected);
        assertThat(result || negativeResult).as(testDescription + " - one method must be true").isTrue();
        assertThat(result && negativeResult).as(testDescription + " - both methods cannot be true").isFalse();
    }

    @ParameterizedTest(name = "{2}")
    @MethodSource("doesLineStartWithACommentProvider")
    @DisplayName("Test if line does not start with a comment")
    void doesLineNotStartWithAComment(String input, boolean expected, String testDescription)
    {
        boolean result = CommentManager.doesLineNotStartWithAComment(input);
        boolean negativeResult = CommentManager.doesLineStartWithAComment(input);
        assertThat(result).as(testDescription).isEqualTo(!expected);
        assertThat(negativeResult).as(testDescription).isEqualTo(expected);
        assertThat(result || negativeResult).as(testDescription + " - one method must be true").isTrue();
        assertThat(result && negativeResult).as(testDescription + " - both methods cannot be true").isFalse();
    }

    @ParameterizedTest(name = "{2}")
    @MethodSource("doesLineEndWithACommentProvider")
    @DisplayName("Test if line ends with a comment")
    void doesLineEndWithAComment(String input, boolean expected, String testDescription)
    {
        boolean result = CommentManager.doesLineEndWithAComment(input);
        boolean negativeResult = CommentManager.doesLineNotEndWithAComment(input);
        assertThat(result).as(testDescription).isEqualTo(expected);
        assertThat(negativeResult).as(testDescription).isEqualTo(!expected);
        assertThat(result || negativeResult).as(testDescription + " - one method must be true").isTrue();
        assertThat(result && negativeResult).as(testDescription + " - both methods cannot be true").isFalse();
    }

    @ParameterizedTest(name = "{2}")
    @MethodSource("doesLineContainACommentProvider")
    @DisplayName("Test if line contains a comment")
    void doesLineContainAComment(String input, boolean expected, String testDescription)
    {
        boolean result = CommentManager.doesLineContainAComment(input);
        boolean negativeResult = CommentManager.doesLineNotContainAComment(input);
        assertThat(result).as(testDescription).isEqualTo(expected);
        assertThat(negativeResult).as(testDescription).isEqualTo(!expected);
        assertThat(result || negativeResult).as(testDescription + " - one method must be true").isTrue();
        assertThat(result && negativeResult).as(testDescription + " - both methods cannot be true").isFalse();
    }

    @ParameterizedTest(name = "{1}")
    @MethodSource("inverseMethodsTestProvider")
    @DisplayName("Test that 'not' methods are truly inverse of their counterparts")
    void verifyInverseMethods(String input, String testDescription)
    {
        boolean startsWithComment = CommentManager.doesLineStartWithAComment(input);
        boolean notStartsWithComment = CommentManager.doesLineNotStartWithAComment(input);

        assertThat(notStartsWithComment).as(testDescription).isEqualTo(!startsWithComment);
        assertThat(startsWithComment || notStartsWithComment).as(testDescription + " - one method must be true").isTrue();
        assertThat(startsWithComment && notStartsWithComment).as(testDescription + " - both methods cannot be true").isFalse();
    }
}