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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;

import de.mpg.mpdl.inge.model.valueobjects.GrantVO;
import de.mpg.mpdl.inge.model.xmltransforming.TestBase;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;
import de.mpg.mpdl.inge.util.ResourceUtil;

/**
 * Test class for {@link XmlTransforming} methods for LockVo transformation.
 * 
 * @author Author: mfranke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class TransformGrantsTest extends TestBase {
  private static final Logger logger = Logger.getLogger(TransformGrantsTest.class);

  /**
   * Test for {@link XmlTransforming#transformToGrantVOList(String)}.
   * 
   * @throws Exception Any exception
   */
  @Test
  public void testTransformToGrantVOList() throws Exception {
    logger.info("### TransformGrantsTest ###");

    String grantsXml =
        ResourceUtil.getResourceAsString(
            "xmltransforming/component/transformGrantsTest/current-grants.xml",
            TransformGrantsTest.class.getClassLoader());

    List<GrantVO> grants = XmlTransformingService.transformToGrantVOList(grantsXml);

    assertNotNull("Grants are null", grants);

    assertEquals("user-account", grants.get(0).getGrantType());
    assertEquals("escidoc:user2", grants.get(0).getGrantedTo());
    assertEquals("escidoc:role-depositor", grants.get(1).getRole());
    assertEquals("group", grants.get(1).getGrantType());

  }
}
