/*
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
package test.framework.cmm;

import static org.junit.Assert.assertNotNull;

import org.apache.log4j.Logger;
import org.junit.Test;

import test.framework.TestBase;
import de.escidoc.core.common.exceptions.application.notfound.ContentModelNotFoundException;
import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 * Testcases for the basic service ContentTypeHandler.
 * 
 * @author Johannes Mueller (initial creation)
 * @author $Author: wfrank $ (last modification)
 * @version $Revision: 314 $ $LastChangedDate: 2007-11-07 13:12:14 +0100 (Wed, 07 Nov 2007) $
 * @revised by FrW: 10.03.2008
 */
public class TestContentModels extends TestBase
{
    private Logger logger = Logger.getLogger(getClass());

    /**
     * Test method for {@link de.fiz.escidoc.ctm.ContentTypeHandlerLocal#retrieve(java.lang.String)}.
     */
    @Test
	public void retrieveContentTypePubItem() throws Exception
	{
        String id = PUBITEM_TYPE_ID;
        long zeit = -System.currentTimeMillis();
        String type = ServiceLocator.getContentModelHandler(userHandle).retrieve(id);
        zeit += System.currentTimeMillis(); 
        logger.info("retrieveContentTypePubItem(" +  id + ")->" + zeit + "ms");
        logger.debug("ContentType(" + id + ")=" + type);
        assertNotNull(type);
	}

    /**
     * Test method for {@link de.fiz.escidoc.ctm.ContentTypeHandlerLocal#retrieve(java.lang.String)}.
     */
    @Test (expected=ContentModelNotFoundException.class)
    public void retrieveContentTypeNotExisting() throws Exception
    {
        String id = ILLEGAL_ID;
        long zeit = -System.currentTimeMillis();
        String type = ServiceLocator.getContentModelHandler(userHandle).retrieve(id);
        zeit += System.currentTimeMillis(); 
        logger.info("retrieveContentTypeNotExisting(" +  id + ")->" + zeit + "ms");
        logger.debug("ContentType(" + id + ")=" + type);
        assertNotNull(type);
    }
}
