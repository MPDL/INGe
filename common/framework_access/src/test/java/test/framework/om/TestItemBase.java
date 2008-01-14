/*
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
package test.framework.om;

import test.framework.TestBase;

/**
 * Base class for item testcases.
 *
 * @author Peter (initial creation)
 * @author $Author: wfrank $ (last modification)
 * @version $Revision: 329 $ $LastChangedDate: 2007-12-06 09:44:45 +0100 (Thu, 06 Dec 2007) $
 * @revised by BrP: 04.09.2007
 */
public class TestItemBase extends TestBase
{
    /**
     * A XML-file with the test item. 
     */
    protected  static final String ITEM_FILE = "src/test/resources/test/item_without_components.xml";
    protected  static final String CONTAINER_FILE = "src/test/resources/test/container1.xml";

    protected static final String PREDICATE_ISREVISIONOF = "http://www.escidoc.de/ontologies/mpdl-ontologies/content-relations#isRevisionOf";

    /**
     * Extracts the id of a item from the given XML.
     * @param item The item XML as a String.
     * @return The id of the item.
     */
    protected String getId(String item)
    {
        String id = "";
        int index = item.indexOf("objid=\"");
        if (index > 0)
        {
            item = item.substring(index + 7);
            index = item.indexOf('\"');
            if (index > 0)
            {
                id = item.substring(0, index);
            }
        }
        return id;
    }
    
    /**
     * Extracts the version of a item from the given XML.
     * @param item The item XML as a String.
     * @return The version of the item.
     */
    protected String getVersion(String item)
    {
        String id = "";
        int index = item.indexOf("version ");
        if (index > 0)
        {
            item = item.substring(index + 8);
            index = item.indexOf("objid=\"");
            if (index > 0)
            {
                item = item.substring(index + 7);
                index = item.indexOf('\"');
                if (index > 0)
                {
                    id = item.substring(0, index);
                }
            }
        }
        return id;
    }
    
    /**
     * Extracts the modification date of a item from the given XML.
     * @param item The item XML as a String.
     * @return The modification date of the item.
     */
    protected String getModificationDate(String item)
    {
        String md = "";
        int index = item.indexOf("last-modification-date=\"");
        if (index > 0)
        {
            item = item.substring(index + 24);
            index = item.indexOf('\"');
            if (index > 0)
            {
                md = item.substring(0, index);
            }
        }
        return md;
    }
    
    /**
     * @param md The modification date.
     * @return The modification date as a XML.
     */
    protected String createModificationDate(String md)
    {
        return "<param last-modification-date=\"" + md + "\"/>";
    }
}
