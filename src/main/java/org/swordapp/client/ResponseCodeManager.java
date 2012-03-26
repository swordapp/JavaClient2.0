package org.swordapp.client;

public class ResponseCodeManager
{
	public ResponseStatus depositNew(int status)
	{
		ResponseStatus rs = new ResponseStatus();

		// correct response
		if (status == 201)
		{
			rs.setCorrect(true);
		}

		// incorrect but allowable
		if (status == 200 || status == 202 || status == 203 || status == 204)
		{
			rs.setIncorrectButAllowed(true);
		}

		// incorrect and violation
		if (status == 205 || status == 206 || (status >=300 && status < 400))
		{
			rs.setIncorrectAndViolation(true);
		}

		// error
		if (status >= 400)
		{
			rs.setError(true);
		}

		// otherwise, was below 200, which shouldn't happen
		return rs;
	}

	public ResponseStatus getDepositReceipt(int status)
	{
		ResponseStatus rs = new ResponseStatus();

		// correct response
		if (status == 200)
		{
			rs.setCorrect(true);
		}

		// incorrect but allowable
		if (status == 203 || (status >= 300 && status < 400))
		{
			rs.setIncorrectButAllowed(true);
		}

		// incorrect and violation
		if (status == 201 || status == 202 || status == 204 || status == 205 || status == 206)
		{
			rs.setIncorrectAndViolation(true);
		}

		// error
		if (status >= 400)
		{
			rs.setError(true);
		}

		// otherwise, was below 200, which shouldn't happen
		return rs;
	}

	public ResponseStatus replaceMedia(int status)
	{
		ResponseStatus rs = new ResponseStatus();

		// correct response
		if (status == 204)
		{
			rs.setCorrect(true);
		}

		// incorrect but allowable
		if (status == 200 || status == 201 || status == 202 || status == 203)
		{
			rs.setIncorrectButAllowed(true);
		}

		// incorrect and violation
		if (status == 205 || status == 206 || (status >= 300 && status < 400))
		{
			rs.setIncorrectAndViolation(true);
		}

		// error
		if (status >= 400)
		{
			rs.setError(true);
		}

		// otherwise, was below 200, which shouldn't happen
		return rs;
	}

	public ResponseStatus replace(int status)
	{
		ResponseStatus rs = new ResponseStatus();

		// correct response
		if (status == 204 || status == 200)
		{
			rs.setCorrect(true);
		}

		// incorrect but allowable
		if (status == 201 || status == 202 || status == 203)
		{
			rs.setIncorrectButAllowed(true);
		}

		// incorrect and violation
		if (status == 205 || status == 206 || (status >= 300 && status < 400))
		{
			rs.setIncorrectAndViolation(true);
		}

		// error
		if (status >= 400)
		{
			rs.setError(true);
		}

		// otherwise, was below 200, which shouldn't happen
		return rs;
	}

	public ResponseStatus delete(int status)
	{
		ResponseStatus rs = new ResponseStatus();

		// correct response
		if (status == 204)
		{
			rs.setCorrect(true);
		}

		// incorrect but allowable
		if (status == 200 || status == 202 || status == 203)
		{
			rs.setIncorrectButAllowed(true);
		}

		// incorrect and violation
		if (status == 201 || status == 205 || status == 206 || (status >= 300 && status < 400))
		{
			rs.setIncorrectAndViolation(true);
		}

		// error
		if (status >= 400)
		{
			rs.setError(true);
		}

		// otherwise, was below 200, which shouldn't happen
		return rs;
	}

	public ResponseStatus addToMediaResource(int status)
	{
		ResponseStatus rs = new ResponseStatus();

		// correct response
		if (status >= 200 && status < 300)
		{
			rs.setCorrect(true);
		}

		// incorrect and violation
		if (status >= 300 && status < 400)
		{
			rs.setIncorrectAndViolation(true);
		}

		// error
		if (status >= 400)
		{
			rs.setError(true);
		}

		// otherwise, was below 200, which shouldn't happen
		return rs;
	}

	public ResponseStatus addToContainer(int status)
	{
		ResponseStatus rs = new ResponseStatus();

		// correct response
		if (status == 200)
		{
			rs.setCorrect(true);
		}

		// incorrect but allowable
		if (status == 201 || status == 204 || status == 202 || status == 203)
		{
			rs.setIncorrectButAllowed(true);
		}

		// incorrect and violation
		if (status == 205 || status == 206 || (status >= 300 && status < 400))
		{
			rs.setIncorrectAndViolation(true);
		}

		// error
		if (status >= 400)
		{
			rs.setError(true);
		}

		// otherwise, was below 200, which shouldn't happen
		return rs;
	}

	public ResponseStatus getStatement(int status)
	{
		ResponseStatus rs = new ResponseStatus();

		// correct response
		if (status == 200)
		{
			rs.setCorrect(true);
		}

		// incorrect but allowable
		if (status == 203 || (status >= 300 && status < 400))
		{
			rs.setIncorrectButAllowed(true);
		}

		// incorrect and violation
		if (status == 201 || status == 202 || status == 205 || status == 206 || status == 204)
		{
			rs.setIncorrectAndViolation(true);
		}

		// error
		if (status >= 400)
		{
			rs.setError(true);
		}

		// otherwise, was below 200, which shouldn't happen
		return rs;
	}

	public ResponseStatus getContent(int status)
	{
		ResponseStatus rs = new ResponseStatus();

		// correct response
		if (status == 200)
		{
			rs.setCorrect(true);
		}

		// incorrect but allowable
		if (status == 203 || (status >= 300 && status < 400))
		{
			rs.setIncorrectButAllowed(true);
		}

		// incorrect and violation
		if (status == 201 || status == 202 || status == 205 || status == 206 || status == 204)
		{
			rs.setIncorrectAndViolation(true);
		}

		// error
		if (status >= 400)
		{
			rs.setError(true);
		}

		// otherwise, was below 200, which shouldn't happen
		return rs;
	}

	public ResponseStatus complete(int status)
	{
		ResponseStatus rs = new ResponseStatus();

		// correct response
		if (status == 200)
		{
			rs.setCorrect(true);
		}

		// incorrect but allowable
		if (status == 201 || status == 202 || status == 203 || status == 204)
		{
			rs.setIncorrectButAllowed(true);
		}

		// incorrect and violation
		if (status == 205 || status == 206 ||(status >= 300 && status < 400))
		{
			rs.setIncorrectAndViolation(true);
		}

		// error
		if (status >= 400)
		{
			rs.setError(true);
		}

		// otherwise, was below 200, which shouldn't happen
		return rs;
	}
}
