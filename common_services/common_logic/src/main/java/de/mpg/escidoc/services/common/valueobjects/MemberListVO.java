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

import java.util.List;

import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;

/**
 * Represents a member list according to http://www.escidoc.de/schemas/memberlist/0.3
 *
 * @author Markus Haarlaender (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * @revised by FrW: 10.06.2008
 *
 */
public class MemberListVO
{
    protected List<? extends ContainerVO> containerVOList;
    
    protected List<? extends ItemVO> itemVOList;

    public List<? extends ContainerVO> getContainerVOList()
    {
        return containerVOList;
    }

    public void setContainerVOList(List<? extends ContainerVO> containerVOList)
    {
        this.containerVOList = containerVOList;
    }

    public List<? extends ItemVO> getItemVOList()
    {
        return itemVOList;
    }

    public void setItemVOList(List<? extends ItemVO> itemVOList)
    {
        this.itemVOList = itemVOList;
    }
}
