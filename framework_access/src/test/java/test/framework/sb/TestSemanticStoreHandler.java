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
package test.framework.sb;

import static org.junit.Assert.assertNotNull;
import org.apache.log4j.Logger;
import org.junit.Test;
import test.framework.om.TestItemBase;
import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 * Testcases for the basic service semantic store handler.
 *
 * @author Peter (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class TestSemanticStoreHandler extends TestItemBase
{
    private static final String OUTPUT_FORMAT = "RDF/XML";

    private Logger logger = Logger.getLogger(getClass());

    /**
     * Test method for {@link de.fiz.escidoc.ssh.SemanticStoreHandlerRemote#spo(java.lang.String)}.
     */
    @Test
    public void findTriplesOfObject() throws Exception
    {
        String item = readFile(ITEM_FILE);
        item = ServiceLocator.getItemHandler(userHandle).create(item);
        assertNotNull(item);
        String id = getId(item);
        String param = "<param>"
                     + "<query>&lt;info:fedora/" + id + "&gt; * *</query>"
                     + "<format>" + OUTPUT_FORMAT + "</format>"
                     + "</param>";
        logger.debug("Param=" + param);
        long zeit = -System.currentTimeMillis();
        try
        {
            ServiceLocator.getSemanticScoreHandler(userHandle).spo(param);
        }
        finally
        {
            zeit += System.currentTimeMillis();
            logger.info("findTriplesOfObject()->" + zeit + "ms");
        }
    }

    /**
     * Test method for {@link de.fiz.escidoc.ssh.SemanticStoreHandlerRemote#spo(java.lang.String)}.
     */
    @Test
    public void findTriplesOfIllegalObject() throws Exception
    {
        String param = "<param>"
                     + "<query>&lt;info:fedora/" + ILLEGAL_ID + "&gt; * *</query>"
                     + "<format>" + OUTPUT_FORMAT + "</format>"
                     + "</param>";
        logger.debug("Param=" + param);
        long zeit = -System.currentTimeMillis();
        try
        {
            ServiceLocator.getSemanticScoreHandler(userHandle).spo(param);
        }
        finally
        {
            zeit += System.currentTimeMillis();
            logger.info("findTriplesOfObject()->" + zeit + "ms");
        }
    }

    /**
     * Test method for {@link de.fiz.escidoc.ssh.SemanticStoreHandlerRemote#spo(java.lang.String)}.
     */
    @Test
    public void findObjectIsRevisionOf() throws Exception
    {
        String param = "<param>"
                     + "<query>&lt;info:fedora/" + createRelatedItems() + "&gt; http://www.escidoc.de/ontologies/mpdl-ontologies/content-relations#isRevisionOf *</query>"
                     + "<format>" + OUTPUT_FORMAT + "</format>"
                     + "</param>";
        logger.debug("Param=" + param);
        long zeit = -System.currentTimeMillis();
        try
        {
            String result = ServiceLocator.getSemanticScoreHandler(userHandle).spo(param);
            logger.debug("Result=" + result);
        }
        finally
        {
            zeit += System.currentTimeMillis();
            logger.info("findObjectIsRevisionOf()->" + zeit + "ms");
        }
    }

    /**
     * Test method for {@link de.fiz.escidoc.ssh.SemanticStoreHandlerRemote#spo(java.lang.String)}.
     */
    @Test
    public void findRevisionsOfObject() throws Exception
    {
        String param = "<param>"
                     + "<query>* " + PREDICATE_ISREVISIONOF + " &lt;info:fedora/" + createRelatedItems() + "&gt;</query>"
                     + "<format>" + OUTPUT_FORMAT + "</format>"
                     + "</param>";
        logger.debug("Param=" + param);
        long zeit = -System.currentTimeMillis();
        try
        {
            String result = ServiceLocator.getSemanticScoreHandler(userHandle).spo(param);
            logger.debug("Result=" + result);
        }
        finally
        {
            zeit += System.currentTimeMillis();
            logger.info("findRevisionOfObject()->" + zeit + "ms");
        }
    }

    /**
     * Create three items and creates isRevisionOf relations of two of them to the third one.
     * 
     * @return The id of the target item.
     * @throws Exception
     */
    private String createRelatedItems() throws Exception
    {
        // Create target item
        String target = ServiceLocator.getItemHandler(userHandle).create(readFile(ITEM_FILE));
        String targetId = getId(target);
        String targetMd = getModificationDate(target);

        // Create Source item 1
        String source = ServiceLocator.getItemHandler(userHandle).create(readFile(ITEM_FILE));
        String sourceId = getId(source);
        String sourceMd = getModificationDate(source);
        
        // add relation source 1 -- isRevisionOf --> target
        String param = "<param last-modification-date=\"" + sourceMd + "\">" +
                       "    <relation>" +
                       "        <targetId>" + targetId + "</targetId>" +
                       "        <predicate>" + PREDICATE_ISREVISIONOF + "</predicate>" +
                       "    </relation>" +
                       "</param>";
        ServiceLocator.getItemHandler(userHandle).addContentRelations(sourceId, param);
        logger.debug(sourceId + " isRevisionOf " + targetId);

        // Create Source item 2
        source = ServiceLocator.getItemHandler(userHandle).create(readFile(ITEM_FILE));
        sourceId = getId(source);
        sourceMd = getModificationDate(source);
        
        // add relation source 2 -- isRevisionOf --> target
        param = "<param last-modification-date=\"" + sourceMd + "\">" +
                       "    <relation>" +
                       "        <targetId>" + targetId + "</targetId>" +
                       "        <predicate>" + PREDICATE_ISREVISIONOF + "</predicate>" +
                       "    </relation>" +
                       "</param>";
        ServiceLocator.getItemHandler(userHandle).addContentRelations(sourceId, param);
        logger.debug(sourceId + " isRevisionOf " + targetId);
    
        return targetId;
    }
}
