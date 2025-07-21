package de.gupta.commons.utility.javaLanguage.packages;

import de.gupta.commons.utility.string.StringSanitizationUtility;

import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class PackageExtractor
{
	public static String extractPackageName(final String classContent)
	{
		final Pattern packagePattern = Pattern.compile("^\\s*package\\s+([\\w.]+)\\s*;.*$");

		return Optional.ofNullable(classContent)
					   .filter(StringSanitizationUtility::isStringNonEmpty)
					   .filter(s -> s.contains("package"))
					   .map(String::trim)
					   .map(CommentManager::removeBlockComments)
					   .map(StringSanitizationUtility::breakIntoLines)
					   .stream()
					   .flatMap(Arrays::stream)
					   .filter(CommentManager::doesLineNotStartWithAComment)
					   .map(packagePattern::matcher)
					   .filter(Matcher::matches)
					   .map(m -> m.group(1))
					   .findFirst()
					   .orElseThrow(() -> new IllegalArgumentException("No package found in the given class content"));
	}

	public static String extractBasePackageName(final String classContent, final String currentPackage)
	{
		return Optional.of(PackageExtractor.extractPackageName(classContent))
					   .map(String::trim)
					   .map(fullPackage ->
							   {
								   final int index = fullPackage.lastIndexOf(currentPackage);
								   final String basePackage = index >= 0 ? fullPackage.substring(0, index) :
										   fullPackage;

								   return basePackage.endsWith(".") ? basePackage.substring(0, basePackage.length() -1) : basePackage;
							   }
					   )
					   .orElseThrow(
							   () -> new IllegalArgumentException("Could not extract package name from model file"));
	}

	private PackageExtractor()
	{
	}
}