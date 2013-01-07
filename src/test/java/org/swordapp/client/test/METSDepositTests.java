package org.swordapp.client.test;


import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Before;
import org.junit.Test;
import org.swordapp.client.AuthCredentials;
import org.swordapp.client.ClientConfiguration;
import org.swordapp.client.Deposit;
import org.swordapp.client.DepositReceipt;
import org.swordapp.client.SWORDClient;
import org.swordapp.client.SWORDCollection;
import org.swordapp.client.ServiceDocument;
import org.swordapp.client.SwordResponse;
import org.swordapp.client.UriRegistry;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class METSDepositTests
{
    private String sdIRI = null;
	private String user = null;
	private String pass = null;
	private String obo = null;
	private String file = null;
	private String fileMd5 = null;
    private String file2 = null;

    private String METS = "http://purl.org/net/sword/package/METSDSpaceSIP";

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
		this.fileMd5 = DigestUtils.md5Hex(new FileInputStream(this.file));
        this.file2 = properties.getProperty("file2");
	}

    @Test
	public void depositNew()
			throws Exception
	{
		SWORDClient client = new SWORDClient(new ClientConfiguration());
		ServiceDocument sd = client.getServiceDocument(this.sdIRI, new AuthCredentials(this.user, this.pass));
		SWORDCollection col = sd.getWorkspaces().get(0).getCollections().get(0);

		Deposit deposit = new Deposit();
		deposit.setFile(new FileInputStream(this.file));
		deposit.setMimeType("application/zip");
		deposit.setFilename("mets.zip");
		deposit.setPackaging(this.METS);
		deposit.setMd5(this.fileMd5);

		DepositReceipt receipt = client.deposit(col, deposit, new AuthCredentials(this.user, this.pass));
		assertEquals(receipt.getStatusCode(), 201);
		assertTrue(receipt.getLocation() != null);
	}

    @Test
	public void update()
			throws Exception
	{
		SWORDClient client = new SWORDClient(new ClientConfiguration());
		ServiceDocument sd = client.getServiceDocument(this.sdIRI, new AuthCredentials(this.user, this.pass));
		SWORDCollection col = sd.getWorkspaces().get(0).getCollections().get(0);

		Deposit deposit = new Deposit();
		deposit.setFile(new FileInputStream(this.file));
		deposit.setMimeType("application/zip");
		deposit.setFilename("mets.zip");
		deposit.setPackaging(this.METS);
		deposit.setMd5(this.fileMd5);
        deposit.setInProgress(true);

		DepositReceipt receipt = client.deposit(col, deposit, new AuthCredentials(this.user, this.pass));
		assertEquals(receipt.getStatusCode(), 201);
		assertTrue(receipt.getLocation() != null);

        Deposit update = new Deposit();
        update.setFile(new FileInputStream(this.file2));
		update.setMimeType("application/zip");
		update.setFilename("mets2.zip");
		update.setPackaging(this.METS);
        update.setMetadataRelevant(true);

        SwordResponse resp = client.replaceMedia(receipt, update, new AuthCredentials(this.user, this.pass));

        assertEquals(resp.getStatusCode(), 204);
	}
}
