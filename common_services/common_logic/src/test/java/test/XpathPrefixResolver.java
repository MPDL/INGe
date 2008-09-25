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
* Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/

package test;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Node;


/**
 * This class maps namespace URIs to namespace prefixes for XPath evaluations.
 * 
 * @author Johannes Mueller (initial creation)
 * @author $Author: jmueller $ (last modification)
 * @version $Revision: 611 $ $LastChangedDate: 2007-11-07 12:04:29 +0100 (Wed, 07 Nov 2007) $
 * @revised by MuJ: 03.09.2007
 */
public class XpathPrefixResolver
{
    /**
     * This map stores the mapping between the namespace names (URIs) and the assigned namespace prefixes.
     */
    private Map<String, String> namespaces;

    /**
     * An XpathPrefixResolver.
     */
    public XpathPrefixResolver()
    {
        Map<String, String> nsMap = new HashMap<String, String>();
        nsMap.put("dc", "http://purl.org/dc/elements/1.1/");
        nsMap.put("dcterms", "http://purl.org/dc/terms/");
        nsMap.put("eidt", "http://escidoc.mpg.de/metadataprofile/schema/0.1/idtypes");
        nsMap.put("escidoc", "http://escidoc.mpg.de/metadataprofile/schema/0.1/types");
        nsMap.put("escidocAccountUser", "${xsd.soap.useraccount.useraccount}");
        nsMap.put("escidocAdminDescriptor", "http://www.escidoc.de/schemas/admindescriptor/0.2");
        nsMap.put("escidocContext", "${xsd.soap.context.context}");
        nsMap.put("escidocContextList", "http://www.escidoc.de/schemas/contextlist/0.2");
        nsMap.put("escidocComponents", "http://www.escidoc.de/schemas/components/0.2");
        nsMap.put("escidocItem", "http://www.escidoc.de/schemas/item/0.3");
        nsMap.put("escidocItemList", "http://www.escidoc.de/schemas/itemlist/0.2");
        nsMap.put("escidocMetadataProfile", "http://escidoc.mpg.de/metadataprofile/schema/0.1/");
        nsMap.put("escidocMetadataRecords", "http://www.escidoc.de/schemas/metadatarecords/0.2");
        nsMap.put("escidocOrganizationalUnit", "${xsd.soap.ou.ou}");
        nsMap.put("escidocRelations", "${xsd.soap.common.relations}");
        nsMap.put("escidocSearchResult", "${xsd.soap.searchresult.searchresult}");
        nsMap.put("xlink", "http://www.w3.org/1999/xlink");
        nsMap.put("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        nsMap.put("xml", "http://www.w3.org/XML/1998/namespace");
        nsMap.put("prop", "${xsd.soap.common.prop}");
        nsMap.put("srel", "${xsd.soap.common.srel}");
        nsMap.put("ou-details", "${xsd.metadata.organization}");
        nsMap.put("kml", "http://www.opengis.net/kml/2.2");
        this.namespaces = nsMap;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.xml.utils.PrefixResolver#getBaseIdentifier()
     */
    public String getBaseIdentifier()
    {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.xml.utils.PrefixResolver#getNamespaceForPrefix(java.lang.String)
     */
    public String getNamespaceForPrefix(String prefix)
    {
        return namespaces.get(prefix);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.xml.utils.PrefixResolver#getNamespaceForPrefix(java.lang.String, org.w3c.dom.Node)
     */
    public String getNamespaceForPrefix(String prefix, Node nsContext)
    {
        return namespaces.get(prefix);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.xml.utils.PrefixResolver#handlesNullPrefixes()
     */
    public boolean handlesNullPrefixes()
    {
        return false;
    }
}
