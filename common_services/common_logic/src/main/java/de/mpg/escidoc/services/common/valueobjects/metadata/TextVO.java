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
 * This class combines a text value with an optional language attribute.
 * 
 * @revised by MuJ: 27.08.2007
 * @version $Revision: 611 $ $LastChangedDate: 2007-11-07 12:04:29 +0100 (Wed, 07 Nov 2007) $ by $Author: jmueller $
 * @updated 05-Sep-2007 12:48:58
 */
public class TextVO extends ValueObject implements Cloneable
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
    private String language;
    private String value;

    /**
     * Creates a new instance with the given value.
     */
    public TextVO(String value)
    {
        super();
        this.value = value;
    }

    /**
     * Creates a new instance.
     */
    public TextVO()
    {
        super();
    }

    /**
     * Creates a new instance with the given value and language.
     * 
     * @param value The text value
     * @param language The text language
     */
    public TextVO(String value, String language)
    {
        this.value = value;
        this.language = language;
    }

    /**
     * Delivers the language of the text.
     */
    public String getLanguage()
    {
        return language;
    }

    /**
     * Delivers the value of the text.
     */
    public String getValue()
    {
        return value;
    }

    /**
     * Sets the language of the text.
     * 
     * @param newVal newVal
     */
    public void setLanguage(String newVal)
    {
        language = newVal;
    }

    /**
     * Sets the value of the text.
     * 
     * @param newVal newVal
     */
    public void setValue(String newVal)
    {
        value = newVal;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#clone()
     */
    @Override
    public Object clone()
    {
        TextVO vo = new TextVO();
        vo.setLanguage(getLanguage());
        vo.setValue(getValue());
        return vo;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals()
     */
    @Override
    public boolean equals(Object o)
    {
        if (o == null || !(o instanceof TextVO))
        {
            return false;
        }
        TextVO vo = (TextVO)o;
        return equals(getLanguage(), vo.getLanguage()) && equals(getValue(), vo.getValue());
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return value;
    }
}