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
package test.framework.om;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;

import de.escidoc.core.common.exceptions.application.notfound.ContainerNotFoundException;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 * Testcases for the basic service IngestHandler.
 * 
 * @author Wilhelm Frank (initial creation)
 * @version $Revision:
 */
public class TestIngestHandler extends TestItemBase
{
    private Logger logger = Logger.getLogger(getClass());

    /**
     * Test method for {@link de.fiz.escidoc.om.IngestHandlerLocal#ingest(java.lang.String)}.
     */
    @Test
    public void ingestion() throws Exception
    {
        userHandle = loginSystemAdministrator();
        String ingest = readFile(INGEST_TEST_FILE);
        long zeit = -System.currentTimeMillis();
        ingest = ServiceLocator.getIngestHandler(userHandle).ingest(ingest);
        zeit += System.currentTimeMillis();
        logger.info("ingestion()->" + zeit + "ms");
        logger.debug("Ingestion()=" + ingest);
        assertNotNull(ingest);
    }

 }
