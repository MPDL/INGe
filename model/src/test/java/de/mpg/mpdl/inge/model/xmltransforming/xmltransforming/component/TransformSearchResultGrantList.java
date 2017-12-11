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
public class TransformSearchResultGrantList extends XmlTransformingTestBase {
  @Test
  public void testContextListSearchRetrieveResponse() throws Exception {
    // TODO Add grant list handling
    // String searchResultXML = readFile(SAMPLE_FILE);
    // SearchRetrieveResponseVO res =
    // xmlTransforming.transformToSearchRetrieveResponseGrant(searchResultXML);
    //
    // assertNotNull(res);
    //
    // GrantList grantList = new GrantList();
    // List<Grant> grantArray = new ArrayList<Grant>();
    // for (int index = 0; index < res.getNumberOfRecords(); index++) {
    // grantArray.add((Grant) res.getRecords().get(index).getData());
    // }
    // System.out.println(grantArray);
    // grantList.setGrants(grantArray);
    //
    // assertTrue(grantList.getNumberOfRecords() == 0);
    //
    // Grant grant = grantList.getGrants().get(0);
    // assertTrue(grant instanceof Grant);
    //
    // assertEquals("Wrong Role", grant.getRole(), "escidoc:role-audience");
    // assertEquals("Wrong Assigned-On", grant.getAssignedOn(), "escidoc:24001");
    // logger.debug("TransformSearchResultGrantListTest successful");
  }
}
