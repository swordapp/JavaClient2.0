package org.swordapp.client;

import org.apache.abdera.Abdera;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.*;
import org.apache.abdera.protocol.Response;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;

import javax.activation.MimeType;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

public class SwordCli
{
	//private String exampleZip = "/home/richard/Code/External/SSS/example.zip";
    private String exampleZip = "/Users/richard/Code/External/SSS/example.zip";
    private String image = "/Users/richard/Code/Internal/DepositMO/Dome_sm.jpg";
    private String docx = "/Users/richard/Code/Internal/DepositMO/hy.docx";
    private String bagit = "/Users/richard/Dropbox/Documents/DUO/BagIt.zip";
    //private String sdIRI = "http://localhost:8080/sd-uri";
    private String sdIRI = "http://localhost:8080/swordv2/servicedocument";
	private String user = "richard";
	private String pass = "dspace";
	private String obo = null;
    private String mediaUri = "http://localhost:8080/swordv2/edit-media/34";

    public static void main(String[] args)
            throws Exception
    {
        SwordCli cli = new SwordCli();
//        cli.trySwordServiceDocument();
//        cli.tryCollectionEntries();
//        cli.tryBinaryDeposit();
//       cli.tryEntryDeposit();
//        cli.tryMultipartDeposit();
//        cli.tryContentRetrieve();
//        cli.tryFeedRetrieve();
//        cli.tryReplaceFileContent();
//        cli.tryReplaceMetadata();
//        cli.tryReplaceContentAndMetadata();
//        cli.tryDeleteContent();
//        cli.tryAddContent();
//        cli.tryAddPackage();
//        cli.tryAddMetadata();
//        cli.tryAddMetadataAndPackage();
//        cli.tryDelete();
//        cli.tryStatement();
//  	  cli.tryContinuedDeposit();
//        cli.tryImage();
//        cli.tryDocument();
//        cli.tryFileReplace();
//        cli.tryAddToMediaResource();
       cli.tryFSDeposit();
    }

    private void tryFSDeposit()
            throws Exception
    {
        System.out.println("tryFSDeposit");
        SWORDClient client = new SWORDClient();
        InputStream is = new FileInputStream(new File(bagit));
		DepositFactory factory = new DepositFactory();
        Deposit deposit = factory.newBinaryOnly(is, "bagit.zip", "application/zip", "http://duo.uio.no/terms/package/FSBagIt", null, null, true);
        AuthCredentials auth = new AuthCredentials(this.user, this.pass, this.obo);
        ServiceDocument sd = client.getServiceDocument(this.sdIRI, auth);
        DepositReceipt receipt = null;
        List<SWORDWorkspace> ws = sd.getWorkspaces();
        for (SWORDWorkspace w : ws)
        {
            List<SWORDCollection> collections = w.getCollections();
            for (SWORDCollection c : collections)
            {
                // do the deposit to the first collection we find
                receipt = client.deposit(c, deposit, auth);
                break;
            }
        }
        System.out.println(receipt.getEditLink().getHref().toString());
        receipt.getEntry().writeTo(System.out);
    }

	private void tryContinuedDeposit()
			throws Exception
	{
        System.out.println("tryContinuedDeposit");

		// first we need to put some content in
        SWORDClient client = new SWORDClient();
        InputStream is = new FileInputStream(new File(exampleZip));
        DepositFactory factory = new DepositFactory();
        Deposit deposit = factory.newBinaryOnly(is, "example.zip", "application/zip", UriRegistry.PACKAGE_SIMPLE_ZIP, null, null, true);
        AuthCredentials auth = new AuthCredentials(this.user, this.pass, this.obo);
        ServiceDocument sd = client.getServiceDocument(this.sdIRI, auth);
        DepositReceipt receipt = null;
        List<SWORDWorkspace> ws = sd.getWorkspaces();
        for (SWORDWorkspace w : ws)
        {
            List<SWORDCollection> collections = w.getCollections();
            for (SWORDCollection c : collections)
            {
                // do the deposit to the first collection we find
                receipt = client.deposit(c, deposit, auth);
                break;
            }
        }

		Link editlink = receipt.getEntry().getEditLink();
		String editiri = editlink.getHref().toString();

		DepositReceipt r = client.complete(editiri, auth);
		r.getEntry().writeTo(System.out);
	}

	private void tryStatement()
			throws Exception
	{
        System.out.println("tryStatement");

		// first we need to put some content in
        SWORDClient client = new SWORDClient();
        InputStream is = new FileInputStream(new File(exampleZip));
        DepositFactory factory = new DepositFactory();
        Deposit deposit = factory.newBinaryOnly(is, "example.zip", "application/zip", UriRegistry.PACKAGE_SIMPLE_ZIP);
        AuthCredentials auth = new AuthCredentials(this.user, this.pass, this.obo);
        ServiceDocument sd = client.getServiceDocument(this.sdIRI, auth);
        DepositReceipt receipt = null;
        List<SWORDWorkspace> ws = sd.getWorkspaces();
        for (SWORDWorkspace w : ws)
        {
            List<SWORDCollection> collections = w.getCollections();
            for (SWORDCollection c : collections)
            {
                // do the deposit to the first collection we find
                receipt = client.deposit(c, deposit, auth);
                break;
            }
        }

		SwordIdentifier oreStatementLink = receipt.getStatementLink("application/rdf+xml");
		String oreUri = oreStatementLink.getHref().toString();
		SwordIdentifier feedStatementLink = receipt.getStatementLink("application/atom+xml;type=feed");
		String feedUri = feedStatementLink.getHref().toString();

		Statement oreStmt = client.getStatement(oreUri, new OreStatement(), auth);
		Statement feedStmt = client.getStatement(feedUri, new AtomStatement(), auth);

        System.out.println(oreStmt.getContentMD5());
        System.out.println(oreStmt.getLastModified());
		System.out.println(oreStmt.toString());

        System.out.println(feedStmt.getContentMD5());
        System.out.println(feedStmt.getLastModified());
		System.out.println(feedStmt.toString());

		System.out.println("---- and now with content negotiation ---");

		Link editlink = receipt.getEntry().getEditLink();
		String editiri = editlink.getHref().toString();

		Statement oreStmtCN = client.getStatement(editiri, new OreStatement(), auth);
        System.out.println(oreStmtCN.getContentMD5());
        System.out.println(oreStmtCN.getLastModified());
		System.out.println(oreStmtCN.toString());

		Statement feedStmtCN = client.getStatement(editiri, new AtomStatement(), auth);
        System.out.println(feedStmtCN.getContentMD5());
        System.out.println(feedStmtCN.getLastModified());
		System.out.println(feedStmtCN.toString());
	}

	private void tryDelete()
			throws Exception
	{
        System.out.println("tryDelete");

		// first we need to put some content in
        SWORDClient client = new SWORDClient();
        InputStream is = new FileInputStream(new File(exampleZip));
        DepositFactory factory = new DepositFactory();
        Deposit deposit = factory.newBinaryOnly(is, "example.zip", "application/zip", UriRegistry.PACKAGE_SIMPLE_ZIP, null, null, true);
        AuthCredentials auth = new AuthCredentials(this.user, this.pass, this.obo);
        ServiceDocument sd = client.getServiceDocument(this.sdIRI, auth);
        DepositReceipt receipt = null;
        List<SWORDWorkspace> ws = sd.getWorkspaces();
        for (SWORDWorkspace w : ws)
        {
            List<SWORDCollection> collections = w.getCollections();
            for (SWORDCollection c : collections)
            {
                // do the deposit to the first collection we find
                receipt = client.deposit(c, deposit, auth);
                break;
            }
        }

		Link editlink = receipt.getEntry().getEditLink();
		String editiri = editlink.getHref().toString();

		Thread.sleep(20000);

		SwordResponse resp = client.delete(editiri, auth);
		System.out.println(resp.getStatusCode());
	}

	private void tryAddMetadataAndPackage()
			throws Exception
	{
        System.out.println("tryAddMetadataAndPackage");

		// first we need to put some content in
        SWORDClient client = new SWORDClient();
        InputStream is = new FileInputStream(new File(exampleZip));
        DepositFactory factory = new DepositFactory();
        Deposit deposit = factory.newBinaryOnly(is, "example.zip", "application/zip", UriRegistry.PACKAGE_SIMPLE_ZIP);
        AuthCredentials auth = new AuthCredentials("sword", "sword", "obo");
        ServiceDocument sd = client.getServiceDocument(this.sdIRI, auth);
        DepositReceipt receipt = null;
        List<SWORDWorkspace> ws = sd.getWorkspaces();
        for (SWORDWorkspace w : ws)
        {
            List<SWORDCollection> collections = w.getCollections();
            for (SWORDCollection c : collections)
            {
                // do the deposit to the first collection we find
                receipt = client.deposit(c, deposit, auth);
				receipt.getEntry().writeTo(System.out);
                break;
            }
        }

		Link editlink = receipt.getEntry().getEditLink();
		String editiri = editlink.getHref().toString();

		EntryPart ep = new EntryPart();
		ep.addDublinCore("title", "Richard Woz Ere Again");
		ep.addDublinCore("bibliographicCitation", "this is not my citation");
		InputStream is2 = new FileInputStream(new File(exampleZip));
		Deposit updateDeposit = factory.addMultipart(ep, is2, "example2.zip", "application/zip", UriRegistry.PACKAGE_SIMPLE_ZIP);
		DepositReceipt resp = client.addToContainer(editiri, updateDeposit, auth);

		resp.getEntry().writeTo(System.out);
	}

	private void tryAddMetadata()
			throws Exception
	{
        System.out.println("tryAddMetadata");
		// first we need to put some content in
        SWORDClient client = new SWORDClient();
        InputStream is = new FileInputStream(new File(exampleZip));
        DepositFactory factory = new DepositFactory();
        Deposit deposit = factory.newBinaryOnly(is, "example.zip", "application/zip", UriRegistry.PACKAGE_SIMPLE_ZIP, null, null, true);
        AuthCredentials auth = new AuthCredentials(this.user, this.pass, this.obo);
        ServiceDocument sd = client.getServiceDocument(this.sdIRI, auth);
        DepositReceipt receipt = null;
        List<SWORDWorkspace> ws = sd.getWorkspaces();
        for (SWORDWorkspace w : ws)
        {
            List<SWORDCollection> collections = w.getCollections();
            for (SWORDCollection c : collections)
            {
                // do the deposit to the first collection we find
                receipt = client.deposit(c, deposit, auth);
				receipt.getEntry().writeTo(System.out);
                break;
            }
        }

		Link editlink = receipt.getEntry().getEditLink();
		String editiri = editlink.getHref().toString();

		EntryPart ep = new EntryPart();
        ep.addDublinCore("title", "Richard Woz Ere Again");
        ep.addDublinCore("bibliographicCitation", "this is not my citation");
		Deposit updateDeposit = factory.addMetadata(ep);
		DepositReceipt resp = client.addToContainer(editiri, updateDeposit, auth);

		resp.getEntry().writeTo(System.out);
	}

	private void tryAddPackage()
			throws Exception
	{
        System.out.println("tryPackage");
		// first we need to put some content in
        SWORDClient client = new SWORDClient();
        InputStream is = new FileInputStream(new File(exampleZip));
        DepositFactory factory = new DepositFactory();
        Deposit deposit = factory.newBinaryOnly(is, "example.zip", "application/zip", UriRegistry.PACKAGE_SIMPLE_ZIP);
        AuthCredentials auth = new AuthCredentials("sword", "sword", "obo");
        ServiceDocument sd = client.getServiceDocument(this.sdIRI, auth);
        DepositReceipt receipt = null;
        List<SWORDWorkspace> ws = sd.getWorkspaces();
        for (SWORDWorkspace w : ws)
        {
            List<SWORDCollection> collections = w.getCollections();
            for (SWORDCollection c : collections)
            {
                // do the deposit to the first collection we find
                receipt = client.deposit(c, deposit, auth);
                break;
            }
        }

		Link editlink = receipt.getEntry().getEditLink();
		String editiri = editlink.getHref().toString();

		InputStream is2 = new FileInputStream(new File(exampleZip));
		Deposit updateDeposit = factory.addBinary(is2, "example2.zip", "application/zip", UriRegistry.PACKAGE_SIMPLE_ZIP);
		DepositReceipt resp = client.addToContainer(editiri, updateDeposit, auth);

		resp.getEntry().writeTo(System.out);
	}

	private void tryAddContent()
			throws Exception
	{
        System.out.println("tryAddContent");
		// first we need to put some content in
        SWORDClient client = new SWORDClient();
        InputStream is = new FileInputStream(new File(exampleZip));
        DepositFactory factory = new DepositFactory();
        Deposit deposit = factory.newBinaryOnly(is, "example.zip", "application/zip", UriRegistry.PACKAGE_SIMPLE_ZIP, null, null, true);
        AuthCredentials auth = new AuthCredentials(this.user, this.pass, this.obo);
        ServiceDocument sd = client.getServiceDocument(this.sdIRI, auth);
        DepositReceipt receipt = null;
        List<SWORDWorkspace> ws = sd.getWorkspaces();
        for (SWORDWorkspace w : ws)
        {
            List<SWORDCollection> collections = w.getCollections();
            for (SWORDCollection c : collections)
            {
                // do the deposit to the first collection we find
                receipt = client.deposit(c, deposit, auth);
                break;
            }
        }

		SwordIdentifier eml = receipt.getEditMediaLink();
        String emiri = eml.getHref().toString();

		InputStream is2 = new FileInputStream(new File(exampleZip));
		Deposit updateDeposit = factory.addMediaResource(is2, "example2.zip", "application/zip");
		SwordResponse resp = client.addToMediaResource(emiri, updateDeposit, auth);
		System.out.println(resp.getStatusCode());

		if (resp instanceof DepositReceipt)
		{
			((DepositReceipt) resp).getEntry().writeTo(System.out);
		}
		else
		{
			System.out.println("Response was NOT a deposit receipt");
		}

		// now we need to get the content out as an atom feed to see the multiple files
        Content content = client.getContent(emiri, "application/atom+xml;type=feed", null, auth);

		// prove that we got a feed
		content.getFeed().writeTo(System.out);
	}

    private void tryAddToMediaResource()
			throws Exception
	{
        System.out.println("tryAddToContainer");
		// first we need to put some content in
        SWORDClient client = new SWORDClient();
        DepositFactory factory = new DepositFactory();
        AuthCredentials auth = new AuthCredentials(this.user, this.pass, this.obo);

		InputStream is2 = new FileInputStream(new File(exampleZip));
		Deposit updateDeposit = factory.addMediaResource(is2, "example2.zip", "application/zip");
		SwordResponse resp = client.addToMediaResource(mediaUri, updateDeposit, auth);
		System.out.println(resp.getStatusCode());

		if (resp instanceof DepositReceipt)
		{
			((DepositReceipt) resp).getEntry().writeTo(System.out);
		}
		else
		{
			System.out.println("Response was NOT a deposit receipt");
		}

        // now we need to get the content out as an atom feed to see the multiple files
        Content content = client.getContent(mediaUri, "application/atom+xml;type=feed", null, auth);

		// prove that we got a feed
		content.getFeed().writeTo(System.out);
	}

	private void tryDeleteContent()
			throws Exception
	{
        System.out.println("tryDeleteContent");
		// first we need to put some content in
        SWORDClient client = new SWORDClient();
        InputStream is = new FileInputStream(new File(exampleZip));
        DepositFactory factory = new DepositFactory();
        Deposit deposit = factory.newBinaryOnly(is, "example.zip", "application/zip", UriRegistry.PACKAGE_SIMPLE_ZIP, null, null, true);
        AuthCredentials auth = new AuthCredentials(this.user, this.pass, this.obo);
        ServiceDocument sd = client.getServiceDocument(this.sdIRI, auth);
        DepositReceipt receipt = null;
        List<SWORDWorkspace> ws = sd.getWorkspaces();
        for (SWORDWorkspace w : ws)
        {
            List<SWORDCollection> collections = w.getCollections();
            for (SWORDCollection c : collections)
            {
                // do the deposit to the first collection we find
                receipt = client.deposit(c, deposit, auth);
                break;
            }
        }

		SwordIdentifier eml = receipt.getEditMediaLink();
        String emiri = eml.getHref().toString();

		SwordResponse resp = client.delete(emiri, auth);
		System.out.println(resp.getStatusCode());
	}

	private void tryReplaceContentAndMetadata()
			throws Exception
	{
        System.out.println("tryReplaceContentAndMetadata");
		// first we need to put some content in
        SWORDClient client = new SWORDClient();
        InputStream is = new FileInputStream(new File(exampleZip));
        DepositFactory factory = new DepositFactory();
        Deposit deposit = factory.newBinaryOnly(is, "example.zip", "application/zip", UriRegistry.PACKAGE_SIMPLE_ZIP);
        AuthCredentials auth = new AuthCredentials("sword", "sword", "obo");
        ServiceDocument sd = client.getServiceDocument(this.sdIRI, auth);
        DepositReceipt receipt = null;
        List<SWORDWorkspace> ws = sd.getWorkspaces();
        for (SWORDWorkspace w : ws)
        {
            List<SWORDCollection> collections = w.getCollections();
            for (SWORDCollection c : collections)
            {
                // do the deposit to the first collection we find
                receipt = client.deposit(c, deposit, auth);
                break;
            }
        }

		Link editlink = receipt.getEntry().getEditLink();
		String editiri = editlink.getHref().toString();

		EntryPart ep = new EntryPart();
        ep.addDublinCore("title", "Richard Woz Ere Again");
        ep.addDublinCore("bibliographicCitation", "this is not my citation");
		InputStream is2 = new FileInputStream(new File(exampleZip));
		Deposit updateDeposit = factory.replaceMultipart(ep, is2, "example2.zip", "application/zip", UriRegistry.PACKAGE_SIMPLE_ZIP);
		
		SwordResponse resp = client.replace(editiri, updateDeposit, auth);

		System.out.println(resp.getStatusCode());
	}

	private void tryReplaceMetadata()
			throws Exception
	{
        System.out.println("tryReplaceMetadata");
		// first we need to put some content in
        SWORDClient client = new SWORDClient();
        InputStream is = new FileInputStream(new File(exampleZip));
        DepositFactory factory = new DepositFactory();
        Deposit deposit = factory.newBinaryOnly(is, "example.zip", "application/zip", UriRegistry.PACKAGE_SIMPLE_ZIP, null, null, true);
        AuthCredentials auth = new AuthCredentials(this.user, this.pass, this.obo);
        ServiceDocument sd = client.getServiceDocument(this.sdIRI, auth);
        DepositReceipt receipt = null;
        List<SWORDWorkspace> ws = sd.getWorkspaces();
        for (SWORDWorkspace w : ws)
        {
            List<SWORDCollection> collections = w.getCollections();
            for (SWORDCollection c : collections)
            {
                // do the deposit to the first collection we find
                receipt = client.deposit(c, deposit, auth);
                break;
            }
        }

		Link editlink = receipt.getEntry().getEditLink();
		String editiri = editlink.getHref().toString();

		EntryPart ep = new EntryPart();
        ep.addDublinCore("title", "Richard Woz Ere Again");
        ep.addDublinCore("bibliographicCitation", "this is not my citation");
		Deposit updateDeposit = factory.replaceMetadata(ep);
		SwordResponse resp = client.replace(editiri, updateDeposit, auth);

		System.out.println(resp.getStatusCode());
	}

	private void tryReplaceFileContent()
			throws Exception
	{
        System.out.println("tryReplaceFileContent");
		// first we need to put some content in
        SWORDClient client = new SWORDClient();
        InputStream is = new FileInputStream(new File(exampleZip));
        DepositFactory factory = new DepositFactory();
        Deposit deposit = factory.newBinaryOnly(is, "example.zip", "application/zip", UriRegistry.PACKAGE_SIMPLE_ZIP, null, null, true);
        AuthCredentials auth = new AuthCredentials(this.user, this.pass, this.obo);
        ServiceDocument sd = client.getServiceDocument(this.sdIRI, auth);
        DepositReceipt receipt = null;
        List<SWORDWorkspace> ws = sd.getWorkspaces();
        for (SWORDWorkspace w : ws)
        {
            List<SWORDCollection> collections = w.getCollections();
            for (SWORDCollection c : collections)
            {
                // do the deposit to the first collection we find
                receipt = client.deposit(c, deposit, auth);
                break;
            }
        }

		SwordIdentifier eml = receipt.getEditMediaLink("application/zip");
        String emiri = eml.getHref().toString();

		InputStream is2 = new FileInputStream(new File(exampleZip));
		Deposit updateDeposit = factory.replaceBinary(is2, "example2.zip", "application/zip", UriRegistry.PACKAGE_SIMPLE_ZIP);
		SwordResponse resp = client.replaceMedia(emiri, updateDeposit, auth);
	}

	private void tryFeedRetrieve()
		throws Exception
    {
        System.out.println("tryFeedRetrieve");
        // first we need to put some content in
        SWORDClient client = new SWORDClient();
        InputStream is = new FileInputStream(new File(exampleZip));
        DepositFactory factory = new DepositFactory();
        Deposit deposit = factory.newBinaryOnly(is, "example.zip", "application/zip", UriRegistry.PACKAGE_SIMPLE_ZIP);
        AuthCredentials auth = new AuthCredentials(this.user, this.pass, this.obo);
        ServiceDocument sd = client.getServiceDocument(this.sdIRI, auth);
        DepositReceipt receipt = null;
        List<SWORDWorkspace> ws = sd.getWorkspaces();
        for (SWORDWorkspace w : ws)
        {
            List<SWORDCollection> collections = w.getCollections();
            for (SWORDCollection c : collections)
            {
                // do the deposit to the first collection we find
                receipt = client.deposit(c, deposit, auth);
                break;
            }
        }

		SwordIdentifier contentSrc = receipt.getContentLink();
        SwordIdentifier eml = receipt.getEditMediaLink("application/atom+xml;type=feed");
        String emiri = eml.getHref().toString();

        System.out.println("Content: " + contentSrc.getHref() + " - " + contentSrc.getType());
        System.out.println("EM: " + emiri);

        // now we need to get the content out as an atom feed
        Content content = client.getContent(emiri, "application/atom+xml;type=feed", null, auth);

		// prove that we got a feed
		content.getFeed().writeTo(System.out);
    }

    private void tryContentRetrieve()
            throws Exception
    {
        System.out.println("tryContentRetrieve");
        // first we need to put some content in
        SWORDClient client = new SWORDClient();
        InputStream is = new FileInputStream(new File(exampleZip));
		DepositFactory factory = new DepositFactory();
        Deposit deposit = factory.newBinaryOnly(is, "example.zip", "application/zip", UriRegistry.PACKAGE_SIMPLE_ZIP);
        AuthCredentials auth = new AuthCredentials(this.user, this.pass, this.obo);
        ServiceDocument sd = client.getServiceDocument(this.sdIRI, auth);
        DepositReceipt receipt = null;
        List<SWORDWorkspace> ws = sd.getWorkspaces();
        for (SWORDWorkspace w : ws)
        {
            List<SWORDCollection> collections = w.getCollections();
            for (SWORDCollection c : collections)
            {
                // do the deposit to the first collection we find
                receipt = client.deposit(c, deposit, auth);
                break;
            }
        }

        SwordIdentifier contentSrc = receipt.getContentLink();
        SwordIdentifier eml = receipt.getEditMediaLink("application/zip");
        String emiri = eml.getHref().toString();

        System.out.println("Content: " + contentSrc.getHref() + " - " + contentSrc.getType());
        System.out.println("EM: " + emiri);

        // now we need to get the content out
        Content content = client.getContent(emiri, contentSrc.getType(), UriRegistry.PACKAGE_SIMPLE_ZIP, auth);
        System.out.println(content.getPackaging() + " - " + content.getMimeType());
    }

    private void tryEntryDeposit()
            throws Exception
    {
        System.out.println("tryEntryDeposit");
        SWORDClient client = new SWORDClient();
        EntryPart ep = new EntryPart();
        ep.addDublinCore("title", "Richard Woz Ere");
        ep.addDublinCore("bibliographicCitation", "this is my citation");
		DepositFactory factory = new DepositFactory();
        Deposit deposit = factory.newMetadataOnly(ep, null, true);
        AuthCredentials auth = new AuthCredentials(this.user, this.pass, this.obo);
        ServiceDocument sd = client.getServiceDocument(this.sdIRI, auth);
        DepositReceipt receipt = null;
        List<SWORDWorkspace> ws = sd.getWorkspaces();
        for (SWORDWorkspace w : ws)
        {
            List<SWORDCollection> collections = w.getCollections();
            for (SWORDCollection c : collections)
            {
                // do the deposit to the first collection we find
                receipt = client.deposit(c, deposit, auth);
                break;
            }
        }
        receipt.getEntry().writeTo(System.out);
    }

    private void tryMultipartDeposit()
            throws Exception
    {
        System.out.println("tryMultipartDeposit");
        SWORDClient client = new SWORDClient();
        InputStream is = new FileInputStream(new File(exampleZip));
        EntryPart ep = new EntryPart();
        ep.addDublinCore("title", "Richard Woz Ere");
        ep.addDublinCore("bibliographicCitation", "this is my citation");
		DepositFactory factory = new DepositFactory();
        Deposit deposit = factory.newMultipart(ep, is, "example.zip", "application/zip", UriRegistry.PACKAGE_SIMPLE_ZIP);
        AuthCredentials auth = new AuthCredentials(this.user, this.pass, this.obo);
        ServiceDocument sd = client.getServiceDocument(this.sdIRI, auth);
        DepositReceipt receipt = null;
        List<SWORDWorkspace> ws = sd.getWorkspaces();
        for (SWORDWorkspace w : ws)
        {
            List<SWORDCollection> collections = w.getCollections();
            for (SWORDCollection c : collections)
            {
                // do the deposit to the first collection we find
                receipt = client.deposit(c, deposit, auth);
                break;
            }
        }
        receipt.getEntry().writeTo(System.out);
    }

    private void tryBinaryDeposit()
            throws Exception
    {
        System.out.println("tryBinaryDeposit");
        SWORDClient client = new SWORDClient();
        InputStream is = new FileInputStream(new File(exampleZip));
		DepositFactory factory = new DepositFactory();
        Deposit deposit = factory.newBinaryOnly(is, "example.zip", "application/zip", UriRegistry.PACKAGE_SIMPLE_ZIP, null, null, true);
        AuthCredentials auth = new AuthCredentials(this.user, this.pass, this.obo);
        ServiceDocument sd = client.getServiceDocument(this.sdIRI, auth);
        DepositReceipt receipt = null;
        List<SWORDWorkspace> ws = sd.getWorkspaces();
        for (SWORDWorkspace w : ws)
        {
            List<SWORDCollection> collections = w.getCollections();
            for (SWORDCollection c : collections)
            {
                // do the deposit to the first collection we find
                receipt = client.deposit(c, deposit, auth);
                break;
            }
        }
        System.out.println(receipt.getContentMD5());
        System.out.println(receipt.getLastModified());
        receipt.getEntry().writeTo(System.out);
    }

    private void tryImage()
            throws Exception
    {
        System.out.println("tryImage");
        SWORDClient client = new SWORDClient();
        InputStream is = new FileInputStream(new File(image));
		DepositFactory factory = new DepositFactory();
        Deposit deposit = factory.newBinaryOnly(is, "image.jpg", "image/jpeg", UriRegistry.PACKAGE_BINARY);
        AuthCredentials auth = new AuthCredentials(this.user, this.pass, this.obo);
        ServiceDocument sd = client.getServiceDocument(this.sdIRI, auth);
        DepositReceipt receipt = null;
        List<SWORDWorkspace> ws = sd.getWorkspaces();
        for (SWORDWorkspace w : ws)
        {
            List<SWORDCollection> collections = w.getCollections();
            for (SWORDCollection c : collections)
            {
                // do the deposit to the first collection we find
                receipt = client.deposit(c, deposit, auth);
                break;
            }
        }
        System.out.println(receipt.getContentMD5());
        System.out.println(receipt.getLastModified());
        receipt.getEntry().writeTo(System.out);
    }

    private void tryDocument()
            throws Exception
    {
        System.out.println("tryDocument");
        SWORDClient client = new SWORDClient();
        InputStream is = new FileInputStream(new File(docx));
		DepositFactory factory = new DepositFactory();
        Deposit deposit = factory.newBinaryOnly(is, "hy.docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", UriRegistry.PACKAGE_BINARY);
        AuthCredentials auth = new AuthCredentials(this.user, this.pass, this.obo);
        ServiceDocument sd = client.getServiceDocument(this.sdIRI, auth);
        DepositReceipt receipt = null;
        List<SWORDWorkspace> ws = sd.getWorkspaces();
        for (SWORDWorkspace w : ws)
        {
            List<SWORDCollection> collections = w.getCollections();
            for (SWORDCollection c : collections)
            {
                // do the deposit to the first collection we find
                receipt = client.deposit(c, deposit, auth);
                break;
            }
        }
        System.out.println(receipt.getContentMD5());
        System.out.println(receipt.getLastModified());
        receipt.getEntry().writeTo(System.out);
    }

    private void tryFileReplace()
            throws Exception
    {
        System.out.println("tryFileReplace");
        SWORDClient client = new SWORDClient();
        InputStream is = new FileInputStream(new File(image));
		DepositFactory factory = new DepositFactory();
        Deposit deposit = factory.newBinaryOnly(is, "image.jpg", "image/jpeg", UriRegistry.PACKAGE_BINARY, null, null, true);
        AuthCredentials auth = new AuthCredentials(this.user, this.pass, this.obo);
        ServiceDocument sd = client.getServiceDocument(this.sdIRI, auth);
        DepositReceipt receipt = null;
        List<SWORDWorkspace> ws = sd.getWorkspaces();
        for (SWORDWorkspace w : ws)
        {
            List<SWORDCollection> collections = w.getCollections();
            for (SWORDCollection c : collections)
            {
                // do the deposit to the first collection we find
                receipt = client.deposit(c, deposit, auth);
                break;
            }
        }

        SwordIdentifier odl = receipt.getOriginalDepositLink();
        InputStream is2 = new FileInputStream(new File(docx));
        Deposit d2 = factory.replaceBinary(is2, "hy.docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", UriRegistry.PACKAGE_BINARY, null, true, false);
        System.out.println(odl.getHref().toString());
        SwordResponse resp = client.replaceMedia(odl.getHref().toString(), d2, auth);

        System.out.println("Original Deposit: " + odl.getHref().toString());
        System.out.println("New Location: " + resp.getLocation());
    }

    private void tryCollectionEntries()
            throws Exception
    {
        System.out.println("tryCollectionEntries");
        SWORDClient client = new SWORDClient();
        AuthCredentials auth = new AuthCredentials(this.user, this.pass, this.obo);
        ServiceDocument sd = this.trySwordServiceDocument();
        List<SWORDWorkspace> ws = sd.getWorkspaces();
        for (SWORDWorkspace w : ws)
        {
            List<SWORDCollection> collections = w.getCollections();
            for (SWORDCollection c : collections)
            {
                System.out.println("Collection: " + c.getTitle());
                IRI href = c.getHref();
                CollectionEntries ces = client.listCollection(href.toString(), auth);
                for (Entry entry : ces.getEntries())
                {
                    IRI id = entry.getId();
                    System.out.println("\t\tID: " + id.toString());
                }
            }
        }
    }

    private ServiceDocument trySwordServiceDocument()
            throws Exception
    {
        System.out.println("trySwordServiceDocument");
        SWORDClient client = new SWORDClient();
        ServiceDocument sd = client.getServiceDocument(this.sdIRI, new AuthCredentials(this.user, this.pass, this.obo));
        System.out.println("Version: " + sd.getVersion());
        System.out.println("Max Upload Size: " + Long.toString(sd.getMaxUploadSize()));
        List<SWORDWorkspace> sws = sd.getWorkspaces();
        for (SWORDWorkspace ws : sws)
        {
            System.out.println("Workspace: " + ws.getTitle());
            List<SWORDCollection> cols = ws.getCollections();
            for (SWORDCollection c : cols)
            {
                System.out.println("\tCollection: " + c.getTitle());
                System.out.println("\t\tPolicy: " + c.getCollectionPolicy());
                System.out.println("\t\tTreatment: " + c.getTreatment());
                System.out.println("\t\tURI: " + c.getHref());
                System.out.println("\t\tURI (Resolved): " + c.getResolvedHref());
                System.out.println("\t\tMediation Allowed: " + c.allowsMediation());

                List<String> packaging = c.getAcceptPackaging();
                for (String pack : packaging)
                {
                    System.out.println("\t\tAccepts Packaging: " + pack);
                }

                List<String> subservices = c.getSubServices();
                for (String ss : subservices)
                {
                    System.out.println("\t\tSub Service: " + ss);
                }

                List<String> maccepts = c.getMultipartAccept();
                for (String acc : maccepts)
                {
                    System.out.println("\t\tMultipart Accepts: " + acc);
                }
                
                List<String> accepts = c.getSinglepartAccept();
                for (String acc : accepts)
                {
                    System.out.println("\t\tAccepts: " + acc);
                }

                System.out.println("\t\tAccepts application/zip? " + c.singlepartAccepts("application/zip"));
                System.out.println("\t\tMultipart Accepts application/zip? " + c.multipartAccepts("application/zip"));
                System.out.println("\t\tAccepts Entry? " + c.singlepartAcceptsEntry());
                System.out.println("\t\tMultipart Accepts Entry? " + c.multipartAcceptsEntry());
                System.out.println("\t\tAccepts Nothing? " + c.acceptsNothing());
            }
        }
        return sd;
    }
}
