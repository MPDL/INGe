/*
*
* CDDL HEADER START
*
* The contents of this file are subject to the terms of the
* Common Development and Distribution License, Version 1.0 only
* (the "License"). You may not use this file except in compliance
* with the License.
*
* You can obtain a copy of the license at license/ESCIDOC.LICENSE
* or http://www.escidoc.de/license.
* See the License for the specific language governing permissions
* and limitations under the License.
*
* When distributing Covered Code, include this CDDL HEADER in each
* file and include the License file at license/ESCIDOC.LICENSE.
* If applicable, add the following below this CDDL HEADER, with the
* fields enclosed by brackets "[]" replaced with your own identifying
* information: Portions Copyright [yyyy] [name of copyright owner]
*
* CDDL HEADER END
*/

/*
* Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.pubman.util;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.HttpServletResponse;

public class RenderResponsePhaseListener implements PhaseListener
{
	public PhaseId getPhaseId()
	{
		return PhaseId.RENDER_RESPONSE;
	}

	public void afterPhase(PhaseEvent event)
	{
	}

	public void beforePhase(PhaseEvent event) 
	{
		FacesContext facesContext = event.getFacesContext();
		HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
//		response.addHeader("Pragma", "no-cache");
//		response.addHeader("Cache-Control", "no-cache, no-store, must-revalidate");
//		response.addHeader("Expires", "-1"); // some date in the past Mon, 8 Aug 2006 10:00:00 GMT
		
		// FIXME Set Debug Breakpoint here 
//		System.out.println("debugRenderResponse");
	}
}
