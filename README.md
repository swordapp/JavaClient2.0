SWORD 2.0 Client
================

This client library is an implementation of the SWORD 2.0 standard defined here

http://swordapp.org/sword-v2/sword-v2-specifications/


Build
-----

To build this library use maven 2:

    mvn clean package

In the root directory will build the software


Usage
-----

The main point of entry for client operations is org.swordapp.client.SWORDClient

To perform deposits, create instances of org.swordapp.client.Deposit and pass it to the SWORDClient's methods.

To authenticate, create instances of org.swordapp.client.AuthCredentials and pass them in with the Deposit object to the SWORDClient.


For example:

    SWORDClient client = new SWORDClient()

Obtain a service document:

    ServiceDocument sd = client.getServiceDocument(this.sdIRI, new AuthCredentials(this.user, this.pass));

Get the first collection from the first workspace in the service document:

    SWORDCollection col = sd.getWorkspaces().get(0).getCollections().get(0);

Create a binary file only Deposit object:

    Deposit deposit = new Deposit();
    deposit.setFile(new FileInputStream(myFile));
    deposit.setMimeType("application/zip");
    deposit.setFilename("example.zip");
    deposit.setPackaging(UriRegistry.PACKAGE_SIMPLE_ZIP);
    deposit.setMd5(fileMD5);
    deposit.setInProgress(true);
    deposit.setSuggestedIdentifier("abcdefg");

Pass the deposit object to the client:

    DepositReceipt receipt = client.deposit(col, deposit, auth)

We can create entry-only depsits too:

    EntryPart ep = new EntryPart();
    ep.addDublinCore("title", "My Title");
    
    Deposit deposit = new Deposit();
    deposit.setEntryPart(ep);

For some deposit operations we get back a DepositReceipt object, from which we can get all the details we might want about the item:

    receipt.getStatusCode();
    receipt.getLocation();
    receipt.getDerivedResourceLinks();
    receipt.getOriginalDepositLink();
    receipt.getEditMediaLink();
    receipt.getAtomStatementLink();
    receipt.getContentLink();
    receipt.getEditLink();
    receipt.getOREStatementLink();
    receipt.getPackaging();
    receipt.getSplashPageLink();
    receipt.getStatementLink("application/rdf+xml");
    receipt.getStatementLink("application/atom+xml;type=feed");
    receipt.getSwordEditLink();
    receipt.getTreatment();
    receipt.getVerboseDescription();


Limitations
-----------

Currently the client DOES NOT support multipart deposit.  Therefore the specification sections 6.3.2, 6.5.3, and 6.7.3 are not supported yet.

