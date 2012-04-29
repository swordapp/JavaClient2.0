package org.swordapp.client.test;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import org.swordapp.client.AtomStatement;
import org.swordapp.client.AuthCredentials;
import org.swordapp.client.ClientConfiguration;
import org.swordapp.client.Content;
import org.swordapp.client.Deposit;
import org.swordapp.client.DepositReceipt;
import org.swordapp.client.EntryPart;
import org.swordapp.client.OreStatement;
import org.swordapp.client.SWORDClient;
import org.swordapp.client.SWORDCollection;
import org.swordapp.client.SWORDError;
import org.swordapp.client.ServiceDocument;
import org.swordapp.client.Statement;
import org.swordapp.client.SwordResponse;
import org.swordapp.client.UriRegistry;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

// FIXME: these tests rely on a hard-coded set of fixtures to do with my local
// set up.  This means anyone else trying to run these tests will need to reconfigure
// and recompile.  Sorry.

public class SpecTests
{
	private String sdIRI = null;
	private String user = null;
	private String pass = null;
	private String obo = null;
	private String file = null;
	private String fileMd5 = null;

	@Before
	public void setUp()
			throws Exception
	{
        InputStream props = this.getClass().getClassLoader().getResourceAsStream("spectests.properties");
        Properties properties = new Properties();
        properties.load(props);
        this.sdIRI = properties.getProperty("sdIRI");
        this.user = properties.getProperty("user");
        this.pass = properties.getProperty("pass");
        this.obo = properties.getProperty("obo");
        this.file = properties.getProperty("file");


		// FIXME: should read this all from some test config, or try to auto-locate
		// resources (particularly the file)
        /*
		this.sdIRI = "http://localhost:8080/swordv2/servicedocument";
		this.user = "richard";
		this.pass = "dspace";
		this.obo = "obo";
		this.file = "/home/richard/Code/External/JavaClient2.0/src/test/resources/example.zip";
		*/
		this.fileMd5 = DigestUtils.md5Hex(new FileInputStream(this.file));
	}

	@Test
	public void getServiceDocument()
			throws Exception
	{
		SWORDClient client = new SWORDClient(new ClientConfiguration());
		ServiceDocument sd = client.getServiceDocument(this.sdIRI, new AuthCredentials(this.user, this.pass));

		// verify that the service document contains the sorts of things we are expecting
		assertTrue(sd.getService() != null);
		assertEquals(sd.getVersion(), "2.0");
	}

	@Test
	public void getServiceDocumentOBO()
			throws Exception
	{
		SWORDClient client = new SWORDClient(new ClientConfiguration());
		ServiceDocument sd = client.getServiceDocument(this.sdIRI, new AuthCredentials(this.user, this.pass, this.obo));

		// verify that the service document contains the sorts of things we are expecting
		assertTrue(sd.getService() != null);
		assertEquals(sd.getVersion(), "2.0");
	}

	@Test
	public void basicCreateResourceWithPackage()
			throws Exception
	{
		SWORDClient client = new SWORDClient(new ClientConfiguration());
		ServiceDocument sd = client.getServiceDocument(this.sdIRI, new AuthCredentials(this.user, this.pass));
		SWORDCollection col = sd.getWorkspaces().get(0).getCollections().get(0);

		Deposit deposit = new Deposit();
		deposit.setFile(new FileInputStream(this.file));
		deposit.setMimeType("application/zip");
		deposit.setFilename("example.zip");
		deposit.setPackaging(UriRegistry.PACKAGE_SIMPLE_ZIP);
		deposit.setMd5(this.fileMd5);

		assertTrue(deposit.isBinaryOnly());

		DepositReceipt receipt = client.deposit(col, deposit, new AuthCredentials(this.user, this.pass));
		assertEquals(receipt.getStatusCode(), 201);
		assertTrue(receipt.getLocation() != null);
	}

	@Test
	public void advancedCreateResourceWithPackage()
			throws Exception
	{
		SWORDClient client = new SWORDClient(new ClientConfiguration());
		ServiceDocument sd = client.getServiceDocument(this.sdIRI, new AuthCredentials(this.user, this.pass, this.obo));
		SWORDCollection col = sd.getWorkspaces().get(0).getCollections().get(0);

		Deposit deposit = new Deposit();
		deposit.setFile(new FileInputStream(this.file));
		deposit.setMimeType("application/zip");
		deposit.setFilename("example.zip");
		deposit.setPackaging(UriRegistry.PACKAGE_SIMPLE_ZIP);
		deposit.setMd5(this.fileMd5);
		deposit.setInProgress(true);
		deposit.setSuggestedIdentifier("abcdefg");

		DepositReceipt receipt = client.deposit(col, deposit, new AuthCredentials(this.user, this.pass, this.obo));
		assertEquals(receipt.getStatusCode(), 201);
		assertTrue(receipt.getLocation() != null);
	}

	/* FIXME: multipart is not currently functional ...
	@Test
	public void basicCreateResourceWithMutlipart()
			throws Exception
	{
		SWORDClient client = new SWORDClient(new ClientConfiguration());
		ServiceDocument sd = client.getServiceDocument(this.sdIRI, new AuthCredentials(this.user, this.pass));
		SWORDCollection col = sd.getWorkspaces().get(0).getCollections().get(0);

		EntryPart ep = new EntryPart();
		ep.addDublinCore("title", "My Title");

		Deposit deposit = new Deposit();
		deposit.setEntryPart(ep);
		deposit.setFile(new FileInputStream(this.file));
		deposit.setMimeType("application/zip");
		deposit.setFilename("example.zip");
		deposit.setPackaging(UriRegistry.PACKAGE_SIMPLE_ZIP);
		deposit.setMd5(this.fileMd5);

		assertTrue(deposit.isMultipart());

		DepositReceipt receipt = client.deposit(col, deposit, new AuthCredentials(this.user, this.pass));
		assertEquals(receipt.getStatusCode(), 201);
		assertTrue(receipt.getLocation() != null);
	}

	@Test
	public void advancedCreateResourceWithMutlipart()
			throws Exception
	{
		SWORDClient client = new SWORDClient(new ClientConfiguration());
		ServiceDocument sd = client.getServiceDocument(this.sdIRI, new AuthCredentials(this.user, this.pass, this.obo));
		SWORDCollection col = sd.getWorkspaces().get(0).getCollections().get(0);

		EntryPart ep = new EntryPart();
		ep.addDublinCore("title", "My Title");

		Deposit deposit = new Deposit();
		deposit.setEntryPart(ep);
		deposit.setFile(new FileInputStream(this.file));
		deposit.setMimeType("application/zip");
		deposit.setFilename("example.zip");
		deposit.setPackaging(UriRegistry.PACKAGE_SIMPLE_ZIP);
		deposit.setMd5(this.fileMd5);
		deposit.setInProgress(true);
		deposit.setSuggestedIdentifier("abcdefg");

		assertTrue(deposit.isMultipart());

		DepositReceipt receipt = client.deposit(col, deposit, new AuthCredentials(this.user, this.pass, this.obo));
		assertEquals(receipt.getStatusCode(), 201);
		assertTrue(receipt.getLocation() != null);
	}
	*/

	@Test
	public void basicCreateResourceWithEntry()
			throws Exception
	{
		SWORDClient client = new SWORDClient(new ClientConfiguration());
		ServiceDocument sd = client.getServiceDocument(this.sdIRI, new AuthCredentials(this.user, this.pass));
		SWORDCollection col = sd.getWorkspaces().get(0).getCollections().get(0);

		EntryPart ep = new EntryPart();
		ep.addDublinCore("title", "An entry only deposit");
		ep.addDublinCore("abstract", "abstract");
		ep.addDublinCore("identifier", "http://whatever/");

		Deposit deposit = new Deposit();
		deposit.setEntryPart(ep);

		assertTrue(deposit.isEntryOnly());

		DepositReceipt receipt = client.deposit(col, deposit, new AuthCredentials(this.user, this.pass));
		assertEquals(receipt.getStatusCode(), 201);
		assertTrue(receipt.getLocation() != null);
	}

	@Test
	public void advancedCreateResourceWithEntry()
			throws Exception
	{
		SWORDClient client = new SWORDClient(new ClientConfiguration());
		ServiceDocument sd = client.getServiceDocument(this.sdIRI, new AuthCredentials(this.user, this.pass, this.obo));
		SWORDCollection col = sd.getWorkspaces().get(0).getCollections().get(0);

		EntryPart ep = new EntryPart();
		ep.addDublinCore("title", "An entry only deposit");
		ep.addDublinCore("abstract", "abstract");
		ep.addDublinCore("identifier", "http://whatever/");

		Deposit deposit = new Deposit();
		deposit.setEntryPart(ep);
		deposit.setInProgress(true);
		deposit.setSuggestedIdentifier("1234567890");

		DepositReceipt receipt = client.deposit(col, deposit, new AuthCredentials(this.user, this.pass, this.obo));
		assertEquals(receipt.getStatusCode(), 201);
		assertTrue(receipt.getLocation() != null);
	}

	@Test
	public void basicRetrieveDepositReceipt()
			throws Exception
	{
		SWORDClient client = new SWORDClient(new ClientConfiguration());
		ServiceDocument sd = client.getServiceDocument(this.sdIRI, new AuthCredentials(this.user, this.pass));
		SWORDCollection col = sd.getWorkspaces().get(0).getCollections().get(0);

		Deposit deposit = new Deposit();
		deposit.setFile(new FileInputStream(this.file));
		deposit.setMimeType("application/zip");
		deposit.setFilename("example.zip");
		deposit.setPackaging(UriRegistry.PACKAGE_SIMPLE_ZIP);
		deposit.setMd5(this.fileMd5);

		DepositReceipt receipt = client.deposit(col, deposit, new AuthCredentials(this.user, this.pass));
		assertTrue(receipt.getLocation() != null);

		DepositReceipt newReceipt = client.getDepositReceipt(receipt.getLocation(), new AuthCredentials(this.user, this.pass));
		assertEquals(newReceipt.getStatusCode(), 200);
		assertTrue(newReceipt.getEntry() != null);
	}

	@Test
	public void advancedRetrieveDepositReceipt()
			throws Exception
	{
		SWORDClient client = new SWORDClient(new ClientConfiguration());
		ServiceDocument sd = client.getServiceDocument(this.sdIRI, new AuthCredentials(this.user, this.pass, this.obo));
		SWORDCollection col = sd.getWorkspaces().get(0).getCollections().get(0);

		Deposit deposit = new Deposit();
		deposit.setFile(new FileInputStream(this.file));
		deposit.setMimeType("application/zip");
		deposit.setFilename("example.zip");
		deposit.setPackaging(UriRegistry.PACKAGE_SIMPLE_ZIP);
		deposit.setMd5(this.fileMd5);
		deposit.setInProgress(true);
		deposit.setSuggestedIdentifier("0987654321");

		DepositReceipt receipt = client.deposit(col, deposit, new AuthCredentials(this.user, this.pass, this.obo));
		assertTrue(receipt.getLocation() != null);

		DepositReceipt newReceipt = client.getDepositReceipt(receipt.getLocation(), new AuthCredentials(this.user, this.pass, this.obo));
		assertEquals(newReceipt.getStatusCode(), 200);
		assertTrue(newReceipt.getEntry() != null);
	}

	@Test
	public void basicRetrieveContentContIRI()
			throws Exception
	{
		SWORDClient client = new SWORDClient(new ClientConfiguration());
		ServiceDocument sd = client.getServiceDocument(this.sdIRI, new AuthCredentials(this.user, this.pass));
		SWORDCollection col = sd.getWorkspaces().get(0).getCollections().get(0);

		Deposit deposit = new Deposit();
		deposit.setFile(new FileInputStream(this.file));
		deposit.setMimeType("application/zip");
		deposit.setFilename("example.zip");
		deposit.setPackaging(UriRegistry.PACKAGE_SIMPLE_ZIP);
		deposit.setMd5(this.fileMd5);

		DepositReceipt receipt = client.deposit(col, deposit, new AuthCredentials(this.user, this.pass));
		receipt = client.getDepositReceipt(receipt.getLocation(), new AuthCredentials(this.user, this.pass));

		assertTrue(receipt.getContentLink() != null);
		Content content = client.getContent(receipt.getContentLink());

		assertTrue(content.getInputStream() != null);
	}

	@Test
	public void basicRetrieveContentEmIRI()
			throws Exception
	{
		SWORDClient client = new SWORDClient(new ClientConfiguration());
		ServiceDocument sd = client.getServiceDocument(this.sdIRI, new AuthCredentials(this.user, this.pass));
		SWORDCollection col = sd.getWorkspaces().get(0).getCollections().get(0);

		Deposit deposit = new Deposit();
		deposit.setFile(new FileInputStream(this.file));
		deposit.setMimeType("application/zip");
		deposit.setFilename("example.zip");
		deposit.setPackaging(UriRegistry.PACKAGE_SIMPLE_ZIP);
		deposit.setMd5(this.fileMd5);

		DepositReceipt receipt = client.deposit(col, deposit, new AuthCredentials(this.user, this.pass));
		receipt = client.getDepositReceipt(receipt.getLocation(), new AuthCredentials(this.user, this.pass));

		assertTrue(receipt.getEditMediaLink() != null);
		Content content = client.getContent(receipt.getEditMediaLink(), new AuthCredentials(this.user, this.pass));

		assertTrue(content.getInputStream() != null);
	}

	@Test
	public void advancedRetrieveContentEmIRI()
			throws Exception
	{
		SWORDClient client = new SWORDClient(new ClientConfiguration());
		ServiceDocument sd = client.getServiceDocument(this.sdIRI, new AuthCredentials(this.user, this.pass, this.obo));
		SWORDCollection col = sd.getWorkspaces().get(0).getCollections().get(0);

		Deposit deposit = new Deposit();
		deposit.setFile(new FileInputStream(this.file));
		deposit.setMimeType("application/zip");
		deposit.setFilename("example.zip");
		deposit.setPackaging(UriRegistry.PACKAGE_SIMPLE_ZIP);
		deposit.setMd5(this.fileMd5);

		DepositReceipt receipt = client.deposit(col, deposit, new AuthCredentials(this.user, this.pass, this.obo));
		receipt = client.getDepositReceipt(receipt.getLocation(), new AuthCredentials(this.user, this.pass, this.obo));

		assertTrue(receipt.getEditMediaLink() != null);

		List<String> packagings = receipt.getPackaging();
		String packaging = UriRegistry.PACKAGE_SIMPLE_ZIP;
		if (packagings.size() > 0)
		{
			packaging = packagings.get(0);
		}
		Content content = client.getContent(receipt.getEditMediaLink(), packaging, new AuthCredentials(this.user, this.pass, this.obo));

		assertTrue(content.getInputStream() != null);
		assertTrue(content.getFeed() == null);
	}

	@Test
	public void errorRetrieveContentEmIRI()
			throws Exception
	{
		SWORDClient client = new SWORDClient(new ClientConfiguration());
		ServiceDocument sd = client.getServiceDocument(this.sdIRI, new AuthCredentials(this.user, this.pass));
		SWORDCollection col = sd.getWorkspaces().get(0).getCollections().get(0);

		Deposit deposit = new Deposit();
		deposit.setFile(new FileInputStream(this.file));
		deposit.setMimeType("application/zip");
		deposit.setFilename("example.zip");
		deposit.setPackaging(UriRegistry.PACKAGE_SIMPLE_ZIP);
		deposit.setMd5(this.fileMd5);

		DepositReceipt receipt = client.deposit(col, deposit, new AuthCredentials(this.user, this.pass));
		receipt = client.getDepositReceipt(receipt.getLocation(), new AuthCredentials(this.user, this.pass));

		String error = "http://purl.org/net/sword/package/IJustMadeThisUp";
		boolean wasError = false;
		try
		{
			Content content = client.getContent(receipt.getEditMediaLink(), error, new AuthCredentials(this.user, this.pass));
		}
		catch (SWORDError e)
		{
			assertEquals(e.getStatus(), 406);
			assertEquals(e.getErrorURI(), "http://purl.org/net/sword/error/ErrorContent");
			wasError = true;
		}
		assertTrue(wasError);
	}

	@Test
	public void retrieveContentEmIRIAsFeed()
			throws Exception
	{
		SWORDClient client = new SWORDClient(new ClientConfiguration());
		ServiceDocument sd = client.getServiceDocument(this.sdIRI, new AuthCredentials(this.user, this.pass));
		SWORDCollection col = sd.getWorkspaces().get(0).getCollections().get(0);

		Deposit deposit = new Deposit();
		deposit.setFile(new FileInputStream(this.file));
		deposit.setMimeType("application/zip");
		deposit.setFilename("example.zip");
		deposit.setPackaging(UriRegistry.PACKAGE_SIMPLE_ZIP);
		deposit.setMd5(this.fileMd5);

		DepositReceipt receipt = client.deposit(col, deposit, new AuthCredentials(this.user, this.pass));
		receipt = client.getDepositReceipt(receipt.getLocation(), new AuthCredentials(this.user, this.pass));

		assertTrue(receipt.getEditMediaLink("application/atom+xml;type=feed") != null);

		Content content = client.getContent(receipt.getEditMediaLink("application/atom+xml;type=feed"), new AuthCredentials(this.user, this.pass));

		assertTrue(content.getFeed() != null);
		assertTrue(content.getInputStream() == null);
	}

	@Test
	public void basicReplaceFileContent()
			throws Exception
	{
		SWORDClient client = new SWORDClient(new ClientConfiguration());
		ServiceDocument sd = client.getServiceDocument(this.sdIRI, new AuthCredentials(this.user, this.pass));
		SWORDCollection col = sd.getWorkspaces().get(0).getCollections().get(0);

		Deposit deposit = new Deposit();
		deposit.setFile(new FileInputStream(this.file));
		deposit.setMimeType("application/zip");
		deposit.setFilename("example.zip");
		deposit.setPackaging(UriRegistry.PACKAGE_SIMPLE_ZIP);
		deposit.setMd5(this.fileMd5);
        deposit.setInProgress(true);

		DepositReceipt receipt = client.deposit(col, deposit, new AuthCredentials(this.user, this.pass));
		receipt = client.getDepositReceipt(receipt.getLocation(), new AuthCredentials(this.user, this.pass));

		Deposit replacement = new Deposit();
		replacement.setFile(new FileInputStream(this.file));
		replacement.setMimeType("application/zip");
		replacement.setFilename("updated.zip");
		replacement.setPackaging(UriRegistry.PACKAGE_SIMPLE_ZIP);
		replacement.setMd5(this.fileMd5);

		SwordResponse resp = client.replaceMedia(receipt, replacement, new AuthCredentials(this.user, this.pass));

		assertEquals(resp.getStatusCode(), 204);
	}

	@Test
	public void advancedReplaceFileContent()
			throws Exception
	{
		SWORDClient client = new SWORDClient(new ClientConfiguration());
		ServiceDocument sd = client.getServiceDocument(this.sdIRI, new AuthCredentials(this.user, this.pass, this.obo));
		SWORDCollection col = sd.getWorkspaces().get(0).getCollections().get(0);

		Deposit deposit = new Deposit();
		deposit.setFile(new FileInputStream(this.file));
		deposit.setMimeType("application/zip");
		deposit.setFilename("example.zip");
		deposit.setPackaging(UriRegistry.PACKAGE_SIMPLE_ZIP);
		deposit.setMd5(this.fileMd5);
        deposit.setInProgress(true);

		DepositReceipt receipt = client.deposit(col, deposit, new AuthCredentials(this.user, this.pass, this.obo));
		receipt = client.getDepositReceipt(receipt.getLocation(), new AuthCredentials(this.user, this.pass, this.obo));

		Deposit replacement = new Deposit();
		replacement.setFile(new FileInputStream(this.file));
		replacement.setMimeType("application/zip");
		replacement.setFilename("updated.zip");
		replacement.setPackaging(UriRegistry.PACKAGE_SIMPLE_ZIP);
		replacement.setMd5(this.fileMd5);
		replacement.setMetadataRelevant(true);

		SwordResponse resp = client.replaceMedia(receipt, replacement, new AuthCredentials(this.user, this.pass, this.obo));

		assertEquals(resp.getStatusCode(), 204);
	}

	@Test
	public void basicReplaceMetadata()
			throws Exception
	{
		SWORDClient client = new SWORDClient(new ClientConfiguration());
		ServiceDocument sd = client.getServiceDocument(this.sdIRI, new AuthCredentials(this.user, this.pass));
		SWORDCollection col = sd.getWorkspaces().get(0).getCollections().get(0);

		EntryPart ep = new EntryPart();
		ep.addDublinCore("title", "An entry only deposit");
		ep.addDublinCore("abstract", "abstract");
		ep.addDublinCore("identifier", "http://whatever/");

		Deposit deposit = new Deposit();
		deposit.setEntryPart(ep);
        deposit.setInProgress(true);

		DepositReceipt receipt = client.deposit(col, deposit, new AuthCredentials(this.user, this.pass));
		receipt = client.getDepositReceipt(receipt.getLocation(), new AuthCredentials(this.user, this.pass));

		EntryPart nep = new EntryPart();
		nep.addDublinCore("title", "A metadata update");
		nep.addDublinCore("abstract", "new abstract");
		nep.addDublinCore("identifier", "http://elsewhere/");

		Deposit replacement = new Deposit();
		replacement.setEntryPart(nep);

		SwordResponse resp = client.replace(receipt, replacement, new AuthCredentials(this.user, this.pass));

		assertTrue(resp.getStatusCode() == 200 || resp.getStatusCode() == 204);
	}

	@Test
	public void advancedReplaceMetadata()
			throws Exception
	{
		SWORDClient client = new SWORDClient(new ClientConfiguration());
		ServiceDocument sd = client.getServiceDocument(this.sdIRI, new AuthCredentials(this.user, this.pass, this.obo));
		SWORDCollection col = sd.getWorkspaces().get(0).getCollections().get(0);

		EntryPart ep = new EntryPart();
		ep.addDublinCore("title", "An entry only deposit");
		ep.addDublinCore("abstract", "abstract");
		ep.addDublinCore("identifier", "http://whatever/");

		Deposit deposit = new Deposit();
		deposit.setEntryPart(ep);
        deposit.setInProgress(true);

		DepositReceipt receipt = client.deposit(col, deposit, new AuthCredentials(this.user, this.pass, this.obo));
		receipt = client.getDepositReceipt(receipt.getLocation(), new AuthCredentials(this.user, this.pass, this.obo));

		EntryPart nep = new EntryPart();
		nep.addDublinCore("title", "A metadata update");
		nep.addDublinCore("abstract", "new abstract");
		nep.addDublinCore("identifier", "http://elsewhere/");

		Deposit replacement = new Deposit();
		replacement.setEntryPart(nep);
		replacement.setInProgress(true);

		SwordResponse resp = client.replace(receipt, replacement, new AuthCredentials(this.user, this.pass, this.obo));

		assertTrue(resp.getStatusCode() == 200 || resp.getStatusCode() == 204);
	}

	/* FIXME: multipart is not currently functional ...
	@Test
	public void basicReplaceWithMutlipart()
			throws Exception
	{
		SWORDClient client = new SWORDClient(new ClientConfiguration());
		ServiceDocument sd = client.getServiceDocument(this.sdIRI, new AuthCredentials(this.user, this.pass));
		SWORDCollection col = sd.getWorkspaces().get(0).getCollections().get(0);

		EntryPart ep = new EntryPart();
		ep.addDublinCore("title", "My Title");

		Deposit deposit = new Deposit();
		deposit.setEntryPart(ep);
		deposit.setFile(new FileInputStream(this.file));
		deposit.setMimeType("application/zip");
		deposit.setFilename("example.zip");
		deposit.setPackaging(UriRegistry.PACKAGE_SIMPLE_ZIP);
		deposit.setMd5(this.fileMd5);

		DepositReceipt receipt = client.deposit(col, deposit, new AuthCredentials(this.user, this.pass));
		receipt = client.getDepositReceipt(receipt.getLocation(), new AuthCredentials(this.user, this.pass));

		EntryPart nep = new EntryPart();
		nep.addDublinCore("title", "A metadata update");
		nep.addDublinCore("abstract", "new abstract");
		nep.addDublinCore("identifier", "http://elsewhere/");

		Deposit replacement = new Deposit();
		replacement.setEntryPart(nep);
		replacement.setFile(new FileInputStream(this.file));
		replacement.setMimeType("application/zip");
		replacement.setFilename("updated.zip");
		replacement.setPackaging(UriRegistry.PACKAGE_SIMPLE_ZIP);
		replacement.setMd5(this.fileMd5);

		SwordResponse resp = client.replace(receipt, replacement, new AuthCredentials(this.user, this.pass));

		assertTrue(resp.getStatusCode() == 200 || resp.getStatusCode() == 204);
	}

	@Test
	public void advancedReplaceWithMutlipart()
			throws Exception
	{
		SWORDClient client = new SWORDClient(new ClientConfiguration());
		ServiceDocument sd = client.getServiceDocument(this.sdIRI, new AuthCredentials(this.user, this.pass, this.obo));
		SWORDCollection col = sd.getWorkspaces().get(0).getCollections().get(0);

		EntryPart ep = new EntryPart();
		ep.addDublinCore("title", "My Title");

		Deposit deposit = new Deposit();
		deposit.setEntryPart(ep);
		deposit.setFile(new FileInputStream(this.file));
		deposit.setMimeType("application/zip");
		deposit.setFilename("example.zip");
		deposit.setPackaging(UriRegistry.PACKAGE_SIMPLE_ZIP);
		deposit.setMd5(this.fileMd5);

		DepositReceipt receipt = client.deposit(col, deposit, new AuthCredentials(this.user, this.pass, this.obo));
		receipt = client.getDepositReceipt(receipt.getLocation(), new AuthCredentials(this.user, this.pass, this.obo));

		EntryPart nep = new EntryPart();
		nep.addDublinCore("title", "A metadata update");
		nep.addDublinCore("abstract", "new abstract");
		nep.addDublinCore("identifier", "http://elsewhere/");

		Deposit replacement = new Deposit();
		replacement.setEntryPart(nep);
		replacement.setFile(new FileInputStream(this.file));
		replacement.setMimeType("application/zip");
		replacement.setFilename("updated.zip");
		replacement.setPackaging(UriRegistry.PACKAGE_SIMPLE_ZIP);
		replacement.setMd5(this.fileMd5);
		replacement.setInProgress(true);

		SwordResponse resp = client.replace(receipt, replacement, new AuthCredentials(this.user, this.pass, this.obo));

		assertTrue(resp.getStatusCode() == 200 || resp.getStatusCode() == 204);
	}
	*/

	@Test
	public void deleteContent()
			throws Exception
	{
		SWORDClient client = new SWORDClient(new ClientConfiguration());
		ServiceDocument sd = client.getServiceDocument(this.sdIRI, new AuthCredentials(this.user, this.pass, this.obo));
		SWORDCollection col = sd.getWorkspaces().get(0).getCollections().get(0);

		Deposit deposit = new Deposit();
		deposit.setFile(new FileInputStream(this.file));
		deposit.setMimeType("application/zip");
		deposit.setFilename("example.zip");
		deposit.setPackaging(UriRegistry.PACKAGE_SIMPLE_ZIP);
		deposit.setMd5(this.fileMd5);
        deposit.setInProgress(true);

		DepositReceipt receipt = client.deposit(col, deposit, new AuthCredentials(this.user, this.pass, this.obo));
		receipt = client.getDepositReceipt(receipt.getLocation(), new AuthCredentials(this.user, this.pass, this.obo));

		SwordResponse resp = client.deleteContent(receipt, new AuthCredentials(this.user, this.pass, this.obo));
		assertEquals(resp.getStatusCode(), 204);
	}

	@Test
	public void basicAddContentSingleFile()
			throws Exception
	{
		SWORDClient client = new SWORDClient(new ClientConfiguration());
		ServiceDocument sd = client.getServiceDocument(this.sdIRI, new AuthCredentials(this.user, this.pass));
		SWORDCollection col = sd.getWorkspaces().get(0).getCollections().get(0);

		Deposit deposit = new Deposit();
		deposit.setFile(new FileInputStream(this.file));
		deposit.setMimeType("application/zip");
		deposit.setFilename("example.zip");
		deposit.setPackaging(UriRegistry.PACKAGE_SIMPLE_ZIP);
		deposit.setMd5(this.fileMd5);
        deposit.setInProgress(true);

		DepositReceipt receipt = client.deposit(col, deposit, new AuthCredentials(this.user, this.pass));
		receipt = client.getDepositReceipt(receipt.getLocation(), new AuthCredentials(this.user, this.pass));

		Deposit addition = new Deposit();
		addition.setFile(new FileInputStream(this.file));
		addition.setMimeType("application/zip");
		addition.setFilename("additional.zip");
		addition.setMd5(this.fileMd5);

		SwordResponse resp = client.addToMediaResource(receipt, addition, new AuthCredentials(this.user, this.pass));

		assertTrue(resp.getStatusCode() >= 200 && resp.getStatusCode() < 400);
		assertTrue(resp.getLocation() != null);
	}

	@Test
	public void advancedAddContentSingleFile()
			throws Exception
	{
		SWORDClient client = new SWORDClient(new ClientConfiguration());
		ServiceDocument sd = client.getServiceDocument(this.sdIRI, new AuthCredentials(this.user, this.pass, this.obo));
		SWORDCollection col = sd.getWorkspaces().get(0).getCollections().get(0);

		Deposit deposit = new Deposit();
		deposit.setFile(new FileInputStream(this.file));
		deposit.setMimeType("application/zip");
		deposit.setFilename("example.zip");
		deposit.setPackaging(UriRegistry.PACKAGE_SIMPLE_ZIP);
		deposit.setMd5(this.fileMd5);
        deposit.setInProgress(true);

		DepositReceipt receipt = client.deposit(col, deposit, new AuthCredentials(this.user, this.pass, this.obo));
		receipt = client.getDepositReceipt(receipt.getLocation(), new AuthCredentials(this.user, this.pass, this.obo));

		Deposit addition = new Deposit();
		addition.setFile(new FileInputStream(this.file));
		addition.setMimeType("application/zip");
		addition.setFilename("additional.zip");
		addition.setMd5(this.fileMd5);
		addition.setMetadataRelevant(true);

		SwordResponse resp = client.addToMediaResource(receipt, addition, new AuthCredentials(this.user, this.pass, this.obo));

		assertTrue(resp.getStatusCode() >= 200 && resp.getStatusCode() < 400);
		assertTrue(resp.getLocation() != null);
	}

	@Test
	public void basicAddContentPackage()
			throws Exception
	{
		SWORDClient client = new SWORDClient(new ClientConfiguration());
		ServiceDocument sd = client.getServiceDocument(this.sdIRI, new AuthCredentials(this.user, this.pass));
		SWORDCollection col = sd.getWorkspaces().get(0).getCollections().get(0);

		Deposit deposit = new Deposit();
		deposit.setFile(new FileInputStream(this.file));
		deposit.setMimeType("application/zip");
		deposit.setFilename("example.zip");
		deposit.setPackaging(UriRegistry.PACKAGE_SIMPLE_ZIP);
		deposit.setMd5(this.fileMd5);
        deposit.setInProgress(true);

		DepositReceipt receipt = client.deposit(col, deposit, new AuthCredentials(this.user, this.pass));
		receipt = client.getDepositReceipt(receipt.getLocation(), new AuthCredentials(this.user, this.pass));

		Deposit addition = new Deposit();
		addition.setFile(new FileInputStream(this.file));
		addition.setMimeType("application/zip");
		addition.setFilename("additional.zip");
		addition.setMd5(this.fileMd5);
		addition.setPackaging(UriRegistry.PACKAGE_SIMPLE_ZIP);

		SwordResponse resp = client.addToMediaResource(receipt, addition, new AuthCredentials(this.user, this.pass));

		assertTrue(resp.getStatusCode() >= 200 && resp.getStatusCode() < 400);
		assertTrue(resp.getLocation() != null);
	}

	@Test
	public void advancedAddContentPackage()
			throws Exception
	{
		SWORDClient client = new SWORDClient(new ClientConfiguration());
		ServiceDocument sd = client.getServiceDocument(this.sdIRI, new AuthCredentials(this.user, this.pass, this.obo));
		SWORDCollection col = sd.getWorkspaces().get(0).getCollections().get(0);

		Deposit deposit = new Deposit();
		deposit.setFile(new FileInputStream(this.file));
		deposit.setMimeType("application/zip");
		deposit.setFilename("example.zip");
		deposit.setPackaging(UriRegistry.PACKAGE_SIMPLE_ZIP);
		deposit.setMd5(this.fileMd5);
        deposit.setInProgress(true);

		DepositReceipt receipt = client.deposit(col, deposit, new AuthCredentials(this.user, this.pass, this.obo));
		receipt = client.getDepositReceipt(receipt.getLocation(), new AuthCredentials(this.user, this.pass, this.obo));

		Deposit addition = new Deposit();
		addition.setFile(new FileInputStream(this.file));
		addition.setMimeType("application/zip");
		addition.setFilename("additional.zip");
		addition.setMd5(this.fileMd5);
		addition.setPackaging(UriRegistry.PACKAGE_SIMPLE_ZIP);
		addition.setMetadataRelevant(true);

		SwordResponse resp = client.addToMediaResource(receipt, addition, new AuthCredentials(this.user, this.pass, this.obo));

		assertTrue(resp.getStatusCode() >= 200 && resp.getStatusCode() < 400);
		assertTrue(resp.getLocation() != null);
	}

	@Test
	public void basicAddMetadata()
			throws Exception
	{
		SWORDClient client = new SWORDClient(new ClientConfiguration());
		ServiceDocument sd = client.getServiceDocument(this.sdIRI, new AuthCredentials(this.user, this.pass));
		SWORDCollection col = sd.getWorkspaces().get(0).getCollections().get(0);

		EntryPart ep = new EntryPart();
		ep.addDublinCore("title", "An entry only deposit");
		ep.addDublinCore("abstract", "abstract");
		ep.addDublinCore("identifier", "http://whatever/");

		Deposit deposit = new Deposit();
		deposit.setEntryPart(ep);
        deposit.setInProgress(true);

		DepositReceipt receipt = client.deposit(col, deposit, new AuthCredentials(this.user, this.pass));
		receipt = client.getDepositReceipt(receipt.getLocation(), new AuthCredentials(this.user, this.pass));

		EntryPart nep = new EntryPart();
		nep.addDublinCore("title", "A metadata update");
		nep.addDublinCore("abstract", "new abstract");
		nep.addDublinCore("identifier", "http://elsewhere/");

		Deposit additional = new Deposit();
		additional.setEntryPart(nep);

		DepositReceipt newReceipt = client.addToContainer(receipt, additional, new AuthCredentials(this.user, this.pass));
		assertTrue(newReceipt.getStatusCode() == 200);
	}

	@Test
	public void advancedAddMetadata()
			throws Exception
	{
		SWORDClient client = new SWORDClient(new ClientConfiguration());
		ServiceDocument sd = client.getServiceDocument(this.sdIRI, new AuthCredentials(this.user, this.pass, this.obo));
		SWORDCollection col = sd.getWorkspaces().get(0).getCollections().get(0);

		EntryPart ep = new EntryPart();
		ep.addDublinCore("title", "An entry only deposit");
		ep.addDublinCore("abstract", "abstract");
		ep.addDublinCore("identifier", "http://whatever/");

		Deposit deposit = new Deposit();
		deposit.setEntryPart(ep);
        deposit.setInProgress(true);

		DepositReceipt receipt = client.deposit(col, deposit, new AuthCredentials(this.user, this.pass, this.obo));
		receipt = client.getDepositReceipt(receipt.getLocation(), new AuthCredentials(this.user, this.pass, this.obo));

		EntryPart nep = new EntryPart();
		nep.addDublinCore("title", "A metadata update");
		nep.addDublinCore("abstract", "new abstract");
		nep.addDublinCore("identifier", "http://elsewhere/");

		Deposit additional = new Deposit();
		additional.setEntryPart(nep);
		additional.setInProgress(true);

		DepositReceipt newReceipt = client.addToContainer(receipt, additional, new AuthCredentials(this.user, this.pass, this.obo));
		assertTrue(newReceipt.getStatusCode() == 200);
	}

	/*
	@Test
	public void basicAddMultipart()
			throws Exception
	{
		SWORDClient client = new SWORDClient(new ClientConfiguration());
		ServiceDocument sd = client.getServiceDocument(this.sdIRI, new AuthCredentials(this.user, this.pass));
		SWORDCollection col = sd.getWorkspaces().get(0).getCollections().get(0);

		EntryPart ep = new EntryPart();
		ep.addDublinCore("title", "My Title");

		Deposit deposit = new Deposit();
		deposit.setEntryPart(ep);
		deposit.setFile(new FileInputStream(this.file));
		deposit.setMimeType("application/zip");
		deposit.setFilename("example.zip");
		deposit.setPackaging(UriRegistry.PACKAGE_SIMPLE_ZIP);
		deposit.setMd5(this.fileMd5);

		DepositReceipt receipt = client.deposit(col, deposit, new AuthCredentials(this.user, this.pass));
		receipt = client.getDepositReceipt(receipt.getLocation(), new AuthCredentials(this.user, this.pass));

		EntryPart nep = new EntryPart();
		nep.addDublinCore("title", "A metadata update");
		nep.addDublinCore("abstract", "new abstract");
		nep.addDublinCore("identifier", "http://elsewhere/");

		Deposit replacement = new Deposit();
		replacement.setEntryPart(nep);
		replacement.setFile(new FileInputStream(this.file));
		replacement.setMimeType("application/zip");
		replacement.setFilename("updated.zip");
		replacement.setPackaging(UriRegistry.PACKAGE_SIMPLE_ZIP);
		replacement.setMd5(this.fileMd5);

		SwordResponse resp = client.addToContainer(receipt, replacement, new AuthCredentials(this.user, this.pass));

		assertTrue(resp.getStatusCode() >= 200 && resp.getStatusCode() < 400);
	}

	@Test
	public void advancedAddMultipart()
			throws Exception
	{
		SWORDClient client = new SWORDClient(new ClientConfiguration());
		ServiceDocument sd = client.getServiceDocument(this.sdIRI, new AuthCredentials(this.user, this.pass, this.obo));
		SWORDCollection col = sd.getWorkspaces().get(0).getCollections().get(0);

		EntryPart ep = new EntryPart();
		ep.addDublinCore("title", "My Title");

		Deposit deposit = new Deposit();
		deposit.setEntryPart(ep);
		deposit.setFile(new FileInputStream(this.file));
		deposit.setMimeType("application/zip");
		deposit.setFilename("example.zip");
		deposit.setPackaging(UriRegistry.PACKAGE_SIMPLE_ZIP);
		deposit.setMd5(this.fileMd5);

		DepositReceipt receipt = client.deposit(col, deposit, new AuthCredentials(this.user, this.pass, this.obo));
		receipt = client.getDepositReceipt(receipt.getLocation(), new AuthCredentials(this.user, this.pass, this.obo));

		EntryPart nep = new EntryPart();
		nep.addDublinCore("title", "A metadata update");
		nep.addDublinCore("abstract", "new abstract");
		nep.addDublinCore("identifier", "http://elsewhere/");

		Deposit replacement = new Deposit();
		replacement.setEntryPart(nep);
		replacement.setFile(new FileInputStream(this.file));
		replacement.setMimeType("application/zip");
		replacement.setFilename("updated.zip");
		replacement.setPackaging(UriRegistry.PACKAGE_SIMPLE_ZIP);
		replacement.setMd5(this.fileMd5);
		replacement.setInProgress(true);
		replacement.setMetadataRelevant(true);

		SwordResponse resp = client.addToContainer(receipt, replacement, new AuthCredentials(this.user, this.pass, this.obo));

		assertTrue(resp.getStatusCode() >= 200 && resp.getStatusCode() < 400);
	}
	*/

	@Test
	public void deleteContainer()
			throws Exception
	{
		SWORDClient client = new SWORDClient(new ClientConfiguration());
		ServiceDocument sd = client.getServiceDocument(this.sdIRI, new AuthCredentials(this.user, this.pass, this.obo));
		SWORDCollection col = sd.getWorkspaces().get(0).getCollections().get(0);

		Deposit deposit = new Deposit();
		deposit.setFile(new FileInputStream(this.file));
		deposit.setMimeType("application/zip");
		deposit.setFilename("example.zip");
		deposit.setPackaging(UriRegistry.PACKAGE_SIMPLE_ZIP);
		deposit.setMd5(this.fileMd5);
        deposit.setInProgress(true);

		DepositReceipt receipt = client.deposit(col, deposit, new AuthCredentials(this.user, this.pass, this.obo));
		receipt = client.getDepositReceipt(receipt.getLocation(), new AuthCredentials(this.user, this.pass, this.obo));

		SwordResponse resp = client.deleteContainer(receipt, new AuthCredentials(this.user, this.pass, this.obo));
		assertEquals(resp.getStatusCode(), 204);

		// now show that the thing 404s if we try to get it
		boolean was404 = false;
		try
		{
			DepositReceipt newReceipt = client.getDepositReceipt(receipt.getLocation(), new AuthCredentials(this.user, this.pass, this.obo));
		}
		catch (SWORDError e)
		{
			assertEquals(e.getStatus(), 404);
			was404 = true;
		}
		assertTrue(was404);
	}

	@Test
	public void getAtomStatement()
			throws Exception
	{
		SWORDClient client = new SWORDClient(new ClientConfiguration());
		ServiceDocument sd = client.getServiceDocument(this.sdIRI, new AuthCredentials(this.user, this.pass, this.obo));
		SWORDCollection col = sd.getWorkspaces().get(0).getCollections().get(0);

		Deposit deposit = new Deposit();
		deposit.setFile(new FileInputStream(this.file));
		deposit.setMimeType("application/zip");
		deposit.setFilename("example.zip");
		deposit.setPackaging(UriRegistry.PACKAGE_SIMPLE_ZIP);
		deposit.setMd5(this.fileMd5);

		DepositReceipt receipt = client.deposit(col, deposit, new AuthCredentials(this.user, this.pass, this.obo));
		receipt = client.getDepositReceipt(receipt.getLocation(), new AuthCredentials(this.user, this.pass, this.obo));

		assertTrue(receipt.getAtomStatementLink() != null);

		Statement statement = client.getStatement(receipt, "application/atom+xml;type=feed", new AuthCredentials(this.user, this.pass, this.obo));

		assertTrue(statement instanceof AtomStatement);
	}

	@Test
	public void getOreStatement()
			throws Exception
	{
		SWORDClient client = new SWORDClient(new ClientConfiguration());
		ServiceDocument sd = client.getServiceDocument(this.sdIRI, new AuthCredentials(this.user, this.pass, this.obo));
		SWORDCollection col = sd.getWorkspaces().get(0).getCollections().get(0);

		Deposit deposit = new Deposit();
		deposit.setFile(new FileInputStream(this.file));
		deposit.setMimeType("application/zip");
		deposit.setFilename("example.zip");
		deposit.setPackaging(UriRegistry.PACKAGE_SIMPLE_ZIP);
		deposit.setMd5(this.fileMd5);

		DepositReceipt receipt = client.deposit(col, deposit, new AuthCredentials(this.user, this.pass, this.obo));
		receipt = client.getDepositReceipt(receipt.getLocation(), new AuthCredentials(this.user, this.pass, this.obo));

		assertTrue(receipt.getOREStatementLink() != null);

		Statement statement = client.getStatement(receipt, "application/rdf+xml", new AuthCredentials(this.user, this.pass, this.obo));

		assertTrue(statement instanceof OreStatement);
	}

	@Test
	public void completeDeposit()
			throws Exception
	{
		SWORDClient client = new SWORDClient(new ClientConfiguration());
		ServiceDocument sd = client.getServiceDocument(this.sdIRI, new AuthCredentials(this.user, this.pass, this.obo));
		SWORDCollection col = sd.getWorkspaces().get(0).getCollections().get(0);

		Deposit deposit = new Deposit();
		deposit.setFile(new FileInputStream(this.file));
		deposit.setMimeType("application/zip");
		deposit.setFilename("example.zip");
		deposit.setPackaging(UriRegistry.PACKAGE_SIMPLE_ZIP);
		deposit.setMd5(this.fileMd5);
		deposit.setInProgress(true);

		DepositReceipt receipt = client.deposit(col, deposit, new AuthCredentials(this.user, this.pass, this.obo));
		receipt = client.getDepositReceipt(receipt.getLocation(), new AuthCredentials(this.user, this.pass, this.obo));

		DepositReceipt newReceipt = client.complete(receipt, new AuthCredentials(this.user, this.pass, this.obo));

		assertEquals(newReceipt.getStatusCode(), 200);
	}

	@Test
	public void errorChecksumMismatch()
			throws Exception
	{
		SWORDClient client = new SWORDClient(new ClientConfiguration());
		ServiceDocument sd = client.getServiceDocument(this.sdIRI, new AuthCredentials(this.user, this.pass, this.obo));
		SWORDCollection col = sd.getWorkspaces().get(0).getCollections().get(0);

		Deposit deposit = new Deposit();
		deposit.setFile(new FileInputStream(this.file));
		deposit.setMimeType("application/zip");
		deposit.setFilename("example.zip");
		deposit.setPackaging(UriRegistry.PACKAGE_SIMPLE_ZIP);
		deposit.setMd5("123456789"); // this is not correct
		deposit.setInProgress(true);

		try
		{
			DepositReceipt receipt = client.deposit(col, deposit, new AuthCredentials(this.user, this.pass, this.obo));
			assertTrue(false);
		}
		catch (SWORDError e)
		{
			assertEquals(e.getStatus(), 412);
			assertEquals(e.getErrorURI(), "http://purl.org/net/sword/error/ErrorChecksumMismatch");
		}
	}

	@Test
	public void errorBadRequest()
			throws Exception
	{
		// FIXME: it is highly non-trivial to make a bad request with the client library
		// How can we do this?

		/*
		SWORDClient client = new SWORDClient(new ClientConfiguration());
		ServiceDocument sd = client.getServiceDocument(this.sdIRI, new AuthCredentials(this.user, this.pass, this.obo));
		SWORDCollection col = sd.getWorkspaces().get(0).getCollections().get(0);

		Deposit deposit = new Deposit();
		deposit.setFile(new FileInputStream(this.file));
		deposit.setMimeType("application/zip");
		deposit.setFilename("example.zip");
		deposit.setPackaging(UriRegistry.PACKAGE_SIMPLE_ZIP);
		deposit.setMd5(this.fileMd5);
		deposit.setInProgress(true);

		try
		{
			DepositReceipt receipt = client.deposit(col, deposit, new AuthCredentials(this.user, this.pass, this.obo));
			assertTrue(false);
		}
		catch (SWORDError e)
		{
			assertEquals(e.getStatus(), 400);
			assertEquals(e.getErrorURI(), "http://purl.org/net/sword/error/ErrorBadRequest");
		}
		*/
	}

	@Test
	public void errorTargetOwnerUnknown()
			throws Exception
	{
		SWORDClient client = new SWORDClient(new ClientConfiguration());
		ServiceDocument sd = client.getServiceDocument(this.sdIRI, new AuthCredentials(this.user, this.pass, this.obo));
		SWORDCollection col = sd.getWorkspaces().get(0).getCollections().get(0);

		Deposit deposit = new Deposit();
		deposit.setFile(new FileInputStream(this.file));
		deposit.setMimeType("application/zip");
		deposit.setFilename("example.zip");
		deposit.setPackaging(UriRegistry.PACKAGE_SIMPLE_ZIP);
		deposit.setMd5(this.fileMd5);
		deposit.setInProgress(true);

		try
		{
			// using an invalid OBO user
			DepositReceipt receipt = client.deposit(col, deposit, new AuthCredentials(this.user, this.pass, "richard"));
			assertTrue(false);
		}
		catch (SWORDError e)
		{
			assertEquals(e.getStatus(), 403);
			assertEquals(e.getErrorURI(), "http://purl.org/net/sword/error/TargetOwnerUnknown");
		}
	}

	@Test
	public void errorMediatonNotAllowed()
			throws Exception
	{
		// FIXME: not possible to reliably test for this

		/*
		SWORDClient client = new SWORDClient(new ClientConfiguration());
		ServiceDocument sd = client.getServiceDocument(this.sdIRI, new AuthCredentials(this.user, this.pass, this.obo));
		SWORDCollection col = sd.getWorkspaces().get(0).getCollections().get(0);

		Deposit deposit = new Deposit();
		deposit.setFile(new FileInputStream(this.file));
		deposit.setMimeType("application/zip");
		deposit.setFilename("example.zip");
		deposit.setPackaging(UriRegistry.PACKAGE_SIMPLE_ZIP);
		deposit.setMd5(this.fileMd5);
		deposit.setInProgress(true);

		try
		{
			// using an invalid OBO user
			DepositReceipt receipt = client.deposit(col, deposit, new AuthCredentials(this.user, this.pass, "richard"));
			assertTrue(false);
		}
		catch (SWORDError e)
		{
			assertEquals(e.getStatus(), 403);
			assertEquals(e.getErrorURI(), "http://purl.org/net/sword/error/TargetOwnerUnknown");
		}
		*/
	}

	@Test
	public void errorMethodNotAllowed()
			throws Exception
	{
		// FIXME: not possible to reliably test for this

		/*
		SWORDClient client = new SWORDClient(new ClientConfiguration());
		ServiceDocument sd = client.getServiceDocument(this.sdIRI, new AuthCredentials(this.user, this.pass, this.obo));
		SWORDCollection col = sd.getWorkspaces().get(0).getCollections().get(0);

		Deposit deposit = new Deposit();
		deposit.setFile(new FileInputStream(this.file));
		deposit.setMimeType("application/zip");
		deposit.setFilename("example.zip");
		deposit.setPackaging(UriRegistry.PACKAGE_SIMPLE_ZIP);
		deposit.setMd5(this.fileMd5);
		deposit.setInProgress(true);

		try
		{
			// using an invalid OBO user
			DepositReceipt receipt = client.deposit(col, deposit, new AuthCredentials(this.user, this.pass, "richard"));
			assertTrue(false);
		}
		catch (SWORDError e)
		{
			assertEquals(e.getStatus(), 403);
			assertEquals(e.getErrorURI(), "http://purl.org/net/sword/error/TargetOwnerUnknown");
		}
		*/
	}

	@Test
	public void errorMaxUploadSizeExceeded()
			throws Exception
	{
		// FIXME: not possible to reliably test for this

		/*
		SWORDClient client = new SWORDClient(new ClientConfiguration());
		ServiceDocument sd = client.getServiceDocument(this.sdIRI, new AuthCredentials(this.user, this.pass, this.obo));
		SWORDCollection col = sd.getWorkspaces().get(0).getCollections().get(0);

		Deposit deposit = new Deposit();
		deposit.setFile(new FileInputStream(this.file));
		deposit.setMimeType("application/zip");
		deposit.setFilename("example.zip");
		deposit.setPackaging(UriRegistry.PACKAGE_SIMPLE_ZIP);
		deposit.setMd5(this.fileMd5);
		deposit.setInProgress(true);

		try
		{
			// using an invalid OBO user
			DepositReceipt receipt = client.deposit(col, deposit, new AuthCredentials(this.user, this.pass, "richard"));
			assertTrue(false);
		}
		catch (SWORDError e)
		{
			assertEquals(e.getStatus(), 403);
			assertEquals(e.getErrorURI(), "http://purl.org/net/sword/error/TargetOwnerUnknown");
		}
		*/
	}

	@Test
	public void getServiceDocumentUnauthorised()
			throws Exception
	{
		SWORDClient client = new SWORDClient(new ClientConfiguration());
		ServiceDocument sd = client.getServiceDocument(this.sdIRI, new AuthCredentials("adfasf", "awerwevwe"));
		assertNull(sd);
	}
}
