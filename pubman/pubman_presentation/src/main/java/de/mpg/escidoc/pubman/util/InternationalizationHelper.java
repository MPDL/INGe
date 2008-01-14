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

import org.apache.log4j.Logger;

/**
 * Class for Internationalization settings.
 * 
 * @author: Tobias Schraut, created 04.07.2007
 * @version: $Revision: 1587 $ $LastChangedDate: 2007-11-20 10:54:36 +0100 (Tue, 20 Nov 2007) $ Revised by ScT: 20.08.2007
 */
public class InternationalizationHelper
{
    public static final String BEAN_NAME = "util$InternationalizationHelper";
    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(InternationalizationHelper.class);
    // The known resource bundles that can be selected as described in the
    // faces-config.xml
    public static final String LABLE_BUNDLE_DE = "de.mpg.escidoc.pubman.bundle.Label_de";
    public static final String LABLE_BUNDLE_EN = "de.mpg.escidoc.pubman.bundle.Label_en";
    public static final String MESSAGES_BUNDLE_DE = "de.mpg.escidoc.pubman.bundle.Messages_de";
    public static final String MESSAGES_BUNDLE_EN = "de.mpg.escidoc.pubman.bundle.Messages_en";
    public static final String HELP_PAGE_DE = "help/eSciDoc_help_de.html";
    public static final String HELP_PAGE_EN = "help/eSciDoc_help_en.html";
    String selectedLableBundle;
    String selectedMessagesBundle;
    String selectedHelpPage;

    public InternationalizationHelper()
    {
        this.selectedLableBundle = LABLE_BUNDLE_EN;
        this.selectedMessagesBundle = MESSAGES_BUNDLE_EN;
        this.selectedHelpPage = HELP_PAGE_EN;
    }

    // Getters and Setters
    public String getSelectedLableBundle()
    {
        return selectedLableBundle;
    }

    public void setSelectedLableBundle(String selectedLableBundle)
    {
        this.selectedLableBundle = selectedLableBundle;
    }

    public String getSelectedMessagesBundle()
    {
        return selectedMessagesBundle;
    }

    public void setSelectedMessagesBundle(String selectedMessagesBundle)
    {
        this.selectedMessagesBundle = selectedMessagesBundle;
    }

    public String getSelectedHelpPage()
    {
        return selectedHelpPage;
    }

    public void setSelectedHelpPage(String selectedHelpPage)
    {
        this.selectedHelpPage = selectedHelpPage;
    }
}
