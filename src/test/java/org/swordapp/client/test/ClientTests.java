package org.swordapp.client.test;

import org.apache.abdera.i18n.iri.IRI;
import org.junit.*;
import static org.junit.Assert.*;

import org.swordapp.client.AtomStatement;
import org.swordapp.client.ClientConfiguration;
import org.swordapp.client.Content;
import org.swordapp.client.Deposit;
import org.swordapp.client.EntryPart;
import org.swordapp.client.OreStatement;
import org.swordapp.client.SWORDClient;
import org.swordapp.client.Statement;
import org.swordapp.client.StatementFactory;
import org.swordapp.client.SwordIdentifier;
import org.swordapp.client.SwordResponse;

import javax.activation.MimeType;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Date;

public class ClientTests
{
    @Test
    public void simpleClientInit()
    {
        // construct without arguments
        SWORDClient client1 = new SWORDClient();

        // construct with a default ClientConfiguration
        SWORDClient client2 = new SWORDClient(new ClientConfiguration());
    }

	@Test
    public void depositObject()
			throws Exception
    {
        Deposit deposit = new Deposit();

		deposit.setSlug("abcdef");
		deposit.setInProgress(true);
		deposit.setFilename("example.zip");
		deposit.setMd5("3df4ed");
		deposit.setMetadataRelevant(true);
		deposit.setMimeType("application/zip");
		deposit.setPackaging("http://package/");
		deposit.setEntryPart(new EntryPart());
		deposit.setFile(new ByteArrayInputStream("test".getBytes()));

		assertEquals(deposit.getSlug(), "abcdef");
		assertEquals(deposit.getSuggestedIdentifier(), "abcdef");
		assertTrue(deposit.isInProgress());
		assertEquals(deposit.getFilename(), "example.zip");
		assertEquals(deposit.getMd5(), "3df4ed");
		assertTrue(deposit.isMetadataRelevant());
		assertEquals(deposit.getPackaging(), "http://package/");
		assertTrue(deposit.getEntryPart() != null);

		byte[] bytes = new byte["test".getBytes().length];
		deposit.getFile().read(bytes);
		String s = new String(bytes);
		assertEquals(s, "test");
    }

	@Test
	public void contentObject()
			throws Exception
	{
		Content content = new Content();

		content.setInputStream(new ByteArrayInputStream("test".getBytes()));
		content.setMimeType(new MimeType("application/atom+xml;type=feed"));
		content.setPackaging("http://packaging/");

		assertEquals(content.getMimeType().toString(), "application/atom+xml; type=feed");
		assertEquals(content.getPackaging(), "http://packaging/");

		byte[] bytes = new byte["test".getBytes().length];
		content.getInputStream().read(bytes);
		String s = new String(bytes);
		assertEquals(s, "test");
	}

	@Test
	public void identifierObject()
			throws Exception
	{
		String href = "http://testing/";
		String type = "application/atom+xml;type=feed";
		String rel = "edit-media";

		IRI iri = new IRI(href);
		MimeType mime = new MimeType(type);

		// test the different constructors
		SwordIdentifier si = new SwordIdentifier(href, type, rel);
		assertEquals(si.getHref(), href);
		assertEquals(si.getType(), type);
		assertEquals(si.getRel(), rel);
		assertNull(si.getIRI());
		assertNull(si.getLink());
		assertNull(si.getMimeType());

		SwordIdentifier si1 = new SwordIdentifier(href, type);
		assertEquals(si1.getHref(), href);
		assertEquals(si1.getType(), type);
		assertNull(si1.getRel());
		assertNull(si1.getIRI());
		assertNull(si1.getLink());
		assertNull(si1.getMimeType());

		SwordIdentifier si2 = new SwordIdentifier(iri, mime, rel);
		assertEquals(si2.getHref(), href);
		assertEquals(si2.getType(), type);
		assertEquals(si2.getRel(), rel);
		assertEquals(si2.getIRI(), iri);
		assertNull(si2.getLink());
		assertEquals(si2.getMimeType(), mime);

		SwordIdentifier si3 = new SwordIdentifier(iri, mime);
		assertEquals(si3.getHref(), href);
		assertEquals(si3.getType(), type);
		assertNull(si3.getRel());
		assertEquals(si3.getIRI(), iri);
		assertNull(si3.getLink());
		assertEquals(si3.getMimeType(), mime);
	}

	@Test
	public void swordResponseObject()
	{
		SwordResponse sr = new SwordResponse(204);

		assertEquals(sr.getStatusCode(), 204);
		assertNull(sr.getContentMD5());
		assertNull(sr.getLastModified());
		assertNull(sr.getLocation());

		SwordResponse sr1 = new SwordResponse(204, "http://location");

		assertEquals(sr1.getStatusCode(), 204);
		assertEquals(sr1.getLocation(), "http://location");
		assertNull(sr.getContentMD5());
		assertNull(sr.getLastModified());

		sr1.setContentMD5("abcdef");
		assertEquals(sr1.getContentMD5(), "abcdef");

		Date date = new Date();
		sr1.setLastModified(date);
		assertEquals(sr1.getLastModified(), date);
	}

	@Test
	public void statementFactory()
	{
		Statement s = StatementFactory.getStatementShell("application/rdf+xml");
		assertTrue(s instanceof OreStatement);

		Statement s1 = StatementFactory.getStatementShell("application/atom+xml;type=feed");
		assertTrue(s1 instanceof AtomStatement);
	}
}
