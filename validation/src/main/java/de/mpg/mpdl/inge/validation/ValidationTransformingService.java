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

package de.mpg.mpdl.inge.validation;

import java.io.StringReader;

import org.apache.log4j.Logger;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;

import de.mpg.mpdl.inge.model.xmltransforming.xmltransforming.exceptions.TransformingException;
import de.mpg.mpdl.inge.model.xmltransforming.xmltransforming.exceptions.UnmarshallingException;
import de.mpg.mpdl.inge.validation.valueobjects.ValidationReportVO;

/**
 * @author mfranke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class ValidationTransformingService {
  private static final Logger LOGGER = Logger.getLogger(ValidationTransformingService.class);

  public ValidationTransformingService() {}

  /**
   * {@inheritDoc}
   */
  public static ValidationReportVO transformToValidationReport(final String report)
      throws TransformingException {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("transformToValidationReport(String) - String report=" + report);
    }
    ValidationReportVO reportVO = null;
    try {
      IBindingFactory bfact = BindingDirectory.getFactory(ValidationReportVO.class);
      // unmarshal ValidationReportVO from String
      IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
      StringReader sr = new StringReader(report);
      reportVO = (ValidationReportVO) uctx.unmarshalDocument(sr, null);
    } catch (JiBXException e) {
      throw new UnmarshallingException("ValidationReportVO", e);
    } catch (java.lang.ClassCastException e) {
      throw new UnmarshallingException("ValidationReportVO", e);
    }

    return reportVO;
  }
}
