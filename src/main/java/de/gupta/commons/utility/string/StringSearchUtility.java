package de.gupta.commons.utility.string;

import java.util.Optional;

public final class StringSearchUtility
{
	public static String afterSearchString(final String input, final String searchString)
	{
		return Optional.of(input)
					   .filter(i -> i.contains(searchString))
					   .map(i -> i.substring(i.indexOf(searchString) + searchString.length()))
					   .orElse(input);
	}

	private StringSearchUtility()
	{
	}
}