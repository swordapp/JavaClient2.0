package org.swordapp.client;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;

public class SWORDError extends Exception
{
    private int status = -1;
    private Document errorDoc = null;
    private String errorBody = null;

    SWORDError(int status, String errorBody)
    {
        this.status = status;
        this.errorBody = errorBody;
    }

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

    public String getErrorBody()
    {
        return errorBody;
    }

    public String getErrorURI()
    {
        if (this.errorDoc == null)
        {
            return null;
        }
        Element root = this.errorDoc.getRootElement();
        return root.getAttributeValue("href");
    }

    public String getSummary()
    {
        if (this.errorDoc == null)
        {
            return null;
        }
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
        if (this.errorDoc == null)
        {
            return null;
        }
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
        if (this.errorDoc == null && this.errorBody == null)
        {
            return Integer.toString(this.status);
        }
        else if (this.errorBody != null)
        {
            return this.errorBody;
        }
        else
        {
            return this.errorDoc.toXML();
        }
    }
}
