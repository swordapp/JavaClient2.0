package org.swordapp.client;

import org.apache.abdera.model.Collection;
import org.apache.abdera.model.Workspace;

import javax.activation.MimeType;
import java.util.ArrayList;
import java.util.List;

public class SWORDWorkspace
{
    private Workspace workspace;

    public SWORDWorkspace(Workspace workspace)
    {
        this.workspace = workspace;
    }

    public Workspace getWorkspace()
    {
        return workspace;
    }

    public List<SWORDCollection> getCollections()
    {
        List<SWORDCollection> scs = new ArrayList<SWORDCollection>();
        List<Collection> cols = this.workspace.getCollections();
        for (Collection c : cols)
        {
            scs.add(new SWORDCollection(c));
        }
        return scs;
    }

    public SWORDCollection getCollection(String s)
    {
        Collection col = this.workspace.getCollection(s);
        return new SWORDCollection(col);
    }

    public SWORDCollection getCollectionThatAccepts(MimeType... mimeTypes)
    {
        Collection col = this.workspace.getCollectionThatAccepts(mimeTypes);
        return new SWORDCollection(col);
    }

    public SWORDCollection getCollectionThatAccepts(String... strings)
    {
        Collection col = this.workspace.getCollectionThatAccepts(strings);
        return new SWORDCollection(col);
    }

    public List<SWORDCollection> getCollectionsThatAccept(MimeType... mimeTypes)
    {
        List<SWORDCollection> scs = new ArrayList<SWORDCollection>();
        List<Collection> cols = this.workspace.getCollectionsThatAccept(mimeTypes);
        for (Collection c : cols)
        {
            scs.add(new SWORDCollection(c));
        }
        return scs;
    }

    public List<SWORDCollection> getCollectionsThatAccept(String... strings)
    {
        List<SWORDCollection> scs = new ArrayList<SWORDCollection>();
        List<Collection> cols = this.workspace.getCollectionsThatAccept(strings);
        for (Collection c : cols)
        {
            scs.add(new SWORDCollection(c));
        }
        return scs;
    }

    public String getTitle()
    {
        return this.workspace.getTitle();
    }
}
