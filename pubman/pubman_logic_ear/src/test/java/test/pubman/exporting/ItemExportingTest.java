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

package test.pubman.exporting;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import test.pubman.TestBase;
import de.mpg.escidoc.services.common.valueobjects.ExportFormatVO;
import de.mpg.escidoc.services.common.valueobjects.FileFormatVO;
//import de.mpg.escidoc.services.common.valueobjects.FileFormatVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.pubman.ItemExporting;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import javax.naming.NamingException;

/**
* Test class for {@link de.mpg.escidoc.services.pubman.ItemExporting}.
* 
* @author Galina Stancheva (initial creation)
* @author $Author$ (last modification)
* @version $Revision$ $LastChangedDate$
* Revised by StG: 05.10.2007
*/
public class ItemExportingTest extends TestBase
{

    /**
     * Logger for this class.
     */
    private static final Logger logger = Logger.getLogger(ItemExportingTest.class);

    private static ItemExporting itemExporting;
    
    private static ExportFormatVO exportLayoutFormat;
    private static ExportFormatVO exportStructuredFormat;
    /**
     * @throws Exception
     */
    @Before
    public void setUp() throws NamingException
    {
        itemExporting  = (ItemExporting)getService(ItemExporting.SERVICE_NAME);
        
        FileFormatVO fileFormat = new FileFormatVO();
        fileFormat.setName(FileFormatVO.PDF_NAME);
        fileFormat.setMimeType(FileFormatVO.PDF_MIMETYPE);
        	
        exportLayoutFormat = new ExportFormatVO();
        exportLayoutFormat.setFormatType(ExportFormatVO.FormatType.LAYOUT);
        exportLayoutFormat.setName("APA");
        exportLayoutFormat.setSelectedFileFormat(fileFormat);

                
        fileFormat = new FileFormatVO();
        fileFormat.setName(FileFormatVO.TEXT_NAME);
        fileFormat.setMimeType(FileFormatVO.TEXT_MIMETYPE);
        exportStructuredFormat = new ExportFormatVO();
        exportStructuredFormat.setFormatType(ExportFormatVO.FormatType.STRUCTURED);
        exportStructuredFormat.setName("ENDNOTE");
        exportStructuredFormat.setSelectedFileFormat(fileFormat);
    }

    
    /**
     * Test method for
     * {@link de.mpg.escidoc.services.external.exportformathandling.ItemExporting#explainExportFormats()}.
     * 
     * @throws Exception 
     */
    @Test
    public void testExplainExportFormats() throws TechnicalException
    {
        logger.debug("\n\n\n###ItemExportingTest testExplainExportFormats ###");        
        List<ExportFormatVO> exportFormatsVOList = itemExporting.explainExportFormats();
        assertNotNull(exportFormatsVOList);
        assertFalse(exportFormatsVOList.isEmpty());
        for (ExportFormatVO formatVO : exportFormatsVOList)
        {
            logger.debug(formatVO);
        }
        assertEquals(4, exportFormatsVOList.size());
        

    }
    
    /**
     * Test method for
     * {@link de.mpg.escidoc.services.external.exportformathandling.ItemExporting#getOutput(Sring, String, String)}.
     * @throws Exception 
     */
    @Test
    public void testGetLayoutFormatOutput() throws TechnicalException
    {
        logger.debug("\n\n\n###ItemExportingTest testGetLayoutFormatOutput ###");

        // create a List<PubItemVO> from the scratch.
        List<PubItemVO> pubItemList = new ArrayList<PubItemVO>();
        PubItemVO pubItem;
        for (int i = 0; i < 1; i++)
        {
            pubItem = getNewPubItemWithoutFiles();
            pubItemList.add(pubItem);
        }         
        assertNotNull(pubItemList);
        logger.debug("input pubItemList.size() " + pubItemList.size());

        long zeit = -System.currentTimeMillis();
        byte[] itemsExportData = itemExporting.getOutput(exportLayoutFormat, pubItemList);        
        zeit += System.currentTimeMillis();
        logger.info("getOutput()->" + zeit + "ms");
        logger.info("List<PubItemVO> exported into " + exportLayoutFormat.getName()+" "+exportLayoutFormat.getSelectedFileFormat().getName() );
        assertNotNull(itemsExportData);
        logger.debug("export data  =\n" + new String(itemsExportData));
        
    }
 

    
    /**
     * Test method for
     * {@link de.mpg.escidoc.services.external.exportformathandling.ItemExporting#getOutput(Sring, String, String)}.
     * 
     * @throws Exception 
     */
    @Test
    public void testGetStructuredFormatOutput() throws TechnicalException
    {
        logger.debug("\n\n\n###ItemExportingTest testGetStructuredFormatOutput ###");

        // create a List<PubItemVO> from the scratch.
        List<PubItemVO> pubItemList = new ArrayList<PubItemVO>();
        PubItemVO pubItem;
        for (int i = 0; i < 2; i++)
        {
            pubItem = getNewPubItemWithoutFiles();
            pubItemList.add(pubItem);
        }         
        assertNotNull(pubItemList);
        logger.debug("input pubItemList.size() " + pubItemList.size());

        long zeit = -System.currentTimeMillis();
        
        byte[] itemsExportData = itemExporting.getOutput(exportStructuredFormat, pubItemList);        
        zeit += System.currentTimeMillis();
        logger.info("getO()->" + zeit + "ms");
        logger.info("List<PubItemVO> exported into " + exportStructuredFormat.getName()+" "+exportStructuredFormat.getSelectedFileFormat().getName() );
        assertNotNull(itemsExportData);
        logger.debug("export data  =\n" + new String(itemsExportData));

    }
}
