/*
 * Copyright (c) 2011, Richard Jones, Cottage Labs
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.swordapp.client;

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Category;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.protocol.client.ClientResponse;
import org.dspace.foresite.DateParser;
import org.dspace.foresite.OREParserException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * An implementation of the Statement interface for representing a SWORD Statement
 * document serialised as an Atom Feed.
 */
public class AtomStatement implements Statement
{
	private Feed feed;
    private String contentMD5;
    private Date lastModified;

    /**
     * Parse the Abdera ClientResponse object and populate this object with the
     * data found therein
     *
     * @param resp
     * @throws SWORDClientException
     * @throws StatementParseException
     */
	public void parse(ClientResponse resp)
			throws SWORDClientException, StatementParseException
	{
        this.contentMD5 = resp.getHeader("Content-MD5");
        String rfc822date = resp.getHeader("Last-Modified");

        if (rfc822date != null)
        {
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z");
            try
            {
                this.lastModified = sdf.parse(rfc822date);
            }
            catch (ParseException e)
            {
                // doesn't matter, we just ignore the date
            }
        }

        // now parse the feed itself
        Document<Feed> doc = resp.getDocument();
		this.feed = doc.getRoot();
	}

    /**
     * The mimetype of an Atom formatted Statement
     *
     * @return
     */
	public String getMimeType()
	{
		return "application/atom+xml;type=feed";
	}

    /**
     * List all the parts of the item as described in the Statement
     *
     * @return
     * @throws SWORDClientException
     */
    public List<ServerResource> getParts()
            throws SWORDClientException
    {
        try
        {
            List<ServerResource> parts = new ArrayList<ServerResource>();
            List<Entry> entries = this.feed.getEntries();
            for (Entry entry : entries)
            {
                ServerResource resource = new ServerResource();
                resource.setUri(entry.getContentSrc().toURI());

                List<String> packs = new ArrayList<String>();
                List<Element> packagings = entry.getExtensions(UriRegistry.SWORD_PACKAGING);
                for (Element packaging : packagings)
                {
                    packs.add(packaging.getText());
                }
                if (packs.size() == 0)
                {
                    packs.add(UriRegistry.PACKAGE_SIMPLE_ZIP);
                }
                resource.setPackaging(packs);

                try
                {
                    Element depositedOn = entry.getExtension(UriRegistry.SWORD_DEPOSITED_ON);
					if (depositedOn != null)
					{
                    	Date date = DateParser.parse(depositedOn.getText());
                    	resource.setDepositedOn(date);
					}
                }
                catch (OREParserException e)
                {
                    // don't worry about it, just carry on
                }

                Element depositedBy = entry.getExtension(UriRegistry.SWORD_DEPOSITED_BY);
				if (depositedBy != null)
				{
                	resource.setDepositedBy(depositedBy.getText());
				}

                Element depositedObo = entry.getExtension(UriRegistry.SWORD_DEPOSITED_ON_BEHALF_OF);
                if (depositedObo != null)
				{
					resource.setDepositedOnBehalfOf(depositedObo.getText());
				}

                parts.add(resource);
            }
            return parts;
        }
        catch (URISyntaxException e)
        {
            throw new SWORDClientException(e);
        }
    }

    /**
     * List all the parts of the item as represented by the statement which have been
     * marked as Original Deposits
     * 
     * @return
     * @throws SWORDClientException
     */
    public List<ServerResource> getOriginalDeposits() throws SWORDClientException
    {
        try
        {
            List<ServerResource> parts = new ArrayList<ServerResource>();
            List<Entry> entries = this.feed.getEntries();
            for (Entry entry : entries)
            {
                boolean isOriginalDeposit = false;
                List<Category> cats = entry.getCategories();
                for (Category cat : cats)
                {
                    if (UriRegistry.REL_ORIGINAL_DEPOSIT.equals(cat.getTerm()))
                    {
                        isOriginalDeposit = true;
                    }
                }
                if (!isOriginalDeposit)
                {
                    // we're only interested in original deposits
                    continue;
                }

                ServerResource resource = new ServerResource();
                resource.setUri(entry.getContentSrc().toURI());

                List<String> packs = new ArrayList<String>();
                List<Element> packagings = entry.getExtensions(UriRegistry.SWORD_PACKAGING);
                for (Element packaging : packagings)
                {
                    packs.add(packaging.getText());
                }
                if (packs.size() == 0)
                {
                    packs.add(UriRegistry.PACKAGE_SIMPLE_ZIP);
                }
                resource.setPackaging(packs);

                try
                {
                    Element depositedOn = entry.getExtension(UriRegistry.SWORD_DEPOSITED_ON);
					if (depositedOn != null)
					{
                    	Date date = DateParser.parse(depositedOn.getText());
                    	resource.setDepositedOn(date);
					}
                }
                catch (OREParserException e)
                {
                    // don't worry about it, just carry on
                }

                Element depositedBy = entry.getExtension(UriRegistry.SWORD_DEPOSITED_BY);
				if (depositedBy != null)
				{
                	resource.setDepositedBy(depositedBy.getText());
				}

                Element depositedObo = entry.getExtension(UriRegistry.SWORD_DEPOSITED_ON_BEHALF_OF);
                if (depositedObo != null)
				{
					resource.setDepositedOnBehalfOf(depositedObo.getText());
				}

                parts.add(resource);
            }
            return parts;
        }
        catch (URISyntaxException e)
        {
            throw new SWORDClientException(e);
        }
    }

    /**
     * Get a list of state objects which represent the item
     *
     * @return
     * @throws SWORDClientException
     */
    public List<ResourceState> getState()
			throws SWORDClientException

    {
		try
		{
			List<ResourceState> states = new ArrayList<ResourceState>();
			List<Category> cats = this.feed.getCategories();
			String comparator = UriRegistry.SWORD_STATE.toURI().toString();
			for (Category cat : cats)
			{
				if (comparator.equals(cat.getScheme().toString()))
				{
					ResourceState state = new ResourceState();
					state.setIri(new IRI(cat.getTerm()));
					state.setDescription(cat.getText());
					states.add(state);
				}
			}
			return states;
		}
		catch (URISyntaxException e)
		{
			throw new SWORDClientException(e);
		}
	}

    public String getContentMD5()
            throws SWORDClientException
    {
        return this.contentMD5;
    }

    public Date getLastModified() throws SWORDClientException
    {
        return this.lastModified;
    }

    /**
     * Get the Abdera Feed object which forms the core of this object
     *
     * @return
     */
    public Feed getFeed()
    {
        return this.feed;
    }

    /**
     * Nice string representation of the feed - a fully pretty printed
     * XML dump
     * 
     * @return
     */
    public String toString()
	{
		try
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			this.feed.writeTo(baos);
			return baos.toString();
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
}
