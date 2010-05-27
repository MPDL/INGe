/*
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
* Copyright 2006-2010 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/
package de.mpg.escidoc.pubman.test.gui.modules.login;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import de.mpg.escidoc.pubman.test.gui.modules.EnumMatrix;
import de.mpg.escidoc.pubman.test.gui.modules.PubmanGuiModules;

/**
 * @author endres
 *
 */
public class PubmanUser
{
    public enum UserType {
        DepositorModeratorSimpleWF,
        ModeratorSimpleWF,
        DepositorSimpleWF,
        DepositorModeratorStandardWF,
        ModeratorStandardWF,
        DepositorStandardWF,
        DepositorModeratorSimpleStandardWF,
        ModeratorSimpleStandardWF,
        DepositorSimpleStandardWF,
    }
    
    public enum UserRights {
        DepositorLink,
        QALink,
        SubmissionLink
    }
    
    public UserType userType = null;
    public Properties properties = null;
    public String username = null;
    public String password = null;
    public EnumMatrix<UserType, UserRights> userRightsMatrix = new EnumMatrix<UserType, UserRights>(20);
    public Map<UserRights, String> userRightsLinksHomepage = new HashMap<UserRights, String>();
    
    public PubmanUser( UserType userType, Properties properties ) {
        this.userType = userType;
        this.properties = properties;
        retrieveLoginCredentials();
        buildUserRightsMatrix();
        buildRightsLinksHomepage();
    }
    
    private void buildRightsLinksHomepage() {
        userRightsLinksHomepage.put(UserRights.DepositorLink, "form1:Header:lnkDepWorkspace");
        userRightsLinksHomepage.put(UserRights.QALink, "form1:Header:lnkQAWorkspace");
        userRightsLinksHomepage.put(UserRights.SubmissionLink, "form1:Header:lnkSubmission");
    }
    
    private void buildUserRightsMatrix() {
        userRightsMatrix.setValue(UserType.DepositorModeratorSimpleWF, UserRights.DepositorLink, true);
        userRightsMatrix.setValue(UserType.DepositorModeratorSimpleWF, UserRights.QALink, true);
        userRightsMatrix.setValue(UserType.DepositorModeratorSimpleWF, UserRights.SubmissionLink, true);
        
        userRightsMatrix.setValue(UserType.ModeratorSimpleWF, UserRights.DepositorLink, false);
        userRightsMatrix.setValue(UserType.ModeratorSimpleWF, UserRights.QALink, true);
        userRightsMatrix.setValue(UserType.ModeratorSimpleWF, UserRights.SubmissionLink, false);
        
        userRightsMatrix.setValue(UserType.DepositorSimpleWF, UserRights.DepositorLink, true);
        userRightsMatrix.setValue(UserType.DepositorSimpleWF, UserRights.QALink, false);
        userRightsMatrix.setValue(UserType.DepositorSimpleWF, UserRights.SubmissionLink, true);
        
        userRightsMatrix.setValue(UserType.DepositorModeratorStandardWF, UserRights.DepositorLink, true);
        userRightsMatrix.setValue(UserType.DepositorModeratorStandardWF, UserRights.QALink, true);
        userRightsMatrix.setValue(UserType.DepositorModeratorStandardWF, UserRights.SubmissionLink, true);
        
        userRightsMatrix.setValue(UserType.ModeratorStandardWF, UserRights.DepositorLink, false);
        userRightsMatrix.setValue(UserType.ModeratorStandardWF, UserRights.QALink, true);
        userRightsMatrix.setValue(UserType.ModeratorStandardWF, UserRights.SubmissionLink, false);
        
        userRightsMatrix.setValue(UserType.DepositorStandardWF, UserRights.DepositorLink, true);
        userRightsMatrix.setValue(UserType.DepositorStandardWF, UserRights.QALink, false);
        userRightsMatrix.setValue(UserType.DepositorStandardWF, UserRights.SubmissionLink, true);
        
        userRightsMatrix.setValue(UserType.DepositorModeratorSimpleStandardWF, UserRights.DepositorLink, true);
        userRightsMatrix.setValue(UserType.DepositorModeratorSimpleStandardWF, UserRights.QALink, true);
        userRightsMatrix.setValue(UserType.DepositorModeratorSimpleStandardWF, UserRights.SubmissionLink, true);
        
        userRightsMatrix.setValue(UserType.ModeratorSimpleStandardWF, UserRights.DepositorLink, false);
        userRightsMatrix.setValue(UserType.ModeratorSimpleStandardWF, UserRights.QALink, true);
        userRightsMatrix.setValue(UserType.ModeratorSimpleStandardWF, UserRights.SubmissionLink, false);
        
        userRightsMatrix.setValue(UserType.DepositorSimpleStandardWF, UserRights.DepositorLink, true);
        userRightsMatrix.setValue(UserType.DepositorSimpleStandardWF, UserRights.QALink, false);
        userRightsMatrix.setValue(UserType.DepositorSimpleStandardWF, UserRights.SubmissionLink, true);
    }
    
    public boolean isAuthorized( UserRights right ) {
        return this.userRightsMatrix.getValue(userType, right);
    }
    
    public void retrieveLoginCredentials() {
        switch( userType ) {
            case DepositorModeratorSimpleWF:
                this.username = properties.getProperty("selenium.depositorModerator.simpleWF.username");
                this.password = properties.getProperty("selenium.depositorModerator.simpleWF.password");
                break;
            case ModeratorSimpleWF:
                this.username =  properties.getProperty("selenium.moderator.simpleWF.username"); 
                this.password =  properties.getProperty("selenium.moderator.simpleWF.password");
                break;
            case DepositorSimpleWF:
                this.username = properties.getProperty("selenium.depositor.simpleWF.username"); 
                this.password = properties.getProperty("selenium.depositor.simpleWF.password");
                break;
            case DepositorModeratorStandardWF:
                this.username = properties.getProperty("selenium.depositorModerator.standardWF.username"); 
                this.password = properties.getProperty("selenium.depositorModerator.standardWF.password");
                break;
            case ModeratorStandardWF:
                this.username = properties.getProperty("selenium.moderator.standardWF.username"); 
                this.password = properties.getProperty("selenium.moderator.standardWF.password");
                break;
            case DepositorStandardWF:
                this.username = properties.getProperty("selenium.depositor.standardWF.username"); 
                this.password = properties.getProperty("selenium.depositor.standardWF.password");
                break;
            case DepositorModeratorSimpleStandardWF:
                this.username = properties.getProperty("selenium.depositorModerator.simpleStandardWF.username"); 
                this.password = properties.getProperty("selenium.depositorModerator.simpleStandardWF.password");
                break;
            case ModeratorSimpleStandardWF:
                this.username = properties.getProperty("selenium.moderator.simpleStandardWF.username"); 
                this.password = properties.getProperty("selenium.moderator.simpleStandardWF.password");
                break;
            case DepositorSimpleStandardWF:
                this.username = properties.getProperty("selenium.depositor.simpleStandardWF.username"); 
                this.password = properties.getProperty("selenium.depositor.simpleStandardWF.password");
                break;
        }
    }

    public String getUsername()
    {
        return username;
    }

    public String getPassword()
    {
        return password;
    }
    public String getUserRightLinkHomepage( UserRights right ) {
        return this.userRightsLinksHomepage.get(right);
    }
}
