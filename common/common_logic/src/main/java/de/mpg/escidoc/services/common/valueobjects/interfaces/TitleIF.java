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

package de.mpg.escidoc.services.common.valueobjects.interfaces;

import java.util.List;
import de.mpg.escidoc.services.common.valueobjects.metadata.TextVO;

/**
 * Interface for ValueObjects that implement a title and/or alternative titles. 
 * 
 * @author: Thomas Diebäcker, created 20.06.2007
 * @version: $Revision$ $LastChangedDate$
 * Revised by DiT: 14.08.2007
 */
public interface TitleIF
{
    /**
     * Gets the title.
     * @return The title as a TextVO.
     */
    public TextVO getTitle();
    
    /**
     * Sets the title.
     * @param title The title as a TextVO.
     */
    public void setTitle(TextVO title);
    
    /**
     * Gets a list of alternative titles.
     * @return A list of alternative titles as TextVOs.
     */
    public List<TextVO> getAlternativeTitles();
}
