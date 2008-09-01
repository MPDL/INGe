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

import de.mpg.escidoc.services.common.valueobjects.metadata.TextVO;

/**
 * The super class for the different metadata sets for the different types of items (Publication, Transcription, etc.)
 * 
 * @revised by MuJ: 28.08.2007
 * @version $Revision: 611 $ $LastChangedDate: 2007-11-07 12:04:29 +0100 (Wed, 07 Nov 2007) $ by $Author: jmueller $
 * @updated 05-Sep-2007 11:10:10
 */
public class MetadataSetVO extends ValueObject
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
     * The title of the item.
     */
    private TextVO title;
    
    

    /**
     * Creates a new instance.
     */
    public MetadataSetVO()
    {
        super();
    }

    /**
     * Copy constructor.
     * 
     * @param other
     */
    public MetadataSetVO(MetadataSetVO other)
    {
        super();
        setTitle(other.getTitle());
    }

    /**
     * Delivers the title.
     */
    public TextVO getTitle()
    {
        return title;
    }

    /**
     * Sets the title.
     * 
     * @param newVal newVal
     */
    public void setTitle(TextVO newVal)
    {
        title = newVal;
    }

    @Override
    protected MetadataSetVO clone() {
        return new MetadataSetVO(this);
    }

    
}