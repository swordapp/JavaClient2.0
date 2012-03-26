package org.swordapp.client;

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Link;

import javax.activation.MimeType;

public class SwordIdentifier
{
	private Link link = null;
	private IRI iri = null;
	private MimeType mimeType = null;

	private String href = null;
	private String type = null;
	private String rel = null;

	public SwordIdentifier(String href, String type, String rel)
	{
		this.href = href;
		this.type = type;
		this.rel = rel;
	}

	public SwordIdentifier(String href, String type)
	{
		this(href, type, null);
	}

	public SwordIdentifier(IRI iri, MimeType mimeType, String rel)
	{
		this(iri.toString(), mimeType != null ? mimeType.toString().replace(" ", "") : null, rel);

		this.iri = iri;
		this.mimeType = mimeType;
	}

	public SwordIdentifier(IRI iri, MimeType mimeType)
	{
		this(iri.toString(), mimeType != null ? mimeType.toString().replace(" ", "") : null);

		this.iri = iri;
		this.mimeType = mimeType;
	}

	public SwordIdentifier(Link link)
	{
		this(link.getHref(), link.getMimeType(), link.getRel());
		this.link = link;
	}

	public String getHref() { return this.href; }

	public String getType() { return this.type; }

	public String getRel() { return this.rel; }

	public Link getLink() { return this.link; }

	public IRI getIRI() { return this.iri; }

	public MimeType getMimeType() { return this.mimeType; }
}
