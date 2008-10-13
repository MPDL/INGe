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

package test.pubman.logging;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.Test;

import de.mpg.escidoc.services.pubman.logging.ApplicationLog;
import de.mpg.escidoc.services.pubman.logging.PMLogicMessages;

/**
 * Test class for application log
 *
 * @author Miriam Doelle (initial creation)
 * @author $Author: jmueller $ (last modification)
 * @version $Revision: 422 $ $LastChangedDate: 2007-11-07 12:15:06 +0100 (Wed, 07 Nov 2007) $
 * @Revised by BrP: 20.09.1007
 */
public class ApplicationLogTest
{
    /**
     * A simple Appender for the logging test.
     *
     * @author Peter Broszeit (initial creation)
     * @author $Author: jmueller $ (last modification)
     * @version $Revision: 422 $ $LastChangedDate: 2007-11-07 12:15:06 +0100 (Wed, 07 Nov 2007) $
     *
     */
    private class TestAppender extends AppenderSkeleton
    {
        private LoggingEvent lastLoggingEvent;

        /* (non-Javadoc)
         * @see org.apache.log4j.AppenderSkeleton#append(org.apache.log4j.spi.LoggingEvent)
         */
        @Override
        protected void append(LoggingEvent loggingevent)
        {
            lastLoggingEvent = loggingevent;
        }

        /**
         * Returns the last logging event which is set by append.
         * 
         * @return the lastLoggingEvent
         */
        public LoggingEvent getLastLoggingEvent()
        {
            return lastLoggingEvent;
        }
        
        /* (non-Javadoc)
         * @see org.apache.log4j.AppenderSkeleton#close()
         */
        public void close()
        {
        }

        /* (non-Javadoc)
         * @see org.apache.log4j.AppenderSkeleton#requiresLayout()
         */
        public boolean requiresLayout()
        {
            return false;
        }
    }

    /**
     * Test method for {@link de.mpg.escidoc.services.pubman.logging.ApplicationLog#info(de.mpg.escidoc.services.common.logging.Messages, java.lang.Object[])}.
     */
    @Test
    public void testInfo()
    {
        BasicConfigurator.resetConfiguration();
        TestAppender appender = new TestAppender();
        BasicConfigurator.configure(appender);
        ApplicationLog.info(PMLogicMessages.PUBITEM_CREATED, new Object[] { "4711", "userid"  });
        assertNotNull(appender.getLastLoggingEvent());
        assertEquals("The PubItem with ID 4711 has been created by user userid.", appender.getLastLoggingEvent().getMessage());
        assertEquals(Level.INFO, appender.getLastLoggingEvent().getLevel());
        assertEquals(ApplicationLog.APPLICATION_LOGGER_NAME, appender.getLastLoggingEvent().getLoggerName());
    }
}
