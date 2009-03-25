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

package de.mpg.escidoc.services.cone.util;

/**
 * A key-value pair.
 * 
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class Pair
{
    private String key;
    private String value;

    /**
     * Default constructor.
     */
    public Pair()
    {
    }

    /**
     * Constructor with fields.
     * 
     * @param key The key
     * @param value The value
     */
    public Pair(String key, String value)
    {
        this.key = key;
        this.value = value;
    }
    
    public String getKey()
    {
        return key;
    }
    public void setKey(String key)
    {
        this.key = key;
    }
    public String getValue()
    {
        return value;
    }
    public void setValue(String value)
    {
        this.value = value;
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean equals(Object other)
    {
        if (other == null)
        {
            return false;
        }
        else if (!(other instanceof Pair))
        {
            return false;
        }
        Pair otherPair = (Pair) other;
        
        return (key == null ? otherPair.key == null : key.equals(otherPair.key))
                && (value == null ? otherPair.value == null : value.equals(otherPair.value));
    }
}
