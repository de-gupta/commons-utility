package de.gupta.commons.utility.javaLanguage.classes;

import de.gupta.commons.utility.string.StringFormatUtility;

import java.util.Optional;

public final class ClassNameUtility
{
	public static boolean hasValidJavaClassNameFormat(String domainName)
	{
		return Optional.ofNullable(domainName)
					   .filter(StringFormatUtility::startsWithUppercase)
					   .filter(name -> name.chars().allMatch(c -> Character.isLetter(c) && c <= 127))
					   .filter(name -> name.length() == 1 || name.chars().anyMatch(Character::isLowerCase))
					   .isPresent();
	}

	private ClassNameUtility()
	{
	}
}