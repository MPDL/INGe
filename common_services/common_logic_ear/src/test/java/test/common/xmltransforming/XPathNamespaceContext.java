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

package test.common.xmltransforming;

import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

/**
 * This class provides namespace mappings for a more convenient use of the Java 5 XPath API.
 * 
 * @author Johannes Mueller (initial creation)
 * @author $Author: jmueller $ (last modification)
 * @version $Revision: 611 $ $LastChangedDate: 2007-11-07 12:04:29 +0100 (Wed, 07 Nov 2007) $
 * @revised by MuJ: 03.09.2007
 */

public class XPathNamespaceContext implements NamespaceContext
{
    public String getNamespaceURI(String prefix)
    {
        if (prefix == null)
            throw new NullPointerException("Null prefix");
        else if ("dc".equals(prefix))
            return "http://purl.org/dc/elements/1.1/";
        else if ("dcterms".equals(prefix))
            return "http://purl.org/dc/terms/";
        else if ("eidt".equals(prefix))
            return "http://escidoc.mpg.de/metadataprofile/schema/0.1/idtypes";
        else if ("escidoc".equals(prefix))
            return "http://escidoc.mpg.de/metadataprofile/schema/0.1/types";
        else if ("escidocAccountUser".equals(prefix))
            return "http://www.escidoc.de/schemas/useraccount/0.3";
        else if ("escidocComponents".equals(prefix))
            return "http://www.escidoc.de/schemas/components/0.2";
        else if ("escidocItem".equals(prefix))
            return "http://www.escidoc.de/schemas/item/0.2";
        else if ("escidocItemList".equals(prefix))
            return "http://www.escidoc.de/schemas/itemlist/0.2";
        else if ("escidocMetadataProfile".equals(prefix))
            return "http://escidoc.mpg.de/metadataprofile/schema/0.1/";
        else if ("escidocMetadataRecords".equals(prefix))
            return "http://www.escidoc.de/schemas/metadatarecords/0.2";
        else if ("escidocOrganizationalUnit".equals(prefix))
            return "http://www.escidoc.de/schemas/organizationalunit/0.3";
        else if ("escidocRelations".equals(prefix))
            return "http://www.escidoc.de/schemas/relations/0.2";
        else if ("escidocResources".equals(prefix))
            return "http://www.escidoc.de/schemas/resources/0.2";
        else if ("xlink".equals(prefix))
            return "http://www.w3.org/1999/xlink";
        else if ("xsi".equals(prefix))
            return "http://www.w3.org/2001/XMLSchema-instance";        
        else if ("xml".equals(prefix))
            return "http://www.w3.org/XML/1998/namespace";
        return XMLConstants.NULL_NS_URI;
    }

    // This method isn't necessary for XPath processing.
    public String getPrefix(String uri)
    {
        throw new UnsupportedOperationException();
    }

    // This method isn't necessary for XPath processing.
    public Iterator getPrefixes(String uri)
    {
        throw new UnsupportedOperationException();
    }

}
