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
 * Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft
 * für wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Förderung der Wissenschaft e.V.
 * All rights reserved. Use is subject to license terms.
 */

package de.mpg.escidoc.main;

import org.apache.commons.httpclient.HttpClient;

import de.mpg.escidoc.http.Login;
import de.mpg.escidoc.http.UserGroup;
import de.mpg.escidoc.util.Util;


/**
 * The main Class of the UgTool. 
 * 
 * UgTool offers nearly all available operations to create, modify oder delete UserGroups.
 * 
 * @author Matthias Walter (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class UgTool
{
    /**
     * @param args
     */
    public static void main(String[] args)
    {
    	String frameworkUrl = null;
    	frameworkUrl = Util.getProperty("frameworkUrl");
        System.out.println(frameworkUrl);
        HttpClient client = Util.getHttpClient();
        Login login = new Login(client,frameworkUrl);
        String userHandle = null;
        while (userHandle == null)
        {
        	userHandle = login.getUserHandle();
        	if (userHandle == null) 
        	{
        		System.out.println("\nLogin failed, please try again!\n");
        	}
        }
        UserGroup userGroup = new UserGroup(client, frameworkUrl, userHandle);
        optionMenu(userGroup);
    }
    
    private static void optionMenu(UserGroup userGroup)
    {
    	String option = null;
        boolean quit = false;
        while (quit != true)
        {
            Util.printMainMenu();
            option = Util.input("Choose your option: ");
            if ("getUG".compareToIgnoreCase(option) == 0)
            {
                userGroup.getAllUserGroups();
            }
            else if ("createUG".compareToIgnoreCase(option) == 0)
            {
            	Boolean complete = userGroup.createUserGroups();
                if (complete == false) 
                {
                	System.out.println("\nAction aborted, please try again:");
                }
            }
            else if ("getSpecificUG".compareToIgnoreCase(option) == 0)
            {
            	Boolean complete = userGroup.getSpecificUserGroup();
                if (complete == false) 
                {
                	System.out.println("\nAction aborted, please try again:");
                }
            }
            else if ("deleteUG".compareToIgnoreCase(option) == 0)
            {
            	Boolean complete = userGroup.deleteUserGroup();
                if (complete == false) 
                {
                	System.out.println("\nAction aborted, please try again:");
                }
            }
            else if ("getGrantUG".compareToIgnoreCase(option) == 0)
            {
            	Boolean complete = userGroup.getGrantsOfUserGroup();
                if (complete == false) 
                {
                	System.out.println("\nAction aborted, please try again:");
                }
            }
            else if ("setGrantUG".compareToIgnoreCase(option) == 0)
            {
                Boolean complete = userGroup.setGrantsOfUserGroup();
                if (complete == false) 
                {
                	System.out.println("\nAction aborted, please try again:");
                }
            }
            else if ("revokeGrantUG".compareToIgnoreCase(option) == 0)
            {
                Boolean complete = userGroup.revokeGrantFromUserGroup();
                if (complete == false) 
                {
                	System.out.println("\nAction aborted, please try again:");
                }
            }
            else if ("addSelectorUG".compareToIgnoreCase(option) == 0)
            {
            	Boolean complete = userGroup.addSelectorToUserGroup();
                if (complete == false) 
                {
                	System.out.println("\nAction aborted, please try again:");
                }
            }
            else if ("removeSelectorUG".compareToIgnoreCase(option) == 0)
            {
            	Boolean complete = userGroup.removeSelectorFromUserGroup();
                if (complete == false) 
                {
                	System.out.println("\nAction aborted, please try again:");
                }
            }
            else if ("activateUG".compareToIgnoreCase(option) == 0)
            {
            	Boolean complete = userGroup.activateUserGroup();
                if (complete == false) 
                {
                	System.out.println("\nAction aborted, please try again:");
                }
            }
            else if ("deactivateUG".compareToIgnoreCase(option) == 0)
            {
            	Boolean complete = userGroup.deactivateUserGroup();
                if (complete == false) 
                {
                	System.out.println("\nAction aborted, please try again:");
                }
            }
            else if ("?".compareToIgnoreCase(option) == 0)
            {
            }
            else if ("quit".compareToIgnoreCase(option) == 0)
            {
                System.out.println("Tool execution stopped.");
                quit = true;
            }
            else
            {
                System.out.println("!NO ALLOWED COMMANDS!\n");
            }
        }
    }
}
