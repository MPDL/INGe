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

import javax.faces.context.FacesContext;

import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.util.LoginHelper;

/**
 * TODO Description
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class ImportItems extends FacesBean
{
    private int importId = 0;
    private int page = 0;
    private int itemsPerPage = 0;
    private String userid = null;
    private ImportLog log = null;
    
    public ImportItems()
    {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        
        String idString = facesContext.getExternalContext().getRequestParameterMap().get("id");
        if (idString != null)
        {
            this.importId = Integer.parseInt(idString);
        }
        String pageString = facesContext.getExternalContext().getRequestParameterMap().get("page");
        if (pageString != null)
        {
            this.page = Integer.parseInt(pageString);
        }
        String itemsPerPageString = facesContext.getExternalContext().getRequestParameterMap().get("itemsPerPage");
        if (itemsPerPageString != null)
        {
            this.itemsPerPage = Integer.parseInt(itemsPerPageString);
        }
        
        LoginHelper loginHelper = (LoginHelper) getSessionBean(LoginHelper.class);
        if (loginHelper.getAccountUser() != null)
        {
            this.userid = loginHelper.getAccountUser().getReference().getObjectId();
        }
    }
    
    public ImportLog getImport()
    {
        if (this.log == null && this.importId != 0 && this.userid != null)
        {
            
            this.log = ImportLog.getImportLog(this.importId, true, false);
        }
        return this.log;
    }

    /**
     * @return the page
     */
    public int getPage()
    {
        return page;
    }

    /**
     * @param page the page to set
     */
    public void setPage(int page)
    {
        this.page = page;
    }

    /**
     * @return the itemsPerPage
     */
    public int getItemsPerPage()
    {
        return itemsPerPage;
    }

    /**
     * @param itemsPerPage the itemsPerPage to set
     */
    public void setItemsPerPage(int itemsPerPage)
    {
        this.itemsPerPage = itemsPerPage;
    }
    
}
