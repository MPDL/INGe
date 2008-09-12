package de.mpg.escidoc.services.cone.common;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

public class AuthenticationBean
{
    private static final String CONE_USER = "admin";
    private static final String CONE_PWD = "admin";
    private String username, password;
    private boolean loggedIn = false;
    public boolean isLoggedIn()
    {
        return loggedIn;
    }
    public void setLoggedIn(boolean loggedIn)
    {
        this.loggedIn = loggedIn;
    }
    public String getPassword()
    {
        return password;
    }
    public void setPassword(String password)
    {
        this.password = password;
    }
    public String getUsername()
    {
        return username;
    }
    public void setUsername(String username)
    {
        this.username = username;
    }
    
    public String login()
    {
        if(username.equals(CONE_USER) && password.equals(CONE_PWD))
        {
            loggedIn = true;
            return "loadJournalNames";
        }
        else
        {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "", "Username or Password INVALID!"));
        }
        return null;
    }
    
    public String logout()
    {
        loggedIn = false;
        return "loadHome";
    }
    
}
