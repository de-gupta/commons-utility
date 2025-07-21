package de.gupta.commons.utility.javaLanguage.packages;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("PackageExtractor Tests")
final class PackageExtractorTest
{
	@Nested
	@DisplayName("extractPackageName Tests")
	class ExtractPackageNameTests
	{
		private static Stream<Arguments> validPackageNameProvider()
		{
			return Stream.of(
					Arguments.of("package com.example;", "com.example", "Standard package declaration"),
					Arguments.of("package com.example.test;", "com.example.test", "Multi-level package declaration"),
					Arguments.of("package a.b.c;", "a.b.c", "Short package name components"),
					Arguments.of("package com.example;\nimport java.util.List;", "com.example", "Package with imports"),
					Arguments.of("// Comment\npackage com.example;", "com.example", "Package with preceding comment"),
					Arguments.of("package com.example; // Comment", "com.example", "Package with inline comment"),
					Arguments.of("package    com.example;", "com.example", "Package with extra whitespace"),
					Arguments.of("package\tcom.example;", "com.example", "Package with tab character"),
					Arguments.of("package com.example_1;", "com.example_1", "Package with underscore"),
					Arguments.of("package com.example.test123;", "com.example.test123", "Package with numbers"),
					Arguments.of("package com.example    ;", "com.example", "Package with spaces before semicolon"),
					Arguments.of("/* Block comment */\npackage com.example;", "com.example",
							"Package with block comment before"),
					Arguments.of("package com.example; /* Block comment */", "com.example",
							"Package with block comment after"),
					Arguments.of("package /*comment*/ com.example;", "com.example",
							"Package with block comment in the middle"),
					Arguments.of("package com.example;\npackage org.another;", "com.example",
							"Multiple package declarations (first one used)"),
					Arguments.of("  \t  package com.example;", "com.example", "Package with mixed whitespace before"),
					Arguments.of("//package com.example;\npackage com.example;", "com.example",
							"Valid package after commented out package")
			);
		}

		private static Stream<Arguments> invalidPackageNameProvider()
		{
			return Stream.of(
					Arguments.of("public class Test {}", "Class without package declaration"),
					Arguments.of("import java.util.List;", "File with imports but no package"),
					Arguments.of("// package com.example;", "Commented out package declaration"),
					Arguments.of("", "Empty string"),
					Arguments.of("Package com.example;", "Incorrect case for package keyword"),
					Arguments.of("package ;", "Missing package name"),
					Arguments.of("/* package com.example; */", "Package declaration inside block comment"),
					Arguments.of("String s = \"package com.example;\";", "Package declaration inside string literal"),
					Arguments.of("package com.example@invalid;", "Package with invalid characters"),
					Arguments.of("// This is a comment\npackage // com.example;", "Package without name after comment"),
					Arguments.of("package com.example", "Package declaration without semicolon"),
					Arguments.of("package\ncom.example;", "Package with newline before name"),
					Arguments.of("// package com.example;\n// Another comment", "Multiple commented out packages"),
					Arguments.of("/* Multi-line\npackage com.example;\ncomment */", "Package in multi-line comment"),
					Arguments.of("// package com.example;\npackage ;", "Commented package followed by invalid package")
			);
		}

		@ParameterizedTest(name = "{2}")
		@MethodSource("validPackageNameProvider")
		@DisplayName("Test extracting valid package names")
		void extractPackageName_validInput_returnsPackageName(String classContent, String expectedPackage,
															  String testDescription)
		{
			String result = PackageExtractor.extractPackageName(classContent);
			assertThat(result)
					.as(testDescription)
					.isEqualTo(expectedPackage);
		}

		@ParameterizedTest(name = "{1}")
		@MethodSource("invalidPackageNameProvider")
		@DisplayName("Test extracting package names from invalid inputs")
		void extractPackageName_invalidInput_throwsException(String classContent, String testDescription)
		{
			assertThatThrownBy(() -> PackageExtractor.extractPackageName(classContent))
					.as(testDescription)
					.isInstanceOf(IllegalArgumentException.class)
					.hasMessageContaining("No package found");
		}
	}

	@Nested
	@DisplayName("extractBasePackageName Tests")
	class ExtractBasePackageNameTests
	{
		private static Stream<Arguments> validBasePackageNameProvider()
		{
			return Stream.of(
					Arguments.of("package com.example.test;", "test", "com.example",
							"Standard base package extraction"),
					Arguments.of("package org.apache.commons;", "commons", "org.apache",
							"Multi-level package extraction"),
					Arguments.of("package a.b.c.d;", "d", "a.b.c", "Short package name components"),
					Arguments.of("package com.example.test;\nimport java.util.List;", "test", "com.example",
							"Package with imports"),
					Arguments.of("// Comment\npackage com.example.test;", "test", "com.example",
							"Package with preceding comment"),
					Arguments.of("package com.example.test; // Comment", "test", "com.example",
							"Package with inline comment"),
					Arguments.of("package    com.example.test;", "test", "com.example",
							"Package with extra whitespace"),
					Arguments.of("package com.example.test    ;", "test", "com.example",
							"Package with spaces before semicolon"),
					Arguments.of("package com.example.test_1;", "test_1", "com.example", "Package with underscore"),
					Arguments.of("package com.example.test123;", "test123", "com.example", "Package with numbers"),
					Arguments.of("package com.example.test;", "", "com.example.test", "Empty current package returns full package"),
					Arguments.of("package com.example.test;", "other.package", "com.example.test",
							"Non-matching current package returns full package"),
					Arguments.of("package com.example.test.test;", "test", "com.example.test",
							"Multi level same package name is parsed correctly"),
					Arguments.of("package com.example.test.subpackage;", "subpackage", "com.example.test",
							"Subpackage extraction")
			);
		}

		private static Stream<Arguments> invalidBasePackageNameProvider()
		{
			return Stream.of(
					Arguments.of("public class Test {}", "test", "Class without package declaration"),
					Arguments.of("import java.util.List;", "test", "File with imports but no package"),
					Arguments.of("// package com.example;", "test", "Commented out package declaration"),
					Arguments.of("", "test", "Empty string"),
					Arguments.of("Package com.example;", "test", "Incorrect case for package keyword"),
					Arguments.of("package ;", "test", "Missing package name"),
					Arguments.of("/* package com.example; */", "test", "Package declaration inside block comment")
			);
		}

		@ParameterizedTest(name = "{3}")
		@MethodSource("validBasePackageNameProvider")
		@DisplayName("Test extracting valid base package names")
		void extractBasePackageName_validInput_returnsBasePackageName(String classContent, String currentPackage,
																	  String expectedBasePackage, String testDescription)
		{
			String result = PackageExtractor.extractBasePackageName(classContent, currentPackage);
			assertThat(result)
					.as(testDescription)
					.isEqualTo(expectedBasePackage);
		}

		@ParameterizedTest(name = "{2}")
		@MethodSource("invalidBasePackageNameProvider")
		@DisplayName("Test extracting base package names from invalid inputs")
		void extractBasePackageName_invalidInput_throwsException(String classContent, String currentPackage,
																 String testDescription)
		{
			assertThatThrownBy(() -> PackageExtractor.extractBasePackageName(classContent, currentPackage))
					.as(testDescription)
					.isInstanceOf(IllegalArgumentException.class);
		}
	}
}