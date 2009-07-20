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
* Copyright 2006-2008 Fachinformationszentrum Karlsruhe Gesellschaft
* f�r wissenschaftlich-technische Information mbH and Max-Planck-
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
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class TransformStatisticReportTest extends XmlTransformingTestBase
{
    Logger logger = Logger.getLogger(this.getClass());
    
    private static XmlTransforming xmlTransforming = new XmlTransformingBean();
    private static String TEST_FILE_ROOT = "xmltransforming/component/transformStatisticReport/";
    private static String REPORT_SAMPLE_FILE = TEST_FILE_ROOT + "report_sample.xml";
    private static String REPORT_DEFINITION_LIST_SAMPLE_FILE = TEST_FILE_ROOT + "report-definition-list_sample.xml";
    
    /**
     * Test of {@link XmlTransforming#transformToStatisticReportRecordList(String)}
     * 
     * @throws Exception 
     */
    @Test
    public void transformToStatisticReportRecordList() throws Exception
    {
        logger.info("### testTransformToStatisticReportList ###");
        
        String reportXML = readFile(REPORT_SAMPLE_FILE);
        
        List<StatisticReportRecordVO> statisticReportRecordList= xmlTransforming.transformToStatisticReportRecordList(reportXML);
        
        assertEquals(9, statisticReportRecordList.size());
        
        StatisticReportRecordVO reportRecord1 = statisticReportRecordList.get(0);
        assertNotNull("Statistic report record is null", reportRecord1);
        
        List<StatisticReportRecordParamVO> statisticParamList = reportRecord1.getParamList();
        assertNotNull("Statistic report record has no parameters", statisticParamList);
        assertEquals("Statistic param list of repord record has invalid size", 2, statisticParamList.size());
        
        StatisticReportRecordParamVO statisticParam1 = statisticParamList.get(0);
        assertEquals("itemid", statisticParam1.getName());
        assertEquals("escidoc:1641:5", statisticParam1.getParamValue().getValue());
        
        StatisticReportRecordParamVO statisticParam2 = statisticParamList.get(1);
        assertEquals("itemrequests", statisticParam2.getName());
        assertEquals("2", ((StatisticReportRecordDecimalParamValueVO)statisticParam2.getParamValue()).getValue());
        
        
        
        StatisticReportRecordVO reportRecord2 = statisticReportRecordList.get(8);
        assertNotNull("Statistic report record is null", reportRecord2);
        
        List<StatisticReportRecordParamVO> statisticParamList2 = reportRecord2.getParamList();
        assertNotNull("Statistic report record has no parameters", statisticParamList2);
        assertEquals("Statistic param list of repord record has invalid size", 2, statisticParamList2.size());
        
        StatisticReportRecordParamVO statisticParam3 = statisticParamList2.get(0);
        assertEquals("itemid", statisticParam3.getName());
        assertEquals("escidoc:1641:3", ((StatisticReportRecordStringParamValueVO)statisticParam3.getParamValue()).getValue());
        
        StatisticReportRecordParamVO statisticParam4 = statisticParamList2.get(1);
        assertEquals("lastitemretrieval", statisticParam4.getName());
        assertEquals("2005-05-07", ((StatisticReportRecordDateParamValueVO)statisticParam4.getParamValue()).getValue());
        
    }
    
    
    @Test
    public void transformToStatisticReportParameters() throws Exception
    {
        StatisticReportParamsVO repParamsVO = new StatisticReportParamsVO();
        List<StatisticReportRecordParamVO> paramList = new ArrayList<StatisticReportRecordParamVO>();
        StatisticReportRecordParamVO param1 = new StatisticReportRecordParamVO("testParamName1", new StatisticReportRecordDecimalParamValueVO("3424"));
        paramList.add(param1);
        
        StatisticReportRecordParamVO param2 = new StatisticReportRecordParamVO("testParamName2", new StatisticReportRecordDateParamValueVO("2008-10-12T09:00:00"));
        paramList.add(param2);
        
        StatisticReportRecordParamVO param3 = new StatisticReportRecordParamVO("testParamName3", new StatisticReportRecordStringParamValueVO("ewfewfewf"));
        paramList.add(param3);
        
        repParamsVO.setReportDefinitionId("23");
        repParamsVO.setParamList(paramList);
        
        String xmlRepParams = xmlTransforming.transformToStatisticReportParameters(repParamsVO);
        
        assertNotNull(xmlRepParams);
        assertXMLValid(xmlRepParams);
        
        logger.info(xmlRepParams);
        
    }
    
    @Test
    public void transformToStatisticReportDefinitionList() throws Exception
    {
        logger.info("### testTransformToStatisticReportDefinitionList ###");
        
        String reportDefXML = readFile(REPORT_DEFINITION_LIST_SAMPLE_FILE);
        
        List<StatisticReportDefinitionVO> statisticReportRecordList= xmlTransforming.transformToStatisticReportDefinitionList(reportDefXML);
        
        assertEquals(9, statisticReportRecordList.size());
        
        StatisticReportDefinitionVO reportDefVO1 = statisticReportRecordList.get(0);
        assertNotNull("report definition is null", reportDefVO1);
        
     
        assertEquals("Wrong object id in report-definition", "1", reportDefVO1.getObjectId());
        assertEquals("Wrong scope id in report-definition", "1", reportDefVO1.getScopeID());
        assertEquals("Wrong name in report-definition", "Successful Framework Requests", reportDefVO1.getName());
        assertEquals("Wrong sql string in report-definition", "select handler, request, day, month, year, sum(requests) from _1_request_statistics group by handler, request, day, month, year;", reportDefVO1.getSql());
        
        StatisticReportDefinitionVO reportDefVO2 = statisticReportRecordList.get(8);
        assertNotNull("report definition is null", reportDefVO2);
        
     
        assertEquals("Wrong object id in report-definition", "9", reportDefVO2.getObjectId());
        assertEquals("Wrong scope id in report-definition", "2", reportDefVO2.getScopeID());
        assertEquals("Wrong name in report-definition", "File downloads, anonymous users", reportDefVO2.getName());
        assertEquals("Wrong sql string in report-definition", "select object_id as fileId, sum(requests) as fileRequests from _1_object_statistics where (object_id = {object_id} OR object_id LIKE {object_id} || ':%') and handler='ItemHandler' and request='retrieveContent' and user_id='' group by object_id;", reportDefVO2.getSql());
        
        
       
    }
    
    @Test
    public void transformToStatisticReportDefinition() throws Exception
    {
        logger.info("### testTransformToStatisticReportDefinition ###");
        
        StatisticReportDefinitionVO repDefVO = new StatisticReportDefinitionVO();
        
        String name = "Successful Framework Requests";
        String objid = "12";
        String scopeId = "1";
        String sql ="select handler, request, day, month, year, sum(requests) from _1_request_statistics group by handler, request, day, month, year;";
        repDefVO.setName(name);
        repDefVO.setObjectId(objid);
        repDefVO.setScopeID(scopeId);
        repDefVO.setSql(sql);
        
        String repDefVOXML = xmlTransforming.transformToStatisticReportDefinition(repDefVO);
        assertNotNull(repDefVOXML);
        assertXMLValid(repDefVOXML);
        logger.info(repDefVOXML);
        
        StatisticReportDefinitionVO repDefVONew = xmlTransforming.transformToStatisticReportDefinition(repDefVOXML);
        
        assertNotNull(repDefVONew);
        assertEquals(name, repDefVONew.getName());
        assertEquals(objid, repDefVONew.getObjectId());
        assertEquals(scopeId, repDefVONew.getScopeID());
        assertEquals(sql, repDefVONew.getSql());
        
    }
    
}
