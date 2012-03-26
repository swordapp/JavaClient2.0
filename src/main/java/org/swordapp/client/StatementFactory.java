package org.swordapp.client;

public class StatementFactory
{
	public static Statement getStatementShell(String type)
	{
		if ("application/rdf+xml".equals(type))
		{
			return new OreStatement();
		}
		else if ("application/atom+xml".equals(type) || "application/atom+xml;type=feed".equals(type))
		{
			return new AtomStatement();
		}
		return null;
	}
}
