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

package de.mpg.escidoc.pubman.util;

import java.util.List;

import javax.faces.event.ValueChangeEvent;

import de.mpg.escidoc.services.common.valueobjects.metadata.OrganizationVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.TextVO;

/**
 * Presentation wrapper for OrganizationVO.
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class OrganizationVOPresentation extends OrganizationVO
{
    private int number;
    private List<OrganizationVOPresentation> list;

    public OrganizationVOPresentation()
    {
        super();
        setName(new TextVO());
    }
    
    public OrganizationVOPresentation(OrganizationVO organizationVO)
    {
        this.setAddress(organizationVO.getAddress());
        this.setIdentifier(organizationVO.getIdentifier());
        this.setName(organizationVO.getName());
    }
    
    public String add()
    {
        OrganizationVOPresentation organizationPresentation = new OrganizationVOPresentation();
        organizationPresentation.setList(list);
        list.add(number, organizationPresentation);
        for (int i = number; i < list.size(); i++)
        {
            list.get(i).setNumber(i + 1);
        }
        return "";
    }

    public String remove()
    {
        list.remove(this);
        for (int i = number - 1; i < list.size(); i++)
        {
            list.get(i).setNumber(i + 1);
        }
        return "";
    }
    
    /**
     * @return the number
     */
    public int getNumber()
    {
        return number;
    }

    /**
     * @param number the number to set
     */
    public void setNumber(int number)
    {
        this.number = number;
    }

    /**
     * @return the list
     */
    public List<OrganizationVOPresentation> getList()
    {
        return list;
    }

    /**
     * @param list the list to set
     */
    public void setList(List<OrganizationVOPresentation> list)
    {
        this.list = list;
    }
    
    public void nameListener(ValueChangeEvent event)
    {
        if(event.getNewValue() != event.getOldValue())
        {
            this.setName(new TextVO(event.getNewValue().toString()));
        }
    }
    
    public boolean getLast()
    {
    	return (this.equals(list.get(list.size() - 1)));
    }
}
