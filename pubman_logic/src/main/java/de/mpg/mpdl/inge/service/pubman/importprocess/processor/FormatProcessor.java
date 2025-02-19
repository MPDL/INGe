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

package de.mpg.mpdl.inge.service.pubman.importprocess.processor;

import java.io.File;
import java.util.Iterator;

/**
 * TODO Description
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public abstract class FormatProcessor implements Iterator<String> {

  File sourceFile;
  String encoding;

  /**
   * @return the source
   */
  public File getSourceFile() {
    return this.sourceFile;
  }

  /**
   * @param source the source to set
   */
  public void setSourceFile(File source) {
    this.sourceFile = source;
  }

  /**
   * @return the encoding
   */
  public String getEncoding() {
    return this.encoding;
  }

  /**
   * @param encoding the encoding to set
   */
  public void setEncoding(String encoding) {
    this.encoding = encoding;
  }

  public abstract int getLength();

  public abstract String getDataAsBase64();
}
