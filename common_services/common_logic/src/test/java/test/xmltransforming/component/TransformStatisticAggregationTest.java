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
* Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur F�rderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 
package test.xmltransforming.component;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;

import test.xmltransforming.XmlTransformingTestBase;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.valueobjects.statistics.AggregationDefinitionVO;
import de.mpg.escidoc.services.common.valueobjects.statistics.AggregationIndexVO;
import de.mpg.escidoc.services.common.valueobjects.statistics.AggregationInfoFieldVO;
import de.mpg.escidoc.services.common.valueobjects.statistics.AggregationTableVO;
import de.mpg.escidoc.services.common.valueobjects.statistics.StatisticReportDefinitionVO;
import de.mpg.escidoc.services.common.valueobjects.statistics.StatisticReportParamsVO;
import de.mpg.escidoc.services.common.valueobjects.statistics.StatisticReportRecordDateParamValueVO;
import de.mpg.escidoc.services.common.valueobjects.statistics.StatisticReportRecordDecimalParamValueVO;
import de.mpg.escidoc.services.common.valueobjects.statistics.StatisticReportRecordParamVO;
import de.mpg.escidoc.services.common.valueobjects.statistics.StatisticReportRecordStringParamValueVO;
import de.mpg.escidoc.services.common.valueobjects.statistics.StatisticReportRecordVO;
import de.mpg.escidoc.services.common.xmltransforming.XmlTransformingBean;

/**
 * Test of {@link XmlTransforming} methods for statistic transformings.
 *
 * @author Markus Haarlaender (initial creation)
 * @author $Author: mfranke $ (last modification)
 * @version $Revision: 4429 $ $LastChangedDate: 2012-05-29 16:20:07 +0200 (Tue, 29 May 2012) $
 *
 */
public class TransformStatisticAggregationTest extends XmlTransformingTestBase
{
    Logger logger = Logger.getLogger(this.getClass());
    
    private static XmlTransforming xmlTransforming = new XmlTransformingBean();
    private static String TEST_FILE_ROOT = "xmltransforming/component/transformStatisticAggregation/";
    private static String AGG_DEFINITION_LIST_SAMPLE_FILE = TEST_FILE_ROOT + "pubman_object_stats_aggregation.xml";
    
    /**
     * Test of {@link XmlTransforming#transformToStatisticReportRecordList(String)}
     * 
     * @throws Exception 
     */
  

    @Test
    public void transformToStatisticAggregationDefinition() throws Exception
    {
        logger.info("### testTransformToStatisticAggregationDefinition ###");
        
        String aggDefXml = readFile(AGG_DEFINITION_LIST_SAMPLE_FILE);
        AggregationDefinitionVO aggDefVO = xmlTransforming.transformToStatisticAggregationDefinition(aggDefXml);
        
        assertEquals("pubman item statistics without version", aggDefVO.getName());
        assertNotNull(aggDefVO.getScopeId());
        assertNotNull(aggDefVO.getStatisticDataXPath());
        
        AggregationTableVO aggTableVO = aggDefVO.getAggregationTables().get(0);
        assertEquals("pubman_object_stats", aggTableVO.getName());
        
        assertEquals("pubman_object_stats", aggTableVO.getName());
        assertEquals(8, aggTableVO.getAggregationFields().size());
        
        AggregationInfoFieldVO infoField = (AggregationInfoFieldVO)aggTableVO.getAggregationFields().get(0);
        assertEquals("handler", infoField.getName());
        assertEquals("text", infoField.getType());
        assertEquals("//parameter[@name=\"handler\"]/stringvalue", infoField.getxPath());
        
        AggregationIndexVO aggIndexVO = aggTableVO.getAggregationIndexes().get(0);
        
        assertEquals("escidocaggdef1_time3_idx", aggIndexVO.getName());
        assertEquals(2, aggIndexVO.getFields().size());
      
        
    }
    

}
