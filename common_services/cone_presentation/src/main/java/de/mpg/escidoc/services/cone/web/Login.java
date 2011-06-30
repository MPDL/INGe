package de.mpg.escidoc.services.cone.web;

import javax.servlet.http.HttpServletRequest;

import de.mpg.escidoc.services.aa.Aa;
import de.mpg.escidoc.services.aa.AuthenticationVO.Role;

public class Login
{

    /**
     * Hide constructor of the static class.
     */
    private Login()
    {}
    
	public static boolean checkLogin(HttpServletRequest request, boolean strict)
	{
	    
	    Aa aa = null;
	    try
	    {
	        aa = new Aa(request);
	    }
	    catch (Exception e)
	    {
            throw new RuntimeException(e);
        }
	    if (aa != null && aa.getAuthenticationVO() != null)
	    {
	        request.getSession().setAttribute("logged_in", Boolean.TRUE);
	    }
	    else
	    {
	        return false;
	    }
	    
	    if (!strict)
	    {
	    	return true;
	    }
	    
        boolean showWarning = true;
        
	    for (Role role : aa.getAuthenticationVO().getRoles())
	    {
	        if ("escidoc:role-system-administrator".equals(role.getKey()))
	        {
	            request.getSession().setAttribute("user", aa.getAuthenticationVO());
	    		request.getSession().setAttribute("edit_open_vocabulary", Boolean.TRUE);
	    		request.getSession().setAttribute("edit_closed_vocabulary", Boolean.TRUE);
	    		showWarning = false;
	    		break;
	        }
	        if ("escidoc:role-cone-open-vocabulary-editor".equals(role.getKey()))
	        {
	        	request.getSession().setAttribute("user", aa.getAuthenticationVO());
	    		request.getSession().setAttribute("logged_in", Boolean.TRUE);
	    		request.getSession().setAttribute("edit_open_vocabulary", Boolean.TRUE);	    		
	        	showWarning = false;
	        	continue;
	        }
	        if ("escidoc:role-cone-closed-vocabulary-editor".equals(role.getKey()))
	        {
	        	request.getSession().setAttribute("user", aa.getAuthenticationVO());
	    		request.getSession().setAttribute("logged_in", Boolean.TRUE);
	    		request.getSession().setAttribute("edit_closed_vocabulary", Boolean.TRUE);	    		
	        	showWarning = false;
	        	continue;
	        }
	    }

	    return showWarning;
	    
	}
	
	public static boolean getLoggedIn(HttpServletRequest request)
    {
	    if (request.getSession().getAttribute("logged_in") != null && ((Boolean) request.getSession().getAttribute("logged_in")).booleanValue())
        {
            return true;
        }
	    else
	    {
	        checkLogin(request, true);
	        return (request.getSession().getAttribute("logged_in") != null && ((Boolean) request.getSession().getAttribute("logged_in")).booleanValue());
	    }
    }
}
