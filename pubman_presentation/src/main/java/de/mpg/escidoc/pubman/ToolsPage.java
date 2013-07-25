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
 * Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft
 * für wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Förderung der Wissenschaft e.V.
 * All rights reserved. Use is subject to license terms.
 */

package de.mpg.escidoc.pubman;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.appbase.BreadcrumbPage;


/**
 * BackingBean for Workspaces Page (ToolsPage.jsp).
 * @author Matthias Walter (initial creation)
 * @author $Author: walter $ (last modification)
 * @version $Revision: 4371 $ $LastChangedDate: 2011-03-23 13:25:53 +0100 (Mi, 23 Mrz 2011) $
 *
 */
public class ToolsPage extends BreadcrumbPage
{
    private static Logger logger = Logger.getLogger(ToolsPage.class);
    public static final String BEAN_NAME = "ToolsPage";


    /**
     * Public constructor.
     */
    public ToolsPage()
    {
        this.init();
    }

    /**
     * Callback method that is called whenever a page containing this page fragment is navigated to, either directly via
     * a URL, or indirectly via page navigation.
     */
    @Override
    public void init()
    {
        super.init();
    }

    @Override
    public boolean isItemSpecific()
    {
        return false;
    }
}
