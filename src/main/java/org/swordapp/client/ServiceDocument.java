package org.swordapp.client;

import org.apache.abdera.model.Collection;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Service;
import org.apache.abdera.model.Workspace;
import org.apache.log4j.Logger;

import javax.activation.MimeType;
import java.util.ArrayList;
import java.util.List;

public class ServiceDocument
{
    private static Logger log = Logger.getLogger(ServiceDocument.class);

    private Service service;

    public ServiceDocument(Service service)
    {
        this.service = service;
    }

    public Service getService()
    {
        return service;
    }

    public String getVersion()
            throws ProtocolViolationException
    {
        List<Element> version = service.getExtensions(UriRegistry.SWORD_VERSION);
        if (version.size() != 1)
        {
            log.error("The Service Document does not contain exactly one sword:version element; throwing Exception");
            throw new ProtocolViolationException("The Service Document does not contain exactly one sword:version element");
        }
        return version.get(0).getText();
    }

    public long getMaxUploadSize()
            throws ProtocolViolationException
    {
        List<Element> mus = service.getExtensions(UriRegistry.SWORD_MAX_UPLOAD_SIZE);
        if (mus.size() > 1)
        {
            log.error("The Service Document contains multiple sword:maxUploadSize elements; throwing Exception");
            throw new ProtocolViolationException("The Service Document contains multiple sword:maxUploadSize elements");
        }
        else if (mus.size() == 0)
        {
            return -1;
        }
        else
        {
            try
            {
                Long s = Long.parseLong(mus.get(0).getText());
                return s.longValue();
            }
            catch (NumberFormatException e)
            {
                log.error("Unable to parse the contents of sword:maxUploadSize to an Long: " + mus.get(0).getText() + "; throwing Exception");
                throw new ProtocolViolationException("Unable to parse the contents of sword:maxUploadSize to an Long: " + mus.get(0).getText());
            }
        }
    }

    public List<SWORDWorkspace> getWorkspaces()
    {
        List<SWORDWorkspace> sws = new ArrayList<SWORDWorkspace>();
        List<Workspace> ws = this.service.getWorkspaces();
        for (Workspace w : ws)
        {
            sws.add(new SWORDWorkspace(w));
        }
        return sws;
    }

    public SWORDWorkspace getWorkspace(String s)
    {
        Workspace ws = this.service.getWorkspace(s);
        return new SWORDWorkspace(ws);
    }

    public SWORDCollection getCollection(String workspace, String collection)
    {
        Collection col = this.service.getCollection(workspace, collection);
        return new SWORDCollection(col);
    }

    public SWORDCollection getCollectionThatAccepts(MimeType... mimeTypes)
    {
        Collection col = this.service.getCollectionThatAccepts(mimeTypes);
        return new SWORDCollection(col);
    }

    public SWORDCollection getCollectionThatAccepts(String... strings)
    {
        Collection col = this.service.getCollectionThatAccepts(strings);
        return new SWORDCollection(col);
    }

    public List<SWORDCollection> getCollectionsThatAccept(MimeType... mimeTypes)
    {
        List<SWORDCollection> scs = new ArrayList<SWORDCollection>();
        List<Collection> col = this.service.getCollectionsThatAccept(mimeTypes);
        for (Collection c : col)
        {
            scs.add(new SWORDCollection(c));
        }
        return scs;
    }

    public List<SWORDCollection> getCollectionsThatAccept(String... strings)
    {
        List<SWORDCollection> scs = new ArrayList<SWORDCollection>();
        List<Collection> col = this.service.getCollectionsThatAccept(strings);
        for (Collection c : col)
        {
            scs.add(new SWORDCollection(c));
        }
        return scs;
    }
}
