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
* Copyright 2006-2009 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/

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
