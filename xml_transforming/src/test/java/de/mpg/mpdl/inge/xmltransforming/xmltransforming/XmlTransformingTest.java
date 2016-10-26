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

package de.mpg.mpdl.inge.xmltransforming.xmltransforming;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import de.mpg.mpdl.inge.xmltransforming.TestBase;
import de.mpg.mpdl.inge.xmltransforming.xmltransforming.component.TransformAffiliationTest;
import de.mpg.mpdl.inge.xmltransforming.xmltransforming.component.TransformContainerTest;
import de.mpg.mpdl.inge.xmltransforming.xmltransforming.component.TransformContextTest;
import de.mpg.mpdl.inge.xmltransforming.xmltransforming.component.TransformExportFormatTest;
import de.mpg.mpdl.inge.xmltransforming.xmltransforming.component.TransformGrantsTest;
import de.mpg.mpdl.inge.xmltransforming.xmltransforming.component.TransformInvalidPubItemTest;
import de.mpg.mpdl.inge.xmltransforming.xmltransforming.component.TransformLockTest;
import de.mpg.mpdl.inge.xmltransforming.xmltransforming.component.TransformParamTest;
import de.mpg.mpdl.inge.xmltransforming.xmltransforming.component.TransformPubItemTest;
import de.mpg.mpdl.inge.xmltransforming.xmltransforming.component.TransformPubItemVersionListTest;
import de.mpg.mpdl.inge.xmltransforming.xmltransforming.component.TransformRelationTest;
import de.mpg.mpdl.inge.xmltransforming.xmltransforming.component.TransformSearchResultGrantList;
import de.mpg.mpdl.inge.xmltransforming.xmltransforming.component.TransformSearchResultTest;
import de.mpg.mpdl.inge.xmltransforming.xmltransforming.component.TransformSearchResultUserGroupList;
import de.mpg.mpdl.inge.xmltransforming.xmltransforming.component.TransformStatisticAggregationTest;
import de.mpg.mpdl.inge.xmltransforming.xmltransforming.component.TransformStatisticReportTest;

/**
 * Component test suite for XmlTransforming.
 * 
 * @author Johannes Mueller (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * @revised by MuJ: 03.09.2007
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({TransformAffiliationTest.class, TransformContainerTest.class,
    TransformExportFormatTest.class, TransformInvalidPubItemTest.class, TransformLockTest.class,
    TransformGrantsTest.class, TransformParamTest.class, TransformContextTest.class,
    TransformPubItemTest.class, TransformRelationTest.class, TransformPubItemVersionListTest.class,
    TransformStatisticReportTest.class, TransformStatisticAggregationTest.class,
    TransformSearchResultTest.class, TransformSearchResultGrantList.class,
    TransformSearchResultUserGroupList.class})
public class XmlTransformingTest extends TestBase {

}
