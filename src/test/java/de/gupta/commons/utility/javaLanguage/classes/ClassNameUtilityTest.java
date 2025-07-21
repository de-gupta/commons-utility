package de.gupta.commons.utility.javaLanguage.classes;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ClassNameUtility Tests")
final class ClassNameUtilityTest
{
	@ParameterizedTest(name = "{2}")
	@MethodSource("hasValidJavaClassNameFormatProvider")
	@DisplayName("Test if string has valid Java class name format")
	void hasValidJavaClassNameFormat(String input, boolean expected, String testDescription)
	{
		boolean result = ClassNameUtility.hasValidJavaClassNameFormat(input);
		assertThat(result).as(testDescription).isEqualTo(expected);
	}

	private static Stream<Arguments> hasValidJavaClassNameFormatProvider()
	{
		return Stream.of(
				Arguments.of("MyClass", true, "Valid Java class name should return true"),
				Arguments.of("myClass", false, "Class name starting with lowercase should return false"),
				Arguments.of("My", true, "Short valid class name should return true"),
				Arguments.of("M", true, "Single uppercase letter is a valid class name"),
				Arguments.of("m", false, "Single lowercase letter is not a valid class name"),
				Arguments.of("", false, "Empty string should return false"),
				Arguments.of("MyClassName", true, "Standard camel case class name should return true"),
				Arguments.of("My123Class", false, "Class name with numbers should return false"),
				Arguments.of("My_Class", false, "Class name with underscore should return false"),
				Arguments.of("MyClass$Inner", false, "Class name with dollar sign should return false"),
				Arguments.of("MYCLASS", false, "All uppercase class name is not valid (not camel case)"),
				Arguments.of("MyClassNameWithVeryLongName", true, "Long valid class name should return true"),
				Arguments.of("ABCClass", true,
						"Class name with multiple uppercase letters at start should return true"),
				Arguments.of("MyCLASSName", true, "Class name with uppercase acronym in the middle should return true"),
				Arguments.of("MyClassNAME", true, "Class name with uppercase acronym at the end should return true"),
    Arguments.of("MyClassNameÄÖÜ", false, "Class name with non-ASCII characters should return false"),
				Arguments.of("MyClassName ", false, "Class name with trailing space should return false"),
				Arguments.of(" MyClassName", false, "Class name with leading space should return false")
		);
	}
}