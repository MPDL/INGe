package de.mpg.mpdl.migration.foxml;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

/**
 * 
 * TODO Description.
 *
 * @author frank (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class GWDGAuthentication extends Authenticator
{
    
    private String username;
    private String password;
    
    /**
     * 
     * @param username {@link String}
     * @param password {@link String}
     */
    public GWDGAuthentication(String username, String password)
    {
        this.username = username;
        this.password = password;
    }
    
    protected PasswordAuthentication getPasswordAuthentication()
    {
        return new PasswordAuthentication(username, password.toCharArray());
    }

}
