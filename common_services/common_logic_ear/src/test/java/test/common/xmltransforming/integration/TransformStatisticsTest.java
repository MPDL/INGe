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
 * für wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Förderung der Wissenschaft e.V.
 * All rights reserved. Use is subject to license terms.
 */

package test.common.xmltransforming.integration;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import test.common.xmltransforming.XmlTransformingTestBase;
import de.escidoc.www.services.sm.ReportDefinitionHandler;
import de.escidoc.www.services.sm.ReportHandler;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.valueobjects.statistics.StatisticReportDefinitionVO;
import de.mpg.escidoc.services.common.valueobjects.statistics.StatisticReportParamsVO;
import de.mpg.escidoc.services.common.valueobjects.statistics.StatisticReportRecordDecimalParamValueVO;
import de.mpg.escidoc.services.common.valueobjects.statistics.StatisticReportRecordParamVO;
import de.mpg.escidoc.services.common.valueobjects.statistics.StatisticReportRecordVO;
import de.mpg.escidoc.services.common.xmltransforming.XmlTransformingBean;
import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 * Test class for statistic xml transformations
 *
 * @author Markus Haarlaender (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class TransformStatisticsTest extends XmlTransformingTestBase
{
    
    private static Logger logger = Logger.getLogger(TransformStatisticsTest.class);
    private static XmlTransformingBean xmlTransforming;
    private String userHandle;

    
    
    /**
     * Get an {@link XmlTransforming} instance once.
     * 
     * @throws Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        xmlTransforming = new XmlTransformingBean();
    }
    
    /**
     * Logs in as system admin (before every single test method).
     * 
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception
    {
        userHandle = loginSystemAdministrator(); 
    }

    /**
     * Logs out (after every single test method).
     * 
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception
    {
        logout(userHandle);
    }
    
    @Test
    public void testTransformToStatisticReportRoundtrip() throws Exception
    {
        logger.info("### testTransformToStatisticReportRoundtrip()");
        ReportDefinitionHandler repDefHandler = ServiceLocator.getReportDefinitionHandler(userHandle);
        
        //Create a test report definition
        StatisticReportDefinitionVO repDefVO = new StatisticReportDefinitionVO();
        repDefVO.setName("TestReportDefiniton Successful Framework Requests by Month and Year");
        repDefVO.setObjectId("15");
        repDefVO.setScopeID("1");
        repDefVO.setSql("select * from _1_request_statistics where month = {month} and year = {year};");
        
        String repDefXML = xmlTransforming.transformToStatisticReportDefinition(repDefVO);
        logger.debug("report definition before creation: - "+repDefXML);
        String repDefXMLReply = repDefHandler.create(repDefXML);
        logger.debug("report definition after creation: - "+repDefXMLReply);
        
        StatisticReportDefinitionVO repDefVOReply = xmlTransforming.transformToStatisticReportDefinition(repDefXMLReply);
        
        String repDefObjId = repDefVOReply.getObjectId();
        
        
        ReportHandler repHandler = ServiceLocator.getReportHandler(userHandle);
        
        //retrieve a report
        StatisticReportParamsVO repParamsVO = new StatisticReportParamsVO();
        repParamsVO.setReportDefinitionId(repDefObjId);
        List<StatisticReportRecordParamVO> paramList = new ArrayList<StatisticReportRecordParamVO>();
        
        StatisticReportRecordParamVO param1 = new StatisticReportRecordParamVO("month", new StatisticReportRecordDecimalParamValueVO(String.valueOf(Calendar.getInstance().get(Calendar.MONTH))));
        StatisticReportRecordParamVO param2 = new StatisticReportRecordParamVO("year", new StatisticReportRecordDecimalParamValueVO(String.valueOf(Calendar.getInstance().get(Calendar.YEAR))));
        paramList.add(param1);
        paramList.add(param2);
        
        repParamsVO.setParamList(paramList);
        
        String repParamsXML = xmlTransforming.transformToStatisticReportParameters(repParamsVO);
        logger.debug("report parameters: - "+repParamsXML);
        String reportXML = repHandler.retrieve(repParamsXML);
        
        List<StatisticReportRecordVO> repRecVOList = xmlTransforming.transformToStatisticReportRecordList(reportXML);
        logger.debug("report after retrieval: - "+reportXML);
        //delete test report definition
        repDefHandler.delete(repDefObjId);
        
        
        
    }
}
