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

package test.common.xmltransforming.component;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import test.common.TestBase;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.valueobjects.RelationVO;
import de.mpg.escidoc.services.common.valueobjects.RelationVO.RelationType;

/**
 * Test of {@link XmlTransforming} methods for Relation transforming.
 * 
 * @author $Author: tendres $ (last modification)
 * @version $Revision: 663 $ $LastChangedDate: 2007-12-12 14:18:51 +0100 (Wed, 12 Dec 2007) $
 */
public class TransformRelationTest extends TestBase
{
    private static XmlTransforming xmlTransforming;

    private Logger logger = Logger.getLogger(getClass());
    private static String TEST_FILE_ROOT = "src/test/resources/xmltransforming/component/transformRelationTest/";
    private static String RELATIONS_SAMPLE_FILE1 = TEST_FILE_ROOT + "relations_sample1.xml";
    private static String RELATIONS_SAMPLE_FILE2 = TEST_FILE_ROOT + "relations_sample2.xml";

    /**
     * @throws Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {        
        xmlTransforming = (XmlTransforming)getService(XmlTransforming.SERVICE_NAME);        
    }

    /**
     * Test of {@link XmlTransforming#transformToRelationVOList(String)}
     * 
     * @throws Exception
     */
    @Test
    public void testTransformToRelationList1() throws Exception
    {
        logger.info("### testTransformToRelationList1 ###");
        // read relations[XML] from a file
        String relationsXml = readFile(RELATIONS_SAMPLE_FILE1);
        List<RelationVO> relationList = xmlTransforming.transformToRelationVOList(relationsXml);
        assertEquals(relationList.size(), 2);
        
        RelationVO relation1 = relationList.get(0);
        assertEquals(relation1.getSourceItemRef().getObjectId(),"escidoc:292");
        assertEquals(relation1.getTargetItemRef().getObjectId(),"escidoc:291");
        assertEquals(relation1.gettype(),RelationType.ISREVISIONOF);
        
        RelationVO relation2 = relationList.get(1);
        assertEquals(relation2.getSourceItemRef().getObjectId(),"escidoc:293");
        assertEquals(relation2.getTargetItemRef().getObjectId(),"escidoc:291");
        assertEquals(relation2.gettype(),RelationType.ISREVISIONOF);
    }
    
    /**
     * Test of {@link XmlTransforming#transformToRelationVOList(String)}
     * 
     * @throws Exception
     */
    @Test
    public void testTransformToRelationList2() throws Exception
    {
        logger.info("### testTransformToRelationList2 ###");
        // read relations[XML] from a file
        String relationsXml = readFile(RELATIONS_SAMPLE_FILE2);
        List<RelationVO> relationList = xmlTransforming.transformToRelationVOList(relationsXml);
        assertEquals(relationList.size(), 2);
        
        RelationVO relation1 = relationList.get(0);
        assertEquals(relation1.getSourceItemRef().getObjectId(),"escidoc:287");
        assertEquals(relation1.getTargetItemRef().getObjectId(),"escidoc:201");
        assertEquals(relation1.gettype(),RelationType.ISREVISIONOF);
        
        RelationVO relation2 = relationList.get(1);
        assertEquals(relation2.getSourceItemRef().getObjectId(),"escidoc:287");
        assertEquals(relation2.getTargetItemRef().getObjectId(),"escidoc:202");
        assertEquals(relation2.gettype(),RelationType.ISREVISIONOF);
    }
}
