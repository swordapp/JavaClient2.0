package org.swordapp.client;

import org.apache.abdera.model.Entry;
import org.apache.abdera.protocol.client.util.MultipartRelatedRequestEntity;
import org.apache.abdera.util.MimeTypeHelper;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.methods.RequestEntity;

import java.io.*;

public class SWORDMultipartRequestEntity implements RequestEntity
{
    static final int BUFF_SIZE = 1024;
    static final byte[] buffer = new byte[BUFF_SIZE];
    private String boundary;

    private Deposit deposit;
    private final EntryPart entry;
    private final InputStream input;
    private final String contentType;

    public SWORDMultipartRequestEntity(Deposit deposit)
			throws SWORDClientException
    {
        // store the deposit
        this.deposit = deposit;

		// irreversibly link the entry and resource parts in the deposit
		this.deposit.linkEntryAndMediaParts();

        // extract the things that we're going to want to use
        this.entry = deposit.getEntryPart();
        this.contentType = deposit.getMimeType();
        this.input = deposit.getFile();

        // construct the default things that we need
        this.boundary = String.valueOf(System.currentTimeMillis());
    }

    public void writeRequest(OutputStream os)
            throws IOException
    {
        DataOutputStream out = new DataOutputStream(os);
        out.writeBytes("--" + this.boundary + "\r\n");
        writeEntry(out);
        writeInput(out);
    }

    private void writeEntry(DataOutputStream out) throws IOException
    {
        out.writeBytes("Content-Type: " + MimeTypeHelper.getMimeType(entry.getEntry()) + "\r\n");
        out.writeBytes("Content-Disposition: attachment; name=\"atom\"\r\n");
        out.writeBytes("\r\n");
        entry.getEntry().writeTo(out);
        out.writeBytes("\r\n--" + this.boundary + "\r\n");
    }

	// FIXME: this fails if we receive an authentication request, because it has
	// already burned through the input stream on the first attempt
    private void writeInput(DataOutputStream out) throws IOException
    {
        if (this.contentType == null)
        {
            throw new NullPointerException("media content type can't be null");
        }
        out.writeBytes("Content-Type: " + this.contentType + "\r\n");

        out.writeBytes("Content-Disposition: attachment; name=\"payload\"; filename=\"" + this.deposit.getFilename() + "\"\r\n");

        // calculate and output the Content-ID part header
        String contentId = this.entry.getEntry().getContentSrc().toString();
        if (!contentId.matches("cid:.+"))
        {
            throw new IllegalArgumentException("entry content source is not a correct content-ID");
        }
        out.writeBytes("Content-ID: <" + contentId.substring(4) + ">\r\n");

        // calculate and output the Content-MD5 part header
        String contentMd5 = this.deposit.getMd5();
        if (contentMd5 != null && !"".equals(contentMd5))
        {
            out.writeBytes("Content-MD5: " + contentMd5 + "\r\n");
        }

        // calculate and output the Packaging part header
        String packaging = this.deposit.getPackaging();
        if (packaging != null && !"".equals(packaging))
        {
            out.writeBytes("Packaging: " + packaging + "\r\n");
        }

        // line break
        out.writeBytes("\r\n");

		// FIXME: this reads everything into memory, which is a terrible idea.  This is from the Abdera code
		// itself, so interesting to note that it's broken there too.  Fix it.
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        while (this.input.read(buffer) != -1)
        {
            output.write(buffer);
        }

        Base64 base64 = new Base64();
        out.write(base64.encode(output.toByteArray()));
        out.writeBytes("\r\n" + "--" + this.boundary + "--");
    }

    public long getContentLength()
    {
        return -1;
    }

    public String getContentType()
    {
        return "multipart/related; boundary=\"" + this.boundary + "\";type=\"" + MimeTypeHelper.getMimeType(this.entry.getEntry()) + "\"";
    }

    public boolean isRepeatable()
    {
        return true;
    }
}
