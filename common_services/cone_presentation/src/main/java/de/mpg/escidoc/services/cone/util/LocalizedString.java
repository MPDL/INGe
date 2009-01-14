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

package de.mpg.escidoc.services.cone.util;

/**
 * A string with a language.
 * 
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class LocalizedString implements CharSequence
{
    private String value;
    private String language;
    
    /**
     * Default constructor.
     */
    public LocalizedString()
    {
        
    }
    
    /**
     * String constructor.
     * 
     * @param value The string value. Language will be null.
     */
    public LocalizedString(String value)
    {
        this.value = value;
        this.language = null;
    }
    
    /**
     * Full constructor.
     * 
     * @param value The string value.
     * @param language The language abbrev., e.g. 'de'.
     */
    public LocalizedString(String value, String language)
    {
        this.value = value;
        this.language = language;
    }
    
    public String getValue()
    {
        return value;
    }
    public void setValue(String value)
    {
        this.value = value;
    }
    public String getLanguage()
    {
        return language;
    }
    public void setLanguage(String language)
    {
        this.language = language;
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object obj)
    {
        if (!(obj instanceof LocalizedString))
        {
            return false;
        }
        else if (obj == null)
        {
            return false;
        }
        else if (this.value == null && ((LocalizedString) obj).getValue() != null)
        {
            return false;
        }
        else if (this.language == null && ((LocalizedString) obj).getLanguage() != null)
        {
            return false;
        }
        else if (this.value != null && !this.value.equals(((LocalizedString) obj).getValue()))
        {
            return false;
        }
        else if (this.language != null && !this.language.equals(((LocalizedString) obj).getLanguage()))
        {
            return false;
        }
        else
        {
            return true;
        }
            
    }

    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        return value;
    }

    /**
     * {@inheritDoc}
     */
    public char charAt(int index)
    {
        return value.charAt(index);
    }

    /**
     * {@inheritDoc}
     */
    public int length()
    {
        return value.length();
    }

    /**
     * {@inheritDoc}
     */
    public CharSequence subSequence(int start, int end)
    {
        return value.subSequence(start, end);
    }

    /**
     * Returns a concatenated localized string with the same language as this object.
     * 
     * @param other Another {@link LocalizedString}.
     * 
     * @return A newly created {@link LocalizedString}.
     */
    public LocalizedString concat(LocalizedString other)
    {
        return new LocalizedString(this.value + other.value, this.language);
    }
    
}