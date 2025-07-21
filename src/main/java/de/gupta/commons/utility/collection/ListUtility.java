package de.gupta.commons.utility.collection;

import java.util.List;

public final class ListUtility
{
	private ListUtility()
	{
	}

	public static List<String> splitToList(final String input, final String delimiter)
	{
		return StreamUtility.splitToStream(input, delimiter).toList();
	}

	public static List<String> splitToListWithComma(final String input)
	{
		return splitToList(input, ",");
	}
}