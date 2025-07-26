package de.gupta.commons.utility.javaLanguage.code.type;

public class NoDeclarationsFoundException extends RuntimeException
{
	public static NoDeclarationsFoundException withMessage(final String message)
	{
		return new NoDeclarationsFoundException(message);
	}

	private NoDeclarationsFoundException(String message)
	{
		super(message);
	}
}