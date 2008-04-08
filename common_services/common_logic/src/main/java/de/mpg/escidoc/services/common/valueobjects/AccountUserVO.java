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

package de.mpg.escidoc.services.common.valueobjects;

import java.util.List;

import de.mpg.escidoc.services.common.referenceobjects.AccountUserRO;
import de.mpg.escidoc.services.common.referenceobjects.AffiliationRO;
import de.mpg.escidoc.services.common.referenceobjects.ReferenceObject;
import de.mpg.escidoc.services.common.valueobjects.GrantVO.PredefinedRoles;

/**
 * An account user is a user who is registered by username (i. e. userID) and password.
 * 
 * @revised by MuJ: 28.08.2007
 * @version $Revision: 611 $ $LastChangedDate: 2007-11-07 12:04:29 +0100 (Wed, 07 Nov 2007) $ by $Author: jmueller $
 * @updated 05-Sep-2007 10:30:46
 */
public class AccountUserVO extends ValueObject
{
    /**
     * Fixed serialVersionUID to prevent java.io.InvalidClassExceptions like
     * 'de.mpg.escidoc.services.common.valueobjects.ItemVO; local class incompatible: stream classdesc
     * serialVersionUID = 8587635524303981401, local class serialVersionUID = -2285753348501257286' that occur after
     * JiBX enhancement of VOs. Without the fixed serialVersionUID, the VOs have to be compiled twice for testing (once
     * for the Application Server, once for the local test).
     * 
     * @author Johannes Mueller
     */
   
    private boolean active;
    /**
     * The references of the affiliations the account user is associated to.
     */
    private List<AffiliationRO> affiliations = new java.util.ArrayList<AffiliationRO>();
    private String email;
    /**
     * name + surname
     */
    private String name;
    private String password;
    private AccountUserRO reference;
    private String userid;
    /**
     * The handle for the authenticated user, given by the framework.
     */
    private String handle;
    /**
     * Caution: This list is NOT filled automatically by JiBX or the AccountUserVO class itself when creating the VO.
     */
    private List<GrantVO> grants = new java.util.ArrayList<GrantVO>();

    /**
     * Delivers the active flag of the user account. The active flag is true if the user account can be used, false
     * otherwise.
     */
    public boolean isActive()
    {
        return active;
    }

    /**
     * Delivers true if the granted role is of type 'depositor' for any object.
     */
    public boolean isDepositor()
    {
        boolean depositor = false;
        for (GrantVO grant : this.grants)
        {
            // every system administrator is a depositor, too
            if (grant.getRole().equals(PredefinedRoles.DEPOSITOR.frameworkValue()) || grant.getRole().equals("escidoc:role-system-administrator"))
            {
                depositor = true;
            }
        }
        return depositor;
    }

    /**
     * Delivers true if the granted role is of type 'moderator' for the given object (normally a PubCollection).
     * 
     * @param refObj true, if the user has the moderator role
     * @return true if the granted role is of type 'moderator' for the given object
     */
    public boolean isModerator(ReferenceObject refObj)
    {
        if (refObj == null)
        {
            throw new IllegalArgumentException(getClass().getSimpleName() + ":isModerator:objectRef is null");
        }
        boolean moderator = false;
        for (GrantVO grant : this.grants)
        {
            // every system administrator is a moderator, too
            if (grant.getRole().equals("escidoc:role-system-administrator"))
            {
                moderator = true;
            }
            if (grant.getRole().equals(PredefinedRoles.MODERATOR.frameworkValue()))                    
            {
                if (grant.getObjectRef().equals(refObj.getObjectId()))
                {
                    moderator = true;
                }
            }
        }
        return moderator;
    }

    /**
     * Delivers the list of affiliations the account user is affiliated to.
     */
    public List<AffiliationRO> getAffiliations()
    {
        return affiliations;
    }

    /**
     * Delivers the email address of the account user.
     */
    public String getEmail()
    {
        return email;
    }

    /**
     * Delivers the list of the account users' grants. Caution: This list is NOT filled automatically by JiBX or the
     * AccountUserVO class itself when creating the VO.
     */
    public List<GrantVO> getGrants()
    {
        return grants;
    }

    /**
     * Delivers the handle for the authenticated user, given back by the framework.
     */
    public String getHandle()
    {
        return handle;
    }

    /**
     * Delivers the name of the account user, i. e. first and last name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Delivers the password of the account user. The password has to be encrypted.
     */
    public String getPassword()
    {
        return password;
    }

    /**
     * Delivers the account users' reference.
     * 
     * @see de.mpg.escidoc.services.common.referenceobjects.ReferenceObject
     */
    public AccountUserRO getReference()
    {
        return reference;
    }

    /**
     * Delivers the user-id of the account user. The user-id is a unique id for the user within the system.
     */
    public String getUserid()
    {
        return userid;
    }

    /**
     * Sets the active flag of the user account. The active flag is true if the user account can be used, false
     * otherwise.
     * 
     * @param newVal newVal
     */
    public void setActive(boolean newVal)
    {
        this.active = newVal;
    }

    /**
     * Sets the email address of the account user.
     * 
     * @param newVal newVal
     */
    public void setEmail(String newVal)
    {
        this.email = newVal;
    }

    /**
     * Sets the handle for the authenticated user, given back by the framework.
     * 
     * @param newVal
     */
    public void setHandle(String newVal)
    {
        this.handle = newVal;
    }

    /**
     * Sets the name of the account user, i. e. first and last name.
     * 
     * @param newVal newVal
     */
    public void setName(String newVal)
    {
        this.name = newVal;
    }

    /**
     * Sets the password of the account user. The password has to be encrypted.
     * 
     * @param newVal newVal
     */
    public void setPassword(String newVal)
    {
        this.password = newVal;
    }

    /**
     * Sets the account users' reference.
     * 
     * @see de.mpg.escidoc.services.common.referenceobjects.ReferenceObject
     * @param newVal newVal
     */
    public void setReference(AccountUserRO newVal)
    {
        this.reference = newVal;
    }

    /**
     * Sets the user-id of the account user. The user-id is a unique id for the user within the system.
     * 
     * @param newVal newVal
     */
    public void setUserid(String newVal)
    {
        this.userid = newVal;
    }

}