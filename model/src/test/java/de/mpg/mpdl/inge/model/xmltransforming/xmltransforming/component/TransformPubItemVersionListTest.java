/*
 * 
 * CDDL HEADER START
 * 
 * The contents of this file are subject to the terms of the Common Development and Distribution
 * License, Version 1.0 only (the "License"). You may not use this file except in compliance with
 * the License.
 * 
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or
 * http://www.escidoc.org/license. See the License for the specific language governing permissions
 * and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License
 * file at license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with
 * the fields enclosed by brackets "[]" replaced with your own identifying information: Portions
 * Copyright [yyyy] [name of copyright owner]
 * 
 * CDDL HEADER END
 */

/*
 * Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft für
 * wissenschaftlich-technische Information mbH and Max-Planck- Gesellschaft zur Förderung der
 * Wissenschaft e.V. All rights reserved. Use is subject to license terms.
 */

package de.mpg.mpdl.inge.model.xmltransforming.xmltransforming.component;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;

import de.mpg.mpdl.inge.model.valueobjects.EventLogEntryVO;
import de.mpg.mpdl.inge.model.valueobjects.ItemVO;
import de.mpg.mpdl.inge.model.valueobjects.VersionHistoryEntryVO;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;
import de.mpg.mpdl.inge.model.xmltransforming.xmltransforming.XmlTransformingTestBase;

/**
 * Test of {@link PubManTransforming} methods for transforming and integration with common_logic and
 * the framework.
 * 
 * @author Johannes M&uuml;ller (initial creation)
 * @author $Author$ (last change)
 * @version $Revision$ $LastChangedDate$
 * @revised by MuJ: 20.09.2007
 */
public class TransformPubItemVersionListTest extends XmlTransformingTestBase {
  private static final Logger logger = Logger.getLogger(TransformPubItemVersionListTest.class);

  private static final String TEST_FILE_ROOT = "xmltransforming/component/transformPubItemVersionListTest/";
  private static final String VERSION_LIST_SAMPLE_FILE = TEST_FILE_ROOT + "version-list-sample.xml";
  private static final String VERSION_LIST_SAMPLE_FILE2 = TEST_FILE_ROOT + "version-list-sample2.xml";

  /**
   * @throws Exception
   */
  @Test
  public void testTransformPubItemVersionList() throws Exception {
    // create version list
    String itemVersionHistoryXml = readFile(VERSION_LIST_SAMPLE_FILE);
    assertNotNull(itemVersionHistoryXml);

    logger.info(itemVersionHistoryXml);

    // transform the version history XML to a list of EventVOs
    long zeit = -System.currentTimeMillis();
    List<VersionHistoryEntryVO> versionList = XmlTransformingService.transformToEventVOList(itemVersionHistoryXml);
    zeit += System.currentTimeMillis();
    logger.info("transformPubItemVersionList() -> " + zeit + "ms");

    assertNotNull(versionList);
    assertEquals(3, versionList.size());

    VersionHistoryEntryVO entry0 = versionList.get(0);

    assertNotNull(entry0);
    assertEquals(3, entry0.getReference().getVersionNumber());
    assertNotNull(entry0.getModificationDate());
    assertEquals(ItemVO.State.RELEASED, entry0.getState());
    assertNotNull(entry0.getEvents());
    assertEquals("Accepted", entry0.getReference().getLastMessage());

    List<EventLogEntryVO> events = entry0.getEvents();

    assertEquals(3, events.size());

    EventLogEntryVO event0 = events.get(0);

    assertEquals(EventLogEntryVO.EventType.RELEASE, event0.getType());
    assertNotNull(event0.getDate());
    assertEquals("Accepted", event0.getComment());
  }

  /**
   * @throws Exception
   */
  @Test
  public void testTransformPubItemVersionList2() throws Exception {
    // create version list
    String itemVersionHistoryXml = readFile(VERSION_LIST_SAMPLE_FILE2);
    assertNotNull(itemVersionHistoryXml);

    logger.info(itemVersionHistoryXml);

    // transform the version history XML to a list of EventVOs
    long zeit = -System.currentTimeMillis();
    List<VersionHistoryEntryVO> versionList = XmlTransformingService.transformToEventVOList(itemVersionHistoryXml);
    zeit += System.currentTimeMillis();
    logger.info("transformPubItemVersionList() -> " + zeit + "ms");

    assertNotNull(versionList);
    assertEquals(8, versionList.size());

    VersionHistoryEntryVO entry0 = versionList.get(0);

    assertNotNull(entry0);
    assertEquals(8, entry0.getReference().getVersionNumber());
    assertNotNull(entry0.getModificationDate());
    assertEquals(ItemVO.State.RELEASED, entry0.getState());
    assertNotNull(entry0.getEvents());
    assertEquals("TestKommentar", entry0.getReference().getLastMessage());

    List<EventLogEntryVO> events = entry0.getEvents();

    assertEquals(3, events.size());

    EventLogEntryVO event0 = events.get(0);

    assertEquals(EventLogEntryVO.EventType.RELEASE, event0.getType());
    assertNotNull(event0.getDate());
    assertEquals("TestKommentar", event0.getComment());
  }
}
