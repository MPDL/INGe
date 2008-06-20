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

package test.common.xmltransforming.integration;

import static org.junit.Assert.assertTrue;

import java.net.URL;

import org.apache.log4j.Logger;
import org.junit.Test;

import test.common.TestBase;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO;
import de.mpg.escidoc.services.common.xmltransforming.XmlTransformingBean;
import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 * Test class for {@link XmlTransforming#transformToFilterTaskParam(FilterTaskParamVO)}.
 * 
 * @author Johannes Mueller (initial creation)
 * @author $Author: jmueller $ (last modification)
 * @version $Revision: 611 $ $LastChangedDate: 2007-11-07 12:04:29 +0100 (Wed, 07 Nov 2007) $
 * @revised by MuJ: 03.09.2007
 */
public class TransformFileUrlTest extends TestBase
{
	private static XmlTransforming xmlTransforming = new XmlTransformingBean();
    private Logger logger = Logger.getLogger(getClass());

    /**
     * Test for {@link XmlTransforming#transformUploadResponseToFileURL(String)}.
     * 
     * @throws Exception
     */
    @Test
    public void transformUploadResponseToFileURL() throws Exception
    {
        String uploadResponseXML = readFile("xmltransforming/component/transformFileUrlTest/staging-file_sample1.xml");

        URL expectedURL = new URL(ServiceLocator.getFrameworkUrl()
                + "/st/staging-file/escidoctoken:2a1082d0-d6c8-11db-8655-af79371c28f2");

        URL url = xmlTransforming.transformUploadResponseToFileURL(uploadResponseXML);
        logger.debug("Deserialized URL: " + url.toString());
        logger.debug("Expected URL:     " + expectedURL.toString());

        assertTrue(url.equals(expectedURL));
    }
}
