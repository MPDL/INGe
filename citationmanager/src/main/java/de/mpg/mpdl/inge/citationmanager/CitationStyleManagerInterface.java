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

package de.mpg.mpdl.inge.citationmanager;

import java.io.IOException;

/**
 * Interface for managing of the Citation Styles. It includes 1) methods for CRUD operations 2)
 * validation 3) compilation 4) view
 * 
 * 
 * @author Vlad Makarenko (initial creation) $Author$ (last modification) $Revision$
 *         $LastChangedDate$
 * 
 **/
public interface CitationStyleManagerInterface {

  /**
   * Compile Citation Style
   * 
   * @param cs - name of Citation Style to be compiled
   * @throws CitationStyleManagerException
   */
  void compile(String cs) throws CitationStyleManagerException;

  /**
   * Validate Citation Style
   * 
   * @param cs - name of Citation Style to be validated
   * @throws IOException
   * @throws CitationStyleManagerException
   * @throws CitationStyleManagerException
   */
  String validate(String cs) throws CitationStyleManagerException;

  /**
   * Create Citation Style The method creates new Citation Style. Default Citation Style will be
   * taken as start up version of it.
   * 
   * @param cs - name of Citation Style to be created
   */
  void create(String cs);

  /**
   * Delete Citation Style
   * 
   * @param cs - name of Citation Style to be deleted
   */
  void delete(String cs);

  /**
   * Update Citation Style
   * 
   * @param cs - name of Citation Style to be updated
   * @param newCs - new definition for Citation Style
   */
  void update(String cs, String newCs);
}
