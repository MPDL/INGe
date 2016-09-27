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

package de.mpg.mpdl.inge.citationmanager.impl;

import java.io.IOException;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.citationmanager.CitationStyleHandler;
import de.mpg.mpdl.inge.citationmanager.CitationStyleManagerException;
import de.mpg.mpdl.inge.model.valueobjects.ExportFormatVO;



/**
 * EJB implementation of interface {@link CitationStyleHandler}. It will use an external package. It
 * can be considered as a wrapper of the external package.
 * 
 * @author Galina Stancheva (initial creation)
 * @author $Author: MWalter $ (last modification)
 * @version $Revision: 5728 $ $LastChangedDate: 2015-10-08 16:26:04 +0200 (Thu, 08 Oct 2015) $
 *          Revised by StG: 24.08.2007
 */
@Stateless
@Remote
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class CitationStyleHandlerBean implements CitationStyleHandler {


  /**
   * Logger for this class.
   */
  private static Logger logger = Logger.getLogger(CitationStyleHandlerBean.class);
  CitationStyleExecutor cse = new CitationStyleExecutor();


  /**
   * {@inheritDoc}
   * 
   * @throws IOException
   * @throws IllegalArgumentException
   */
  public String explainStyles() throws CitationStyleManagerException {
    return cse.explainStyles();
  }

  /**
   * {@inheritDoc}
   */
  public byte[] getOutput(String itemList, ExportFormatVO exportFormat) throws CitationStyleManagerException {
      
    logger
        .debug("CitationStyleHandlerBean getOutput with citationStyle: " + exportFormat.getName());
    return cse.getOutput(itemList, exportFormat);
  }

  /**
   * {@inheritDoc}
   */
  public boolean isCitationStyle(String citationStyle) throws CitationStyleManagerException {
    return cse.isCitationStyle(citationStyle);
  }

  /**
   * {@inheritDoc}
   */
  public String[] getStyles() throws CitationStyleManagerException {
    return cse.getStyles();
  }

  /**
   * {@inheritDoc}
   */
  public String[] getOutputFormats(String cs) throws CitationStyleManagerException {
    return cse.getOutputFormats(cs);
  }

  /**
   * {@inheritDoc}
   */
  public String getMimeType(String cs, String ouf) throws CitationStyleManagerException {
    return cse.getMimeType(cs, ouf);
  }

}
