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
* or http://www.escidoc.org/license.
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

package de.mpg.escidoc.pubman;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import de.escidoc.www.services.aa.UserAccountHandler;
import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.util.LoginHelper;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.valueobjects.AccountUserVO;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.Filter;
import de.mpg.escidoc.services.common.valueobjects.SearchRetrieveRecordVO;
import de.mpg.escidoc.services.common.valueobjects.SearchRetrieveResponseVO;
import de.mpg.escidoc.services.common.xmltransforming.XmlTransformingBean;
import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 * @author franke
 *
 */
public class UserAccountSuggest extends FacesBean
{

    Logger logger = Logger.getLogger(UserAccountSuggest.class);

    private List<AccountUserVO> userAccountList;
    
    public UserAccountSuggest() throws Exception
    {
        // Get query from URL parameters
        FacesContext context = FacesContext.getCurrentInstance();
        Map<String, String> parameters = context.getExternalContext().getRequestParameterMap();
        String query = parameters.get("q"); 
        
        // Initialize search service
        
        LoginHelper loginHelper = (LoginHelper) getSessionBean(LoginHelper.class);
        if(loginHelper.getESciDocUserHandle()!=null)
        {
        	UserAccountHandler uag = ServiceLocator.getUserAccountHandler(loginHelper.getESciDocUserHandle());
        	XmlTransforming xmlt = new XmlTransformingBean();

	        // Perform search request
	        if (query != null)
	        {
	            String queryString = "";
	            for (String snippet : query.split(" "))
	            {
	                if (!"".equals(queryString))
	                {
	                    queryString += " and ";
	                }
	                queryString += "(\"/properties/name\"=\"%" + snippet + "%\"  or \"/properties/login-name\"=\"%" + snippet + "%\")";
	            }
	            
	            
	            
	            
	            FilterTaskParamVO filter = new FilterTaskParamVO();
	            Filter f1 = filter.new CqlFilter(queryString);
	            filter.getFilterList().add(f1);
	            Filter f3 = filter.new LimitFilter("50");
	            filter.getFilterList().add(f3);


	            
	            String xmlUserList = uag.retrieveUserAccounts(filter.toMap());
	            SearchRetrieveResponseVO resp  = xmlt.transformToSearchRetrieveResponseAccountUser(xmlUserList);
	            
	            userAccountList = new ArrayList<AccountUserVO>(); 
	            
	            
	            if(resp.getRecords()!=null)
	            {
	            	for(SearchRetrieveRecordVO rec : resp.getRecords())
		            {
	            		if(rec!=null)
	            		{
	            			getUserAccountList().add((AccountUserVO)rec.getData());
	            		}
		            	
		            }
	            }
	            

	        }
        }
    }

	public List<AccountUserVO> getUserAccountList() {
		return userAccountList;
	}

	public void setUserAccountList(List<AccountUserVO> userAccountList) {
		this.userAccountList = userAccountList;
	}
	
	public int getUserAccountListSize()
	{
		if(userAccountList != null)
		{
			return this.userAccountList.size();
		}
		return 0;
		
	}

 
}
