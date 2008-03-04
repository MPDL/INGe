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

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.appbase.BreadcrumbPage;

/**
 * CommonSessionBean.java General session bean for storing session wide information Created on 30. Mai 2007, 16:53
 * 
 * @author: Tobias Schraut, created 30.05.2007
 * @version: $Revision: 1587 $ $LastChangedDate: 2007-11-20 10:54:36 +0100 (Di, 20 Nov 2007) $ Revised by ScT: 17.08.2007
 */
public class CommonSessionBean extends BreadcrumbPage
{
    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(ErrorPage.class);
    // used by calling components to get this Bean
    public final static String BEAN_NAME = "CommonSessionBean";
    // flag to examine if the pubman application has been started as GUI Tool.
    private boolean runAsGUITool = false;

    /**
     * Public constructor
     */
    public CommonSessionBean()
    {
    	
    }
    // Getters and Setters
    public boolean isRunAsGUITool()
    {
        return runAsGUITool;
    }

    public void setRunAsGUITool(boolean runAsGUITool)
    {
        this.runAsGUITool = runAsGUITool;
    }

    /**
     * Generate a string for displaying file sizes.
     * Added by FrM to compute a better result for values < 1024.
     * 
     * @param size The size of an uploaded file.
     * @return A string representing the file size in a readable format.
     */
	public String computeFileSize(long size) {
		if (size < 1024)
		{
			return size + getLabel("ViewItemMedium_lblFileSizeB");
		}
		else if (size < 1024 * 1024)
		{
			return ((size - 1) / 1024 + 1) + getLabel("ViewItemMedium_lblFileSizeKB");
		}
		else
		{
			return ((size - 1) / (1024 * 1024) + 1) + getLabel("ViewItemMedium_lblFileSizeMB");
		}
	}

}
