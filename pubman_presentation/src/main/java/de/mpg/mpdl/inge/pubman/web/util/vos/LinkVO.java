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

package de.mpg.mpdl.inge.pubman.web.util.vos;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Class for link objects in the browse by presentation
 *
 * @author kleinfe1 (initial creation)
 * @author $Author: haarlaender $ (last modification)
 * @version $Revision: 4246 $ $LastChangedDate: 2011-01-28 14:01:12 +0100 (Fr, 28 Jan 2011) $
 */
public class LinkVO {
  String label;
  String value;

  public LinkVO(String label, String value) {
    this.label = label;
    this.value = value;
  }

  public String getLabel() {
    return this.label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getValue() {
    return this.value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getEncodedLabel() {
    try {
      return URLEncoder.encode(this.label, StandardCharsets.UTF_8);
    } catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof LinkVO)) {
      return false;
    } else if (!this.label.equals(((LinkVO) obj).getLabel())) {
      return false;
    } else if (!this.value.equals(((LinkVO) obj).getValue())) {
      return false;
    } else {
      return true;
    }
  }
}
