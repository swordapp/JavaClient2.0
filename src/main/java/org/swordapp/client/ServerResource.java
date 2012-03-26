package org.swordapp.client;

import java.net.URI;
import java.util.Date;
import java.util.List;

public class ServerResource
{
    private URI uri;
    private List<String> packaging;
    private String depositedBy;
    private Date depositedOn;
    private String depositedOnBehalfOf;

    public URI getUri()
    {
        return uri;
    }

    public void setUri(URI uri)
    {
        this.uri = uri;
    }

    public List<String> getPackaging()
    {
        return packaging;
    }

    public void setPackaging(List<String> packaging)
    {
        this.packaging = packaging;
    }

    public String getDepositedBy()
    {
        return depositedBy;
    }

    public void setDepositedBy(String depositedBy)
    {
        this.depositedBy = depositedBy;
    }

    public Date getDepositedOn()
    {
        return depositedOn;
    }

    public void setDepositedOn(Date depositedOn)
    {
        this.depositedOn = depositedOn;
    }

    public String getDepositedOnBehalfOf()
    {
        return depositedOnBehalfOf;
    }

    public void setDepositedOnBehalfOf(String depositedOnBehalfOf)
    {
        this.depositedOnBehalfOf = depositedOnBehalfOf;
    }
}
