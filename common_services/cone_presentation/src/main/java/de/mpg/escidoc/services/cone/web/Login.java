package de.mpg.escidoc.services.cone.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import de.escidoc.www.services.aa.UserAccountHandler;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.valueobjects.AccountUserVO;
import de.mpg.escidoc.services.common.valueobjects.GrantVO;
import de.mpg.escidoc.services.common.xmltransforming.XmlTransformingBean;
import de.mpg.escidoc.services.framework.ServiceLocator;

public class Login
{

	public static boolean checkLogin(HttpServletRequest request, String userHandle, boolean strict) throws Exception
	{
		XmlTransforming xmlTransforming = new XmlTransformingBean();
		boolean showWarning = false;
	    UserAccountHandler userAccountHandler = ServiceLocator.getUserAccountHandler(userHandle);
	    String xmlUser = userAccountHandler.retrieve(userHandle);
	    
	    AccountUserVO accountUser = xmlTransforming.transformToAccountUser(xmlUser);
	    // add the user handle to the transformed account user
	    accountUser.setHandle(userHandle);
	    request.getSession().setAttribute("user_handle_exist",Boolean.TRUE);
	    String userGrantXML = ServiceLocator.getUserAccountHandler(userHandle).retrieveCurrentGrants(accountUser.getReference().getObjectId());
	    List<GrantVO> grants = xmlTransforming.transformToGrantVOList(userGrantXML);
	    
	    request.getSession().setAttribute("logged_in", Boolean.TRUE);
	    
	    if (!strict)
	    {
	    	return true;
	    }
	    
        showWarning = true;
        
	    for (GrantVO grant : grants)
	    {
	        accountUser.getGrants().add(grant);
	        if ("escidoc:role-system-administrator".equals(grant.getRole()))
	        {
	            request.getSession().setAttribute("user", accountUser);
	    		request.getSession().setAttribute("logged_in", Boolean.TRUE);
	    		request.getSession().setAttribute("edit_open_vocabulary", Boolean.TRUE);
	    		request.getSession().setAttribute("edit_closed_vocabulary", Boolean.TRUE);
	    		showWarning = false;
	    		break;
	        }
	        if ("escidoc:role-cone-editor".equals(grant.getRole()))
	        {
	        	request.getSession().setAttribute("user", accountUser);
	    		request.getSession().setAttribute("logged_in", Boolean.TRUE);
	    		request.getSession().setAttribute("edit_open_vocabulary", Boolean.TRUE);
	    		request.getSession().setAttribute("edit_closed_vocabulary", Boolean.TRUE);
	        	showWarning = false;
	        	break;
	        }
	        if ("escidoc:role-cone-open-vocabulary-editor".equals(grant.getRole()))
	        {
	        	request.getSession().setAttribute("user", accountUser);
	    		request.getSession().setAttribute("logged_in", Boolean.TRUE);
	    		request.getSession().setAttribute("edit_open_vocabulary", Boolean.TRUE);	    		
	        	showWarning = false;
	        	break;
	        }
	        if ("escidoc:role-cone-closed-vocabulary-editor".equals(grant.getRole()))
	        {
	        	request.getSession().setAttribute("user", accountUser);
	    		request.getSession().setAttribute("logged_in", Boolean.TRUE);
	    		request.getSession().setAttribute("edit_closed_vocabulary", Boolean.TRUE);	    		
	        	showWarning = false;
	        	break;
	        }
	    }

	    return showWarning;
	    
	}
	
}
