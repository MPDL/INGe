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

/**
 * This converter escapes HTML elements for displaying them correctly in the jsp pages
 * 
 * @author Tobias Schraut
 */
package de.mpg.mpdl.inge.pubman.web.util.converter;

import de.mpg.mpdl.inge.pubman.web.util.CommonUtils;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.ConverterException;

/**
 * JSF converter for javascript strings.
 * 
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
public class JavascriptEscapeConverter implements Converter {
  public static final String CONVERTER_ID = "HTMLEscapeConverter";

  @Override
  public String getAsString(FacesContext context, UIComponent c, Object object) throws ConverterException {
    final String textValue = object.toString();
    return CommonUtils.javascriptEscape(textValue);
  }

  @Override
  public Object getAsObject(FacesContext context, UIComponent c, String text) {
    return text;
  }
}
