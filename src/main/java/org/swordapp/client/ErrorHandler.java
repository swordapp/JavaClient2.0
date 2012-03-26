package org.swordapp.client;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.ParsingException;
import org.apache.abdera.protocol.client.ClientResponse;

import java.io.IOException;
import java.io.InputStream;

public class ErrorHandler
{
	public SWORDError handleError(ClientResponse resp)
            throws SWORDClientException
	{
        try
        {
			int status = resp.getStatus();

            // get hold of the XML content of the response if available
            InputStream inputStream = resp.getInputStream();
            Builder parser = new Builder();
			try
			{
            	Document doc = parser.build(inputStream);
				return new SWORDError(status, doc);
			}
			catch (ParsingException e)
			{
				// just ignore the body, probably content is erroneous
				return new SWORDError(status, null);
			}
        }
        catch (IOException e)
        {
            throw new SWORDClientException(e);
        }
    }
}
