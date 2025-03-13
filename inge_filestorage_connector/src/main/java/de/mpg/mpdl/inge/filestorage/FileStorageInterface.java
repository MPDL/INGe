/*
 *
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the Common Development and Distribution
 * License, Version 1.0 only (the "License"). You may not use this file except in compliance with
 * the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license.
 * See the License for the specific language governing permissions and limitations under the
 * License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License
 * file at license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with
 * the fields enclosed by brackets "[]" replaced with your own identifying information: Portions
 * Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 */

/*
 * Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft für
 * wissenschaftlich-technische Information mbH and Max-Planck- Gesellschaft zur Förderung der
 * Wissenschaft e.V. All rights reserved. Use is subject to license terms.
 */

package de.mpg.mpdl.inge.filestorage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;

/**
 * Interface for file storage systems
 *
 * @author walter (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public interface FileStorageInterface {

  /**
   * creates a file in a file storage
   *
   * @param fileInputstream
   * @param fileName
   * @return a path or id with which the file can be retrieved again
   * @throws IOException
   */
  String createFile(InputStream fileInputstream, String fileName) throws IngeTechnicalException;

  /**
   * reads a file from the file storage into a given OutputStream
   *
   * @param filePath
   * @param out
   * @throws IOException
   */
  void readFile(String filePath, OutputStream out) throws IngeTechnicalException;

  void readFile(String filePath, OutputStream out, Range range) throws IngeTechnicalException;

  /**
   * deletes a file from the file storage
   *
   * @param filePath
   * @throws Exception
   */
  void deleteFile(String filePath) throws IngeTechnicalException;



}
