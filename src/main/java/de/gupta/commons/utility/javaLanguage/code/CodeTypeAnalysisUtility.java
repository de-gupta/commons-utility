package de.gupta.commons.utility.javaLanguage.code;

import de.gupta.commons.utility.javaLanguage.classes.ClassNameUtility;
import de.gupta.commons.utility.string.StringSanitizationUtility;
import de.gupta.commons.utility.string.StringSearchUtility;
import de.gupta.commons.utility.string.StringTokenizeUtility;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public final class CodeTypeAnalysisUtility
{
	private static String findUniqueTypeName(final String sourceCode)
	{
		return Optional.ofNullable(sourceCode)
					   .filter(StringSanitizationUtility::isStringNonBlank)
					   .map(String::trim)
					   .map(s -> s.split("\\r?\\n"))
					   .map(Arrays::stream)
					   .map(lines -> findTheRelevantDeclarationFrom(typeDeclarationsIn(lines.toList())))
					   .map(TypeDeclaration::name)
					   .orElseThrow(() -> EmptySourceCodeException.withMessage("Source code cannot be null or blank"));
	}

	private static TypeDeclaration findTheRelevantDeclarationFrom(final Collection<TypeDeclaration> typeDeclarations)
	{
		return uniquePublicDeclarationFrom(typeDeclarations)
				.orElseGet(() -> uniqueNonPublicDeclarationFrom(typeDeclarations)
						.orElseThrow(() -> NoDeclarationsFoundException.withMessage("No unique declaration " +
								"found in source code"))
				);
	}

	private static Collection<TypeDeclaration> typeDeclarationsIn(final Collection<String> lines)
	{
		return lines.stream()
					.map(String::trim)
					.filter(CodeTypeAnalysisUtility::isTypeDeclaration)
					.map(CodeTypeAnalysisUtility::parseTypeDeclaration)
					.toList();
	}

	private static Optional<TypeDeclaration> uniquePublicDeclarationFrom(
			final Collection<TypeDeclaration> typeDeclarations)
	{
		return containsAUniquePublicDeclaration(typeDeclarations) ? aPublicDeclarationFrom(typeDeclarations) :
				Optional.empty();
	}

	private static Optional<TypeDeclaration> uniqueNonPublicDeclarationFrom(
			final Collection<TypeDeclaration> typeDeclarations)
	{
		return containsAUniqueNonPublicDeclaration(typeDeclarations) ? aNonPublicDeclarationFrom(typeDeclarations) :
				Optional.empty();
	}

	public static boolean isTypeDeclaration(String line)
	{
		String pattern = "^(\\s*)(public\\s+|private\\s+|protected\\s+|static\\s+|final\\s+|abstract\\s+)*" +
				"(class|interface|record)\\s+[A-Za-z0-9_$]+";
		return line.matches(pattern);
	}

	public static TypeDeclaration parseTypeDeclaration(String line)
	{
		return Optional.ofNullable(line)
					   .map(String::trim)
					   .flatMap(CodeTypeAnalysisUtility::extractTypeType)
					   .map(typeType -> extractTypeName(line, typeType))
					   .map(typeName -> TypeDeclaration.of(typeName, line.contains("public")))
					   .orElseThrow(() -> new IllegalArgumentException("Line cannot be null or blank"));
	}

	private static boolean containsAUniquePublicDeclaration(final Collection<TypeDeclaration> typeDeclarations)
	{
		return typeDeclarations.stream()
							   .filter(td -> td.isPublic)
							   .distinct()
							   .count() == 1;
	}

	private static Optional<TypeDeclaration> aPublicDeclarationFrom(final Collection<TypeDeclaration> typeDeclarations)
	{
		return typeDeclarations.stream()
							   .filter(td -> td.isPublic)
							   .findFirst();
	}

	private static boolean containsAUniqueNonPublicDeclaration(final Collection<TypeDeclaration> typeDeclarations)
	{
		return typeDeclarations.stream()
							   .filter(td -> !td.isPublic)
							   .distinct()
							   .count() == 1;
	}

	private static Optional<TypeDeclaration> aNonPublicDeclarationFrom(
			final Collection<TypeDeclaration> typeDeclarations)
	{
		return typeDeclarations.stream()
							   .filter(td -> !td.isPublic)
							   .findFirst();
	}

	private static Optional<String> extractTypeType(final String line)
	{
		Set<String> typeTypes = Set.of("class", "interface", "record");

		return typeTypes.stream()
						.filter(typeType -> line.contains(typeType + " "))
						.findFirst();
	}

	private static String extractTypeName(final String line, final String typeType)
	{
		return Optional.ofNullable(line)
					   .map(l -> StringSearchUtility.afterSearchString(l, typeType + " "))
					   .map(String::trim)
					   .map(afterTypeType -> StringTokenizeUtility.tokenize(afterTypeType,
							   Set.of("\\s", "<", ">", "{")))
					   .map(array -> array[0])
					   .orElseThrow(() -> new IllegalArgumentException("Line cannot be null or blank"));
	}

	public record TypeDeclaration(String name, boolean isPublic)
	{
		static TypeDeclaration of(final String name, final boolean isPublic)
		{
			return new TypeDeclaration(name, isPublic);
		}

		boolean isTypeNameValid()
		{
			return ClassNameUtility.hasValidJavaClassNameFormat(name());
		}
	}
}