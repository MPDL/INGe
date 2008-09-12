package de.mpg.escidoc.services.cone.journalnames;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class JournalNameAR implements Comparable, Serializable
{
    private static final long serialVersionUID = 1L;
    
    private String id = null;
    
    private String identifier = null;
    private String title = null;
    private List<String> altTitles = null;
    private String publisher = null;
    private String subject = null;
    private String rights = null;
    
    ResourceBundle mts = ResourceBundle.getBundle("properties.mulgara");

    public JournalNameAR()
    {
    }
    
    public JournalNameAR(String id)
    {
        this.id = id;
    }

    public JournalNameAR(String id, String identifier, String publisher, String rights, String subject, String title,
            List<String> altTitles)
    {
        this.id = id;
        this.identifier = identifier;
        this.publisher = publisher;
        this.rights = rights;
        this.subject = subject;
        this.title = title;
        this.altTitles = altTitles;
    }

    public List<String> getAltTitles()
    {
        return altTitles;
    }

    public void setAltTitles(List<String> altTitles)
    {
        this.altTitles = altTitles;
    }

    public String getIdentifier()
    {
        return identifier;
    }

    public void setIdentifier(String identifier)
    {
        this.identifier = identifier;
    }

    public String getPublisher()
    {
        return publisher;
    }

    public void setPublisher(String publisher)
    {
        this.publisher = publisher;
    }

    public String getRights()
    {
        return rights;
    }

    public void setRights(String rights)
    {
        this.rights = rights;
    }

    public String getSubject()
    {
        return subject;
    }

    public void setSubject(String subject)
    {
        this.subject = subject;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public int compareTo(Object o)
    {
        JournalNameAR n = (JournalNameAR)o;
        int lastComp = identifier.compareTo(n.identifier);
        return (lastComp);
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public void addProperty(String propertyName, String value)
    {
        System.out.println(propertyName + "::" + value);
        
        propertyName = "<" + propertyName + ">";
        
        if (mts.getString("dc.identifier.urn").equals(propertyName))
        {
            identifier = value;
        }
        else if (mts.getString("dc.title.urn").equals(propertyName))
        {
            title = value;
        }
        else if (mts.getString("dc.publisher.urn").equals(propertyName))
        {
            publisher = value;
        }
        else if (mts.getString("dc.rights.urn").equals(propertyName))
        {
            rights = value;
        }
        else if (mts.getString("dc.subject.urn").equals(propertyName))
        {
            subject = value;
        }
        else if (mts.getString("dc.alternative.urn").equals(propertyName))
        {
            if (altTitles == null)
            {
                altTitles = new ArrayList<String>();
            }
            altTitles.add(value);
        }
        
        System.out.println("Publisher: " + publisher);
    }
}
