package de.gupta.commons.utility.javaLanguage.packages;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

final class PackageNameValidatorTest
{
	@Nested
	@DisplayName("Valid package names")
	class ValidPackageNames
	{
		@ParameterizedTest(name = "{0} is a valid package name")
		@MethodSource("validPackageNamesProvider")
		void shouldAcceptValidPackageNames(String packageName)
		{
			assertThat(PackageNameValidator.isValidJavaPackageName(packageName))
					.as("Package name '%s' should be valid", packageName)
					.isTrue();
		}

		private static Stream<Arguments> validPackageNamesProvider()
		{
			return Stream.of(
					Arguments.of("com"),
					Arguments.of("com.example"),
					Arguments.of("com.example.test"),
					Arguments.of("a.b.c"),
					Arguments.of("com.example_test"),
					Arguments.of("com.example.test123"),
					Arguments.of("com.example.TEST"),
					Arguments.of("com123"),
					Arguments.of("com_example")
			);
		}
	}

	@Nested
	@DisplayName("Invalid package names")
	class InvalidPackageNames
	{
		@ParameterizedTest(name = "{0} is an invalid package name")
		@MethodSource("invalidPackageNamesProvider")
		void shouldRejectInvalidPackageNames(String packageName)
		{
			assertThat(PackageNameValidator.isValidJavaPackageName(packageName))
					.as("Package name '%s' should be invalid", packageName)
					.isFalse();
		}

		private static Stream<Arguments> invalidPackageNamesProvider()
		{
			return Stream.of(
					Arguments.of(""),
					Arguments.of(" "),
					Arguments.of("com..example"),
					Arguments.of("com.example."),
					Arguments.of("com.example_"),
					Arguments.of("com.example-test"),
					Arguments.of("com.example test"),
					Arguments.of("com.example$test"),
					Arguments.of("com.example.test!"),
					Arguments.of("com.example.test@"),
					Arguments.of("com.example.test#"),
					Arguments.of("com.example.test%"),
					Arguments.of("com.example.test^"),
					Arguments.of("com.example.test&"),
					Arguments.of("com.example.test*"),
					Arguments.of("com.example.test("),
					Arguments.of("com.example.test)"),
					Arguments.of("com.example.test+"),
					Arguments.of("com.example.test="),
					Arguments.of("com.example.test{"),
					Arguments.of("com.example.test}"),
					Arguments.of("com.example.test["),
					Arguments.of("com.example.test]"),
					Arguments.of("com.example.test|"),
					Arguments.of("com.example.test\\"),
					Arguments.of("com.example.test;"),
					Arguments.of("com.example.test:"),
					Arguments.of("com.example.test'"),
					Arguments.of("com.example.test\""),
					Arguments.of("com.example.test<"),
					Arguments.of("com.example.test>"),
					Arguments.of("com.example.test,"),
					Arguments.of("com.example.test/")
			);
		}
	}

	@Nested
	@DisplayName("Null and blank package names")
	class NullAndBlankPackageNames
	{
		@ParameterizedTest(name = "Null package name is invalid")
		@NullSource
		void shouldRejectNullPackageName(String packageName)
		{
			assertThat(PackageNameValidator.isValidJavaPackageName(packageName))
					.as("Null package name should be invalid")
					.isFalse();
		}

		@Test
		@DisplayName("Blank package name is invalid")
		void shouldRejectBlankPackageName()
		{
			assertThat(PackageNameValidator.isValidJavaPackageName("   "))
					.as("Blank package name should be invalid")
					.isFalse();
		}
	}

	@Nested
	@DisplayName("Package names with consecutive periods")
	class ConsecutivePeriodsInPackageNames
	{

		@ParameterizedTest(name = "{0} has consecutive periods and is invalid")
		@MethodSource("packageNamesWithConsecutivePeriodsProvider")
		void shouldRejectPackageNamesWithConsecutivePeriods(String packageName)
		{
			assertThat(PackageNameValidator.isValidJavaPackageName(packageName))
					.as("Package name '%s' with consecutive periods should be invalid", packageName)
					.isFalse();
		}

		private static Stream<Arguments> packageNamesWithConsecutivePeriodsProvider()
		{
			return Stream.of(
					Arguments.of("com..example"),
					Arguments.of("com...example"),
					Arguments.of("com.example..test"),
					Arguments.of("..com.example"),
					Arguments.of("com.example..")
			);
		}
	}

	@Nested
	@DisplayName("Package names ending with period or underscore")
	class PackageNamesWithInvalidEndings
	{
		@ParameterizedTest(name = "{0} ends with period and is invalid")
		@MethodSource("packageNamesEndingWithPeriodProvider")
		void shouldRejectPackageNamesEndingWithPeriod(String packageName)
		{
			assertThat(PackageNameValidator.isValidJavaPackageName(packageName))
					.as("Package name '%s' ending with period should be invalid", packageName)
					.isFalse();
		}

		@ParameterizedTest(name = "{0} ends with underscore and is invalid")
		@MethodSource("packageNamesEndingWithUnderscoreProvider")
		void shouldRejectPackageNamesEndingWithUnderscore(String packageName)
		{
			assertThat(PackageNameValidator.isValidJavaPackageName(packageName))
					.as("Package name '%s' ending with underscore should be invalid", packageName)
					.isFalse();
		}

		private static Stream<Arguments> packageNamesEndingWithPeriodProvider()
		{
			return Stream.of(
					Arguments.of("com."),
					Arguments.of("com.example."),
					Arguments.of("a.b.c.")
			);
		}

		private static Stream<Arguments> packageNamesEndingWithUnderscoreProvider()
		{
			return Stream.of(
					Arguments.of("com_"),
					Arguments.of("com.example_"),
					Arguments.of("a.b.c_")
			);
		}
	}

	@Nested
	@DisplayName("Package names with invalid characters")
	class PackageNamesWithInvalidCharacters
	{

		@ParameterizedTest(name = "{0} contains invalid characters")
		@MethodSource("packageNamesWithInvalidCharactersProvider")
		void shouldRejectPackageNamesWithInvalidCharacters(String packageName)
		{
			assertThat(PackageNameValidator.isValidJavaPackageName(packageName))
					.as("Package name '%s' with invalid characters should be invalid", packageName)
					.isFalse();
		}

		private static Stream<Arguments> packageNamesWithInvalidCharactersProvider()
		{
			return Stream.of(
					Arguments.of("com-example"),
					Arguments.of("com.example-test"),
					Arguments.of("com.example test"),
					Arguments.of("com.example$test"),
					Arguments.of("com.example@test"),
					Arguments.of("com.example#test"),
					Arguments.of("com.example%test"),
					Arguments.of("com.example^test"),
					Arguments.of("com.example&test"),
					Arguments.of("com.example*test"),
					Arguments.of("com.example(test"),
					Arguments.of("com.example)test"),
					Arguments.of("com.example+test"),
					Arguments.of("com.example=test"),
					Arguments.of("com.example{test"),
					Arguments.of("com.example}test"),
					Arguments.of("com.example[test"),
					Arguments.of("com.example]test"),
					Arguments.of("com.example|test"),
					Arguments.of("com.example\\test"),
					Arguments.of("com.example;test"),
					Arguments.of("com.example:test"),
					Arguments.of("com.example'test"),
					Arguments.of("com.example\"test"),
					Arguments.of("com.example<test"),
					Arguments.of("com.example>test"),
					Arguments.of("com.example,test"),
					Arguments.of("com.example/test")
			);
		}
	}

	@Nested
	@DisplayName("Package names not starting with a letter")
	class PackageNamesNotStartingWithLetter
	{
		@ParameterizedTest(name = "{0} doesn't start with a letter and is invalid")
		@MethodSource("packageNamesNotStartingWithLetterProvider")
		void shouldRejectPackageNamesNotStartingWithLetter(String packageName)
		{
			assertThat(PackageNameValidator.isValidJavaPackageName(packageName))
					.as("Package name '%s' not starting with a letter should be invalid", packageName)
					.isFalse();
		}

		private static Stream<Arguments> packageNamesNotStartingWithLetterProvider()
		{
			return Stream.of(
					Arguments.of("1com"),
					Arguments.of("123.example"),
					Arguments.of("9test.example"),
					Arguments.of("_test.example")
			);
		}
	}

	@Nested
	@DisplayName("Package names with segments not starting with a letter")
	class PackageNamesWithSegmentsNotStartingWithLetter
	{
		@ParameterizedTest(name = "{0} has segments not starting with a letter and is invalid")
		@MethodSource("packageNamesWithSegmentsNotStartingWithLetterProvider")
		void shouldRejectPackageNamesWithSegmentsNotStartingWithLetter(String packageName)
		{
			assertThat(PackageNameValidator.isValidJavaPackageName(packageName))
					.as("Package name '%s' with segments not starting with a letter should be invalid", packageName)
					.isFalse();
		}

		private static Stream<Arguments> packageNamesWithSegmentsNotStartingWithLetterProvider()
		{
			return Stream.of(
					Arguments.of("com.1example"),
					Arguments.of("com.example.1test"),
					Arguments.of("com.example.123"),
					Arguments.of("com.example._test"),
					Arguments.of("com.9example.test"),
					Arguments.of("com..test"),
					Arguments.of("com. .test")
			);
		}
	}

	@Nested
	@DisplayName("Package names containing spaces")
	class PackageNamesWithSpaces
	{
		@ParameterizedTest(name = "{0} contains spaces and is invalid")
		@MethodSource("packageNamesWithSpacesProvider")
		void shouldRejectPackageNamesWithSpaces(String packageName)
		{
			assertThat(PackageNameValidator.isValidJavaPackageName(packageName))
					.as("Package name '%s' containing spaces should be invalid", packageName)
					.isFalse();
		}

		private static Stream<Arguments> packageNamesWithSpacesProvider()
		{
			return Stream.of(
					Arguments.of("com example"),
					Arguments.of("com.example test"),
					Arguments.of("com.example. test"),
					Arguments.of("com. example"),
					Arguments.of(" com.example"),
					Arguments.of("com.example ")
			);
		}
	}
}