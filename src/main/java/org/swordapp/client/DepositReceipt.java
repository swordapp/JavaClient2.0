package org.swordapp.client;

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Link;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DepositReceipt extends SwordResponse
{
    private Entry entry;

    public DepositReceipt(Entry entry)
    {
		super(201);
        this.entry = entry;
    }

	public DepositReceipt(int status, Entry entry)
    {
		super(status);
        this.entry = entry;
    }

	public DepositReceipt(int status, String location, Entry entry)
    {
		super(status, location);
        this.entry = entry;
    }

	public DepositReceipt(int status, String location)
	{
		super(status, location);
	}

    public Entry getEntry()
    {
        return entry;
    }

    public SwordIdentifier getEditLink()
    {
        return new SwordIdentifier(this.entry.getEditLink());
    }

    public SwordIdentifier getContentLink()
    {
        return new SwordIdentifier(this.entry.getContentSrc(), this.entry.getContentMimeType());
    }

    public SwordIdentifier getEditMediaLink()
    {
        return new SwordIdentifier(this.entry.getEditMediaLink());
    }

    public SwordIdentifier getEditMediaLink(String type)
			throws SWORDClientException
    {
		try
		{
			MimeType mt = new MimeType(type);
			for (Link link : this.entry.getLinks())
			{
				if (link.getMimeType() != null)
				{
					if ("edit-media".equals(link.getRel()) &&
							link.getMimeType().toString().equals(mt.toString()))
					{
						return new SwordIdentifier(link);
					}
				}
			}
			return null;
		}
		catch (MimeTypeParseException e)
		{
			throw new SWORDClientException(e);
		}
    }

    public SwordIdentifier getEditMediaLink(String type, String hreflang)
    {
        return new SwordIdentifier(this.entry.getEditMediaLink(type, hreflang));
    }

    public SwordIdentifier getSwordEditLink()
    {
        return new SwordIdentifier(this.entry.getLink(UriRegistry.REL_SWORD_EDIT));
    }

    public String getTreatment()
    {
        Element element = this.entry.getExtension(UriRegistry.SWORD_TREATMENT);
        return element.getText();
    }

    public String getVerboseDescription()
    {
        Element element = this.entry.getExtension(UriRegistry.SWORD_VERBOSE_DESCRIPTION);
        return element.getText();
    }

	public List<String> getPackaging()
	{
		List<Element> packagings = this.entry.getExtensions(UriRegistry.SWORD_PACKAGING);
		List<String> packages = new ArrayList<String>();
		for (Element element : packagings)
		{
			packages.add(element.getText());
		}

		// now deal with the default case
		if (packages.size() == 0)
		{
			// add the simple zip
			packages.add(UriRegistry.PACKAGE_SIMPLE_ZIP);
		}

		return packages;
	}

	public SwordIdentifier getStatementLink(String type)
			throws SWORDClientException
	{
		try
		{
			String statementRel = UriRegistry.REL_STATEMENT;
			MimeType mt = new MimeType(type);
			for (Link link : this.entry.getLinks())
			{
				if (statementRel.equals(link.getRel()) &&
						link.getMimeType().toString().equals(mt.toString()))
				{
					return new SwordIdentifier(link);
				}
			}
			return null;
		}
		catch (MimeTypeParseException e)
		{
			throw new SWORDClientException(e);
		}
	}

	public SwordIdentifier getOREStatementLink()
			throws SWORDClientException
	{
		return this.getStatementLink("application/rdf+xml");
	}

	public SwordIdentifier getAtomStatementLink()
			throws SWORDClientException
	{
		SwordIdentifier stmt = this.getStatementLink("application/atom+xml;type=feed");
		if (stmt == null)
		{
			// try an alternative mimetype
			stmt = this.getStatementLink("application/atom+xml");
		}
		return stmt;
	}

    public List<Element> getDublinCore()
    {
        List<Element> dc = new ArrayList<Element>();
        List<Element> allextensions = this.entry.getExtensions();
        for (Element el : allextensions)
        {
            if (el.getQName().toString().startsWith("{" + UriRegistry.DC_NAMESPACE))
            {
                dc.add(el);
            }
        }
        return dc;
    }

    public SwordIdentifier getSplashPageLink()
    {
        Link alt = this.entry.getAlternateLink();
        if (alt != null)
        {
            return new SwordIdentifier(alt);
        }
        return null;
    }

    public SwordIdentifier getOriginalDepositLink()
    {
        return new SwordIdentifier(this.entry.getLink(UriRegistry.REL_ORIGINAL_DEPOSIT));
    }

    public List<SwordIdentifier> getDerivedResourceLinks()
    {
		List<SwordIdentifier> sis = new ArrayList<SwordIdentifier>();
        List<Link> links = this.entry.getLinks(UriRegistry.REL_DERIVED_RESOURCE);
		for (Link link : links)
		{
			sis.add(new SwordIdentifier(link));
		}
		return sis;
    }
}
