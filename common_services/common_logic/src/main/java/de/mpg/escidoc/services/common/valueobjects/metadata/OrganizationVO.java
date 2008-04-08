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
 * @revised by MuJ: 27.08.2007
 * @version $Revision: 611 $ $LastChangedDate: 2007-11-07 12:04:29 +0100 (Wed, 07 Nov 2007) $ by $Author: jmueller $
 * @updated 22-Okt-2007 15:27:10
 */
public class OrganizationVO extends ValueObject implements Cloneable
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
    private String address;
    private String identifier;
    private TextVO name;

    /**
     * Delivers the address of the organization as used in the item.
     */
    public String getAddress()
    {
        return address;
    }

    /**
     * Delivers the id of the corresponding affiliation in the system.
     */
    public String getIdentifier()
    {
        return identifier;
    }

    /**
     * Delivers the name of the organization as used in the item.
     */

    public TextVO getName()
    {
        return name;
    }

    /**
	 * Sets the address of the organization as used in the item.
	 * 
	 * @param newVal
	 */
    public void setAddress(String newVal)
    {
        address = newVal;
    }

    /**
     * Sets the name of the organization as used in the item.
     * 
     * @param newVal
     */
    public void setName(TextVO newVal)
    {
        name = newVal;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#clone()
     */
    @Override
    public Object clone()
    {
        OrganizationVO clone = new OrganizationVO();
        clone.setAddress(getAddress());
        if (getIdentifier() != null)
        {
            clone.setIdentifier(getIdentifier());
        }
        if (getName() != null)
        {
            clone.setName((TextVO)getName().clone());
        }
        return clone;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (obj == null || !(getClass().isAssignableFrom(obj.getClass())))
        {
            return false;
        }
        OrganizationVO other = (OrganizationVO)obj;
        return equals(getAddress(), other.getAddress()) && 
               equals(getIdentifier(), other.getIdentifier()) && 
               equals(getName(), other.getName());
    }

    /**
	 * Sets the id of the corresponding affiliation in the system.
	 * 
	 * @param newVal
	 */
    public void setIdentifier(String newVal)
    {
        identifier = newVal;
    }
}