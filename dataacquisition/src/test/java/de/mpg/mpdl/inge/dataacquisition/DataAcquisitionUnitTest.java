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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.mpg.mpdl.inge.dataacquisition.valueobjects.DataSourceVO;
import de.mpg.mpdl.inge.transformation.TransformerFactory;

/**
 * Test suite for unit test of dataAcquisition service.
 * 
 * @author Friederike Kleinfercher (initial creation)
 */
public class DataAcquisitionUnitTest {
  private DataHandlerService datahandler = new DataHandlerService();
  private final DataSourceHandlerService dataSourceHandler = new DataSourceHandlerService();

  private static final String arxivId = "arXiv:0904.3933";

  @Before
  public void setup() throws Exception {}

  @Ignore
  @Test
  public void fetchFromCone() throws Exception {
    String fileEnding = Util.retrieveFileEndingFromCone("application/pdf");
    Assert.assertNotNull(fileEnding);
    Assert.assertTrue(fileEnding.equals(".pdf"));
  }

  @Ignore
  @Test
  public void fetchArxiv() throws Exception {
    final DataSourceVO dataSourceVO = this.dataSourceHandler.getSourceByName("arXiv");
    final String identifier = Util.trimIdentifier(dataSourceVO, arxivId);

    byte[] test = this.datahandler.doFetchMetaData("arXiv", dataSourceVO, identifier, TransformerFactory.getInternalFormat());
    Assert.assertNotNull(test);
  }
}
