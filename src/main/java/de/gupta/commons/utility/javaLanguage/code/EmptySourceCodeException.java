package de.gupta.commons.utility.javaLanguage.code;

public class EmptySourceCodeException extends RuntimeException
{
	public static EmptySourceCodeException withMessage(final String message)
	{
		return new EmptySourceCodeException(message);
	}

	private EmptySourceCodeException(String message)
	{
		super(message);
	}
}