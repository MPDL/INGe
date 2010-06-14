package de.mpg.escidoc.services.transformation.transformations.otherFormats.mets;

import de.mpg.escidoc.services.framework.AdminHelper;
import de.mpg.escidoc.services.framework.PropertyReader;

/**
 * Login class for escidoc log ins.
 *
 * @author kleinfe1 (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class Login
{


    /**
     * Logs in the system administrator and returns the corresponding user handle.
     * @return A handle for the logged in user.
     * @throws Exception
     */
    public String loginSysAdmin() throws Exception
    {
        String user = "";
        String pw = "";
        
        user = PropertyReader.getProperty("framework.admin.username");
        pw = PropertyReader.getProperty("framework.admin.password");
        return AdminHelper.loginUser(user, pw);
    }
}
