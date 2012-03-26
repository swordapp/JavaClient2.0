package org.swordapp.client;

public class ProtocolViolationException extends Exception
{
    public ProtocolViolationException()
    {
        super();
    }

    public ProtocolViolationException(String message)
    {
        super(message);
    }

    public ProtocolViolationException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public ProtocolViolationException(Throwable cause)
    {
        super(cause);
    }
}
