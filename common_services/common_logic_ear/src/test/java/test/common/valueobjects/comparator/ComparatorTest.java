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
* or http://www.escidoc.org/license.
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
* Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/

package test.common.valueobjects.comparator;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * All Tests of the comparator class for PubItemVOs.
 * 
 * @author Peter Broszeit (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * Revised by BrP: 03.09.2007
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({TitleComparatorTest.class
                    ,DateComparatorTest.class
                    ,GenreComparatorTest.class
                    ,CreatorComparatorTest.class
                    ,PublishingInfoComparatorTest.class
                    ,ReviewMethodComparatorTest.class
                    ,SourceCreatorComparatorTest.class
                    ,SourceTitleComparatorTest.class
                    ,EventTitleComparatorTest.class
                    })
public class ComparatorTest
{
}