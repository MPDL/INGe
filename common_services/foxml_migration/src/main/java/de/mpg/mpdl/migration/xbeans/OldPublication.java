package de.mpg.mpdl.migration.xbeans;

import org.apache.xmlbeans.XmlException;
import org.purl.dc.elements.x11.SimpleLiteral;

import de.mpg.escidoc.metadataprofile.schema.x01.PublicationDocument;
import de.mpg.escidoc.metadataprofile.schema.x01.organization.OrganizationDetailsType;
import de.mpg.escidoc.metadataprofile.schema.x01.types.CreatorType;
import de.mpg.escidoc.metadataprofile.schema.x01.types.PersonType;

public class OldPublication
{
    PublicationDocument oldPubDoc = null;
    public OldPublication(String xml)
    {
        try
        {
            oldPubDoc = PublicationDocument.Factory.parse(xml);
        }
        catch (XmlException e)
        {
            e.printStackTrace();
        }
    }
    
    public String getPublicationType()
    {
        return oldPubDoc.getPublication().getType().toString();
    }
    
    public void getCreators()
    {
        CreatorType[] creators = oldPubDoc.getPublication().getCreatorArray();
        for (CreatorType ct : creators)
        {
            String role = ct.getRole().toString();
            if (ct.getPerson() != null)
            {
                PersonType person = ct.getPerson();
                String complete = person.getCompleteName().getDomNode().getNodeValue();
                String family = person.getFamilyName().getDomNode().getNodeValue();
                String given = person.getGivenName().getDomNode().getNodeValue();
            }
            else
            {
                
            }
        }
        
    }
}
