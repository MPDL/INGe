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

package test.common.xmltransforming;

import static org.junit.Assert.assertEquals;

import org.apache.log4j.Logger;
import org.junit.Test;

import test.common.TestBase;
import de.mpg.escidoc.services.common.xmltransforming.JiBXHelper;

/**
 * Tests for testing the {@link de.mpg.escidoc.services.common.xmltransforming.JiBXHelper} class.
 * 
 * @author Johannes Mueller (initial creation)
 * @author $Author: jmueller $ (last modification)
 * @version $Revision: 611 $ $LastChangedDate: 2007-11-07 12:04:29 +0100 (Wed, 07 Nov 2007) $
 * @revised by MuJ: 03.09.2007
 */
public class JiBXHelperTest extends TestBase
{
    /**
     * Logger for this class.
     */
    private static final Logger logger = Logger.getLogger(JiBXHelperTest.class);

    private final String[] samples = { "Simple String without problematic characters.", "an ampersand: &", "an escaped ampersand: &amp;",
            "an apostrophe '", "MPI-HD-49%25'N,8%43'O" };

    /**
     * Test of escaping/unescaping XML special characters
     */
    @Test
    public final void testXmlEscaping()
    {
        String escapedSample, unescapedSample;
        for (String sample : samples)
        {
            escapedSample = JiBXHelper.xmlEscape(sample);
            logger.debug("  Escaping: '" + sample + "': '" + escapedSample + "'");
            unescapedSample = JiBXHelper.xmlUnescape(escapedSample);
            logger.debug("Unescaping: '" + escapedSample + "': '" + unescapedSample + "'");
            assertEquals(sample, unescapedSample);
        }
    }

}
