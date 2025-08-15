package de.gupta.commons.utility.string;

public final class StringSanitizationUtility
{
	public static boolean isStringNonEmpty(final String input)
	{
		return !isStringEmpty(input);
	}

	public static boolean isStringEmpty(final String input)
	{
		return input == null || input.isEmpty();
	}

	public static boolean isStringNonBlank(final String input)
	{
		return !isStringBlank(input);
	}

	public static boolean isStringBlank(final String input)
	{
		return input == null || input.isBlank();
	}

	public static boolean isStringTrimmed(final String input)
	{
		return input != null && input.trim().equals(input);
	}

	public static String[] breakIntoLines(final String input)
	{
		return input.split("\\r?\\n", -1);
	}

	public static String[] breakIntoLines(final String input, final String delimiter)
	{
		return input.split(delimiter, -1);
	}

	private StringSanitizationUtility()
	{
	}
}