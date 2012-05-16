package org.swordapp.client.test;


import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Before;
import org.junit.Test;
import org.swordapp.client.AtomStatement;
import org.swordapp.client.AuthCredentials;
import org.swordapp.client.ClientConfiguration;
import org.swordapp.client.Deposit;
import org.swordapp.client.DepositReceipt;
import org.swordapp.client.ProtocolViolationException;
import org.swordapp.client.SWORDClient;
import org.swordapp.client.SWORDClientException;
import org.swordapp.client.SWORDCollection;
import org.swordapp.client.SWORDError;
import org.swordapp.client.ServiceDocument;
import org.swordapp.client.Statement;
import org.swordapp.client.SwordResponse;
import org.swordapp.client.UriRegistry;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

public class ScratchPad
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
        this.file = "/home/richard/Code/External/BagItLibrary/src/test/resources/testbags/fullbag.zip";
        this.fileMd5 = DigestUtils.md5Hex(new FileInputStream(this.file));
    }

    @Test
    public void scratch()
            throws Exception
    {
        try
        {
            SWORDClient client = new SWORDClient(new ClientConfiguration());
            ServiceDocument sd = client.getServiceDocument(this.sdIRI, new AuthCredentials(this.user, this.pass));
            SWORDCollection col = sd.getWorkspaces().get(0).getCollections().get(0);

            Deposit deposit = new Deposit();
            deposit.setFile(new FileInputStream(this.file));
            deposit.setMimeType("application/zip");
            deposit.setFilename("bag.zip");
            deposit.setPackaging("http://duo.uio.no/terms/package/FSBagIt");
            deposit.setMd5(this.fileMd5);
            // deposit.setInProgress(true);

            DepositReceipt receipt = client.deposit(col, deposit, new AuthCredentials(this.user, this.pass));
            System.out.println(receipt.getLocation());
        }
        catch (SWORDClientException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        catch (ProtocolViolationException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        catch (SWORDError swordError)
        {
            swordError.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        /*
        receipt = client.getDepositReceipt(receipt.getLocation(), new AuthCredentials("another", "dspace"));
        receipt = client.getDepositReceipt(receipt.getLocation(), new AuthCredentials(this.user, this.pass, "another"));

        Statement statement = client.getStatement(receipt.getAtomStatementLink().getHref(), new AtomStatement(), new AuthCredentials("another", "dspace"));
        statement = client.getStatement(receipt.getAtomStatementLink().getHref(), new AtomStatement(), new AuthCredentials(this.user, this.pass, "another"));



        Deposit replacement = new Deposit();
        replacement.setFile(new FileInputStream(this.file));
        replacement.setMimeType("application/zip");
        replacement.setFilename("updated.zip");
        replacement.setPackaging(UriRegistry.PACKAGE_SIMPLE_ZIP);
        replacement.setMd5(this.fileMd5);

        SwordResponse resp = client.replaceMedia(receipt, replacement, new AuthCredentials("another", "dspace"));
        */
    }
}

