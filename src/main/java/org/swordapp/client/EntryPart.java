package org.swordapp.client;

import org.apache.abdera.Abdera;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;

import javax.xml.namespace.QName;
import java.util.UUID;

public class EntryPart
{
    private Entry entry;

    public EntryPart()
    {
        Abdera ab = new Abdera();
        this.entry = ab.newEntry();
    }

    public Entry getEntry()
    {
        return entry;
    }

    public void referenceMediaPart(String mimeType)
    {
        String uuid = UUID.randomUUID().toString();
        this.entry.setContent(new IRI("cid:" + uuid), mimeType);
    }

    public Element addDublinCore(String field, String value)
    {
        QName term = new QName(UriRegistry.DC_NAMESPACE, field);
        return this.entry.addSimpleExtension(term, value);
    }

    public Element addSimpleExtension(QName qName, String s)
    {
        return this.entry.addSimpleExtension(qName, s);
    }
}
