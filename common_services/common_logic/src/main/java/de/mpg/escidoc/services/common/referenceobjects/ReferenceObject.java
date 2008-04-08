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

package de.mpg.escidoc.services.common.referenceobjects;

import java.io.Serializable;

/**
 * Root Class of all typed references of ValueObjects.
 * 
 * @created 18-Jan-2007 15:42:40
 * @revised by MuJ: 27.08.2007
 * @author Full Access
 * @version $Revision: 632 $ $LastChangedDate: 2007-07-09 16:4
 * @updated 04-Sep-2007 11:47:55
 */
public abstract class ReferenceObject implements Serializable
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
     * Technical objectId-attribute of corresponding ValueOject.
     */
    private String objectId;

    /**
	 * Creates a new instance.
	 */
    public ReferenceObject()
    {
        objectId = null;
    }

    /**
     * Copy constructor.
     * 
     * @author Thomas Diebaecker
     * @param other The instance to copy.
     */
    public ReferenceObject(ReferenceObject other)
    {
        this.setObjectId(other.getObjectId());
    }
    
    /**
	 * Creates a new instance with the given ID.
	 * 
	 * @param objectId
	 */
    public ReferenceObject(String objectId)
    {
        this.objectId = objectId;
    }
    
    /**
	 * Delivers the technical objectId-attribute of corresponding ValueObject.
	 */
    public String getObjectId()
    {
        return objectId;
    }

    /**
	 * Sets the technical objectId-attribute of corresponding ValueObject.
	 * 
	 * @param objectId
	 */
    public void setObjectId(String objectId)
    {
        this.objectId = objectId;
    }

    /**
	 * Softens the strict equivalence relation defined by the overwritten {@link java.
	 * lang.Object#equals(Object)} method. Two ReferenceObjects are equal, if they are
	 * identical, or if they are of the same type and contain the same objectId.
	 * 
	 * @returns true if the objects are equal according to the defined equivalence
	 * relation.
	 * 
	 * @param object
	 */
    @Override
    public boolean equals(Object object)
    {
        if (this == object)
        {
            return true;
        }
        if (getClass().isInstance(object))
        {
            ReferenceObject otherRef = (ReferenceObject)object;
            if (objectId != null && objectId.equals(otherRef.getObjectId()))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Delivers the name of the class and the objectId, separated by a colon.
     */
    @Override
    public String toString()
    {
        return new StringBuilder(getClass().getName()).append(':').append(objectId).toString();
    }
}