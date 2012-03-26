package org.swordapp.client;

import java.util.HashMap;
import java.util.Map;

public class Endpoints
{
	private String serviceDocument = null;
	private String collection = null;
	private String edit = null;
	private Map<String, String> statements = new HashMap<String, String>();

	public Endpoints() {}

	public Endpoints(String serviceDocument, String collection, String edit, Map<String, String> statements)
	{
		this.serviceDocument = serviceDocument;
		this.collection = collection;
		this.edit = edit;
		this.statements = statements;
	}

	public String getServiceDocument()
	{
		return serviceDocument;
	}

	public void setServiceDocument(String serviceDocument)
	{
		this.serviceDocument = serviceDocument;
	}

	public String getCollection()
	{
		return collection;
	}

	public void setCollection(String collection)
	{
		this.collection = collection;
	}

	public String getEdit()
	{
		return edit;
	}

	public void setEdit(String edit)
	{
		this.edit = edit;
	}

	public Map<String, String> getStatements()
	{
		if (statements.size() == 0)
		{
			return null;
		}
		return statements;
	}

	public void setStatements(Map<String, String> statements)
	{
		this.statements = statements;
	}

	public void addStatement(String url, String type)
	{
		this.statements.put(url, type);
	}
}
