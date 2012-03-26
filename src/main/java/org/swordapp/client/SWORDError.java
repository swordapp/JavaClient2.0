package org.swordapp.client;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;

public class SWORDError extends Exception
{
    private int status;
    private Document errorDoc;
    
    SWORDError(int status, Document errorDoc)
    {
        this.status = status;
        this.errorDoc = errorDoc;
    }

    public int getStatus()
    {
        return status;
    }

    public Document getErrorDoc()
    {
        return errorDoc;
    }

    public String getErrorURI()
    {
        Element root = this.errorDoc.getRootElement();
        return root.getAttributeValue("href");
    }

    public String getSummary()
    {
        Element root = this.errorDoc.getRootElement();
        Elements elements = root.getChildElements("summary", UriRegistry.ATOM_NAMESPACE);
        if (elements.size() > 0)
        {
            return elements.get(0).getValue();
        }
        return null;
    }

    public String getVerboseDescription()
    {
        Element root = this.errorDoc.getRootElement();
        Elements elements = root.getChildElements("verboseDescription", UriRegistry.SWORD_TERMS_NAMESPACE);
        if (elements.size() > 0)
        {
            return elements.get(0).getValue();
        }
        return null;
    }

    public String toString()
    {
        return this.errorDoc.toXML();
    }
}
