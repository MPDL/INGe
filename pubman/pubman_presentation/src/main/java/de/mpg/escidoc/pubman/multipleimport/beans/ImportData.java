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
* Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.pubman.multipleimport.beans;

import java.sql.Connection;
import java.sql.SQLException;

import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.multipleimport.ImportLog;
import de.mpg.escidoc.pubman.util.LoginHelper;

/**
 * JSF bean class to hold an import's data.
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class ImportData extends FacesBean
{
    private int importId = 0;
    private String userid = null;
    private String userHandle = null;
    private ImportLog log = null;
    
    private static Logger logger = Logger.getLogger(ImportData.class);
    
    /**
     * Constructor extracting the import's id from the URL and setting user settings.
     */
    public ImportData()
    {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        String idString = facesContext.getExternalContext().getRequestParameterMap().get("id");
        if (idString != null)
        {
            this.importId = Integer.parseInt(idString);
        }
        LoginHelper loginHelper = (LoginHelper) getSessionBean(LoginHelper.class);
        if (loginHelper.getAccountUser() != null && loginHelper.getAccountUser().getReference() != null)
        {
            this.userid = loginHelper.getAccountUser().getReference().getObjectId();
            this.userHandle = loginHelper.getAccountUser().getHandle();
        }
    }
    
    /**
     * Getter.
     * 
     * @return the import
     */
    public ImportLog getImport()
    {
    	
        if (this.log == null && this.importId != 0 && this.userid != null)
        {
        	Connection conn = ImportLog.getConnection();
            this.log = ImportLog.getImportLog(this.importId, false, false, conn);
            this.log.setUser(this.userid);
            this.log.setUserHandle(this.userHandle);
            
            try {
				conn.close();
			} catch (SQLException e) {
				 logger.error("Error closing db connection", e);
			}
        }
        return this.log;
    }
    
    public String getRemove()
    {
        getImport().remove();
        return null;
    }

    public String getDelete()
    {
        getImport().deleteAll();
        return null;
    }

    public String getSubmit()
    {
        getImport().submitAll();
        return null;
    }
    
    public String getRelease()
    {
        getImport().submitAndReleaseAll();
        return null;
    }
    
    public int getImportId()
    {
        return importId;
    }

    public void setImportId(int importId)
    {
        this.importId = importId;
    }
    
}