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

import java.util.Date;

import de.mpg.escidoc.services.common.referenceobjects.GrantRO;
import de.mpg.escidoc.services.common.referenceobjects.ReferenceObject;

/**
 * A grant wraps a role that is granted to a certain certain object (like an affiliation or a collection).
 * 
 * @revised by MuJ: 28.08.2007
 * @version $Revision: 611 $ $LastChangedDate: 2007-11-07 12:04:29 +0100 (Wed, 07 Nov 2007) $ by $Author: jmueller $
 * @updated 05-Sep-2007 10:46:17
 */
public class GrantVO extends ValueObject
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
    
    /**
     * The role that is granted. The value of this attribute matches the value the framework gives back as role (e. g.
     * "escidoc:role-depositor").
     */
    private String role;
    /**
     * The reference to the object for which the role is granted. Changed to String by FrM.
     */
    private String objectRef;

    /**
     * The possible predefined roles. Caution: To compare roles to PredefinedRoles, use the according isPredefinedRole() method, or compare the role with the PredefinedRole.value().
     * It is: user.isModerator <=> frameworkValue="escidoc:role-md-editor" 
     * (the framework role "role-moderator" is not the same as the PubMan role MODERATOR!) 
     */
    
    private GrantRO reference;
    
    private Date lastModificationDate;
    
    

   

    public enum PredefinedRoles
    {
        DEPOSITOR("escidoc:role-depositor"), MODERATOR("escidoc:role-md-editor"), PRIVILEGEDVIEWER("escidoc:role-privileged-viewer");

        private final String frameworkValue;

        PredefinedRoles(String frameworkValue)
        {
            this.frameworkValue = frameworkValue;
        }

        public String frameworkValue()
        {
            return frameworkValue;
        }
    }

    /**
     * Default constructor.
     */
    public GrantVO()
    {
    }

    /**
     * Constructor using fields.
     * 
     * @param role The granted role.
     * @param object The object the role is granted on.
     */
    public GrantVO(String role, String object)
    {
        this.role = role;
        this.objectRef = object;
    }

    /**
     * Delivers true if the granted role is of type 'depositor' for the given object (normally a PubCollection).
     */
    public boolean isDepositor(ReferenceObject objRef)
    {
        if (objRef == null)
        {
            throw new IllegalArgumentException(getClass().getSimpleName()
                    + ":isModerator:objectRef is null");
        }
        return (PredefinedRoles.DEPOSITOR.frameworkValue().equals(role) && this.objectRef.equals(objRef));
    }

    /**
     * Delivers true if the granted role is of type 'moderator' for the given object (normally a PubCollection).
     */
    public boolean isModerator(ReferenceObject objRef)
    {
        if (objRef == null)
        {
            throw new IllegalArgumentException(getClass().getSimpleName()
                    + ":isModerator:objectRef is null");
        }
        return (PredefinedRoles.MODERATOR.frameworkValue().equals(role) && this.objectRef.equals(objRef));
    }

    /**
     * Delivers the object reference of the object the rights are granted for.
     */
    public String getObjectRef()
    {
        return objectRef;
    }

    /**
     * Delivers the role that is granted. The value of this attribute matches the value the framework gives back as role
     * (e. g. "escidoc:role-depositor"). If you want to check if the role matches a predefined role, use the according
     * isPredefinedRole() method instead.
     */
    public String getRole()
    {
        return role;
    }

    /**
     * Sets the object reference of the object the rights are granted for.
     * 
     * @param newVal newVal
     */
    public void setObjectRef(String newVal)
    {
        this.objectRef = newVal;
    }

    /**
     * Sets the role that is granted. The value of this attribute must match the value the framework expects as role (e.
     * g. "escidoc:role-depositor").
     * 
     * @param newVal newVal
     */
    public void setRole(String newVal)
    {
        this.role = newVal;
    }

    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        return "[" + objectRef + " : " + role + "]";
    }
    
    public GrantRO getReference()
    {
        return reference;
    }

    public void setReference(GrantRO reference)
    {
        this.reference = reference;
    }
    
    public Date getLastModificationDate()
    {
        return lastModificationDate;
    }

    public void setLastModificationDate(Date lastModificationDate)
    {
        this.lastModificationDate = lastModificationDate;
    }
}