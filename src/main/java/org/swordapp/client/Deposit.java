package org.swordapp.client;

import java.io.InputStream;

public class Deposit
{
    private EntryPart entryPart = null;
    private InputStream file = null;
    private long contentLength = -1;
    private String filename = null;
    private String mimeType = null;
    private String slug = null;
    private String md5 = null;
    private String packaging;
    private boolean inProgress = false;
    private boolean metadataRelevant = false;

	public Deposit() {}

    public Deposit(EntryPart entryPart, InputStream file, String filename, String mimeType, String packaging,
                   String slug, String md5, boolean inProgress, boolean metadataRelevant)
    {
        this(entryPart, file, -1, filename, mimeType, packaging, slug, md5, inProgress, metadataRelevant);
    }

    public Deposit(EntryPart entryPart, InputStream file, long contentLength, String filename, String mimeType, String packaging,
                   String slug, String md5, boolean inProgress, boolean metadataRelevant)
    {
        this.entryPart = entryPart;
        this.file = file;
        this.contentLength = contentLength;
        this.filename = filename;
        this.mimeType = mimeType;
        this.packaging = packaging;
        this.slug = slug;
        this.md5 = md5;
        this.inProgress = inProgress;
        this.metadataRelevant = metadataRelevant;
    }

	public void linkEntryAndMediaParts()
			throws SWORDClientException
	{
		if (this.entryPart != null && this.file != null)
		{
			if (this.mimeType != null)
			{
        		this.entryPart.referenceMediaPart(mimeType);
			}
			else
			{
				throw new SWORDClientException("Unable to link Entry and Media Parts in multipart deposit, as no mimetype supplied");
			}
		}
	}

    public boolean isEntryOnly()
    {
        return this.entryPart != null && this.file == null;
    }

    public boolean isMultipart()
    {
        return this.entryPart != null && this.file != null;
    }

    public boolean isBinaryOnly()
    {
        return this.entryPart == null && this.file != null;
    }

    public EntryPart getEntryPart()
    {
        return entryPart;
    }

    public void setEntryPart(EntryPart entryPart)
    {
        this.entryPart = entryPart;
    }

    public InputStream getFile()
    {
        return file;
    }

    public void setFile(InputStream file)
    {
        this.file = file;
    }

    public String getFilename()
    {
        return filename;
    }

    public void setFilename(String filename)
    {
        this.filename = filename;
    }

    public long getContentLength()
    {
        return contentLength;
    }

    public void setContentLength(long contentLength)
    {
        this.contentLength = contentLength;
    }

    public String getMimeType()
    {
        return mimeType;
    }

    public void setMimeType(String mimeType)
    {
        this.mimeType = mimeType;
    }

	public void setSuggestedIdentifier(String slug)
	{
		this.setSlug(slug);
	}

	public String getSuggestedIdentifier()
	{
		return this.getSlug();
	}

    public String getSlug()
    {
        return slug;
    }

    public void setSlug(String slug)
    {
        this.slug = slug;
    }

    public String getMd5()
    {
        return md5;
    }

    public void setMd5(String md5)
    {
        this.md5 = md5;
    }

    public String getPackaging()
    {
        return packaging;
    }

    public void setPackaging(String packaging)
    {
        this.packaging = packaging;
    }

    public boolean isInProgress()
    {
        return inProgress;
    }

    public void setInProgress(boolean inProgress)
    {
        this.inProgress = inProgress;
    }

	public boolean isMetadataRelevant()
	{
		return metadataRelevant;
	}

	public void setMetadataRelevant(boolean metadataRelevant)
	{
		this.metadataRelevant = metadataRelevant;
	}
}
