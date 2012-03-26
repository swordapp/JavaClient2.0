package org.swordapp.client.test;

import org.apache.abdera.model.Element;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Before;
import org.junit.Test;
import org.swordapp.client.AtomStatement;
import org.swordapp.client.AuthCredentials;
import org.swordapp.client.ClientConfiguration;
import org.swordapp.client.Deposit;
import org.swordapp.client.DepositReceipt;
import org.swordapp.client.Endpoints;
import org.swordapp.client.EntryPart;
import org.swordapp.client.OreStatement;
import org.swordapp.client.ResourceState;
import org.swordapp.client.SWORDClient;
import org.swordapp.client.SWORDCollection;
import org.swordapp.client.SWORDWorkspace;
import org.swordapp.client.ServerResource;
import org.swordapp.client.ServiceDocument;
import org.swordapp.client.Statement;
import org.swordapp.client.SwordResponse;
import org.swordapp.client.UriRegistry;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 * This class uses explicit knowledge of the SSS test server configuration
 * in order to test units of functionality in the client library.  This is
 * because the Abdera library around which this is based does not support
 * mocking (at least not trivially), and it is therefore impossible to
 * truly isolate application units for testing.  Instead we do the best
 * we can here
 */
public class SSSSemiUnits
{
	private String homePage = null;
	private String depositPage = null;
	private String resourcePage = null;
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
		this.homePage = "http://localhost:8080/";
		this.depositPage = "http://localhost:8080/html/ded3c328-d8f0-4307-8b86-086cf46a953c";
		this.resourcePage = "http://localhost:8080/html/ded3c328-d8f0-4307-8b86-086cf46a953c/f2966e10-c6a0-40ab-90ad-6fc90d5b8bd3";
		this.sdIRI = "http://localhost:8080/sd-uri";
		this.user = "sword";
		this.pass = "sword";
		this.obo = "obo";
		this.file = "/home/richard/Code/External/JavaClient2.0/src/test/resources/example.zip";
		this.fileMd5 = DigestUtils.md5Hex(new FileInputStream(this.file));
	}

	@Test
	public void serviceDocumentBasics()
			throws Exception
	{
		SWORDClient client = new SWORDClient(new ClientConfiguration());
		ServiceDocument sd = client.getServiceDocument(this.sdIRI, new AuthCredentials(this.user, this.pass));

		// verify that the service document contains the sorts of things we are expecting
		assertTrue(sd.getService() != null);
		assertEquals(sd.getVersion(), "2.0");
		assertTrue(sd.getMaxUploadSize() > 0);
	}

	@Test
	public void serviceDocumentWorkspaces()
			throws Exception
	{
		SWORDClient client = new SWORDClient(new ClientConfiguration());
		ServiceDocument sd = client.getServiceDocument(this.sdIRI, new AuthCredentials(this.user, this.pass));

		assertEquals(sd.getWorkspaces().size(), 1);
	}

	@Test
	public void serviceDocumentCollections()
			throws Exception
	{
		SWORDClient client = new SWORDClient(new ClientConfiguration());
		ServiceDocument sd = client.getServiceDocument(this.sdIRI, new AuthCredentials(this.user, this.pass));

		List<SWORDWorkspace> workspaces = sd.getWorkspaces();

		assertEquals(workspaces.get(0).getCollections().size(), 10);

		SWORDCollection collection = workspaces.get(0).getCollections().get(0);

		List<String> ma = collection.getMultipartAccept();
		List<String> sa = collection.getSinglepartAccept();

		assertEquals(ma.size(), 1);
		assertEquals(sa.size(), 1);
		assertEquals(ma.get(0), sa.get(0));
		assertTrue("*/*".equals(ma.get(0)));

		assertTrue(collection.getCollectionPolicy() != null);
		assertTrue(collection.allowsMediation());
		assertTrue("Treatment description".equals(collection.getTreatment()));

		List<String> ap = collection.getAcceptPackaging();
		assertTrue(ap.size() == 3);
		List<String> expectedPackaging = new ArrayList<String>();
		expectedPackaging.add("http://purl.org/net/sword/package/SimpleZip");
		expectedPackaging.add("http://purl.org/net/sword/package/Binary");
		expectedPackaging.add("http://purl.org/net/sword/package/METSDSpaceSIP");
		for (String packaging : ap)
		{
			assertTrue(expectedPackaging.contains(packaging));
		}

		List<String> ss = collection.getSubServices();
		assertTrue(ss.size() == 1);
	}

	@Test
	public void depositReceipt()
			throws Exception
	{
		SWORDClient client = new SWORDClient(new ClientConfiguration());
		ServiceDocument sd = client.getServiceDocument(this.sdIRI, new AuthCredentials(this.user, this.pass));
		SWORDCollection col = sd.getWorkspaces().get(0).getCollections().get(0);

		Deposit media = new Deposit();
		media.setFile(new FileInputStream(this.file));
		media.setMimeType("application/zip");
		media.setFilename("example.zip");
		media.setPackaging(UriRegistry.PACKAGE_SIMPLE_ZIP);
		media.setMd5(this.fileMd5);

		DepositReceipt receipt = client.deposit(col, media, new AuthCredentials(this.user, this.pass, this.obo));

		assertEquals(receipt.getStatusCode(), 201);
		assertTrue(receipt.getLocation() != null);
		assertEquals(receipt.getDerivedResourceLinks().size(), 4);
		assertTrue(receipt.getOriginalDepositLink() != null);

		assertTrue(receipt.getEditMediaLink() != null);
		assertTrue(receipt.getAtomStatementLink() != null);
		assertEquals(receipt.getContentLink().getType(), "application/zip");
		assertTrue(receipt.getEditLink() != null);
		assertTrue(receipt.getOREStatementLink() != null);
		assertEquals(receipt.getPackaging().size(), 1);
		assertTrue(receipt.getPackaging().contains(UriRegistry.PACKAGE_SIMPLE_ZIP));
		assertTrue(receipt.getSplashPageLink() != null);
		assertTrue(receipt.getStatementLink("application/rdf+xml") != null);
		assertTrue(receipt.getStatementLink("application/atom+xml;type=feed") != null);
		assertTrue(receipt.getSwordEditLink() != null);
		assertEquals(receipt.getTreatment(), "Treatment description");
		assertEquals(receipt.getVerboseDescription(), "SSS has done this, that and the other to process the deposit");

		EntryPart ep = new EntryPart();
		ep.addDublinCore("title", "An entry only deposit");
		ep.addDublinCore("abstract", "abstract");
		ep.addDublinCore("identifier", "http://whatever/");

		Deposit metadata = new Deposit();
		metadata.setEntryPart(ep);

		SwordResponse response = client.replace(receipt, metadata, new AuthCredentials(this.user, this.pass, this.obo));
		assertEquals(response.getStatusCode(), 200);

		receipt = client.getDepositReceipt(receipt.getLocation(), new AuthCredentials(this.user, this.pass, this.obo));

		assertTrue(receipt.getEditMediaLink() != null);
		assertTrue(receipt.getAtomStatementLink() != null);
		assertEquals(receipt.getContentLink().getType(), "application/zip");
		assertTrue(receipt.getEditLink() != null);
		assertTrue(receipt.getOREStatementLink() != null);
		assertEquals(receipt.getPackaging().size(), 1);
		assertTrue(receipt.getPackaging().contains(UriRegistry.PACKAGE_SIMPLE_ZIP));
		assertTrue(receipt.getSplashPageLink() != null);
		assertTrue(receipt.getStatementLink("application/rdf+xml") != null);
		assertTrue(receipt.getStatementLink("application/atom+xml;type=feed") != null);
		assertTrue(receipt.getSwordEditLink() != null);
		assertTrue(receipt.getTreatment() != null);
		assertTrue(receipt.getVerboseDescription() != null);

		List<Element> dcs = receipt.getDublinCore();
		int count = 0;
		for (Element dc : dcs)
		{
			if (dc.getQName().getLocalPart().equals("title"))
			{
				assertEquals(dc.getText(), "An entry only deposit");
				count++;
			}
			if (dc.getQName().getLocalPart().equals("abstract"))
			{
				assertEquals(dc.getText(), "abstract");
				count++;
			}
			if (dc.getQName().getLocalPart().equals("identifier"))
			{
				assertEquals(dc.getText(), "http://whatever/");
				count++;
			}
		}
		assertEquals(count, 3);
	}

	@Test
	public void atomStatement()
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
		assertNotNull(((AtomStatement) statement).getFeed());
		assertEquals(statement.getMimeType(), "application/atom+xml;type=feed");
		assertEquals(statement.getOriginalDeposits().size(), 1);
		assertEquals(statement.getParts().size(), 5);
		assertEquals(statement.getState().size(), 1);

		List<ServerResource> ods = statement.getOriginalDeposits();
		boolean checked = false;
		for (ServerResource od : ods)
		{
			assertTrue(od.getDepositedOn() != null);
			assertEquals(od.getDepositedBy(), this.user);
			assertEquals(od.getDepositedOnBehalfOf(), this.obo);
			assertEquals(od.getPackaging().size(), 1);
			assertTrue(od.getPackaging().contains(UriRegistry.PACKAGE_SIMPLE_ZIP));
			checked = true;
		}
		assertTrue(checked);

		checked = false;
		List<ResourceState> states = statement.getState();
		for (ResourceState state : states)
		{
			assertNotNull(state.getDescription());
			checked = true;
		}
		assertTrue(checked);
	}

	@Test
	public void oreStatement()
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
		assertNotNull(((OreStatement) statement).getResourceMap());
		assertEquals(statement.getMimeType(), "application/rdf+xml");
		assertEquals(statement.getOriginalDeposits().size(), 1);
		assertEquals(statement.getParts().size(), 5);
		assertEquals(statement.getState().size(), 1);

		List<ServerResource> ods = statement.getOriginalDeposits();
		boolean checked = false;
		for (ServerResource od : ods)
		{
			assertTrue(od.getDepositedOn() != null);
			assertEquals(od.getDepositedBy(), this.user);
			assertEquals(od.getDepositedOnBehalfOf(), this.obo);
			assertEquals(od.getPackaging().size(), 1);
			assertTrue(od.getPackaging().contains(UriRegistry.PACKAGE_SIMPLE_ZIP));
			checked = true;
		}
		assertTrue(checked);

		checked = false;
		List<ResourceState> states = statement.getState();
		for (ResourceState state : states)
		{
			assertNotNull(state.getDescription());
			checked = true;
		}
		assertTrue(checked);
	}

	@Test
	public void autoDiscoverService()
			throws Exception
	{
		SWORDClient client = new SWORDClient(new ClientConfiguration());
		Endpoints endpoints = client.autoDiscover(this.homePage);

		assertEquals(endpoints.getServiceDocument(), this.sdIRI);
		assertNull(endpoints.getCollection());
		assertNull(endpoints.getEdit());
		assertNull(endpoints.getStatements());

		endpoints = client.autoDiscover(this.depositPage);

		assertNull(endpoints.getServiceDocument());
		assertEquals(endpoints.getCollection(), "http://localhost:8080/col-uri/ded3c328-d8f0-4307-8b86-086cf46a953c");
		assertNull(endpoints.getEdit());
		assertNull(endpoints.getStatements());

		endpoints = client.autoDiscover(this.resourcePage);

		assertNull(endpoints.getServiceDocument());
		assertNull(endpoints.getCollection());
		assertEquals(endpoints.getEdit(), "http://localhost:8080/edit-uri/ded3c328-d8f0-4307-8b86-086cf46a953c/f2966e10-c6a0-40ab-90ad-6fc90d5b8bd3");

		Map<String, String> statements = endpoints.getStatements();
		int count = 0;
		for (String url : statements.keySet())
		{
			String type = statements.get(url);

			if (type.equals("application/atom+xml") || type.equals("application/atom+xml;type=feed"))
			{
				assertEquals(url, "http://localhost:8080/state-uri/ded3c328-d8f0-4307-8b86-086cf46a953c/f2966e10-c6a0-40ab-90ad-6fc90d5b8bd3.atom");
				count++;
			}
			else if (type.equals("application/rdf+xml"))
			{
				assertEquals(url, "http://localhost:8080/state-uri/ded3c328-d8f0-4307-8b86-086cf46a953c/f2966e10-c6a0-40ab-90ad-6fc90d5b8bd3.rdf");
				count++;
			}
		}
		assertEquals(count, 2);
	}
}
