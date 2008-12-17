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

package test.xmltransforming.component;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import org.apache.log4j.Logger;
import org.junit.Test;

import test.xmltransforming.XmlTransformingTestBase;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.valueobjects.AffiliationResultVO;
import de.mpg.escidoc.services.common.valueobjects.ContainerResultVO;
import de.mpg.escidoc.services.common.valueobjects.ItemResultVO;
import de.mpg.escidoc.services.common.valueobjects.interfaces.SearchResultElement;
import de.mpg.escidoc.services.common.valueobjects.metadata.MdsOrganizationalUnitDetailsVO;
import de.mpg.escidoc.services.common.xmltransforming.XmlTransformingBean;

/**
 * TODO Description
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class TransformSearchResultTest extends XmlTransformingTestBase
{
    private static final Logger logger = Logger.getLogger(TransformSearchResultTest.class);
    private static XmlTransforming xmlTransforming = new XmlTransformingBean();
    private static final String TEST_FILE_ROOT = "xmltransforming/component/transformSearchResultTest/";
    private static final String SEARCH_SAMPLE_FILE1 = TEST_FILE_ROOT + "search-result_sample.xml";
    private static final String SEARCH_SAMPLE_FILE2 = TEST_FILE_ROOT + "search-result_sample2.xml";
    private static final String SEARCH_SAMPLE_FILE3 = TEST_FILE_ROOT + "search-result_sample3.xml";
    
    @Test
    public void testItemSearchResult() throws Exception
    {
        String searchResultXML = readFile(SEARCH_SAMPLE_FILE1);
        SearchResultElement itemResultVO = xmlTransforming.transformToSearchResult(searchResultXML);
        assertNotNull(itemResultVO);
        assertTrue(itemResultVO instanceof ItemResultVO);

        assertTrue(((ItemResultVO) itemResultVO).getFiles().size() == 1);
        
    }
    
    @Test
    public void testContainerSearchResult() throws Exception
    {
        String searchResultXML = readFile(SEARCH_SAMPLE_FILE2);
        SearchResultElement containerResultVO = xmlTransforming.transformToSearchResult(searchResultXML);
        assertNotNull(containerResultVO);
        assertTrue(containerResultVO instanceof ContainerResultVO);

        assertTrue(((ContainerResultVO) containerResultVO).getMetadataSets().size() == 2);
        assertTrue(((ContainerResultVO) containerResultVO).getMembers().size() > 1);
    }
    
    @Test
    public void testAffiliationSearchResult() throws Exception
    {
        String searchResultXML = readFile(SEARCH_SAMPLE_FILE3);
        SearchResultElement affiliationResultVO = xmlTransforming.transformToSearchResult(searchResultXML);
        assertNotNull(affiliationResultVO);
        assertTrue(affiliationResultVO instanceof AffiliationResultVO);

        assertTrue(((AffiliationResultVO) affiliationResultVO).getMetadataSets().size() == 1);
        assertTrue(((AffiliationResultVO) affiliationResultVO).getMetadataSets().get(0) instanceof MdsOrganizationalUnitDetailsVO);
        assertEquals("MPI for the History of Science", ((MdsOrganizationalUnitDetailsVO)((AffiliationResultVO) affiliationResultVO).getMetadataSets().get(0)).getName());
    }
}
