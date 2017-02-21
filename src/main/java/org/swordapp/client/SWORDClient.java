package org.swordapp.client;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Service;
import org.apache.abdera.parser.ParseException;
import org.apache.abdera.protocol.Response;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.abdera.protocol.client.RequestOptions;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;

public class SWORDClient
{
    private static Logger log = Logger.getLogger(SWORDClient.class);

    private ClientConfiguration config;

    private Abdera abdera;

    public SWORDClient()
    {
        this(new ClientConfiguration());
    }

    public SWORDClient(ClientConfiguration config)
    {
        this.config = config;
        this.abdera = new Abdera();
    }

	public Endpoints autoDiscover(String url)
			throws SWORDClientException
	{
		try
		{
			org.jsoup.nodes.Document doc = Jsoup.connect(url).get();
			Elements links = doc.select("link[rel]");
			Endpoints endpoints = new Endpoints();

			for (int i = 0; i < links.size(); i++)
			{
				Element element = links.get(i);
				String rel = element.attr("rel");
				if (rel == null)
				{
					continue;
				}
				rel = rel.toLowerCase();

				if (rel.equals("sword") || rel.equals(UriRegistry.REL_SERVICE_DOCUMENT))
				{
					endpoints.setServiceDocument(element.attr("href"));
				}
				else if (rel.equals(UriRegistry.REL_DEPOSIT))
				{
					endpoints.setCollection(element.attr("href"));
				}
				else if (rel.equals(UriRegistry.REL_EDIT))
				{
					endpoints.setEdit(element.attr("href"));
				}
				else if (rel.equals(UriRegistry.REL_STATEMENT))
				{
					endpoints.addStatement(element.attr("href"), element.attr("type"));
				}
			}

			return endpoints;
		}
		catch (IOException e)
		{
			throw new SWORDClientException(e);
		}
	}

    public ServiceDocument getServiceDocument(String sdURL)
            throws SWORDClientException, ProtocolViolationException
    {
        return this.getServiceDocument(sdURL, null);
    }

    public ServiceDocument getServiceDocument(String sdURL, AuthCredentials auth)
            throws SWORDClientException, ProtocolViolationException
    {
        // do some error checking and validations
        if (sdURL == null)
        {
            log.error("Null string passed in to getServiceDocument; returning null");
            return null;
        }
        if (log.isDebugEnabled())
        {
            log.debug("getting service document from " + sdURL);
        }

        AbderaClient client = new AbderaClient(this.abdera);
        RequestOptions options = this.getDefaultRequestOptions();

        // prepare the bits of the request

        // ensure that the URL is valid
        URL url = this.formaliseURL(sdURL);
        if (log.isDebugEnabled())
        {
            log.debug("Formalised Service Document URL to " + url.toString());
        }

        // sort out the HTTP basic authentication credentials
        this.prepAuth(auth, client, options);

        // log the request
        this.logGetRequest(auth, url.toString(), "Service Document");

        // make the request for the service document
        if (log.isDebugEnabled())
        {
           log.debug("Connecting to Server to retrieve Service Document from " + url.toString() + " ...");
        }
        ClientResponse resp = client.get(url.toString(), options);
        if (log.isDebugEnabled())
        {
            log.debug("Successfully retrieved Service Document from " + url.toString());
        }

        // if the response is successful, get the service document out of the response,
        // and wrap it in the SWORD ServiceDocument class
        if (resp.getType() == Response.ResponseType.SUCCESS)
        {
            log.info("Retrieved Service Document from " + url.toString() + " with HTTP success code");
            Document<Service> doc = resp.getDocument();
            Service sd = doc.getRoot();
            ServiceDocument ssd = new ServiceDocument(sd);
            return ssd;
        }

        // if we don't get anything respond with null
        log.warn("Unable to retrieve service document from " + url.toString() + "; responded with " + resp.getStatus()
                + ". Possible problem with SWORD server, or URL");
        return null;
    }

    public CollectionEntries listCollection(SWORDCollection collection)
            throws SWORDClientException, ProtocolViolationException
    {
        return this.listCollection(collection.getHref().toString(), null);
    }

    public CollectionEntries listCollection(String colURL)
            throws SWORDClientException, ProtocolViolationException
    {
        return this.listCollection(colURL, null);
    }

    public CollectionEntries listCollection(SWORDCollection collection, AuthCredentials auth)
            throws SWORDClientException, ProtocolViolationException
    {
        return this.listCollection(collection.getHref().toString(), auth);
    }

    public CollectionEntries listCollection(String colURL, AuthCredentials auth)
            throws SWORDClientException, ProtocolViolationException
    {
        // do some error checking and validation
        if (colURL == null)
        {
            log.error("Null URL passed in to listCollection; returning null");
            return null;
        }
        if (log.isDebugEnabled())
        {
            log.debug("listing collection contents from " + colURL);
        }

        AbderaClient client = new AbderaClient(this.abdera);
        RequestOptions options = this.getDefaultRequestOptions();

        // ensure that the URL is valid
        URL url = this.formaliseURL(colURL);
        if (log.isDebugEnabled())
        {
            log.debug("Formalised Collection URL to " + url.toString());
        }

        // sort out the HTTP basic authentication credentials
        this.prepAuth(auth, client, options);

        // log the request
        this.logGetRequest(auth, colURL, "Collection Entry List");

        // make the request for the service document
        if (log.isDebugEnabled())
        {
           log.debug("Connecting to Server to list contents of Collection " + url.toString() + " ...");
        }
        ClientResponse resp = client.get(url.toString(), options);
        if (log.isDebugEnabled())
        {
            log.debug("Successfully retrieved Collection contents list from " + url.toString());
        }

        // if the response is successful, get the service document out of the response,
        // and wrap it in the SWORD ServiceDocument class
        if (resp.getType() == Response.ResponseType.SUCCESS)
        {
            log.info("Successfully retrieved Collection Entry List from " + url.toString());
            Document<Feed> doc = resp.getDocument();
            Feed feed = doc.getRoot();
            CollectionEntries ce = new CollectionEntries(feed);
            return ce;
        }

        // if we don't get anything respond with null
        log.warn("Unable to retrieve collection entry list from " + url.toString() + "; responded with " + resp.getStatus()
                + ". Possible problem with SWORD server, or URL");
        return null;
    }

    public DepositReceipt deposit(SWORDCollection collection, Deposit deposit)
            throws SWORDClientException, SWORDError, ProtocolViolationException
    {
        return this.deposit(collection, deposit, null);
    }

    public DepositReceipt deposit(String targetURL, Deposit deposit)
            throws SWORDClientException, SWORDError, ProtocolViolationException
    {
        return this.deposit(targetURL, deposit, null);
    }

    public DepositReceipt deposit(SWORDCollection collection, Deposit deposit, AuthCredentials auth)
            throws SWORDClientException, SWORDError, ProtocolViolationException
    {
		if (!collection.allowsMediation() && auth.getOnBehalfOf() != null)
		{
			log.warn("About to attempt to do mediated deposit onto a collection which does not support" +
					" mediation; an error in response is likely.  Provide an AuthCredentials object with " +
					"valid contents on next run");
		}
		if (deposit.getPackaging() != null)
		{
			if (!collection.getAcceptPackaging().contains(deposit.getPackaging()))
			{
				log.warn("About to attempt a deposit of a package format that the collection does not " +
						"support; an error response is likely.  Check the collection object for acceptable " +
						"package formats before next run");
			}
		}
        return this.deposit(collection.getHref().toString(), deposit, auth);
    }

    public DepositReceipt deposit(String collectionURL, Deposit deposit, AuthCredentials auth)
            throws SWORDClientException, SWORDError, ProtocolViolationException
    {
        // some initial error checking and validation
        if (collectionURL == null)
        {
            log.error("Null URL passed into deposit method");
            throw new SWORDClientException("Null URL passed into deposit method");
        }
        if (deposit == null)
        {
            log.error("Null Deposit Object passed into deposit method");
            throw new SWORDClientException("Null Deposit Object passed into deposit method");
        }
        if (log.isDebugEnabled())
        {
            log.debug("beginning deposit on Collection url " + collectionURL);
        }

        AbderaClient client = new AbderaClient(this.abdera);
        RequestOptions options = this.getDefaultRequestOptions();
        HttpHeaders http = new HttpHeaders();

        // ensure that the URL is valid
        URL url = this.formaliseURL(collectionURL);
        if (log.isDebugEnabled())
        {
            log.debug("Formalised Collection URL to " + url.toString());
        }

        // sort out the HTTP basic authentication credentials
        this.prepAuth(auth, client, options);

        // log the request
        this.logDepositRequest(auth, url.toString(), deposit, "Create");

        // prepare the common HTTP headers (other than the auth ones)
        http.addInProgress(options, deposit.isInProgress());
        http.addSlug(options, deposit.getSlug());

        ClientResponse resp;
        if (deposit.isEntryOnly())
        {
            if (log.isDebugEnabled())
            {
                log.debug("Connecting to server to do Entry-Only deposit at url " + url.toString());
            }
            resp = client.post(url.toString(), deposit.getEntryPart().getEntry(), options);
            if (log.isDebugEnabled())
            {
                log.debug("Successfully completed Entry-Only deposit request (doesn't mean the deposit was successful!) on url: " + url.toString());
            }
        }
        else if (deposit.isMultipart())
        {
            if (log.isDebugEnabled())
            {
                log.debug("Connecting to server to do Multipart deposit at url " + url.toString());
            }
            resp = client.execute("POST", url.toString(), new SWORDMultipartRequestEntity(deposit), options);
            // resp = client.post(url.toString(), deposit.getEntryPart().getEntry(), deposit.getFile(), deposit.getMimeType(), options);
            if (log.isDebugEnabled())
            {
                log.debug("Successfully completed Multipart deposit request (doesn't mean the deposit was successful!) on url: " + url.toString());
            }
        }
        else if (deposit.isBinaryOnly())
        {
            // add the headers specific to a binary only deposit
            http.addContentDisposition(options, deposit.getFilename());
            http.addContentMd5(options, deposit.getMd5());
            http.addPackaging(options, deposit.getPackaging());

            // prepare the content to be delivered
            long cl = deposit.getContentLength();
            InputStreamRequestEntity media;
            if (cl > -1)
            {
                media = new InputStreamRequestEntity(deposit.getFile(), cl, deposit.getMimeType());
            }
            else
            {
                media = new InputStreamRequestEntity(deposit.getFile(), deposit.getMimeType());
            }

            // carry out the deposit
            if (log.isDebugEnabled())
            {
                log.debug("Connecting to server to do Binary-Only deposit at url " + url.toString());
            }
            resp = client.post(url.toString(), media, options);
            if (log.isDebugEnabled())
            {
                log.debug("Successfully completed Binary-Only deposit request (doesn't mean the deposit was successful!) on url: " + url.toString());
            }
        }
        else
        {
            log.error("Deposit Object does not have one/both of entry/content set; throwing Exception");
            throw new SWORDClientException("Deposit Object does not have one/both of entry/content set");
        }

        int status = resp.getStatus();
		ResponseCodeManager rcm = new ResponseCodeManager();
		ResponseStatus rs = rcm.depositNew(status);
        if (rs.isCorrect() || rs.isIncorrectButAllowed())
        {
            log.info("Deposit request on " + url.toString() + " returned HTTP status " + status + "; SUCCESS");

			if (rs.isIncorrectButAllowed())
			{
				log.warn("Server responded with status " + status + " which is incorrect.  Attempting to continue " +
						"processing response ...");
			}

            // SUCCESS
			DepositReceipt receipt = this.getDepositReceipt(resp, auth);
            return receipt;
        }
		else if (rs.isIncorrectAndViolation())
		{
			throw new ProtocolViolationException("Server responded with invalid status " + status);
		}
        else if (rs.isError())
        {
            // FIXME: this needs to handle all the other possible response codes
            log.info("Deposit request on " + url.toString() + " returned Error HTTP status " + status);
			ErrorHandler eh = new ErrorHandler();
            throw eh.handleError(resp);
        }
		else
		{
			throw new ProtocolViolationException("Unexpected response code " + status);
		}
    }

	public SwordResponse replaceMedia(DepositReceipt receipt, Deposit deposit, AuthCredentials auth)
			throws SWORDClientException, SWORDError, ProtocolViolationException
	{
		if (receipt.getEditMediaLink() == null)
		{
			receipt = this.getDepositReceipt(receipt.getLocation(), auth);
		}

		String url = null;
		SwordIdentifier eml = receipt.getEditMediaLink();
		if (eml != null)
		{
			url = eml.getHref();
			return this.replaceMedia(url, deposit, auth);
		}
		return null;
	}

	public SwordResponse replaceMedia(String editMediaURL, Deposit deposit, AuthCredentials auth)
			throws SWORDClientException, SWORDError, ProtocolViolationException
	{
		// some initial error checking and validation
        if (editMediaURL == null)
        {
            log.error("Null URL passed into replace method");
            throw new SWORDClientException("Null URL passed into replace method");
        }
        if (deposit == null)
        {
            log.error("Null Deposit Object passed into replace method");
            throw new SWORDClientException("Null Deposit Object passed into replace method");
        }
        if (log.isDebugEnabled())
        {
            log.debug("beginning replace on Edit Media URL " + editMediaURL);
        }

        AbderaClient client = new AbderaClient(this.abdera);
        RequestOptions options = this.getDefaultRequestOptions();
        HttpHeaders http = new HttpHeaders();

        // ensure that the URL is valid
        URL url = this.formaliseURL(editMediaURL);
        if (log.isDebugEnabled())
        {
            log.debug("Formalised Edit-Media URL to " + url.toString());
        }

        // sort out the HTTP basic authentication credentials
        this.prepAuth(auth, client, options);

        // log the request
        this.logDepositRequest(auth, url.toString(), deposit, "Replace Media");

		if (!deposit.isBinaryOnly())
		{
			log.error("replaceMedia request does not take an entry-only or multipart request");
			throw new ProtocolViolationException("replaceMedia request does not take an entry-only or multipart request");
		}

		// add the headers specific to a binary only deposit
		http.addContentDisposition(options, deposit.getFilename());
		http.addContentMd5(options, deposit.getMd5());
		http.addPackaging(options, deposit.getPackaging());
		http.addMetadataRelevant(options, deposit.isMetadataRelevant());

		// prepare the content to be delivered
        long cl = deposit.getContentLength();
        InputStreamRequestEntity media;
        if (cl > -1)
        {
            media = new InputStreamRequestEntity(deposit.getFile(), cl, deposit.getMimeType());
        }
        else
        {
            media = new InputStreamRequestEntity(deposit.getFile(), deposit.getMimeType());
        }

		// carry out the deposit
		if (log.isDebugEnabled())
		{
			log.debug("Connecting to server to do Media replace on url " + url.toString());
		}
		ClientResponse resp = client.put(url.toString(), media, options);
		if (log.isDebugEnabled())
		{
			log.debug("Successfully completed Media replace request (doesn't mean the replace was successful!) on url: " + url.toString());
		}

		int status = resp.getStatus();
		ResponseCodeManager rcm = new ResponseCodeManager();
		ResponseStatus rs = rcm.replaceMedia(status);
        if (rs.isCorrect() || rs.isIncorrectButAllowed())
        {
            log.info("Replace request on " + url.toString() + " returned HTTP status " + status + "; SUCCESS");

			if (rs.isIncorrectButAllowed())
			{
				log.warn("Server responded with status " + status + " which is incorrect.  Attempting to continue " +
						"processing response ...");
			}

            // SUCCESS
			// we just return an empty response and the location
            String location = http.getLocation(resp);
			SwordResponse receipt = new SwordResponse(status);
            receipt.setLocation(location);
            return receipt;
        }
		else if (rs.isIncorrectAndViolation())
		{
			throw new ProtocolViolationException("Server responded with invalid status " + status);
		}
        else if (rs.isError())
        {
            // FIXME: this needs to handle all the other possible response codes
            log.info("Replace request on " + url.toString() + " returned Error HTTP status " + status);
			ErrorHandler eh = new ErrorHandler();
            throw eh.handleError(resp);
        }
		else
		{
			throw new ProtocolViolationException("Unexpected response code " + status);
		}
	}

	public SwordResponse replace(DepositReceipt receipt, Deposit deposit, AuthCredentials auth)
			 throws SWORDClientException, SWORDError, ProtocolViolationException
	{
		if (receipt.getEditLink() == null)
		{
			receipt = this.getDepositReceipt(receipt.getLocation(), auth);
		}
		return this.replace(receipt.getEditLink().getHref(), deposit, auth);
	}

	public SwordResponse replace(String editURL, Deposit deposit, AuthCredentials auth)
            throws SWORDClientException, SWORDError, ProtocolViolationException
    {
        // some initial error checking and validation
        if (editURL == null)
        {
            log.error("Null URL passed into replace method");
            throw new SWORDClientException("Null URL passed into replace method");
        }
        if (deposit == null)
        {
            log.error("Null Deposit Object passed into replace method");
            throw new SWORDClientException("Null Deposit Object passed into replace method");
        }
        if (log.isDebugEnabled())
        {
            log.debug("beginning deposit on Edit url " + editURL);
        }

        AbderaClient client = new AbderaClient(this.abdera);
        RequestOptions options = this.getDefaultRequestOptions();
        HttpHeaders http = new HttpHeaders();

        // ensure that the URL is valid
        URL url = this.formaliseURL(editURL);
        if (log.isDebugEnabled())
        {
            log.debug("Formalised Edit URL to " + url.toString());
        }

        // sort out the HTTP basic authentication credentials
        this.prepAuth(auth, client, options);

        // log the request
        this.logDepositRequest(auth, url.toString(), deposit, "Replace");

        // prepare the common HTTP headers (other than the auth ones)
        http.addInProgress(options, deposit.isInProgress());

        ClientResponse resp;
        if (deposit.isEntryOnly())
        {
            if (log.isDebugEnabled())
            {
                log.debug("Connecting to server to do Entry-Only replace at url " + url.toString());
            }
            resp = client.put(url.toString(), deposit.getEntryPart().getEntry(), options);
            if (log.isDebugEnabled())
            {
                log.debug("Successfully completed Entry-Only replace request (doesn't mean the replace was successful!) on url: " + url.toString());
            }
        }
        else if (deposit.isMultipart())
        {

            if (log.isDebugEnabled())
            {
                log.debug("Connecting to server to do Multipart replace at url " + url.toString());
            }
            resp = client.execute("POST", url.toString(), new SWORDMultipartRequestEntity(deposit), options);
            if (log.isDebugEnabled())
            {
                log.debug("Successfully completed Multipart replace request (doesn't mean the replace was successful!) on url: " + url.toString());
            }
        }
        else
        {
            log.error("Deposit Object does not have one/both of entry/content set; throwing Exception");
            throw new SWORDClientException("Deposit Object does not have one/both of entry/content set");
        }

		int status = resp.getStatus();
		ResponseCodeManager rcm = new ResponseCodeManager();
		ResponseStatus rs = rcm.replace(status);
        if (rs.isCorrect() || rs.isIncorrectButAllowed())
        {
            log.info("Replace request on " + url.toString() + " returned HTTP status " + status + "; SUCCESS");

			if (rs.isIncorrectButAllowed())
			{
				log.warn("Server responded with status " + status + " which is incorrect.  Attempting to continue " +
						"processing response ...");
			}

            // SUCCESS
			// we just return an empty response
			SwordResponse receipt = new SwordResponse(status);
            return receipt;
        }
		else if (rs.isIncorrectAndViolation())
		{
			throw new ProtocolViolationException("Server responded with invalid status " + status);
		}
        else if (rs.isError())
        {
            // FIXME: this needs to handle all the other possible response codes
            log.info("Replace request on " + url.toString() + " returned Error HTTP status " + status);
			ErrorHandler eh = new ErrorHandler();
            throw eh.handleError(resp);
        }
		else
		{
			throw new ProtocolViolationException("Unexpected response code " + status);
		}
    }

	public SwordResponse deleteContent(DepositReceipt receipt, AuthCredentials auth)
			throws SWORDClientException, SWORDError, ProtocolViolationException
	{
		if (receipt.getEditMediaLink() == null)
		{
			receipt = this.getDepositReceipt(receipt.getLocation(), auth);
		}
		return this.delete(receipt.getEditMediaLink().getHref(), auth);
	}

	public SwordResponse deleteContainer(DepositReceipt receipt, AuthCredentials auth)
			throws SWORDClientException, SWORDError, ProtocolViolationException
	{
		if (receipt.getEditLink() == null)
		{
			receipt = this.getDepositReceipt(receipt.getLocation(), auth);
		}
		return this.delete(receipt.getEditLink().getHref(), auth);
	}

    public SwordResponse delete(String deleteUrl, AuthCredentials auth)
            throws SWORDClientException, SWORDError, ProtocolViolationException
    {
        // some initial error checking and validation
        if (deleteUrl == null)
        {
            log.error("Null URL passed into delete method");
            throw new SWORDClientException("Null URL passed into delete method");
        }
        if (log.isDebugEnabled())
        {
            log.debug("beginning delete on url " + deleteUrl);
        }

		AbderaClient client = new AbderaClient(this.abdera);
        RequestOptions options = this.getDefaultRequestOptions();
        HttpHeaders http = new HttpHeaders();

		// ensure that the URL is valid
        URL url = this.formaliseURL(deleteUrl);
        if (log.isDebugEnabled())
        {
            log.debug("Formalised URL to " + url.toString());
        }

        // sort out the HTTP basic authentication credentials
        this.prepAuth(auth, client, options);

        // log the request
        this.logDeleteRequest(auth, url.toString());

		ClientResponse resp = client.delete(url.toString(), options);

		int status = resp.getStatus();
		ResponseCodeManager rcm = new ResponseCodeManager();
		ResponseStatus rs = rcm.delete(status);
        if (rs.isCorrect() || rs.isIncorrectButAllowed())
        {
            log.info("Delete request on " + url.toString() + " returned HTTP status " + status + "; SUCCESS");

			if (rs.isIncorrectButAllowed())
			{
				log.warn("Server responded with status " + status + " which is incorrect.  Attempting to continue " +
						"processing response ...");
			}

            // SUCCESS
			// we just return an empty response
			SwordResponse receipt = new SwordResponse(status);
            return receipt;
        }
		else if (rs.isIncorrectAndViolation())
		{
			throw new ProtocolViolationException("Server responded with invalid status " + status);
		}
        else if (rs.isError())
        {
            // FIXME: this needs to handle all the other possible response codes
            log.info("Replace request on " + url.toString() + " returned Error HTTP status " + status);
			ErrorHandler eh = new ErrorHandler();
            throw eh.handleError(resp);
        }
		else
		{
			throw new ProtocolViolationException("Unexpected response code " + status);
		}
    }

	public SwordResponse addToMediaResource(DepositReceipt receipt, Deposit deposit, AuthCredentials auth)
			throws SWORDClientException, SWORDError, ProtocolViolationException
	{
		if (receipt.getEditMediaLink() == null)
		{
			receipt = this.getDepositReceipt(receipt.getLocation(), auth);
		}
		return this.addToMediaResource(receipt.getEditMediaLink().getHref(), deposit, auth);
	}

	public SwordResponse addToMediaResource(String editMediaURL, Deposit deposit, AuthCredentials auth)
			throws SWORDClientException, SWORDError, ProtocolViolationException
	{
		// some initial error checking and validation
        if (editMediaURL == null)
        {
            log.error("Null URL passed into addToMediaResource method");
            throw new SWORDClientException("Null URL passed into addToMediaResource method");
        }
        if (deposit == null)
        {
            log.error("Null Deposit Object passed into addToMediaResource method");
            throw new SWORDClientException("Null Deposit Object passed into addToMediaResource method");
        }
        if (log.isDebugEnabled())
        {
            log.debug("beginning add on edit-media url " + editMediaURL);
        }

        AbderaClient client = new AbderaClient(this.abdera);
        RequestOptions options = this.getDefaultRequestOptions();
        HttpHeaders http = new HttpHeaders();

        // ensure that the URL is valid
        URL url = this.formaliseURL(editMediaURL);
        if (log.isDebugEnabled())
        {
            log.debug("Formalised Edit-Media URL to " + url.toString());
        }

        // sort out the HTTP basic authentication credentials
        this.prepAuth(auth, client, options);

        // log the request
        this.logDepositRequest(auth, url.toString(), deposit, "Add To Media Resource");

        // prepare the HTTP headers (other than the auth ones)
		http.addContentDisposition(options, deposit.getFilename());
		http.addContentMd5(options, deposit.getMd5());
		http.addMetadataRelevant(options, deposit.isMetadataRelevant());
		http.addPackaging(options, deposit.getPackaging());

		// prepare the content to be delivered
        long cl = deposit.getContentLength();
        InputStreamRequestEntity media;
        if (cl > -1)
        {
            media = new InputStreamRequestEntity(deposit.getFile(), cl, deposit.getMimeType());
        }
        else
        {
            media = new InputStreamRequestEntity(deposit.getFile(), deposit.getMimeType());
        }

		// carry out the deposit
		if (log.isDebugEnabled())
		{
			log.debug("Connecting to server to do add at url " + url.toString());
		}
		ClientResponse resp = client.post(url.toString(), media, options);
		if (log.isDebugEnabled())
		{
			log.debug("Successfully completed Binary-Only add request (doesn't mean the add was successful!) on url: " + url.toString());
		}

		// the response to a deposit on the Media Resource can be anything, although it is RECOMMENDED to be a
		// Deposit Receipt.  Here we try to get a Deposit Receipt if possible
		int status = resp.getStatus();
		ResponseCodeManager rcm = new ResponseCodeManager();
		ResponseStatus rs = rcm.addToMediaResource(status);
        if (rs.isCorrect() || rs.isIncorrectButAllowed())
        {
            log.info("Add request on " + url.toString() + " returned HTTP status " + status + "; SUCCESS");

			if (rs.isIncorrectButAllowed())
			{
				log.warn("Server responded with status " + status + " which is incorrect.  Attempting to continue " +
						"processing response ...");
			}

			SwordResponse receipt = null;
			String location = http.getLocation(resp);

            // we don't know what is returned, so we just sidestep the whole issue and give
			// the client an inputstream
			try
			{
				InputStream is = resp.getInputStream();
				if (is == null)
				{
					receipt = new SwordResponse(status, location);
				}
				else
				{
					receipt = new BinaryResponse(status, location, is);
				}

				return receipt;
			}
			catch (IOException e)
			{
				throw new SWORDClientException(e);
			}
        }
		else if (rs.isIncorrectAndViolation())
		{
			throw new ProtocolViolationException("Server responded with invalid status " + status);
		}
        else if (rs.isError())
        {
            // FIXME: this needs to handle all the other possible response codes
            log.info("Add request on " + url.toString() + " returned Error HTTP status " + status);
			ErrorHandler eh = new ErrorHandler();
            throw eh.handleError(resp);
        }
		else
		{
			throw new ProtocolViolationException("Unexpected response code " + status);
		}
	}

	public DepositReceipt addToContainer(DepositReceipt receipt, Deposit deposit, AuthCredentials auth)
			throws SWORDClientException, SWORDError, ProtocolViolationException
	{
		if (receipt.getEditLink() == null)
		{
			receipt = this.getDepositReceipt(receipt.getLocation(), auth);
		}
		return this.addToContainer(receipt.getEditLink().getHref(), deposit, auth);
	}

	public DepositReceipt addToContainer(String editURL, Deposit deposit, AuthCredentials auth)
			throws SWORDClientException, SWORDError, ProtocolViolationException
	{
		// some initial error checking and validation
        if (editURL == null)
        {
            log.error("Null URL passed into addToContainer method");
            throw new SWORDClientException("Null URL passed into addToContainer method");
        }
        if (deposit == null)
        {
            log.error("Null Deposit Object passed into addToContainer method");
            throw new SWORDClientException("Null Deposit Object passed into addToContainer method");
        }
        if (log.isDebugEnabled())
        {
            log.debug("beginning deposit on Edit url " + editURL);
        }

        AbderaClient client = new AbderaClient(this.abdera);
        RequestOptions options = this.getDefaultRequestOptions();
        HttpHeaders http = new HttpHeaders();

        // ensure that the URL is valid
        URL url = this.formaliseURL(editURL);
        if (log.isDebugEnabled())
        {
            log.debug("Formalised Edit URL to " + url.toString());
        }

        // sort out the HTTP basic authentication credentials
        this.prepAuth(auth, client, options);

        // log the request
        this.logDepositRequest(auth, url.toString(), deposit, "Add To Container");

        // prepare the common HTTP headers (other than the auth ones)
        http.addInProgress(options, deposit.isInProgress());

        ClientResponse resp;
        if (deposit.isEntryOnly())
        {
            if (log.isDebugEnabled())
            {
                log.debug("Connecting to server to do Entry-Only add at url " + url.toString());
            }
            resp = client.post(url.toString(), deposit.getEntryPart().getEntry(), options);
            if (log.isDebugEnabled())
            {
                log.debug("Successfully completed Entry-Only add request (doesn't mean the add was successful!) on url: " + url.toString());
            }
        }
        else if (deposit.isMultipart())
        {
            if (log.isDebugEnabled())
            {
                log.debug("Connecting to server to do Multipart add at url " + url.toString());
            }
            resp = client.execute("POST", url.toString(), new SWORDMultipartRequestEntity(deposit), options);
            // resp = client.post(url.toString(), deposit.getEntryPart().getEntry(), deposit.getFile(), deposit.getMimeType(), options);
            if (log.isDebugEnabled())
            {
                log.debug("Successfully completed Multipart add request (doesn't mean the add was successful!) on url: " + url.toString());
            }
        }
        else if (deposit.isBinaryOnly())
        {
            // add the headers specific to a binary only deposit
            http.addContentDisposition(options, deposit.getFilename());
            http.addContentMd5(options, deposit.getMd5());
            http.addPackaging(options, deposit.getPackaging());

            // prepare the content to be delivered
            long cl = deposit.getContentLength();
            InputStreamRequestEntity media;
            if (cl > -1)
            {
                media = new InputStreamRequestEntity(deposit.getFile(), cl, deposit.getMimeType());
            }
            else
            {
                media = new InputStreamRequestEntity(deposit.getFile(), deposit.getMimeType());
            }

            // carry out the deposit
            if (log.isDebugEnabled())
            {
                log.debug("Connecting to server to do Binary-Only add at url " + url.toString());
            }
            resp = client.post(url.toString(), media, options);
            if (log.isDebugEnabled())
            {
                log.debug("Successfully completed Binary-Only add request (doesn't mean the add was successful!) on url: " + url.toString());
            }
        }
        else
        {
            log.error("Deposit Object does not have one/both of entry/content set; throwing Exception");
            throw new SWORDClientException("Deposit Object does not have one/both of entry/content set");
        }

		int status = resp.getStatus();
		ResponseCodeManager rcm = new ResponseCodeManager();
		ResponseStatus rs = rcm.addToContainer(status);
        if (rs.isCorrect() || rs.isIncorrectButAllowed())
        {
            log.info("Deposit request on " + url.toString() + " returned HTTP status " + status + "; SUCCESS");

			if (rs.isIncorrectButAllowed())
			{
				log.warn("Server responded with status " + status + " which is incorrect.  Attempting to continue " +
						"processing response ...");
			}

            // SUCCESS
            DepositReceipt receipt = null;
            Document<Entry> doc = resp.getDocument();
            String location = http.getLocation(resp);

            // it is possible that the doc will be null
			// it is also possible that the location will be null

            // if there is no doc, we need to see what the configuration asks us to do about getting it
            if (doc == null && this.config.returnDepositReceipt())
            {
                receipt = this.getDepositReceipt(location, auth);
            }
            else if (doc != null)
            {
                Entry entry = doc.getRoot();
                receipt = new DepositReceipt(status, location, entry);
            }

            return receipt; // which may be null
        }
		else if (rs.isIncorrectAndViolation())
		{
			throw new ProtocolViolationException("Server responded with invalid status " + status);
		}
        else if (rs.isError())
        {
            // FIXME: this needs to handle all the other possible response codes
            log.info("Replace request on " + url.toString() + " returned Error HTTP status " + status);
			ErrorHandler eh = new ErrorHandler();
            throw eh.handleError(resp);
        }
		else
		{
			throw new ProtocolViolationException("Unexpected response code " + status);
		}
	}

	// These methods are syntactic sugar for speaking to individual files if such an operation
	// is supported by the server.  They re-use existing calls (e.g. to getContent or replaceMedia)
	// but with the URLs of the file resource rather than those of the standard sword urls
	// (see sections 6.10 and 6.11 of the spec)

	public Content getFile(String url, String mimeType, AuthCredentials auth)
			throws SWORDClientException, ProtocolViolationException, SWORDError
	{
		return this.getContent(url, mimeType, null, auth);
	}

	public SwordResponse replaceFile(String url, Deposit deposit, AuthCredentials auth)
			throws SWORDClientException, ProtocolViolationException, SWORDError
	{
		return this.replaceMedia(url, deposit, auth);
	}

	public SwordResponse deleteFile(String url, AuthCredentials auth)
			throws SWORDClientException, ProtocolViolationException, SWORDError
	{
		return this.delete(url, auth);
	}

	/////////////////////////////////////////////////////////////////////////////////////////

	public DepositReceipt complete(DepositReceipt receipt, AuthCredentials auth)
			throws SWORDClientException, ProtocolViolationException, SWORDError
	{
		if (receipt.getEditLink() == null)
		{
			receipt = this.getDepositReceipt(receipt.getLocation(), auth);
		}

		String url = null;
		SwordIdentifier el = receipt.getEditLink();
		if (el != null)
		{
			url = el.getHref();
			return this.complete(url, auth);
		}
		return null;
	}

	public DepositReceipt complete(String editURL, AuthCredentials auth)
			throws SWORDClientException, ProtocolViolationException, SWORDError
	{
		// some initial error checking and validation
        if (editURL == null)
        {
            log.error("Null URL passed into addToContainer method");
            throw new SWORDClientException("Null URL passed into addToContainer method");
        }
        if (log.isDebugEnabled())
        {
            log.debug("beginning Complete Operation on Edit url " + editURL);
        }

        AbderaClient client = new AbderaClient(this.abdera);
        RequestOptions options = this.getDefaultRequestOptions();
        HttpHeaders http = new HttpHeaders();

        // ensure that the URL is valid
        URL url = this.formaliseURL(editURL);
        if (log.isDebugEnabled())
        {
            log.debug("Formalised Edit URL to " + url.toString());
        }

        // sort out the HTTP basic authentication credentials
        this.prepAuth(auth, client, options);

        // set the in progress header to false
        http.addInProgress(options, false);

		ClientResponse resp = client.post(url.toString(), new EmptyRequestEntity(), options);

		int status = resp.getStatus();
		ResponseCodeManager rcm = new ResponseCodeManager();
		ResponseStatus rs = rcm.complete(status);
        if (rs.isCorrect() || rs.isIncorrectButAllowed())
        {
            log.info("Complete request on " + url.toString() + " returned HTTP status " + status + "; SUCCESS");

			if (rs.isIncorrectButAllowed())
			{
				log.warn("Server responded with status " + status + " which is incorrect.  Attempting to continue " +
						"processing response ...");
			}

            // SUCCESS
            DepositReceipt receipt = null;
            Document<Entry> doc = resp.getDocument();
            String location = http.getLocation(resp);

            // it is possible that the doc will be null
			// it is also possible that the location will be null

            // if there is no doc, we need to see what the configuration asks us to do about getting it
            if (doc == null && this.config.returnDepositReceipt())
            {
                receipt = this.getDepositReceipt(location, auth);
            }
            else if (doc != null)
            {
                Entry entry = doc.getRoot();
                receipt = new DepositReceipt(status, location, entry);
            }

            return receipt; // which may be null
        }
		else if (rs.isIncorrectAndViolation())
		{
			throw new ProtocolViolationException("Server responded with invalid status " + status);
		}
        else if (rs.isError())
        {
            // FIXME: this needs to handle all the other possible response codes
            log.info("Complete request on " + url.toString() + " returned Error HTTP status " + status);
			ErrorHandler eh = new ErrorHandler();
            throw eh.handleError(resp);
        }
		else
		{
			throw new ProtocolViolationException("Unexpected response code " + status);
		}
	}

	public DepositReceipt getDepositReceipt(ClientResponse resp, AuthCredentials auth)
			throws ProtocolViolationException, SWORDClientException, SWORDError
	{
		DepositReceipt receipt;
		HttpHeaders http = new HttpHeaders();

		// we have to do this rather annoying thing to determine if the receipt is included
		boolean receiptIncluded = true;
		Document<Entry> doc = null;
		try
		{
			doc = resp.getDocument();
		}
		catch (ParseException e)
		{
			receiptIncluded = false;
		}

		String location = http.getLocation(resp);
        String contentMD5 = http.getContentMD5(resp);
        Date lastModified = http.getLastModified(resp);
        
		// it is possible that the doc will be null

		// if there is no doc and no location header this is broken
		if (!receiptIncluded && (location == null || "".equals(location)))
		{
			throw new ProtocolViolationException("SWORD Server responded " + resp.getStatus() + " but failed to provide a deposit receipt or a Location header");
		}
		// if there is no doc, we need to see what the configuration asks us to do about getting it
		if (!receiptIncluded && this.config.returnDepositReceipt())
		{
			// load the deposit receipt from the location
			receipt = this.getDepositReceipt(location, auth);
		}
		else if (!receiptIncluded)
		{
			receipt = new DepositReceipt(resp.getStatus(), location);
            if (contentMD5 != null)
            {
                receipt.setContentMD5(contentMD5);
            }
            if (lastModified != null)
            {
                receipt.setLastModified(lastModified);
            }
		}
		else
		{
			Entry entry = doc.getRoot();
			receipt = new DepositReceipt(resp.getStatus(), location, entry);
            if (contentMD5 != null)
            {
                receipt.setContentMD5(contentMD5);
            }
            if (lastModified != null)
            {
                receipt.setLastModified(lastModified);
            }
		}

		return receipt;
	}

	public DepositReceipt getDepositReceipt(String editURL, AuthCredentials auth)
            throws SWORDClientException, ProtocolViolationException, SWORDError
    {
        // some initial error checking and validation
        if (editURL == null)
        {
            log.error("Null URL passed into getDepositReceipt method");
            throw new SWORDClientException("Null URL passed into getDepositReceipt method");
        }
        if (log.isDebugEnabled())
        {
            log.debug("beginning retrieve on entry document/deposit receipt url " + editURL);
        }

		AbderaClient client = new AbderaClient(this.abdera);
		RequestOptions options = this.getDefaultRequestOptions();
		HttpHeaders http = new HttpHeaders();



		// ensure that the URL is valid
		URL url = this.formaliseURL(editURL);
		if (log.isDebugEnabled())
		{
			log.debug("Formalised Edit URL to " + url.toString());
		}

		// sort out the HTTP basic authentication credentials
		this.prepAuth(auth, client, options);

		ClientResponse resp = client.get(url.toString(), options);
        String contentMD5 = http.getContentMD5(resp);
        Date lastModified = http.getLastModified(resp);

		int status = resp.getStatus();
		ResponseCodeManager rcm = new ResponseCodeManager();
		ResponseStatus rs = rcm.getDepositReceipt(status);
        if (rs.isCorrect() || rs.isIncorrectButAllowed())
        {
            log.info("Get request on " + url.toString() + " returned HTTP status " + status + "; SUCCESS");

			if (rs.isIncorrectButAllowed())
			{
				log.warn("Server responded with status " + status + " which is incorrect.  Attempting to continue " +
						"processing response ...");
			}

            // SUCCESS
			Document<Entry> doc = resp.getDocument();
			Entry entry = doc.getRoot();
			DepositReceipt receipt = new DepositReceipt(resp.getStatus(), editURL, entry);
            if (contentMD5 != null)
            {
                receipt.setContentMD5(contentMD5);
            }
            if (lastModified != null)
            {
                receipt.setLastModified(lastModified);
            }
            return receipt;
        }
		else if (rs.isIncorrectAndViolation())
		{
			throw new ProtocolViolationException("Server responded with invalid status " + status);
		}
        else if (rs.isError())
        {
            // FIXME: this needs to handle all the other possible response codes
            log.info("Deposit Receipt request on " + url.toString() + " returned Error HTTP status " + status);
			ErrorHandler eh = new ErrorHandler();
            throw eh.handleError(resp);
        }
		else
		{
			throw new ProtocolViolationException("Unexpected response code " + status);
		}
    }

	public Statement getStatement(DepositReceipt receipt, String type, AuthCredentials auth)
			throws SWORDClientException, StatementParseException, ProtocolViolationException, SWORDError
	{
		if (receipt.getStatementLink(type) == null)
		{
			receipt = this.getDepositReceipt(receipt.getLocation(), auth);
		}

		SwordIdentifier si = receipt.getStatementLink(type);
		if (si == null)
		{
			return null;
		}
		Statement shell = StatementFactory.getStatementShell(type);
		return this.getStatement(si.getHref(), shell, auth);
	}

    public Statement getStatement(String statementUrl, Statement statementShell, AuthCredentials auth)
            throws SWORDClientException, StatementParseException, ProtocolViolationException, SWORDError
    {
        // some initial error checking and validation
        if (statementUrl == null)
        {
            log.error("Null URL passed into getStatement method");
            throw new SWORDClientException("Null URL passed into getStatement method");
        }
        if (log.isDebugEnabled())
        {
            log.debug("beginning retrieve on statement url " + statementUrl);
        }

		AbderaClient client = new AbderaClient(this.abdera);
		RequestOptions options = this.getDefaultRequestOptions();
		HttpHeaders http = new HttpHeaders();

		// we add accept headers in case content negotiation is required
		http.addAccept(options, statementShell.getMimeType());

		// ensure that the URL is valid
		URL url = this.formaliseURL(statementUrl);
		if (log.isDebugEnabled())
		{
			log.debug("Formalised Statement URL to " + url.toString());
		}

		// sort out the HTTP basic authentication credentials
		this.prepAuth(auth, client, options);

		ClientResponse resp = client.get(url.toString(), options);

		int status = resp.getStatus();
		ResponseCodeManager rcm = new ResponseCodeManager();
		ResponseStatus rs = rcm.getStatement(status);
        if (rs.isCorrect() || rs.isIncorrectButAllowed())
        {
            log.info("Get request on " + url.toString() + " returned HTTP status " + status + "; SUCCESS");

			if (rs.isIncorrectButAllowed())
			{
				log.warn("Server responded with status " + status + " which is incorrect.  Attempting to continue " +
						"processing response ...");
			}

            statementShell.parse(resp);
			return statementShell;
        }
		else if (rs.isIncorrectAndViolation())
		{
			throw new ProtocolViolationException("Server responded with invalid status " + status);
		}
        else if (rs.isError())
        {
            // FIXME: this needs to handle all the other possible response codes
            log.info("Statement GET request on " + url.toString() + " returned Error HTTP status " + status);
			ErrorHandler eh = new ErrorHandler();
            throw eh.handleError(resp);
        }
		else
		{
			throw new ProtocolViolationException("Unexpected response code " + status);
		}
    }
	
	public Content getContent(SwordIdentifier contentId)
			throws SWORDClientException, ProtocolViolationException, SWORDError
	{
		return this.getContent(contentId.getHref(), contentId.getType(), UriRegistry.PACKAGE_SIMPLE_ZIP, null);
	}

	public Content getContent(SwordIdentifier contentId, AuthCredentials auth)
			throws SWORDClientException, ProtocolViolationException, SWORDError
	{
		return this.getContent(contentId.getHref(), contentId.getType(), UriRegistry.PACKAGE_SIMPLE_ZIP, auth);
	}

	public Content getContent(SwordIdentifier contentId, String packaging, AuthCredentials auth)
			throws SWORDClientException, ProtocolViolationException, SWORDError
	{
		return this.getContent(contentId.getHref(), contentId.getType(), packaging, auth);
	}

	public Content getContent(String contentURL, String mimeType)
            throws SWORDClientException, ProtocolViolationException, SWORDError
    {
        return this.getContent(contentURL, mimeType, UriRegistry.PACKAGE_SIMPLE_ZIP, null);
    }

    public Content getContent(String contentURL, String mimeType, String packaging, AuthCredentials auth)
            throws SWORDClientException, ProtocolViolationException, SWORDError
    {
        // some initial error checking and validation
        if (contentURL == null)
        {
            log.error("Null URL passed into getContent method");
            throw new SWORDClientException("Null URL passed into getContent method");
        }

        AbderaClient client = new AbderaClient(this.abdera);
        RequestOptions options = this.getDefaultRequestOptions();
        HttpHeaders http = new HttpHeaders();

        // ensure that the URL is valid
        URL url = this.formaliseURL(contentURL);
        if (log.isDebugEnabled())
        {
            log.debug("Formalised Content URL to " + url.toString());
        }

        // sort out the HTTP basic authentication credentials
        this.prepAuth(auth, client, options);

        // log the request
        this.logGetRequest(auth, contentURL, "Content");

        // add the Accept-Packaging header
        http.addAcceptPackaging(options, packaging);
        http.addAccept(options, mimeType);

        // make the request for the service document
        if (log.isDebugEnabled())
        {
           log.debug("Connecting to Server to obtain Content " + url.toString() + " ...");
        }
        ClientResponse resp = client.get(url.toString(), options);
        if (log.isDebugEnabled())
        {
            log.debug("Successfully retrieved response from " + url.toString());
        }

        // if the response is successful, get the service document out of the response,
        // and wrap it in the SWORD ServiceDocument class
		int status = resp.getStatus();
		ResponseCodeManager rcm = new ResponseCodeManager();
		ResponseStatus rs = rcm.getContent(status);
        if (rs.isCorrect() || rs.isIncorrectButAllowed())
        {
            log.info("Successfully retrieved Content from " + url.toString());

			if (rs.isIncorrectButAllowed())
			{
				log.warn("Server responded with status " + status + " which is incorrect.  Attempting to continue " +
						"processing response ...");
			}

            String responsePackaging = http.getPackaging(resp);
			MimeType responseType = resp.getContentType();
			Content content = new Content();
            content.setMimeType(responseType);

			// is this a feed?
			MimeType feedType = null;
			try
			{
				feedType = new MimeType("application/atom+xml;type=feed");
			}
			catch (MimeTypeParseException e)
			{
				throw new SWORDClientException(e);
			}
			if (responseType != null && feedType.toString().equals(responseType.toString())) // urgh, but such is life
            {
                log.info("Content retrieved from " + url.toString() + " is an Atom Feed");
                Document<Feed> doc = resp.getDocument();
                Feed feed = doc.getRoot();
                content.setFeed(feed);
                return content;
            }
            else
            {
                if (responsePackaging == null || !"".equals(responsePackaging))
                {
                    if (log.isDebugEnabled())
                    {
                        log.debug("Content retrieved from " + url.toString() + " provided no Packaging header; using default");
                    }
                    responsePackaging = UriRegistry.PACKAGE_SIMPLE_ZIP;
                }
                content.setPackaging(responsePackaging);
                try
                {
                    content.setInputStream(resp.getInputStream());
                }
                catch (IOException e)
                {
                    log.error("Error reading input stream from response", e);
                    throw new SWORDClientException(e);
                }
                return content;
            }
        }
		else if (rs.isIncorrectAndViolation())
		{
			throw new ProtocolViolationException("Server responded with invalid status " + status);
		}
        else if (rs.isError())
        {
            log.info("Content retrieve request on " + url.toString() + " returned Error HTTP status " + status);
			ErrorHandler eh = new ErrorHandler();
            throw eh.handleError(resp);
        }
		else
		{
			throw new ProtocolViolationException("Unexpected response code " + status);
		}
    }

    private URL formaliseURL(String url)
            throws SWORDClientException
    {
        try
        {
            URL nurl = new URL(url);
            return nurl;
        }
        catch (MalformedURLException e)
        {
            // No dice, can't even form base URL...
            throw new SWORDClientException(url + " is not a valid URL ("
                    + e.getMessage()
                    + ")");
        }
    }

    private void prepAuth(AuthCredentials auth, AbderaClient client, RequestOptions options)
            throws SWORDClientException
    {
        client.usePreemptiveAuthentication(true);
        HttpHeaders http = new HttpHeaders();

        // sort out the HTTP basic authentication credentials
        if (auth != null)
        {
            // if there's a username and password pair, then set up the authentication credentials
            if (auth.getUsername() != null && auth.getPassword() != null)
            {
                if (log.isDebugEnabled())
                {
                    log.debug("Setting username/password: " + auth.getUsername() + "/****password omitted *****");
                }
                UsernamePasswordCredentials unpw = new UsernamePasswordCredentials(auth.getUsername(), auth.getPassword());

                // create the credentials - target and realm can be null (and are so by default)
                try
                {
                    client.addCredentials(auth.getTarget(), auth.getRealm(), "basic", unpw);
                }
                catch (URISyntaxException e)
                {
                    log.error("Unable to parse authentication target in AuthCredential", e);
                    throw new SWORDClientException("Unable to parse authentication target in AuthCredentials", e);
                }
            }

            // add the on-behalf-of header if required
            if (auth.getOnBehalfOf() != null)
            {
                if (log.isDebugEnabled())
                {
                    log.debug("Setting On-Behalf-Of header: " + auth.getOnBehalfOf());
                }
                http.addOnBehalfOf(options, auth.getOnBehalfOf());
            }
        }
    }

    private RequestOptions getDefaultRequestOptions()
    {
        RequestOptions options = new RequestOptions();
        options.setHeader("User-Agent", this.config.getUserAgent());
        return options;
    }

    private void logGetRequest(AuthCredentials auth, String url, String type)
    {
        // log the request
        String unLog = "";
        String oboLog = "";
        if (auth != null)
        {
            unLog = auth.getUsername() == null ? "" : " with username " + auth.getUsername();
            oboLog = auth.getOnBehalfOf() == null ? "" : " On-Behalf-Of " + auth.getOnBehalfOf();
        }
        log.info("Requesting " + type + " from " + url + unLog + oboLog);
    }

    private void logDepositRequest(AuthCredentials auth, String url, Deposit deposit, String operation)
    {
        // log the request

        // create the auth part of the log message
        String unLog = "";
        String oboLog = "";
        if (auth != null)
        {
            unLog = auth.getUsername() == null ? "" : " with username " + auth.getUsername();
            oboLog = auth.getOnBehalfOf() == null ? "" : " On-Behalf-Of " + auth.getOnBehalfOf();
        }

        // create the deposit object part of the log message
        String depositLog = "";
        if (deposit.isBinaryOnly())
        {
            String fn = " filename=" + deposit.getFilename();
            String md5 = deposit.getMd5() != null ? " md5=" + deposit.getMd5() : "";
            String packaging = deposit.getPackaging() != null ? " packaging=" + deposit.getPackaging() : "";
            String mimeType = " mimetype=" + deposit.getMimeType();
            String slug = deposit.getSlug() != null ? " slug=" + deposit.getSlug() : "";
            depositLog = " Binary Only deposit;" + fn + md5 + packaging + mimeType + slug;
        }
        else if (deposit.isEntryOnly())
        {
            String slug = deposit.getSlug() != null ? " slug=" + deposit.getSlug() : "";
            depositLog = " Entry Only deposit;" + slug;
        }
        else if (deposit.isMultipart())
        {
            String fn = " filename=" + deposit.getFilename();
            String md5 = deposit.getMd5() != null ? " md5=" + deposit.getMd5() : "";
            String packaging = deposit.getPackaging() != null ? " packaging=" + deposit.getPackaging() : "";
            String mimeType = " mimetype=" + deposit.getMimeType();
            String slug = deposit.getSlug() != null ? " slug=" + deposit.getSlug() : "";
            depositLog = " Multipart deposit;" + fn + md5 + packaging + mimeType + slug;
        }

        String inprog = "In-Progress: " + (deposit.isInProgress() ? "true" : "false");
        String suppress = "Metadata-Relevant: " + (deposit.isMetadataRelevant() ? "true" : "false");
        String headerLog = " Addition HTTP headers: " + inprog + "; " + suppress;

        log.info(operation + "Request: " + unLog + oboLog + depositLog + headerLog);
    }

	private void logDeleteRequest(AuthCredentials auth, String url)
    {
        // log the request

        // create the auth part of the log message
        String unLog = "";
        String oboLog = "";
        if (auth != null)
        {
            unLog = auth.getUsername() == null ? "" : " with username " + auth.getUsername();
            oboLog = auth.getOnBehalfOf() == null ? "" : " On-Behalf-Of " + auth.getOnBehalfOf();
        }

        log.info("Delete Request: " + unLog + oboLog + ";  url: " + url);
    }
}
