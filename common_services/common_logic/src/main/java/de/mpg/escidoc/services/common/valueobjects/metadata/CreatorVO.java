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

package de.mpg.escidoc.services.common.valueobjects.metadata;

import de.mpg.escidoc.services.common.valueobjects.ValueObject;

/**
 * @revised by MuJ: 29.08.2007
 * @version $Revision: 611 $ $LastChangedDate: 2007-11-07 12:04:29 +0100 (Wed, 07 Nov 2007) $ by $Author: jmueller $
 * @updated 05-Sep-2007 12:48:55
 */
public class CreatorVO extends ValueObject implements Cloneable
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
    private static final long serialVersionUID = 1L;

    /**
     * The possible roles of the creator.
     * 
     * @updated 05-Sep-2007 12:48:55
     */
    public enum CreatorRole
    {
        ARTIST, AUTHOR, EDITOR, PAINTER, ILLUSTRATOR, PHOTOGRAPHER, COMMENTATOR, TRANSCRIBER, ADVISOR, TRANSLATOR, CONTRIBUTOR
    }

    /**
     * The possible creator types.
     * 
     * @updated 05-Sep-2007 12:48:55
     */
    public enum CreatorType
    {
        PERSON, ORGANIZATION
    }

    private OrganizationVO organization;
    private PersonVO person;
    private CreatorRole role;
    private CreatorType type;

    /**
     * Creates a new instance.
     */
    public CreatorVO()
    {
        super();
    }

    /**
     * Creates a new instance with the given organization and role.
     * 
     * @param organization The organization
     * @param role The creator role
     */
    public CreatorVO(OrganizationVO organization, CreatorRole role)
    {
        super();
        // use the setter as the setter does more than just setting the property
        setOrganization(organization);
        this.role = role;
    }

    /**
     * Creates a new instance with the given person and role.
     * 
     * @param person The person
     * @param role The creator role
     */
    public CreatorVO(PersonVO person, CreatorRole role)
    {
        super();
        // use the setter as the setter does more than just setting the property
        setPerson(person);
        this.role = role;
    }

    /**
     * Delivers the organization (or null if the creator is not an organization).
     */
    public OrganizationVO getOrganization()
    {
        return organization;
    }

    /**
     * Delivers the person (or null if the creator is not an person).
     */
    public PersonVO getPerson()
    {
        return person;
    }

    /**
     * Delivers the creators' role.
     */
    public CreatorRole getRole()
    {
        return role;
    }

    /**
     * Delivers the creators' type.
     */
    public CreatorType getType()
    {
        return type;
    }

    /**
     * Set the creator to the given organization. Because the creator cannot be an organization and a person at the same
     * time, the person is set to null.
     * 
     * @param newVal newVal
     */
    public void setOrganization(OrganizationVO newVal)
    {
        // DiT, 13.08.2007: set type newly and delete counterpart
        this.type = CreatorType.ORGANIZATION;
        this.person = null;
        organization = newVal;
    }

    /**
     * Set the creator to the given person. Because the creator cannot be a person and an organization at the same time,
     * the organization is set to null.
     * 
     * @param newVal newVal
     */
    public void setPerson(PersonVO newVal)
    {
        // DiT, 13.08.2007: set type newly and delete counterpart
        this.type = CreatorType.PERSON;
        this.organization = null;
        person = newVal;
    }

    /**
     * Set the creators' role.
     * 
     * @param newVal newVal
     */
    public void setRole(CreatorRole newVal)
    {
        role = newVal;
    }

    public void setType(CreatorType newVal)
    {
        type = newVal;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object clone() 
    {

        CreatorVO clone = new CreatorVO();
        clone.setRole(this.getRole());
        if (getPerson() != null)
        {
            clone.setPerson((PersonVO) getPerson().clone());
        }
        else if (getOrganization() != null)
        {
            clone.setOrganization((OrganizationVO) getOrganization().clone());
        }
        return clone;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj)
    {
        if (obj == null || !(getClass().isAssignableFrom(obj.getClass())))
        {
            return false;
        }
        CreatorVO other = (CreatorVO) obj;
        return equals(getPerson(), other.getPerson()) && equals(getOrganization(), other.getOrganization())
                && equals(getRole(), other.getRole());
    }

    /**
     * Delivers the value of the role Enum as a String. If the enum is not set, an empty String is returned.
     * 
     * @return the value of the role Enum
     */
    public String getRoleString()
    {
        if (role == null || role.toString() == null)
        {
            return "";
        }
        return role.toString();
    }

    /**
     * Sets the value of the role Enum by a String.
     * 
     * @param newValString A string containing the new value.
     */
    public void setRoleString(String newValString)
    {
        if (newValString == null || newValString.length() == 0)
        {
            role = null;
        }
        else
        {
            CreatorVO.CreatorRole newVal = CreatorVO.CreatorRole.valueOf(newValString);
            role = newVal;
        }
    }

    /**
     * Delivers the value of the type Enum as a String. If the enum is not set, an empty String is returned.
     * 
     * @return the value of the type Enum
     */
    public String getTypeString()
    {
        if (getType() == null || getType().toString() == null)
        {
            return "";
        }
        return getType().toString();
    }

    /**
     * Sets the value of the type Enum by a String.
     * 
     * @param newValString A string containing the new value.
     */
    public void setTypeString(String newValString)
    {
        if (newValString == null || newValString.length() == 0)
        {
            setType(null);
        }
        else
        {
            CreatorVO.CreatorType newVal = CreatorVO.CreatorType.valueOf(newValString);
            setType(newVal);
        }
    }
}