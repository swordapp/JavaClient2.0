package org.swordapp.client;

import javax.xml.namespace.QName;
import java.net.URI;
import java.net.URISyntaxException;

public class SQName extends QName
	{
		public SQName(java.lang.String namespaceURI, java.lang.String localPart)
		{
			super(namespaceURI, localPart);
		}

    	public SQName(java.lang.String namespaceURI, java.lang.String localPart, java.lang.String prefix)
		{
			super(namespaceURI, localPart, prefix);
		}

    	public URI toURI()
				throws URISyntaxException
		{
			return new URI(this.getNamespaceURI() + this.getLocalPart());
		}
	}
