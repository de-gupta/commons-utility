package de.gupta.commons.utility.javaLanguage.code;

import de.gupta.commons.utility.javaLanguage.code.type.CodeTypeAnalysisUtility;
import de.gupta.commons.utility.javaLanguage.code.type.TypeDeclaration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CodeTypeAnalysisUtility Tests")
class CodeTypeAnalysisUtilityTest
{
	private record TypeDeclarationTestCase(String input, boolean expected, String description)
	{
		static TypeDeclarationTestCase of(final String input, final boolean expected, final String description)
		{
			return new TypeDeclarationTestCase(input, expected, description);
		}
	}

	private record ParseTypeDeclarationTestCase(String input, String expectedName, boolean expectedPublic,
												String description)
	{
		static ParseTypeDeclarationTestCase of(final String input, final String expectedName,
											   final boolean expectedPublic, final String description)
		{
			return new ParseTypeDeclarationTestCase(input, expectedName, expectedPublic, description);
		}
	}

	@Nested
	@DisplayName("isTypeDeclaration Tests")
	class IsTypeDeclarationTests
	{
		@ParameterizedTest(name = "{2}")
		@MethodSource("isTypeDeclarationProvider")
		@DisplayName("Test isTypeDeclaration method")
		void isTypeDeclaration(String input, boolean expected, String testDescription)
		{
			boolean result = CodeTypeAnalysisUtility.isTypeDeclaration(input);
			assertThat(result).as(testDescription).isEqualTo(expected);
		}

		private static Stream<Arguments> isTypeDeclarationProvider()
		{
			return Stream.of(
					validClassDeclarations(),
					validInterfaceDeclarations(),
					validRecordDeclarations(),
					invalidDeclarations()
			).flatMap(s -> s);
		}

		private static Stream<Arguments> validClassDeclarations()
		{
			return Stream.of(
					TypeDeclarationTestCase.of("public class MyClass", true,
							"Public class declaration should be recognized"),
					TypeDeclarationTestCase.of("class MyClass", true,
							"Default access class declaration should be recognized"),
					TypeDeclarationTestCase.of("private class InnerClass", true,
							"Private class declaration should be recognized"),
					TypeDeclarationTestCase.of("protected class ProtectedClass", true,
							"Protected class declaration should be recognized"),
					TypeDeclarationTestCase.of("public static class StaticClass", true,
							"Public static class declaration should be recognized"),
					TypeDeclarationTestCase.of("public final class FinalClass", true,
							"Public final class declaration should be recognized"),
					TypeDeclarationTestCase.of("public abstract class AbstractClass", true,
							"Public abstract class declaration should be recognized"),
					TypeDeclarationTestCase.of("  public class IndentedClass", true,
							"Indented class declaration should be recognized"),
					// Complex class declarations
					TypeDeclarationTestCase.of("public static final class UtilityClass", true,
							"Class with multiple modifiers should be recognized"),
					TypeDeclarationTestCase.of("public abstract static class AbstractStaticClass", true,
							"Class with abstract and static modifiers should be recognized"),
					TypeDeclarationTestCase.of("  public  class  TabIndentedClass", true,
							"Class declaration with tabs should be recognized"),
					TypeDeclarationTestCase.of("public    class    MultiSpacedClass", true,
							"Class declaration with multiple spaces should be recognized"),
					TypeDeclarationTestCase.of("public class ClassWithSingleCharA", true,
							"Class with single character in name should be recognized"),
					TypeDeclarationTestCase.of("public class A", true,
							"Single uppercase letter class name should be recognized"),
					TypeDeclarationTestCase.of("public class _ValidName", true,
							"Class name starting with underscore should be recognized"),
					TypeDeclarationTestCase.of("public class GenericClass<T>", true,
							"Class with single generic type parameter should be recognized"),
					TypeDeclarationTestCase.of("public class GenericClass<T, U, V>", true,
							"Class with multiple generic type parameters should be recognized"),
					TypeDeclarationTestCase.of(
							"public class ComplexGenericClass<T extends Comparable<T>, U super V, Map<K, V>>", true,
							"Class with complex generic type parameters should be recognized")
			).map(tc -> Arguments.of(tc.input(), tc.expected(), tc.description()));
		}

		private static Stream<Arguments> validInterfaceDeclarations()
		{
			return Stream.of(
					TypeDeclarationTestCase.of("public interface MyInterface", true,
							"Public interface declaration should be recognized"),
					TypeDeclarationTestCase.of("interface MyInterface", true,
							"Default access interface declaration should be recognized"),
					TypeDeclarationTestCase.of("private interface InnerInterface", true,
							"Private interface declaration should be recognized"),
					TypeDeclarationTestCase.of("protected interface ProtectedInterface", true,
							"Protected interface declaration should be recognized"),
					// Complex interface declarations
					TypeDeclarationTestCase.of("public static interface StaticInterface", true,
							"Static interface declaration should be recognized"),
					TypeDeclarationTestCase.of("  protected  interface  IndentedInterface", true,
							"Interface with irregular spacing should be recognized"),
					TypeDeclarationTestCase.of("  public  interface  TabbedInterface", true,
							"Interface with irregular spacing should be recognized"),
					TypeDeclarationTestCase.of("public interface I", true, "Interface with single letter name should " +
							"be recognized"),
					TypeDeclarationTestCase.of("public interface ServiceInterface", true,
							"Interface with proper naming should be recognized"),
					TypeDeclarationTestCase.of("public interface ServiceInterface<A, B>", true,
							"Interface with generics should be recognized")
			).map(tc -> Arguments.of(tc.input(), tc.expected(), tc.description()));
		}

		private static Stream<Arguments> validRecordDeclarations()
		{
			return Stream.of(
					TypeDeclarationTestCase.of("public record MyRecord", true,
							"Public record declaration should be recognized"),
					TypeDeclarationTestCase.of("record MyRecord", true,
							"Default access record declaration should be recognized"),
					TypeDeclarationTestCase.of("private record InnerRecord", true,
							"Private record declaration should be recognized"),
					TypeDeclarationTestCase.of("protected record ProtectedRecord", true,
							"Protected record declaration should be recognized"),
					// Complex record declarations
					TypeDeclarationTestCase.of("public static record StaticRecord", true,
							"Static record declaration should be recognized"),
					TypeDeclarationTestCase.of("  public  record  IndentedRecord", true,
							"Record with irregular spacing should be recognized"),
					TypeDeclarationTestCase.of("  private  record  TabbedRecord", true,
							"Record with irregular spacing should be recognized"),
					TypeDeclarationTestCase.of("public record R", true,
							"Record with single letter name should be recognized"),
					TypeDeclarationTestCase.of("public record DataRecord", true,
							"Record with proper naming should be recognized"),
					TypeDeclarationTestCase.of("public final record FinalRecord", true,
							"Final record declaration should be recognized"),
					TypeDeclarationTestCase.of(
							"public final record GenericRecord<String, Integer, Map<Object, Object>>", true,
							"Generic record declaration should be recognized")
			).map(tc -> Arguments.of(tc.input(), tc.expected(), tc.description()));
		}

		private static Stream<Arguments> invalidDeclarations()
		{
			return Stream.of(
					// Basic invalid declarations
					TypeDeclarationTestCase.of("public MyClass", false,
							"Missing type keyword should not be recognized"),
					TypeDeclarationTestCase.of("public static void main", false,
							"Method declaration should not be recognized"),
					TypeDeclarationTestCase.of("import java.util.List;", false,
							"Import statement should not be recognized"),
					TypeDeclarationTestCase.of("// public class Comment", false,
							"Comment with class declaration should not be recognized"),
					TypeDeclarationTestCase.of("String class = \"not a class\";", false,
							"Variable declaration with class keyword should not be recognized"),
					TypeDeclarationTestCase.of("public class", false,
							"Incomplete class declaration should not be recognized"),
					TypeDeclarationTestCase.of("public class 123InvalidName", true,
							"Class with invalid name should be recognized"),
					TypeDeclarationTestCase.of("", false, "Empty string should not be recognized"),
					TypeDeclarationTestCase.of("    ", false, "Whitespace string should not be recognized"),
					TypeDeclarationTestCase.of("public enum MyEnum", false,
							"Enum declaration should not be recognized"),
					TypeDeclarationTestCase.of("@interface Annotation", false,
							"Annotation declaration should not be recognized"),

					// Malformed declarations with syntax errors
					TypeDeclarationTestCase.of("public class ;", false,
							"Class declaration with missing name should not be recognized"),
					TypeDeclarationTestCase.of("public class class", true, "Class declaration with keyword as name " +
							"should be recognized"),
					TypeDeclarationTestCase.of("public class 'InvalidName'", false,
							"Class declaration with quotes in name should not be recognized"),
					TypeDeclarationTestCase.of("public class My-Class", false,
							"Class declaration with hyphen in name should not be recognized"),
					TypeDeclarationTestCase.of("public class My Class", false,
							"Class declaration with space in name should not be recognized"),
					TypeDeclarationTestCase.of("public class My.Class", false,
							"Class declaration with dot in name should not be recognized"),

					// Edge cases with unusual formatting
					TypeDeclarationTestCase.of("publicclass MyClass", false,
							"Missing space between modifier and keyword should not be recognized"),
					TypeDeclarationTestCase.of("public classMyClass", false,
							"Missing space between keyword and name should not be recognized"),
					TypeDeclarationTestCase.of("class public MyClass", false,
							"Incorrect order of modifiers should not be recognized"),
					TypeDeclarationTestCase.of("class.MyClass", false, "Dot after keyword should not be recognized"),
					TypeDeclarationTestCase.of("public.class MyClass", false,
							"Dot between modifier and keyword should not be recognized"),
					TypeDeclarationTestCase.of("public/*comment*/class MyClass", false,
							"Comment between modifier and keyword should not be recognized"),
					TypeDeclarationTestCase.of("public class //MyClass", false,
							"Comment instead of class name should not be recognized"),
					TypeDeclarationTestCase.of("public class MyClass{}", false,
							"Class declaration with braces should not be recognized")
			).map(tc -> Arguments.of(tc.input(), tc.expected(), tc.description()));
		}
	}

	@Nested
	@DisplayName("parseTypeDeclaration Tests")
	class ParseTypeDeclarationTests
	{
		@ParameterizedTest(name = "{3}")
		@MethodSource("parseTypeDeclarationProvider")
		@DisplayName("Test parseTypeDeclaration method")
		void parseTypeDeclaration(String input, String expectedName, boolean expectedPublic, String testDescription)
		{
			TypeDeclaration result = CodeTypeAnalysisUtility.parseTypeDeclaration(input);
			assertThat(result.name()).as("Type name should match").isEqualTo(expectedName);
			assertThat(result.isPublic()).as("Public flag should match").isEqualTo(expectedPublic);
		}

		private static Stream<Arguments> parseTypeDeclarationProvider()
		{
			return Stream.of(
					// Basic class declarations
					ParseTypeDeclarationTestCase.of("public class MyClass", "MyClass", true,
							"Public class declaration should be parsed correctly"),
					ParseTypeDeclarationTestCase.of("class MyClass", "MyClass", false,
							"Default access class declaration should be parsed correctly"),
					ParseTypeDeclarationTestCase.of("private class InnerClass", "InnerClass", false,
							"Private class declaration should be parsed correctly"),
					ParseTypeDeclarationTestCase.of("protected class ProtectedClass", "ProtectedClass", false,
							"Protected class declaration should be parsed correctly"),

					// Interface declarations
					ParseTypeDeclarationTestCase.of("public interface MyInterface", "MyInterface", true,
							"Public interface declaration should be parsed correctly"),
					ParseTypeDeclarationTestCase.of("interface MyInterface", "MyInterface", false,
							"Default access interface declaration should be parsed correctly"),

					// Record declarations
					ParseTypeDeclarationTestCase.of("public record MyRecord", "MyRecord", true,
							"Public record declaration should be parsed correctly"),
					ParseTypeDeclarationTestCase.of("record MyRecord", "MyRecord", false,
							"Default access record declaration should be parsed correctly"),

					// Complex declarations with modifiers
					ParseTypeDeclarationTestCase.of("public static final class UtilityClass", "UtilityClass", true,
							"Class with multiple modifiers should be parsed correctly"),
					ParseTypeDeclarationTestCase.of("public abstract class AbstractClass", "AbstractClass", true,
							"Abstract class declaration should be parsed correctly"),

					// Declarations with unusual spacing
					ParseTypeDeclarationTestCase.of("  public  class  IndentedClass", "IndentedClass", true,
							"Class with irregular spacing should be parsed correctly"),
					ParseTypeDeclarationTestCase.of("  public  interface  TabbedInterface", "TabbedInterface", true,
							"Interface with irregular spacing should be parsed correctly"),

					// Edge cases for valid names
					ParseTypeDeclarationTestCase.of("public class Ab", "Ab", true,
							"Class with two letter name should be parsed correctly"),
					ParseTypeDeclarationTestCase.of("public class ValidName", "ValidName", true,
							"Class with proper naming should be parsed correctly"),

					// Generic type declarations
					ParseTypeDeclarationTestCase.of("public class GenericClass<T>", "GenericClass", true,
							"Class with single generic type parameter should be parsed correctly"),
					ParseTypeDeclarationTestCase.of("public class GenericClass<T, U, V>", "GenericClass", true,
							"Class with multiple generic type parameters should be parsed correctly"),
					ParseTypeDeclarationTestCase.of(
							"public class ComplexGenericClass<T extends Comparable<T>, U super V, Map<K, V>>",
							"ComplexGenericClass", true,
							"Class with complex generic type parameters should be parsed correctly"),
					ParseTypeDeclarationTestCase.of("public interface GenericInterface<T>", "GenericInterface", true,
							"Interface with generic type parameter should be parsed correctly"),
					ParseTypeDeclarationTestCase.of("public record GenericRecord<T, U>", "GenericRecord", true,
							"Record with generic type parameters should be parsed correctly")
			).map(tc -> Arguments.of(tc.input(), tc.expectedName(), tc.expectedPublic(), tc.description()));
		}
	}
}