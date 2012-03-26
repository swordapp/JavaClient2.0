package org.swordapp.client.test;

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Entry;
import org.swordapp.client.AuthCredentials;
import org.swordapp.client.CollectionEntries;
import org.swordapp.client.SWORDClient;
import org.swordapp.client.SWORDCollection;
import org.swordapp.client.SWORDWorkspace;
import org.swordapp.client.ServiceDocument;

import java.util.List;

public class ServerInteractionTests
{
	private String sdIRI = null;
	private String user = null;
	private String pass = null;
	private String obo = null;
	private SWORDClient client = null;

	public ServerInteractionTests(String sdIRI, String user, String pass, String obo)
	{
		this.sdIRI = sdIRI != null ? sdIRI : "http://localhost:8080/sword2/servicedocument";
		this.user = user != null ? user : "richard";
		this.pass = pass != null ? pass : "dspace";
		this.obo = obo;
		this.client = new SWORDClient();
	}

	public static void main(String[] args)
            throws Exception
    {
		ServerInteractionTests tests = new ServerInteractionTests(null, null, null, null);
		// tests.testServiceDocument();
		tests.testCollectionList();
	}

	///////////////////////////////////////////////////////////////////
	// TEST METHODS
	///////////////////////////////////////////////////////////////////

	public void testServiceDocument()
			throws Exception
	{
		System.out.println("Starting Testing Service Document Interactions");

		// Test variables on the service document:
		// 1/ a)authenticated b)un-authenticated
		// 2/ a)with obo b)without obo

		ServiceDocument sd;

		// authenticated with obo (1a2a)
		System.out.println("... Authenticated with OBO");
		sd = this.client.getServiceDocument(this.sdIRI, new AuthCredentials(this.user, this.pass, this.obo));
		this.outputServiceDocument(sd);
		System.out.println("... End Authenticated with OBO");

		// authenticated without obo (1a2b)
		System.out.println("... Authenticated without OBO");
		sd = this.client.getServiceDocument(this.sdIRI, new AuthCredentials(this.user, this.pass));
		this.outputServiceDocument(sd);
		System.out.println("... End Authenticated with OBO");

		// unauthenticated (1b)
		System.out.println("... Unauthenticated");
		sd = this.client.getServiceDocument(this.sdIRI);
		this.outputServiceDocument(sd);
		System.out.println("... End Unauthenticated");

		System.out.println("Finished Testing Service Document Interactions");
	}

	public void testCollectionList()
			throws Exception
	{
		System.out.println("Starting Testing Collection List Interactions");

		// test variables on the collection list
		// 1/ a)authenticated b)un-authenticated
		// 2/ a) with obo b) without obo

		ServiceDocument sd;
		AuthCredentials auth;

		// authenticated with obo (1a2a)
		System.out.println("... Authenticated with OBO");
		auth = new AuthCredentials(this.user, this.pass, this.obo);
		sd = this.client.getServiceDocument(this.sdIRI, auth);
		this.iterateAndListCollections(sd, auth);
		System.out.println("... End Authenticated with OBO");

		// authenticated without obo (1a2b)
		System.out.println("... Authenticated without OBO");
		auth = new AuthCredentials(this.user, this.pass);
		sd = this.client.getServiceDocument(this.sdIRI, auth);
		this.iterateAndListCollections(sd, auth);
		System.out.println("... End Authenticated with OBO");

		// unauthenticated (1b)
		System.out.println("... Unauthenticated");
		sd = this.client.getServiceDocument(this.sdIRI);
		this.iterateAndListCollections(sd, null);
		System.out.println("... End Unauthenticated");

		System.out.println("Finished Testing Collection List Interactions");
	}

	private void iterateAndListCollections(ServiceDocument sd, AuthCredentials auth)
			throws Exception
	{
		if (sd == null)
		{
			System.out.println("--- Service Document was NULL --");
			return;
		}

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

	private void outputServiceDocument(ServiceDocument sd)
			throws Exception
	{
		if (sd == null)
		{
			System.out.println("--- Service Document was NULL --");
			return;
		}
		
		System.out.println("--- service document interpretation ---");
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
	}
}
