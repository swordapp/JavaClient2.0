package org.swordapp.client;

import java.util.Date;

public class SwordResponse
{
	private int statusCode = -1;
	private String location = null;
    private String contentMD5;
    private Date lastModified;

	public SwordResponse(int statusCode)
	{
		this(statusCode, null);
	}

	public SwordResponse(int statusCode, String location)
	{
		this.statusCode = statusCode;
		this.location = location;
	}

	public int getStatusCode()
	{
		return statusCode;
	}

    public String getContentMD5()
    {
        return contentMD5;
    }

    public void setContentMD5(String contentMD5)
    {
        this.contentMD5 = contentMD5;
    }

    public Date getLastModified()
    {
        return lastModified;
    }

    public void setLastModified(Date lastModified)
    {
        this.lastModified = lastModified;
    }

    public String getLocation()
    {
        return location;
    }

    public void setLocation(String location)
    {
        this.location = location;
    }
}
