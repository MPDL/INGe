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

package de.mpg.mpdl.inge.pubman.web.util;

import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO;

/**
 * ObjectFormatter.java Backing bean.
 *
 * @author: Tobias Schraut, created 24.01.2007
 * @version: $Revision$ $LastChangedDate$ Revised by ScT: 21.08.2007
 */
public class ObjectFormatter {
  public ObjectFormatter() {}

  /**
   * distinguish between organizations and persons as creators and formats them to present on jsp
   * pages.
   *
   * @return String formatted creator
   * @param creatorObject unformatted creator VO
   */
  public String formatCreator(final CreatorVO creatorObject, String annotation) {
    final StringBuilder creator = new StringBuilder();

    if (creatorObject.getPerson() != null) {
      creator.append(creatorObject.getPerson().getFamilyName());
      if (creatorObject.getPerson().getGivenName() != null && !creatorObject.getPerson().getGivenName().isEmpty()) {
        creator.append(", " + creatorObject.getPerson().getGivenName());
      }
      if (annotation != null) {
        creator.append(annotation);
      }
    }
    if (creatorObject.getOrganization() != null && creatorObject.getOrganization().getName() != null) {
      creator.append(creatorObject.getOrganization().getName());
    }
    /*
     * Code below commented, as the language switching in jsp is problematic with already derived
     * label value upfront
     *
     * if (creatorObject != null && creatorObject.getRoleString() != null &&
     * !"".equals(creatorObject.getRoleString())) {
     *
     * creator.append(", " + getLabel("ENUM_CREATORROLE_" + creatorObject.getRoleString())); }
     */
    return creator.toString();
  }
}
