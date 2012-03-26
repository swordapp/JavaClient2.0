package org.swordapp.client;

import org.apache.abdera.i18n.iri.IRI;

public class ResourceState
{
    private IRI iri;
    private String description;

    public IRI getIri()
    {
        return iri;
    }

    public void setIri(IRI iri)
    {
        this.iri = iri;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }
}
