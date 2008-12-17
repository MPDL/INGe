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

package de.mpg.escidoc.pubman;

import java.lang.reflect.Method;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.appbase.BreadcrumbPage;
import de.mpg.escidoc.pubman.easySubmission.EasySubmission;

/**
 * BackingBean for EasySubmissionPage.jsp. This one is empty because all code is implemented in the BackingBean of the
 * fragment.
 *
 * @author: mfranke
 * @author: $Author: mfranke$
 * @version: $Revision$ $LastChangedDate$
 */
public class EasySubmissionPage extends BreadcrumbPage
{
	private static Logger logger = Logger.getLogger(EasySubmissionPage.class);
	
	public final static String BEAN_NAME = "EasySubmissionPage";
	
    /**
     * Public constructor.
     */
    public EasySubmissionPage()
    {
        this.init();
    }

    /**
     * Callback method that is called whenever a page containing this page fragment is navigated to, either directly via
     * a URL, or indirectly via page navigation.
     */
    public void init()
    {
        // Perform initializations inherited from our superclass
        super.init();
    }

    protected Method getDefaultAction() throws NoSuchMethodException
    {
    	EasySubmission easySubmission = (EasySubmission)getRequestBean(EasySubmission.class);
    	return easySubmission.getClass().getMethod("newEasySubmission", null);
    }
}
