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
* Copyright 2006-2008 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/
package de.mpg.escidoc.services.pubman.statistics;

import java.util.HashMap;




/**
 * Singleton class for storing the mapping between report-definition ids and their sql-strings.
 *
 * @author Markus Haarlaender (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class ReportDefinitionStorage
{
    
    private static ReportDefinitionStorage instance = null;
    
    private HashMap<String, String> reportDefinitionMap;
    
    
    public ReportDefinitionStorage()
    {
        this.reportDefinitionMap = new HashMap<String, String>();
    }
    
    public static ReportDefinitionStorage getInstance()
    {
        if (instance == null)
        {
            instance = new ReportDefinitionStorage();
        }
        return instance;
    }

    public HashMap<String, String> getReportDefinitionMap()
    {
        return reportDefinitionMap;
    }

    public void setReportDefinitionMap(HashMap<String, String> reportDefinitionMap)
    {
        this.reportDefinitionMap = reportDefinitionMap;
    }

    
    
    
    
}
