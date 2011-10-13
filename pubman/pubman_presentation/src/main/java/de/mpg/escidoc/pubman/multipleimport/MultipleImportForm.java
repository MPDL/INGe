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

package de.mpg.escidoc.pubman.multipleimport;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.appbase.FacesBean;

/**
 * Session bean to hold data needed for an import of multiple items.
 *
 * @author franke (initial creation)
 * @author $Author: mfranke $ (last modification)
 * @version $Revision: 4287 $ $LastChangedDate: 2011-03-10 14:23:22 +0100 (Do, 10 Mrz 2011) $
 *
 */
public class MultipleImportForm extends FacesBean
{
    
    private static final Logger logger = Logger.getLogger(MultipleImportForm.class);
    
    public static final String BEAN_NAME = "NewMultipleImport";
    
    public MultipleImportForm()
    {
        super.init();
        try
        {
            ((MultipleImport) getSessionBean(MultipleImport.class)).initConfigParameters();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

}
