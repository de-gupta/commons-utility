package de.gupta.commons.utility.string;

import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class StringTokenizeUtility
{
	public static String[] tokenize(final String input, final Set<String> delimiters)
	{
		return delimiters.isEmpty() ? new String[]{input} :
				input.split(delimiters.stream().map(Pattern::quote).collect(Collectors.joining("|")));
	}

	public static String[] tokenize(final String input, final String delimiter)
	{
		return input.split(delimiter);
	}

	private StringTokenizeUtility()
	{
	}
}