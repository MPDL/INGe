package de.mpg.escidoc.services.cone.web;

import javax.servlet.http.HttpServletRequest;

import de.mpg.escidoc.services.aa.Aa;
import de.mpg.escidoc.services.aa.AuthenticationVO.Role;
import de.mpg.escidoc.services.aa.Config;

public class Login
{

    private static final String PROPERTY_ROLE_CONE_OPEN_VOCABULARY = "escidoc.aa.role.open.vocabulary.id";
    private static final String PROPERTY_ROLE_CONE_CLOSED_VOCABULARY = "escidoc.aa.role.closed.vocabulary.id";
   
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
        String roleConeOpenVocabularyId = Config.getProperty(PROPERTY_ROLE_CONE_OPEN_VOCABULARY);
        String roleConeClosedVocabularyId = Config.getProperty(PROPERTY_ROLE_CONE_CLOSED_VOCABULARY);
                
	    for (Role role : aa.getAuthenticationVO().getRoles())
	    {
	        if ("escidoc:role-system-administrator".equals(role.getKey()))
	        {
	            request.getSession().setAttribute("logged_in", Boolean.TRUE);
	            request.getSession().setAttribute("user", aa.getAuthenticationVO());
	    		request.getSession().setAttribute("edit_open_vocabulary", Boolean.TRUE);
	    		request.getSession().setAttribute("edit_closed_vocabulary", Boolean.TRUE);
	    		showWarning = false;
	    		break;
	        }
	        if (roleConeOpenVocabularyId != null && roleConeOpenVocabularyId.equals(role.getKey()))
	        {
	        	request.getSession().setAttribute("user", aa.getAuthenticationVO());
	    		request.getSession().setAttribute("logged_in", Boolean.TRUE);
	    		request.getSession().setAttribute("edit_open_vocabulary", Boolean.TRUE);	    		
	        	showWarning = false;
	        	continue;
	        }
	        if (roleConeClosedVocabularyId != null && roleConeClosedVocabularyId.equals(role.getKey()))
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
