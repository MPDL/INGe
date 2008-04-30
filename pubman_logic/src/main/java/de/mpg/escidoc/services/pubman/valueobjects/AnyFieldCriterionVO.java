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

package de.mpg.escidoc.services.pubman.valueobjects;

/**
 * any filed criterion vo for the advanced search.
 * @created 10-Mai-2007 15:42:17
 * @author NiH
 * @version 1.0
 * Revised by NiH: 13.09.2007
 */
public class AnyFieldCriterionVO extends CriterionVO
{
	/** serial for the serializable interface*/
	private static final long serialVersionUID = 1L;
	
    // boolean flag for the full text search
    private boolean includeFiles;

    /**
     * constructor.
     */
    public AnyFieldCriterionVO()
    {
        super();
    }

    public boolean isIncludeFiles()
    {
        return includeFiles;
    }

    public void setIncludeFiles(boolean newVal)
    {
        includeFiles = newVal;
    }
}