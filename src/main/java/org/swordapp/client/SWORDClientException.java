package org.swordapp.client;

public class SWORDClientException extends Exception
{
    public SWORDClientException()
    {
        super();
    }

    public SWORDClientException(String message)
    {
        super(message);
    }

    public SWORDClientException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public SWORDClientException(Throwable cause)
    {
        super(cause);
    }
}