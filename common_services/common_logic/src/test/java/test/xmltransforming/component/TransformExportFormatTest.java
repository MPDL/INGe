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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;

import test.TestBase;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.valueobjects.ExportFormatVO;
import de.mpg.escidoc.services.common.valueobjects.FileFormatVO;
import de.mpg.escidoc.services.common.xmltransforming.XmlTransformingBean;

/**
 * Test of {@link XmlTransforming} methods for ExportFormat transforming.
 *
 * @author mfranke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate:$
 * @revised 
 */
public class TransformExportFormatTest extends TestBase
{
	private static XmlTransforming xmlTransforming = new XmlTransformingBean();

    private Logger logger = Logger.getLogger(getClass());

    /**
     * Test of {@link XmlTransforming#transformToExportFormatVOList(String)}.
     * 
     * @throws Exception Any exception.
     */
    @Test
    public final void testTransformToExportFormatVOList() throws Exception
    {
        logger.info("### testTransformToExportFormatVOList ###");

        String exportFormatList =
            readFile("src/test/resources/xmltransforming/component/transformExportFormatTest/export-format-list_sample1.xml");
        List<ExportFormatVO> formatList = xmlTransforming.transformToExportFormatVOList(exportFormatList);
        assertNotNull(formatList);
        assertFalse(formatList.isEmpty());
        assertEquals(2, formatList.size());
        
        for (ExportFormatVO formatVO : formatList)
        {
            logger.info(formatVO);
            logger.info("Style Name: " +  formatVO.getName());
            logger.info("Style Id: " + formatVO.getId());
            logger.info("Style Description : " + formatVO.getDescription());
            for (FileFormatVO fileFormatVO : formatVO.getFileFormats()) 
            	logger.info(
            			"File Format name: " + fileFormatVO.getName()
            			+ "; File Format mime-type: " + fileFormatVO.getMimeType() 
            	);
            for (String creator : formatVO.getCreators()) 
            	logger.info("creator: " + creator);
        }
    }
}
