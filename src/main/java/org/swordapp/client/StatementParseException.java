package org.swordapp.client;

public class StatementParseException extends Exception
{
	public StatementParseException()
	{
		super();
	}

	public StatementParseException(String message)
	{
		super(message);
	}

	public StatementParseException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public StatementParseException(Throwable cause)
	{
		super(cause);
	}
}
