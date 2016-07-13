/*
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

package de.mpg.mpdl.inge.exportmanager;


import static org.junit.Assert.assertFalse;

import java.io.FileOutputStream;

import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;

import de.mpg.mpdl.inge.exportmanager.Export;
import de.mpg.mpdl.inge.exportmanager.ExportHandler;


public class ExportTest {
  private ExportHandler export = new Export();
  private String pubManItemList;
  private static String facesItemList;
  private long start = 0;

  private static Logger logger = Logger.getLogger(ExportTest.class);

  private FileOutputStream fos;

  /**
   * Test generate output.
   * 
   * @throws Exception Any exception.
   */
  @Test
  @Ignore
  public final void testExports() throws Exception {

    byte[] result;
    for (String ef : new String[] {"ENDNOTE", "BIBTEX", "APA"}) {
      logger.info("start " + ef + " export ");
      start = -System.currentTimeMillis();
      result = export.getOutput(ef, ef.equals("APA") ? "snippet" : null, null, pubManItemList);
      start += System.currentTimeMillis();
      assertFalse(ef + " export failed", result == null || result.length == 0);
      logger.info(ef + " export (" + start + "ms):\n" + new String(result));

    }

  }


}