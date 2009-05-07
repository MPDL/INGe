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
 * @version $Revision$ $LastChangedDate$ by $Author$
 * @updated 05-Sep-2007 12:48:58
 */
public class PublishingInfoVO extends ValueObject implements Cloneable
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
    private String edition;
    private String place;
    private String publisher;

    /**
     * Delivers the version of the described item.
     */
    public String getEdition()
    {
        return edition;
    }

    /**
     * Delivers the place where the item has been published.
     */
    public String getPlace()
    {
        return place;
    }

    /**
     * Delivers the name of the institution who has published the item.
     */
    public String getPublisher()
    {
        return publisher;
    }

    /**
     * Sets the version of the described item.
     * 
     * @param newVal
     */
    public void setEdition(String newVal)
    {
        edition = newVal;
    }

    /**
     * Sets the place where the item has been published.
     * 
     * @param newVal
     */
    public void setPlace(String newVal)
    {
        place = newVal;
    }

    /**
     * Sets the name of the institution who has published the item.
     * 
     * @param newVal
     */
    public void setPublisher(String newVal)
    {
        publisher = newVal;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#clone()
     */
    @Override
    public Object clone()
    {
        PublishingInfoVO vo = new PublishingInfoVO();
        vo.setEdition(getEdition());
        vo.setPlace(getPlace());
        vo.setPublisher(getPublisher());
        return vo;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#clone()
     */
    @Override
    public boolean equals(Object o)
    {
        if (o == null || !(o instanceof PublishingInfoVO))
        {
            return false;
        }
        PublishingInfoVO vo = (PublishingInfoVO)o;
        return equals(getEdition(), vo.getEdition()) && equals(getPlace(), vo.getPlace())
                && equals(getPublisher(), vo.getPublisher());
    }
}