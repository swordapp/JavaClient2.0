package org.swordapp.client;

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.protocol.client.ClientResponse;
import org.dspace.foresite.AggregatedResource;
import org.dspace.foresite.Aggregation;
import org.dspace.foresite.DateParser;
import org.dspace.foresite.OREException;
import org.dspace.foresite.OREParser;
import org.dspace.foresite.OREParserException;
import org.dspace.foresite.OREParserFactory;
import org.dspace.foresite.ORESerialiser;
import org.dspace.foresite.ORESerialiserException;
import org.dspace.foresite.ORESerialiserFactory;
import org.dspace.foresite.ResourceMap;
import org.dspace.foresite.ResourceMapDocument;
import org.dspace.foresite.Triple;
import org.dspace.foresite.TripleSelector;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OreStatement implements Statement
{
	private ResourceMap rem;
    private String contentMD5;
    private Date lastModified;

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

		try
		{
			InputStream is = resp.getInputStream();
			if (is != null)
			{
				OREParser parser = OREParserFactory.getInstance("RDF/XML");
				this.rem = parser.parse(is);
			}
		}
		catch (IOException e)
		{
			throw new SWORDClientException(e);
		}
		catch (OREParserException e)
		{
			throw new StatementParseException(e);
		}
	}

	public String getMimeType()
	{
		return "application/rdf+xml";
	}

    public List<ServerResource> getParts()
            throws SWORDClientException
    {
        try
        {
            List<ServerResource> parts = new ArrayList<ServerResource>();
            List<AggregatedResource> ars = this.rem.getAggregatedResources();
			for (AggregatedResource ar : ars)
			{
				ServerResource resource = new ServerResource();
                resource.setUri(ar.getURI());
				this.populateResource(ar, resource);
				parts.add(resource);
			}
			return parts;
        }
        catch (OREException e)
        {
            throw new SWORDClientException(e);
        }
    }

	private void populateResource(AggregatedResource ar, ServerResource resource)
			throws SWORDClientException
	{
		try
		{
			TripleSelector packaging = new TripleSelector(ar.getURI(), UriRegistry.SWORD_PACKAGING.toURI(), null);
			TripleSelector depositedOn = new TripleSelector(ar.getURI(), UriRegistry.SWORD_DEPOSITED_ON.toURI(), null);
			TripleSelector depositedBy = new TripleSelector(ar.getURI(), UriRegistry.SWORD_DEPOSITED_BY.toURI(), null);
			TripleSelector depositedObo = new TripleSelector(ar.getURI(), UriRegistry.SWORD_DEPOSITED_ON_BEHALF_OF.toURI(), null);

			List<Triple> pt = ar.listTriples(packaging);
			List<Triple> dot = ar.listAllTriples(depositedOn);
			List<Triple> dbt = ar.listTriples(depositedBy);
			List<Triple> dobot = ar.listTriples(depositedObo);

			List<String> packs = new ArrayList<String>();
			if (pt != null && pt.size() > 0)
			{
				for (Triple triple : pt)
				{
					if (triple.isLiteral())
					{
						packs.add(triple.getObjectLiteral());
					}
					else
					{
						packs.add(triple.getObjectURI().toString());
					}
				}
			}
			else
			{
				packs.add(UriRegistry.PACKAGE_SIMPLE_ZIP);
			}
			resource.setPackaging(packs);

			Date depositedDate = null;
			if (dot != null && dot.size() > 0)
			{
				for (Triple triple : dot)
				{
					if (triple.isLiteral())
					{
						try
						{
							depositedDate = DateParser.parse(triple.getObjectLiteral());
						}
						catch (OREParserException e)
						{
							// no reason that this should kill the parser
							continue;
						}
					}
					// we only want one
					break;
				}
			}
			resource.setDepositedOn(depositedDate);

			String depositor = null;
			if (dbt != null && dbt.size() > 0)
			{
				for (Triple triple : dbt)
				{
					if (triple.isLiteral())
					{
						depositor = triple.getObjectLiteral();
					}
					else
					{
						depositor = triple.getObjectURI().toString();
					}
					// we only want one
					break;
				}
			}
			resource.setDepositedBy(depositor);

			String obo = null;
			if (dobot != null && dobot.size() > 0)
			{
				for (Triple triple : dobot)
				{
					if (triple.isLiteral())
					{
						obo = triple.getObjectLiteral();
					}
					else
					{
						obo = triple.getObjectURI().toString();
					}
					// we only want one
					break;
				}
			}
			resource.setDepositedOnBehalfOf(obo);
		}
        catch (OREException e)
        {
            throw new SWORDClientException(e);
        }
        catch (URISyntaxException e)
        {
            throw new SWORDClientException(e);
        }
	}

    public List<ServerResource> getOriginalDeposits()
            throws SWORDClientException
    {
        try
        {
            List<ServerResource> ods = new ArrayList<ServerResource>();
			Aggregation agg = this.rem.getAggregation();
			TripleSelector selector = new TripleSelector(agg.getURI(), new URI(UriRegistry.REL_ORIGINAL_DEPOSIT), null);
			List<Triple> odt = agg.listTriples(selector);
			if (odt == null || odt.size() == 0)
			{
				// no original deposits in this object
				return null;
			}

			List<AggregatedResource> ars = this.rem.getAggregatedResources();
			for (Triple od : odt)
			{
				ServerResource resource = new ServerResource();
				resource.setUri(od.getObjectURI());
				for (AggregatedResource ar : ars)
				{
					if (ar.getURI().toString().equals(od.getObjectURI().toString()))
					{
						this.populateResource(ar, resource);
						break;
					}
				}
				ods.add(resource);
			}
			return ods;
        }
        catch (OREException e)
        {
            throw new SWORDClientException(e);
        }
        catch (URISyntaxException e)
        {
            throw new SWORDClientException(e);
        }
    }

    public List<ResourceState> getState()
            throws SWORDClientException
    {
        try
        {
            Aggregation agg = this.rem.getAggregation();
            TripleSelector state = new TripleSelector(agg.getURI(), UriRegistry.SWORD_STATE.toURI(), null);
            List<Triple> states = agg.listTriples(state);
            if (states == null || states.size() == 0)
            {
                return null;
            }

            List<ResourceState> rss = new ArrayList<ResourceState>();
            for (Triple triple : states)
            {
                ResourceState rs = new ResourceState();
                rs.setIri(new IRI(triple.getObjectURI().toString()));

                // find the state description
                TripleSelector desc = new TripleSelector(triple.getObjectURI(), UriRegistry.SWORD_STATE_DESCRIPTION.toURI(), null);
                List<Triple> dts = this.rem.listAllTriples(desc);
                if (dts != null)
                {
                    for (Triple dt : dts)
                    {
                        if (dt.isLiteral())
                        {
                            rs.setDescription(dt.getObjectLiteral());
                        }
                        else
                        {
                            rs.setDescription(dt.getObjectURI().toString());
                        }
                    }
                }
                rss.add(rs);
            }

            return rss;
        }
        catch (OREException e)
        {
            throw new SWORDClientException(e);
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

    public ResourceMap getResourceMap()
    {
        return this.rem;
    }

	public String toString()
	{
		try
		{
			ORESerialiser s = ORESerialiserFactory.getInstance("RDF/XML");
			ResourceMapDocument rmd = s.serialise(rem);
			return rmd.toString();
		}
		catch (ORESerialiserException e)
		{
			throw new RuntimeException(e);
		}
	}
}
