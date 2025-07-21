package de.gupta.commons.utility.javaLanguage.packages;

import java.util.regex.Pattern;

public final class CommentManager
{
	private CommentManager()
	{
	}

	public static String removeBlockComments(final String content)
	{
		if (content == null || content.isEmpty()) return content;

		StringBuilder result = new StringBuilder();
		int length = content.length();
		int nesting = 0;
		boolean inString = false;

		for (int i = 0; i < length; )
		{
			char c = content.charAt(i);
			char next = (i + 1 < length) ? content.charAt(i + 1) : '\0';

			// Handle string literal start/end (skip escaped quotes)
			if (c == '"' && (i == 0 || content.charAt(i - 1) != '\\'))
			{
				inString = !inString;
				result.append(c);
				i++;
				continue;
			}

			if (!inString)
			{
				if (c == '/' && next == '*')
				{
					nesting++;
					i += 2;
					continue;
				}
				if (c == '*' && next == '/' && nesting > 0)
				{
					nesting--;
					i += 2;
					continue;
				}
			}

			if (nesting == 0)
			{
				result.append(c);
			}
			i++;
		}

		return result.toString();
	}

	public static boolean doesLineStartWithAComment(final String line)
	{
		return line.trim().startsWith("//") || line.trim().startsWith("/*");
	}

	public static boolean doesLineNotStartWithAComment(final String line)
	{
		return !doesLineStartWithAComment(line);
	}

	public static boolean doesLineContainAComment(final String line)
	{

		Pattern pattern = Pattern.compile("^(?:\"[^\"]*\"|[^\"])*?(//|/\\*|\\*/)");
		return pattern.matcher(line).find();
	}

	public static boolean doesLineNotContainAComment(final String line)
	{
		return !doesLineContainAComment(line);
	}

	public static boolean doesLineEndWithAComment(final String line)
	{
		return line.trim().endsWith("//") || line.trim().endsWith("*/");
	}

	public static boolean doesLineNotEndWithAComment(final String line)
	{
		return !doesLineEndWithAComment(line);
	}
}