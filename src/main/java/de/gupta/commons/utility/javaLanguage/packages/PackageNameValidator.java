package de.gupta.commons.utility.javaLanguage.packages;

import de.gupta.commons.utility.string.StringSanitizationUtility;

import java.util.Arrays;
import java.util.Optional;

public final class PackageNameValidator
{
	public static boolean isValidJavaPackageName(final String packageName)
	{
		return Optional.ofNullable(packageName)
					   .filter(StringSanitizationUtility::isStringNonBlank)
					   .filter(PackageNameValidator::containsNoSpaces)
					   .filter(PackageNameValidator::onlyContainsAllowedCharacters)
					   .filter(PackageNameValidator::hasNoConsecutivePeriods)
					   .filter(PackageNameValidator::doesNotEndsWithPeriod)
					   .filter(PackageNameValidator::doesNotEndWithUnderscore)
					   .filter(PackageNameValidator::startsWithLetter)
					   .filter(PackageNameValidator::eachSegmentStartsWithLetter)
					   .isPresent();
	}

	private static boolean containsNoSpaces(final String packageName)
	{
		return !packageName.contains(" ");
	}

	private static boolean onlyContainsAllowedCharacters(final String packageName)
	{
		return packageName.matches("[a-zA-Z0-9._]+");
	}

	private static boolean hasNoConsecutivePeriods(final String packageName)
	{
		return !packageName.matches(".*\\.\\..*");
	}

	private static boolean doesNotEndsWithPeriod(final String packageName)
	{
		return !packageName.endsWith(".");
	}

	private static boolean doesNotEndWithUnderscore(final String packageName)
	{
		return !packageName.endsWith("_");
	}

	private static boolean startsWithLetter(final String packageName)
	{
		return packageName.matches("^[a-zA-Z].*");
	}

	private static boolean eachSegmentStartsWithLetter(final String packageName)
	{
		return Arrays.stream(packageName.split("\\."))
					 .allMatch(segment -> StringSanitizationUtility.isStringNonBlank(segment) && Character.isLetter(
							 segment.charAt(0)));
	}

	private PackageNameValidator()
	{
	}
}