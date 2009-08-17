package de.mpg.mpdl.migration.foxml;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

public class GWDGAuthentication extends Authenticator
{
    
    private String username,
                   password;
                      
    public GWDGAuthentication(String username,String password)
    {
       this.username = username;
       this.password = password;
    }
    
    protected PasswordAuthentication getPasswordAuthentication()
    {
       return new PasswordAuthentication(
              username,password.toCharArray());
    }

}
