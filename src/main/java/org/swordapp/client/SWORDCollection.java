package org.swordapp.client;

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Categories;
import org.apache.abdera.model.Collection;
import org.apache.abdera.model.Element;

import javax.activation.MimeType;
import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;

public class SWORDCollection
{
    private Collection collection;

    public SWORDCollection(Collection collection)
    {
        this.collection = collection;
    }

    public String getCollectionPolicy()
            throws ProtocolViolationException
    {
        List<Element> cps = this.collection.getExtensions(UriRegistry.SWORD_COLLECTION_POLICY);
        if (cps.size() == 0)
        {
            return null;
        }
        else if (cps.size() == 1)
        {
            return cps.get(0).getText();
        }
        else
        {
            throw new ProtocolViolationException("More than one sword:collectionPolicy provided in collection description");
        }
    }

    public boolean allowsMediation()
            throws ProtocolViolationException
    {
        List<Element> mediation = this.collection.getExtensions(UriRegistry.SWORD_MEDIATION);
        if (mediation.size() == 0)
        {
            return false;
        }
        else if (mediation.size() == 1)
        {
            return mediation.get(0).getText().trim().equals("true");
        }
        else
        {
            throw new ProtocolViolationException("More than one sword:mediation provided in collection description");
        }
    }

    public String getTreatment()
            throws ProtocolViolationException
    {
        List<Element> treatments = this.collection.getExtensions(UriRegistry.SWORD_TREATMENT);
        if (treatments.size() == 0)
        {
            return null;
        }
        else if (treatments.size() == 1)
        {
            return treatments.get(0).getText();
        }
        else
        {
            throw new ProtocolViolationException("More than one sword:treatment provided in collection description");
        }
    }

	public String getAbstract()
	{
		// there ought to be only one abstract, but just in case let's just return the first one
		List<Element> abstracts = this.collection.getExtensions(UriRegistry.DC_ABSTRACT);
		for (Element ab : abstracts)
		{
			return ab.getText();
		}
		return null;
	}

    public List<String> getAcceptPackaging()
    {
        List<String> packaging = new ArrayList<String>();
        List<Element> acceptPackagings = this.collection.getExtensions(UriRegistry.SWORD_ACCEPT_PACKAGING);
        boolean foundDefault = false;
        for (Element ap : acceptPackagings)
        {
            String uri = ap.getText();
            if (uri.equals(UriRegistry.PACKAGE_BINARY))
            {
                foundDefault = true;
            }
            packaging.add(uri);
        }
        if (!foundDefault)
        {
            packaging.add(UriRegistry.PACKAGE_BINARY);
        }
        return packaging;
    }

    public List<String> getSubServices()
    {
        List<String> subservices = new ArrayList<String>();
        List<Element> services = this.collection.getExtensions(UriRegistry.SWORD_SERVICE);
        for (Element s : services)
        {
            subservices.add(s.getText());
        }
        return subservices;
    }

    public List<String> getMultipartAccept()
    {
        List<String> accepts = new ArrayList<String>();
        List<Element> elements = this.collection.getElements();
        boolean noAccept = false;
        for (Element e : elements)
        {
            String multipartRelated = e.getAttributeValue("alternate");
            QName qn = e.getQName();
            if (qn.getLocalPart().equals("accept") &&
                    qn.getNamespaceURI().equals(UriRegistry.APP_NAMESPACE) &&
                    "multipart-related".equals(multipartRelated))
            {
                String content = e.getText();
                if (content == null || "".equals(content))
                {
                    noAccept = true;
                }
                if (content != null && !"".equals(content) && !accepts.contains(content))
                {
                    accepts.add(content);
                }
            }
        }

        // if there are no accept values, and noAccept has not been triggered, then we add the
        // default accept type
        if (accepts.size() == 0 && !noAccept)
        {
            accepts.add("application/atom+xml;type=entry");
        }

        // rationalise and return
        return this.rationaliseAccepts(accepts);
    }

    public List<String> getSinglepartAccept()
    {
        List<String> accepts = new ArrayList<String>();
        List<Element> elements = this.collection.getElements();
        boolean noAccept = false;
        for (Element e : elements)
        {
            String multipartRelated = e.getAttributeValue("alternate");
            QName qn = e.getQName();
            if (qn.getLocalPart().equals("accept") &&
                    qn.getNamespaceURI().equals(UriRegistry.APP_NAMESPACE) &&
                    !"multipart-related".equals(multipartRelated))
            {
                String content = e.getText();
                if (content == null || "".equals(content))
                {
                    noAccept = true;
                }
                if (content != null && !"".equals(content) && !accepts.contains(content))
                {
                    accepts.add(content);
                }
            }
        }

        // if there are no accept values, and noAccept has not been triggered, then we add the
        // default accept type
        if (accepts.size() == 0 && !noAccept)
        {
            accepts.add("application/atom+xml;type=entry");
        }

        // rationalise and return
        return this.rationaliseAccepts(accepts);
    }

    public boolean singlepartAccepts(String mediaType)
    {
        List<String> accs = this.getSinglepartAccept();
        for (String a : accs)
        {
            if (this.acceptMatches(mediaType, a))
            {
                return true;
            }
        }
        return false;
    }

    public boolean multipartAccepts(String mediaType)
    {
        List<String> accs = this.getMultipartAccept();
        for (String a : accs)
        {
            if (this.acceptMatches(mediaType, a))
            {
                return true;
            }
        }
        return false;
    }

    public boolean singlepartAcceptsEntry()
    {
        return this.singlepartAccepts("application/atom+xml;type=entry");
    }

    public boolean multipartAcceptsEntry()
    {
        return this.multipartAccepts("application/atom+xml;type=entry");
    }

    public boolean acceptsNothing()
    {
        return this.collection.acceptsNothing();
    }

    public Collection getCollection()
    {
        return collection;
    }

    public String getTitle()
    {
        return this.collection.getTitle();
    }

    public IRI getHref()
    {
        return this.collection.getHref();
    }

    public IRI getResolvedHref()
    {
        return this.collection.getResolvedHref();
    }

    public List<Categories> getCategories()
    {
        return this.collection.getCategories();
    }

    private boolean acceptMatches(String one, String two)
    {
        if (one.equals(two))
        {
            return true;
        }

        String oneType = one.substring(0, one.indexOf("/"));
        String oneSubtype = one.substring(one.indexOf("/") + 1);

        String twoType = two.substring(0, two.indexOf("/"));
        String twoSubtype = two.substring(two.indexOf("/") + 1);

        boolean typeMatch = oneType.equals(twoType) || oneType.equals("*") || twoType.equals("*");
        boolean subMatch = oneSubtype.equals(twoSubtype) || oneSubtype.equals("*") || twoSubtype.equals("*");
        return typeMatch && subMatch;
    }

    private List<String> rationaliseAccepts(List<String> accepts)
    {
        List<String> rational = new ArrayList<String>();

        // first, if "*/*" is there, then we accept anything
        if (accepts.contains("*/*"))
        {
            rational.add("*/*");
            return rational;
        }

        // now look to see if we have <x>/* and if so eliminate the unnecessary accepts
        List<String> wildcards = new ArrayList<String>();
        for (String a : accepts)
        {
            if (a.contains("/*"))
            {
                String wild = a.substring(0, a.indexOf("/"));
                wildcards.add(wild);
                if (!rational.contains(a))
                {
                    rational.add(a);
                }
            }
        }

        for (String a : accepts)
        {
            String type = a.substring(0, a.indexOf("/"));
            if (!wildcards.contains(type))
            {
                rational.add(a);
            }
        }

        // by the time we get here we will have only unique and correctly wildcarded accept fields
        return rational;
    }
}
