package de.gupta.commons.utility.collection;

import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public final class StreamUtility
{
	private StreamUtility()
	{
	}

	public static Stream<String> splitToStream(final String input, final String delimiter)
	{
		if (input == null)
		{
			throw new IllegalArgumentException("Input string cannot be null");
		}

		// Handle whitespace-only delimiter as a special case
		if (delimiter.trim().isEmpty())
		{
			return splitByWhitespace(input);
		}

		// Prepare the delimiter for regex use
		String regexSafeDelimiter = escapeRegexSpecialChars(delimiter);

		// Split and trim the results
		return Arrays.stream(input.split(regexSafeDelimiter, -1));
	}

	private static Stream<String> splitByWhitespace(String input)
	{
		return Arrays.stream(input.split("\\s+"))
					 .filter(str -> !str.isEmpty());
	}

	private static String escapeRegexSpecialChars(String delimiter)
	{
		// If the delimiter starts with backslash, assume it's already escaped
		if (delimiter.startsWith("\\"))
		{
			return delimiter;
		}
		return Pattern.quote(delimiter);
	}
}