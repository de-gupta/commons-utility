package de.gupta.commons.utility.javaLanguage.packages;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;

import java.io.File;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("PackagePathManager")
class PackagePathManagerTest
{
	private static final String PATH_SEPARATOR = File.separator;

	@Nested
	@DisplayName("Valid Package Names")
	class ValidPackageNames
	{
		@ParameterizedTest(name = "{0} -> converted path")
		@MethodSource("validPackageNamesProvider")
		@DisplayName("should convert valid package names to file paths")
		void shouldConvertValidPackageNamesToFilePaths(String packageName, String expectedPath)
		{
			String result = PackagePathManager.packagePath(packageName);

			assertThat(result).as("Package path for '%s'", packageName).isEqualTo(expectedPath);
		}

		private static Stream<Arguments> validPackageNamesProvider()
		{
			return Stream.of(Arguments.of("com.example", "com" + PATH_SEPARATOR + "example"),
					Arguments.of("org.junit.jupiter", "org" + PATH_SEPARATOR + "junit" + PATH_SEPARATOR + "jupiter"),
					Arguments.of("a.b.c", "a" + PATH_SEPARATOR + "b" + PATH_SEPARATOR + "c"),
					Arguments.of("single", "single"),
					Arguments.of("com.example_test", "com" + PATH_SEPARATOR + "example_test"),
					Arguments.of("com.example1.v2", "com" + PATH_SEPARATOR + "example1" + PATH_SEPARATOR + "v2"),
					Arguments.of("a_b.c_d.e_f", "a_b" + PATH_SEPARATOR + "c_d" + PATH_SEPARATOR + "e_f"));
		}
	}

	@Nested
	@DisplayName("Invalid Package Names")
	class InvalidPackageNames
	{
		@ParameterizedTest(name = "{0} -> throws exception")
		@MethodSource("invalidPackageNamesProvider")
		@DisplayName("should throw exception for invalid package names")
		void shouldThrowExceptionForInvalidPackageNames(String packageName)
		{
			assertThatThrownBy(() -> PackagePathManager.packagePath(packageName))
					.as("Exception for invalid package name '%s'", packageName)
					.isInstanceOf(IllegalArgumentException.class)
					.hasMessage("Invalid package name");
		}

		private static Stream<Arguments> invalidPackageNamesProvider()
		{
			return Stream.of(Arguments.of("com..example"), Arguments.of("com.example."), Arguments.of("com.example_"),
					Arguments.of("1com.example"), Arguments.of("com.1example"), Arguments.of("com.exam ple"),
					Arguments.of("com.exam-ple"), Arguments.of("com.exam$ple"), Arguments.of("com.exam#ple"),
					Arguments.of("com.exam@ple"));
		}
	}

	@Nested
	@DisplayName("Null and Blank Package Names")
	class NullAndBlankPackageNames
	{
		@ParameterizedTest
		@NullSource
		@DisplayName("should throw exception for null package name")
		void shouldThrowExceptionForNullPackageName(String packageName)
		{
			assertThatThrownBy(() -> PackagePathManager.packagePath(packageName))
					.as("Exception for null package name")
					.isInstanceOf(IllegalArgumentException.class)
					.hasMessage("Invalid package name");
		}

		@Test
		@DisplayName("should throw exception for blank package name")
		void shouldThrowExceptionForBlankPackageName()
		{
			assertThatThrownBy(() -> PackagePathManager.packagePath("")).as("Exception for blank package name")
																		.isInstanceOf(IllegalArgumentException.class)
																		.hasMessage("Invalid package name");

			assertThatThrownBy(() -> PackagePathManager.packagePath("  ")).as("Exception for whitespace package name")
																		  .isInstanceOf(IllegalArgumentException.class)
																		  .hasMessage("Invalid package name");
		}
	}

	@Nested
	@DisplayName("Edge Cases")
	class EdgeCases
	{
		@ParameterizedTest(name = "{0} -> {1}")
		@MethodSource("edgeCasesProvider")
		@DisplayName("should handle edge cases correctly")
		void shouldHandleEdgeCasesCorrectly(String packageName, String expectedPath)
		{
			String result = PackagePathManager.packagePath(packageName);

			assertThat(result).as("Package path for edge case '%s'", packageName).isEqualTo(expectedPath);
		}

		private static Stream<Arguments> edgeCasesProvider()
		{
			return Stream.of(Arguments.of("a", "a"), Arguments.of("Z", "Z"),
					Arguments.of("very.long.package.name.with.many.segments",
							"very" + PATH_SEPARATOR + "long" + PATH_SEPARATOR + "package" + PATH_SEPARATOR + "name" + PATH_SEPARATOR + "with" + PATH_SEPARATOR + "many" + PATH_SEPARATOR + "segments"),
					Arguments.of("a.b.c.d.e.f.g.h.i.j.k",
							"a" + PATH_SEPARATOR + "b" + PATH_SEPARATOR + "c" + PATH_SEPARATOR + "d" + PATH_SEPARATOR + "e" + PATH_SEPARATOR + "f" + PATH_SEPARATOR + "g" + PATH_SEPARATOR + "h" + PATH_SEPARATOR + "i" + PATH_SEPARATOR + "j" + PATH_SEPARATOR + "k"));
		}
	}

	@Nested
	@DisplayName("Special Characters in Valid Package Names")
	class SpecialCharactersInValidPackageNames
	{
		@ParameterizedTest(name = "{0} -> {1}")
		@MethodSource("specialCharactersProvider")
		@DisplayName("should handle underscores in package names")
		void shouldHandleUnderscoresInPackageNames(String packageName, String expectedPath)
		{
			String result = PackagePathManager.packagePath(packageName);

			assertThat(result).as("Package path for name with special characters '%s'", packageName)
							  .isEqualTo(expectedPath);
		}

		private static Stream<Arguments> specialCharactersProvider()
		{
			return Stream.of(Arguments.of("package_name", "package_name"),
					Arguments.of("package_name.sub_package", "package_name" + PATH_SEPARATOR + "sub_package"),
					Arguments.of("a_very_long_package_name", "a_very_long_package_name"),
					Arguments.of("a_1.b_2.c_3", "a_1" + PATH_SEPARATOR + "b_2" + PATH_SEPARATOR + "c_3"));
		}
	}
}