/*
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

package de.mpg.escidoc.services.common.valueobjects.face;

import org.apache.log4j.Logger;

import de.mpg.escidoc.services.common.valueobjects.ItemVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.framework.PropertyReader;

/**
 * 
 * eSciDoc item for Faces.
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class FaceItemVO extends ItemVO
{
    
    private static Logger logger = Logger.getLogger(FaceItemVO.class);
    
    /**
     * Default constructor.
     */
    public FaceItemVO()
    {
        try
        {
            this.setContentModel(PropertyReader.getProperty("escidoc.framework_access.content-type.id.publication"));
        }
        catch (Exception e) {
            logger.error("Unable to set publication content model", e);
        }
    }
    
    /**
     * Clone constructor.
     * 
     * @param itemVO The item to be copied.
     */
    public FaceItemVO(ItemVO itemVO)
    {
        super(itemVO);
    }
    
    /**
     * {@inheritDoc}
     * 
     * @author Tobias Schraut
     */
    @Override
    public Object clone()
    {
        return new FaceItemVO(this);
    }
    
    /**
     * Getter for face-metadata. This only works if the forst metadata set of the item is of type face-metadata.
     * @return The face's metadata 
     */
    public MdsFaceVO getMetadata()
    {
        if (getMetadataSets() != null && getMetadataSets().size() > 0
                && getMetadataSets().get(0) instanceof MdsFaceVO)
        {
            return (MdsFaceVO) getMetadataSets().get(0);
        }
        else
        {
            return null;
        }
    }

    /**
     * Setter for face-metadata. Overrrides the first metadata set of the item.
     * If there is no metadata set, it will be added.
     * 
     * @param mdsFaceVO The new face metadata set.
     */
    public void setMetadata(MdsFaceVO mdsFaceVO)
    {
        if (getMetadataSets().size() > 0 && getMetadataSets().get(0) instanceof MdsFaceVO)
        {
            getMetadataSets().set(0, mdsFaceVO);
        }
        else if (getMetadataSets() != null)
        {
            getMetadataSets().add(mdsFaceVO);
        }
    }
}
