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

import de.mpg.mpdl.inge.model.valueobjects.ValueObject;

/**
 * Wrapper for ValueObjects that provides additional attributes for the presentation layer.
 * 
 * @author: Thomas Diebäcker, created 29.08.2007
 * @version: $Revision$ $LastChangedDate$
 */
public class ValueObjectWrapper {
  protected ValueObject valueObject = null;
  protected boolean selected = false;

  public ValueObjectWrapper() {}

  public ValueObjectWrapper(ValueObject valueObject) {
    this.valueObject = valueObject;
  }

  public ValueObject getValueObject() {
    return this.valueObject;
  }

  public void setValueObject(ValueObject valueObject) {
    this.valueObject = valueObject;
  }

  public boolean getSelected() {
    return this.selected;
  }

  public void setSelected(boolean selected) {
    this.selected = selected;
  }
}
