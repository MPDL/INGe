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

package de.mpg.mpdl.inge.cone;

import de.mpg.mpdl.inge.cone.ModelList.Model;

/**
 * Indicates whether an object can be an object of an s-p-o triple.
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public interface LocalizedTripleObject extends Describable {
  /**
   * Get the language of this element.
   *
   * @return The iso 639-2 code of the language. If the element has no language, "" or null is
   *         returned.
   */
  String getLanguage();

  /**
   * Set the language of this element.
   *
   * @param language The iso 639-2 code of the language. If the element has no language, language
   *        should be set to null.
   */
  void setLanguage(String language);

  /**
   * Check if this object has useful content.
   *
   * @return true if either this element has a value or a sub-element of it.
   */
  boolean hasValue();

  /**
   * Display this object as RDF/XML.
   *
   * @return The object as RDF
   * @throws ConeException
   */
  String toRdf(Model model) throws ConeException;

  /**
   * Display this object as JSON object.
   *
   * @return The object as JSON
   */
  String toJson();

  int hashCode();
}
