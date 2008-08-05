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

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.escidoc.www.services.om.TocHandler;
import de.escidoc.www.services.sm.ReportDefinitionHandler;
import de.escidoc.www.services.sm.ReportHandler;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.referenceobjects.ContextRO;
import de.mpg.escidoc.services.common.valueobjects.MetadataSetVO;
import de.mpg.escidoc.services.common.valueobjects.TocDivVO;
import de.mpg.escidoc.services.common.valueobjects.TocItemVO;
import de.mpg.escidoc.services.common.valueobjects.TocPtrVO;
import de.mpg.escidoc.services.common.valueobjects.TocVO;
import de.mpg.escidoc.services.common.valueobjects.statistics.StatisticReportDefinitionVO;
import de.mpg.escidoc.services.common.valueobjects.statistics.StatisticReportParamsVO;
import de.mpg.escidoc.services.common.valueobjects.statistics.StatisticReportRecordDecimalParamValueVO;
import de.mpg.escidoc.services.common.valueobjects.statistics.StatisticReportRecordParamVO;
import de.mpg.escidoc.services.common.valueobjects.statistics.StatisticReportRecordVO;
import de.mpg.escidoc.services.common.xmltransforming.XmlTransformingBean;
import de.mpg.escidoc.services.framework.ServiceLocator;
import test.common.TestBase;
import test.common.xmltransforming.XmlTransformingTestBase;

/**
 * Test class for statistic xml transformations
 *
 * @author Markus Haarlaender (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class TransformTocTest extends XmlTransformingTestBase
{
    
    private static Logger logger = Logger.getLogger(TransformTocTest.class);
    private static XmlTransformingBean xmlTransforming;
    private String userHandle;
    
    private static String TEST_FILE_ROOT = "xmltransforming/integration/transformTocTest/";
    private static String TOC_SAMPLE_FILE = TEST_FILE_ROOT + "toc_sample.xml";

    
    
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
    public void testCreateAndRetrieveTocItem() throws Exception
    {
        logger.info("### testCreateToc()");
        TocHandler tocHandler = ServiceLocator.getTocHandler(userHandle);
       
        TocItemVO tocItemVO = getNestedTocItem();
        String tocXML = xmlTransforming.transformToTocItem(tocItemVO);

        String newTocXML = tocHandler.create(tocXML);
        TocItemVO newTocItemVO = xmlTransforming.transformToTocItemVO(newTocXML);
        
        TocVO tocVO = tocItemVO.getTocVO();
        TocVO newTocVO = newTocItemVO.getTocVO();
        
        assertEquals(tocVO.getTocId(), newTocVO.getTocId());
        assertEquals(tocVO.getTocBase(), newTocVO.getTocBase());
        assertEquals(tocVO.getTocLabel(), newTocVO.getTocLabel());
        assertEquals(tocVO.getTocType(), newTocVO.getTocType());
        
        TocDivVO tocDivVO = tocVO.getTocDiv();
        TocDivVO newTocDivVO = newTocVO.getTocDiv();
       
        assertNotNull(newTocDivVO);
        assertEquals(tocDivVO.getId(), newTocDivVO.getId());
        assertEquals(tocDivVO.getLabel(), newTocDivVO.getLabel());
        assertEquals(tocDivVO.getOrder(), newTocDivVO.getOrder());
        assertEquals(tocDivVO.getOrderLabel(), newTocDivVO.getOrderLabel());
        assertEquals(tocDivVO.getType(), newTocDivVO.getType());
        assertEquals(tocDivVO.getTocDivList().size(), newTocDivVO.getTocDivList().size());
        assertEquals(tocDivVO.getTocPtrList().size(), newTocDivVO.getTocPtrList().size());
        
        TocPtrVO tocPtrVO = tocDivVO.getTocPtrList().get(0);
        TocPtrVO newTocPtrVO = newTocDivVO.getTocPtrList().get(0);
        assertNotNull(newTocPtrVO);
        assertEquals(tocPtrVO.getId(), newTocPtrVO.getId());
        assertEquals(tocPtrVO.getMimetype(), newTocPtrVO.getMimetype());
        assertEquals(tocPtrVO.getUse(), newTocPtrVO.getUse());
        assertEquals(tocPtrVO.getLoctype(), newTocPtrVO.getLoctype());
        assertEquals(tocPtrVO.getLinkRef(), newTocPtrVO.getLinkRef());
        assertEquals(tocPtrVO.getLinkTitle(), newTocPtrVO.getLinkTitle());
        assertEquals(tocPtrVO.getLinkType(), newTocPtrVO.getLinkType());
        
        String retrievedTocXml = tocHandler.retrieve(newTocItemVO.getVersion().getObjectId());
        xmlTransforming.transformToTocItemVO(retrievedTocXml);
        
        String retrievedTocOnlyXml = tocHandler.retrieveToc(newTocItemVO.getVersion().getObjectId());
       
    }
    
    @Test
    public void testUpdateTocItem() throws Exception
    {
        logger.info("### testUpdateToc()");
        TocHandler tocHandler = ServiceLocator.getTocHandler(userHandle);
       
        TocItemVO tocItemVO = getNestedTocItem();
        String tocXML = xmlTransforming.transformToTocItem(tocItemVO);

        String newTocXML = tocHandler.create(tocXML);
        TocItemVO newTocItemVO = xmlTransforming.transformToTocItemVO(newTocXML);
        
        String newDivLabel = "UpdatedLabel";
        String newPtrRef = "/ir/container/escidoc:11";
        newTocItemVO.getTocVO().getTocDiv().setLabel(newDivLabel);
        newTocItemVO.getTocVO().getTocDiv().getTocPtrList().get(0).setLinkRef(newPtrRef);
        
        String updatedTocXml = tocHandler.update(newTocItemVO.getVersion().getObjectId(), xmlTransforming.transformToTocItem(newTocItemVO));
        TocItemVO updatedTocItem = xmlTransforming.transformToTocItemVO(updatedTocXml);
        
        assertEquals(newDivLabel, updatedTocItem.getTocVO().getTocDiv().getLabel());
        assertEquals(newPtrRef, updatedTocItem.getTocVO().getTocDiv().getTocPtrList().get(0).getLinkRef());

    }
    
    
    public TocItemVO getNestedTocItem() 
    {
        TocItemVO tocItem = new TocItemVO();
        MetadataSetVO mds = getMdsPublication1();
        tocItem.getMetadataSets().add(mds);
        ContextRO collectionRef = new ContextRO();
        collectionRef.setObjectId(PUBMAN_TEST_COLLECTION_ID);
        tocItem.setContext(collectionRef);
        tocItem.setContentModel("escidoc:TOC");
        
        TocVO tocVO = new TocVO();
        tocItem.setTocVO(tocVO);
        
        tocVO.setTocBase("http://localhost:8080");
        tocVO.setTocId("tocId1");
        tocVO.setTocLabel("TocTestLabel");
        tocVO.setTocType("monograph");
        
        TocDivVO tocDivVO = new TocDivVO();
        tocDivVO.setId("tocDivIdRoot12");
        tocDivVO.setLabel("rootLabel");
        tocDivVO.setOrder(1);
        tocDivVO.setOrderLabel("1.0");
        tocDivVO.setType("chapter");
        
        tocVO.setTocDiv(tocDivVO);
        
        TocDivVO tocDivVO2 = new TocDivVO();
        tocDivVO2.setId("tocDivId1.1");
        tocDivVO2.setLabel("Chapter 1.1");
        tocDivVO2.setOrder(2);
        tocDivVO2.setOrderLabel("1.1");
        tocDivVO2.setType("chapter");
        tocDivVO.getTocDivList().add(tocDivVO2);
        
        TocPtrVO tocPtrVO = new TocPtrVO();
        tocPtrVO.setId("tocPtrId1");
        tocPtrVO.setLinkRef("/ir/container/escidoc:11");
        tocPtrVO.setLinkType("simple");
        tocPtrVO.setLinkTitle("link title container");
        tocPtrVO.setMimetype("text/plain");
        tocPtrVO.setUse("DEFAULT");
        tocDivVO.getTocPtrList().add(tocPtrVO);
        
        TocDivVO tocDivVO3 = new TocDivVO();
        tocDivVO3.setId("tocDivId1.11");
        tocDivVO3.setLabel("Chapter 1.1.1");
        tocDivVO3.setOrder(3);
        tocDivVO3.setOrderLabel("1.1.1");
        tocDivVO3.setType("chapter");
        tocDivVO2.getTocDivList().add(tocDivVO3);
        
        return tocItem;
    }
}
