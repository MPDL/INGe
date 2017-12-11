/*
 * 
 * CDDL HEADER START
 * 
 * The contents of this file are subject to the terms of the Common Development and Distribution
 * License, Version 1.0 only (the "License"). You may not use this file except in compliance with
 * the License.
 * 
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or
 * http://www.escidoc.org/license. See the License for the specific language governing permissions
 * and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License
 * file at license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with
 * the fields enclosed by brackets "[]" replaced with your own identifying information: Portions
 * Copyright [yyyy] [name of copyright owner]
 * 
 * CDDL HEADER END
 */

/*
 * Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft für
 * wissenschaftlich-technische Information mbH and Max-Planck- Gesellschaft zur Förderung der
 * Wissenschaft e.V. All rights reserved. Use is subject to license terms.
 */

package de.mpg.mpdl.inge.model.xmltransforming.xmltransforming.component;

import org.junit.Test;

import de.mpg.mpdl.inge.model.xmltransforming.xmltransforming.XmlTransformingTestBase;

/**
 * TODO Description
 * 
 * @author Matthias Walter (initial creation)
 * @author $Author: $ (last modification)
 * @version $Revision: 4140 $ $LastChangedDate: 2011-09-29 16:18:31 +0200 (Do, 29 Sep 2011) $
 * 
 */
public class TransformSearchResultUserGroupList extends XmlTransformingTestBase {
  @Test
  public void testContextListSearchRetrieveResponse() throws Exception {
    // TODO Add user group handling
    // String searchResultXML = readFile(SAMPLE_FILE);
    // SearchRetrieveResponseVO res =
    // xmlTransforming.transformToSearchRetrieveResponseUserGroup(searchResultXML);
    //
    // assertNotNull(res);
    //
    // UserGroupList userGroupList = new UserGroupList();
    // List<UserGroup> userGroupArray = new ArrayList<UserGroup>();
    // for (int index = 0; index < res.getNumberOfRecords(); index++) {
    // userGroupArray.add((UserGroup) res.getRecords().get(index).getData());
    // }
    // userGroupList.setUserGroupLists(userGroupArray);
    //
    // assertTrue(userGroupList.getUserGroupLists().size() == 4);
    //
    // UserGroup userGroup = userGroupList.getUserGroupLists().get(0);
    // assertTrue(userGroup instanceof UserGroup);
    //
    // assertEquals("Wrong UserGroupName", userGroup.getName(), "TestName");
    // assertEquals("Wrong UserGroupLabel", userGroup.getLabel(), "TestLabel");
    // assertEquals("Wrong UserGroup-createdBy", userGroup.getCreatedBy(), "escidoc:exuser1");
    // logger.debug("TransformSearchResultGrantListTest successfull");
  }
}
