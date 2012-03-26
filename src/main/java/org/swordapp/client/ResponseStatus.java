package org.swordapp.client;

public class ResponseStatus
{
	boolean correct = false;
	boolean incorrectButAllowed = false;
	boolean incorrectAndViolation = false;
	boolean error = false;

	public boolean isCorrect()
	{
		return correct;
	}

	public void setCorrect(boolean correct)
	{
		this.correct = correct;
	}

	public boolean isIncorrectButAllowed()
	{
		return incorrectButAllowed;
	}

	public void setIncorrectButAllowed(boolean incorrectButAllowed)
	{
		this.incorrectButAllowed = incorrectButAllowed;
	}

	public boolean isIncorrectAndViolation()
	{
		return incorrectAndViolation;
	}

	public void setIncorrectAndViolation(boolean incorrectAndViolation)
	{
		this.incorrectAndViolation = incorrectAndViolation;
	}

	public boolean isError()
	{
		return error;
	}

	public void setError(boolean error)
	{
		this.error = error;
	}
}
