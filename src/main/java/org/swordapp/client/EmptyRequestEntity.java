package org.swordapp.client;

import org.apache.commons.httpclient.methods.RequestEntity;

import java.io.IOException;
import java.io.OutputStream;

public class EmptyRequestEntity implements RequestEntity
{
	public boolean isRepeatable()
	{
		return true;
	}

	public void writeRequest(OutputStream outputStream) throws IOException
	{
		// Does nothing
	}

	public long getContentLength()
	{
		return 0;
	}

	public String getContentType()
	{
		return null;
	}
}
