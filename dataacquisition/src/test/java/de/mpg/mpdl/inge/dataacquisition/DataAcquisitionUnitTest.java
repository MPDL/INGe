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

package de.mpg.mpdl.inge.dataacquisition;

import java.util.List;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.mpg.mpdl.inge.dataacquisition.DataHandlerService;
import de.mpg.mpdl.inge.dataacquisition.DataSourceHandlerService;
import de.mpg.mpdl.inge.dataacquisition.Util;
import de.mpg.mpdl.inge.dataacquisition.valueobjects.DataSourceVO;
import de.mpg.mpdl.inge.dataacquisition.valueobjects.MetadataVO;
import de.mpg.mpdl.inge.util.PropertyReader;

/**
 * Test suite for unit test of dataAcquisition service.
 * 
 * @author Friederike Kleinfercher (initial creation)
 */
public class DataAcquisitionUnitTest {
  private DataHandlerService datahandler = new DataHandlerService();

  private final static String arxivId = "arXiv:0904.3933";
  private final static String pmcId = "PMC2043518";
  private final static String bmcId = "1472-6890-9-1";
  private final static String spiresId = "hep-ph/0001001 ";
  private final static String escidocId = "escidoc:1801318";

  @Before
  public void setup() throws Exception {
    // skip tests in case of a release build
    Assume.assumeTrue(!PropertyReader.getProperty("escidoc.common.release.build").equals("true"));
  }

  @Test
  public void fetchFromCone() throws Exception {
    String fileEnding = Util.retrieveFileEndingFromCone("application/pdf");
    Assert.assertNotNull(fileEnding);
    Assert.assertTrue(fileEnding.equals(".pdf"));
  }

  @Test
  public void fetchArxiv() throws Exception {
    byte[] test = this.datahandler.doFetch("arxiv", arxivId);
    Assert.assertNotNull(test);
  }

  @Test
  public void fetchPmc() throws Exception {
    byte[] test = this.datahandler.doFetch("PubMedCentral", pmcId);
    Assert.assertNotNull(test);
  }

  @Test
  public void fetchBmc() throws Exception {
    byte[] test = this.datahandler.doFetch("BioMed Central", bmcId);
    Assert.assertNotNull(test);
  }

  @Test
  public void fetchSpires() throws Exception {
    byte[] test = this.datahandler.doFetch("spires", spiresId);
    Assert.assertNotNull(test);
  }

  @Test
  @Ignore
  // TODO: test with item after update of metadata on live server
  public void fetcheSciDoc() throws Exception {
    byte[] test = this.datahandler.doFetch("escidoc", escidocId);
    Assert.assertNotNull(test);
  }

  /**
   * This test fetches an arxiv item in all formats the sources provides.
   * 
   * @throws Exception
   */
  @Test
  public void fetchItemInSpecificFormatTest() throws Exception {
    DataSourceHandlerService sourceHandler = new DataSourceHandlerService();
    DataSourceVO test = sourceHandler.getSourceByIdentifier("arXiv");

    List<MetadataVO> formats = test.getMdFormats();
    byte[] ret;

    for (int i = 0; i < formats.size(); i++) {
      ret = null;
      ret = this.datahandler.doFetch("arxiv", arxivId, formats.get(i).getName());
      Assert.assertNotNull(ret);
    }
  }

}
