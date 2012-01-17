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
* Copyright 2006-2010 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package test.xmltransforming.component;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;

import test.xmltransforming.XmlTransformingTestBase;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.valueobjects.SearchRetrieveResponseVO;
import de.mpg.escidoc.services.common.valueobjects.intelligent.usergroup.UserGroup;
import de.mpg.escidoc.services.common.valueobjects.intelligent.usergroup.UserGroupList;
import de.mpg.escidoc.services.common.xmltransforming.XmlTransformingBean;

/**
 * TODO Description
 *
 * @author Matthias Walter (initial creation)
 * @author $Author: $ (last modification)
 * @version $Revision: 4140 $ $LastChangedDate: 2011-09-29 16:18:31 +0200 (Do, 29 Sep 2011) $
 *
 */
public class TransformSearchResultUserGroupList extends XmlTransformingTestBase
{
    private static final Logger logger = Logger.getLogger(TransformSearchResultUserGroupList.class);
    private static XmlTransforming xmlTransforming = new XmlTransformingBean();
    private static final String TEST_FILE_ROOT = "xmltransforming/component/transformSearchResultUserGroupListTest/";
    private static final String SAMPLE_FILE = TEST_FILE_ROOT + "searchRetrieveUserGroupList_sample.xml";
    @Test
    public void testContextListSearchRetrieveResponse() throws Exception
    {
        String searchResultXML = readFile(SAMPLE_FILE);
        SearchRetrieveResponseVO res = xmlTransforming.transformToSearchRetrieveResponseUserGroup(searchResultXML);
        
        assertNotNull(res);
        
        UserGroupList userGroupList = new UserGroupList();
        List<UserGroup> userGroupArray = new ArrayList<UserGroup>();
        for (int index = 0; index < res.getNumberOfRecords(); index++)
        {
        	userGroupArray.add((UserGroup) res.getRecords().get(index).getData());
        }
        userGroupList.setUserGroupLists(userGroupArray);

        assertTrue(userGroupList.getUserGroupLists().size() == 4);
        
        UserGroup userGroup = userGroupList.getUserGroupLists().get(0);
        assertTrue(userGroup instanceof UserGroup);
        
        assertEquals("Wrong UserGroupName", userGroup.getName(), "TestName");
        assertEquals("Wrong UserGroupLabel", userGroup.getLabel(), "TestLabel");
        assertEquals("Wrong UserGroup-createdBy", userGroup.getCreatedBy(), "escidoc:exuser1");
        logger.debug("TransformSearchResultGrantListTest successfull");
    }
}
