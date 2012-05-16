package org.swordapp.client;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.ParsingException;
import org.apache.abdera.protocol.client.ClientResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;

public class ErrorHandler
{
	public SWORDError handleError(ClientResponse resp)
            throws SWORDClientException
	{
        try
        {
			int status = resp.getStatus();

            // get hold of the response body as a string
            InputStream inputStream = resp.getInputStream();
            StringWriter writer = new StringWriter();
            IOUtils.copy(inputStream, writer);
            String errorBody = writer.toString();

            // try to parse the string as XML
            Builder parser = new Builder();
			try
			{
            	Document doc = parser.build(errorBody);
				return new SWORDError(status, errorBody, doc);
			}
			catch (ParsingException e)
			{
				// just ignore the body, probably content is erroneous
				return new SWORDError(status, errorBody);
			}
        }
        catch (IOException e)
        {
            throw new SWORDClientException(e);
        }
    }
}
