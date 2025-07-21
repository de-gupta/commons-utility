package de.gupta.commons.utility.string;

public final class StringFormatUtility
{
	private StringFormatUtility()
	{
	}

	public static boolean startsWithUppercase(String text)
	{
		return !text.isEmpty() && Character.isUpperCase(text.charAt(0));
	}
}