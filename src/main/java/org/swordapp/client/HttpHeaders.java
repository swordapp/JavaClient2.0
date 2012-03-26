package org.swordapp.client;

import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.abdera.protocol.client.RequestOptions;
import org.apache.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HttpHeaders
{
    private static Logger log = Logger.getLogger(HttpHeaders.class);

    public void addContentDisposition(RequestOptions options, String filename)
            throws SWORDClientException
    {
        if (filename == null || "".equals(filename))
        {
            log.error("No filename has been specified for the Content-Disposition, but this is a required field; throwing Exception");
            throw new SWORDClientException("No filename has been specified for the Content-Disposition, but this is a required field");
        }
        options.setHeader("Content-Disposition", "attachment; filename=" + filename);
    }

    public void addContentMd5(RequestOptions options, String checksum)
    {
        if (checksum != null && !"".equals(checksum))
        {
            if (log.isDebugEnabled())
            {
                log.debug("Adding HTTP Header to RequestOptions; Content-MD5: " + checksum);
            }
            options.setHeader("Content-MD5", checksum);
        }
    }

    public void addPackaging(RequestOptions options, String packaging)
    {
        if (packaging != null && !"".equals(packaging))
        {
            if (log.isDebugEnabled())
            {
                log.debug("Adding HTTP Header to RequestOptions; Packaging: " + packaging);
            }
            options.setHeader("Packaging", packaging);
        }
    }

    public void addAcceptPackaging(RequestOptions options, String packaging)
    {
        if (packaging != null && !"".equals(packaging))
        {
            if (log.isDebugEnabled())
            {
                log.debug("Adding HTTP Header to RequestOptions; Accept-Packaging: " + packaging);
            }
            options.setHeader("Accept-Packaging", packaging);
        }
    }

    public void addAccept(RequestOptions options, String mimeType)
    {
        if (mimeType != null && !"".equals(mimeType))
        {
            if (log.isDebugEnabled())
            {
                log.debug("Adding HTTP Header to RequestOptions; Accept: " + mimeType);
            }
            options.setHeader("Accept", mimeType);
        }
    }

    public void addInProgress(RequestOptions options, boolean inProgress)
    {
        if (log.isDebugEnabled())
        {
            log.debug("Adding HTTP Header to RequestOptions; In-Progress: " + (inProgress ? "true" : "false"));
        }
        options.setHeader("In-Progress", inProgress ? "true" : "false");
    }

    public void addMetadataRelevant(RequestOptions options, boolean suppressMetadata)
    {
        if (log.isDebugEnabled())
        {
            log.debug("Adding HTTP Header to RequestOptions; Metadata-Relevant: " + (suppressMetadata ? "true" : "false"));
        }
        options.setHeader("Metadata-Relevant", suppressMetadata ? "true" : "false");
    }

    public void addSlug(RequestOptions options, String slug)
    {
        if (slug != null && !"".equals(slug))
        {
            if (log.isDebugEnabled())
            {
                log.debug("Adding HTTP Header to RequestOptions; Slug: " + slug);
            }
            options.setHeader("Slug", slug);
        }
    }

    public void addOnBehalfOf(RequestOptions options, String username)
    {
        if (username != null && !"".equals(username))
        {
            if (log.isDebugEnabled())
            {
                log.debug("Adding HTTP Header to RequestOptions; On-Behalf-Of: " + username);
            }
            options.setHeader("On-Behalf-Of", username);
        }
    }

    public String getLocation(ClientResponse resp)
    {
        return resp.getHeader("Location");
    }

    public String getPackaging(ClientResponse resp)
    {
        return resp.getHeader("Packaging");
    }

    public String getContentMD5(ClientResponse resp)
    {
        return resp.getHeader("Content-MD5");
    }

    public Date getLastModified(ClientResponse resp)
    {
        String rfc822date = resp.getHeader("Last-Modified");

        if (rfc822date != null)
        {
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z");
            try
            {
                return sdf.parse(rfc822date);
            }
            catch (ParseException e)
            {
                // doesn't matter, we just ignore the date
                return null;
            }
        }
        return null;
    }
}
