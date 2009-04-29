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

package de.mpg.escidoc.pubman.multipleimport;

import java.util.List;

import javax.faces.context.FacesContext;

import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.multipleimport.ImportLog.SortColumn;
import de.mpg.escidoc.pubman.multipleimport.ImportLog.SortDirection;
import de.mpg.escidoc.pubman.util.LoginHelper;
import de.mpg.escidoc.services.common.valueobjects.AccountUserVO;

/**
 * TODO Description
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class ImportWorkspace extends FacesBean
{
    
    private ImportLog.SortColumn sortColumn = SortColumn.STARTDATE;
    private ImportLog.SortDirection sortDirection = SortDirection.DESCENDING;
    
    List<ImportLog> imports = null;
    public ImportWorkspace()
    {
        LoginHelper loginHelper = (LoginHelper) getSessionBean(LoginHelper.class);
        AccountUserVO user = loginHelper.getAccountUser();
        
        FacesContext facesContext = FacesContext.getCurrentInstance();
        
        ImportLog.SortColumn currentColumn = null;
        ImportLog.SortDirection currentDirection = null;
        ImportLog.SortColumn newColumn = null;
        
        String sortColumnString = facesContext.getExternalContext().getRequestParameterMap().get("sortColumn");
        if (sortColumnString != null && !"".equals(sortColumnString))
        {
            newColumn = SortColumn.valueOf(sortColumnString);
        }
        
        String currentColumnString = facesContext.getExternalContext().getRequestParameterMap().get("currentColumn");
        if (currentColumnString != null && !"".equals(currentColumnString))
        {
             currentColumn = SortColumn.valueOf(currentColumnString);
        }
        
        String currentDirectionString = facesContext.getExternalContext().getRequestParameterMap().get("currentDirection");
        if (currentDirectionString != null && !"".equals(currentDirectionString))
        {
            currentDirection = SortDirection.valueOf(currentDirectionString);
        }
        
        if (newColumn != null && newColumn.equals(currentColumn))
        {
            this.sortColumn = newColumn;
            if (currentDirection == SortDirection.ASCENDING)
            {
                this.sortDirection = SortDirection.DESCENDING;
            }
            else
            {
                this.sortDirection = SortDirection.ASCENDING;
            }
        }
        else if (newColumn != null)
        {
            this.sortColumn = newColumn;
            this.sortDirection = SortDirection.ASCENDING;
        }
        
        if (user != null)
        {
            imports = ImportLog.getImportLogs("import", user, this.sortColumn, this.sortDirection, false, false);
        }
    }

    /**
     * @return the imports
     */
    public List<ImportLog> getImports()
    {
        return imports;
    }

    /**
     * @param imports the imports to set
     */
    public void setImports(List<ImportLog> imports)
    {
        this.imports = imports;
    }

    /**
     * @return the sortColumn
     */
    public ImportLog.SortColumn getSortColumn()
    {
        return sortColumn;
    }

    /**
     * @param sortColumn the sortColumn to set
     */
    public void setSortColumn(ImportLog.SortColumn sortColumn)
    {
        this.sortColumn = sortColumn;
    }

    /**
     * @return the sortDirection
     */
    public ImportLog.SortDirection getSortDirection()
    {
        return sortDirection;
    }

    /**
     * @param sortDirection the sortDirection to set
     */
    public void setSortDirection(ImportLog.SortDirection sortDirection)
    {
        this.sortDirection = sortDirection;
    }
    
}
