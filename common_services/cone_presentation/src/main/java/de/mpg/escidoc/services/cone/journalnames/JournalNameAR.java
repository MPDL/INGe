/*
*
* CDDL HEADER START
*
* The contents of this file are subject to the terms of the
* Common Development and Distribution License, Version 1.0 only
* (the "License"). You may not use this file except in compliance
* with the License.
*
* You can obtain a copy of the license at license/ESCIDOC.LICENSE
* or http://www.escidoc.de/license.
* See the License for the specific language governing permissions
* and limitations under the License.
*
* When distributing Covered Code, include this CDDL HEADER in each
* file and include the License file at license/ESCIDOC.LICENSE.
* If applicable, add the following below this CDDL HEADER, with the
* fields enclosed by brackets "[]" replaced with your own identifying
* information: Portions Copyright [yyyy] [name of copyright owner]
*
* CDDL HEADER END
*/

/*
* Copyright 2006-2009 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/

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
