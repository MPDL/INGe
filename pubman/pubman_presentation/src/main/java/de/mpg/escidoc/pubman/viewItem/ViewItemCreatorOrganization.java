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
* Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.pubman.viewItem;

import de.mpg.escidoc.pubman.util.CommonUtils;

/**
 * ViewItemCreatorOrganization.java
 * 
 * stores information about an organization that is a creator. This information will be presented in a popup window
 *
 * Created on 20. Februar 2007, 12:17
 * @author: Tobias Schraut
 * @version: $Revision: 1587 $ $LastChangedDate: 2007-08-20 16:24:53 +0200 (Mo,
 *           20 Aug 2007) $ Revised by ScT: 20.08.2007
 */
public class ViewItemCreatorOrganization
{
    // the position of the organization within the list
    private String position;
    // The Name of the organization
    private String organizationName;
    // the address of the organization
    private String organizationAddress;
    // the html code of the organization info page popup window
    private String organizationInfoPage;

    public ViewItemCreatorOrganization()
    {
    }

    // Getters and setters
    public String getOrganizationName()
    {
        return organizationName;
    }

    public void setOrganizationName(String organizationName)
    {
        this.organizationName = organizationName;
    }

    public String getPosition()
    {
        return position;
    }

    public void setPosition(String position)
    {
        this.position = position;
    }

    public String getOrganizationAddress()
    {
        return organizationAddress;
    }

    public void setOrganizationAddress(String organizationAddress)
    {
        this.organizationAddress = organizationAddress;
    }

    public String getOrganizationInfoPage()
    {
        return organizationInfoPage;
    }

    // the information page (popup window)
    public void setOrganizationInfoPage(String organizationName, String organizationAddress)
    {
        String addr = "";
        if (organizationAddress != null)
        {
            addr = organizationAddress;
        }
        this.organizationInfoPage = "'<html><head><title>Organisationp</title></head><body scroll=no bgcolor=#FFFFFC><br/><p style=font-family:verdana,arial;font-size:12px>"
                + CommonUtils.htmlEscape(organizationName)
                + "</p><p style=font-family:verdana,arial;font-size:12px>"
                + CommonUtils.htmlEscape(addr) + "</p></body></html>'";
    }
}
