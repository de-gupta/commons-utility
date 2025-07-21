package de.gupta.commons.utility.string;

import java.util.Optional;

public final class StringCaseUtility
{
	private StringCaseUtility()
	{
	}

	public static String capitalizeFirstLetter(final String input)
	{
		return Optional.ofNullable(input)
				.filter(i -> !i.isEmpty())
				.map(i -> i.substring(0, 1).toUpperCase() + i.substring(1))
				.orElse(input);
	}
}