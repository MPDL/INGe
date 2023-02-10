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

package de.mpg.mpdl.inge.pubman.web.util.renderer;

/**
 * Custom MessagesRenderer that prevents faces messages from being not encoded (u can disable this
 * renderer by adding the attribute escape=false to the <h:messages> tag)
 * 
 * @author walter (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.context.ResponseWriterWrapper;

import com.sun.faces.renderkit.html_basic.MessagesRenderer;

// TODO test if this is working in Versions after JSF 2.2
// @FacesRenderer(componentFamily = "javax.faces.Messages", rendererType = "javax.faces.Messages")
// Annotation not working correctly. For this reason it is added to the renderkit in
// faces-config.xml
public class EscapableMessagesRenderer extends MessagesRenderer {

  @Override
  public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
    final ResponseWriter originalResponseWriter = context.getResponseWriter();
    context.setResponseWriter(new ResponseWriterWrapper() {

      @Override
      public ResponseWriter getWrapped() {
        return originalResponseWriter;
      }

      @Override
      public void writeText(Object text, UIComponent component, String property) throws IOException {
        final String string = String.valueOf(text);
        String escape = (String) component.getAttributes().get("escape");
        if (escape != null && !Boolean.valueOf(escape)) {
          super.write(string);
        } else {
          super.writeText(string, component, property);
        }
      }
    });

    super.encodeEnd(context, component); // Now, render it!
    context.setResponseWriter(originalResponseWriter); // Restore original writer.
  }
}
