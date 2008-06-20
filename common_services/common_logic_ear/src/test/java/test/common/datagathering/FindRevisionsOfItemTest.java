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

package test.common.datagathering;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.junit.Test;

import test.common.TestBase;
import de.mpg.escidoc.services.common.DataGathering;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.common.valueobjects.RelationVO;
import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 * Test cases for method DataGathering.findRevisionsOfItem
 *
 * @author Peter (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class FindRevisionsOfItemTest extends TestBase
{
    private static final String ITEM_WITHOUT_COMPONENTS = "test/item_without_components.xml";
    private static final String PREDICATE_ISREVISIONOF = "http://www.escidoc.de/ontologies/mpdl-ontologies/content-relations#isRevisionOf";

    private Logger logger = Logger.getLogger(getClass());

    /**
     * Helper method to retrieve DataGathering instance.
     * 
     * @return instance of DataGathering
     * @throws NamingException
     */
    private static DataGathering getDataGathering() throws NamingException
    {
        return (DataGathering)getService(DataGathering.SERVICE_NAME);
    }

    /**
     * Test case similar to the one in framework_access.
     * 
     * @throws Exception 
     */
    @Test
    public void findRevisionsOfItem() throws Exception
    {
        // login as Scientist
        String userHandle = loginScientist();
        // create the item with one file
        String item = createItemWithFile(userHandle);
        PubItemVO pubItem = ((XmlTransforming) getService(XmlTransforming.SERVICE_NAME)).transformToPubItem(item);
        logger.debug("Target is " + pubItem.getVersion().getObjectId());

        // Create two revisions
        // revision 1
        String source = ServiceLocator.getItemHandler(userHandle).create(readFile(ITEM_WITHOUT_COMPONENTS));
        String sourceId1 = getObjid(source);
        String sourceMd = getLastModificationDate(source);
        // add relation source 1 -- isRevisionOf --> target
        String param = "<param last-modification-date=\"" + sourceMd + "\">" +
                       "    <relation>" +
                       "        <targetId>" + pubItem.getVersion().getObjectId() + "</targetId>" +
                       "        <predicate>" + PREDICATE_ISREVISIONOF + "</predicate>" +
                       "    </relation>" +
                       "</param>";
        ServiceLocator.getItemHandler(userHandle).addContentRelations(sourceId1, param);
        logger.debug(sourceId1 + " isRevisionOf " + pubItem.getVersion().getObjectId());
        // revision 2
        source = ServiceLocator.getItemHandler(userHandle).create(readFile(ITEM_WITHOUT_COMPONENTS));
        String sourceId2 = getObjid(source);
        sourceMd = getLastModificationDate(source);
        // add relation source 1 -- isRevisionOf --> target
        param = "<param last-modification-date=\"" + sourceMd + "\">" +
                "    <relation>" +
                "        <targetId>" + pubItem.getVersion().getObjectId() + "</targetId>" +
                "        <predicate>" + PREDICATE_ISREVISIONOF + "</predicate>" +
                "    </relation>" +
                "</param>";
        ServiceLocator.getItemHandler(userHandle).addContentRelations(sourceId2, param);
        logger.debug(sourceId2 + " isRevisionOf " + pubItem.getVersion().getObjectId());

        // Call the gathering method
        List<RelationVO> revisions = getDataGathering().findRevisionsOfItem(userHandle, pubItem.getVersion());
        // Check the result
        assertNotNull(revisions);
        assertTrue(revisions.size() == 2);
        assertTrue(revisions.get(0).getTargetItemRef().getObjectId().equals(pubItem.getVersion().getObjectId()));
        assertTrue(revisions.get(0).getSourceItemRef().getObjectId().equals(sourceId1));
        assertTrue(revisions.get(1).getTargetItemRef().getObjectId().equals(pubItem.getVersion().getObjectId()));
        assertTrue(revisions.get(1).getSourceItemRef().getObjectId().equals(sourceId2));
    }
}
