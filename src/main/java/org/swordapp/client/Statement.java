package org.swordapp.client;

import org.apache.abdera.protocol.client.ClientResponse;

import java.util.Date;
import java.util.List;

public interface Statement
{
	public void parse(ClientResponse resp) throws SWORDClientException, StatementParseException;

	public String getMimeType();

    public List<ServerResource> getParts() throws SWORDClientException;

    public List<ServerResource> getOriginalDeposits() throws SWORDClientException;

    public List<ResourceState> getState() throws SWORDClientException;

    public String getContentMD5() throws SWORDClientException;

    public Date getLastModified() throws SWORDClientException;
}
