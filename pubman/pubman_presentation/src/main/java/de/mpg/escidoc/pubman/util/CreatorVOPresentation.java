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

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.editItem.EditItem;
import de.mpg.escidoc.pubman.editItem.EditItemSessionBean;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.IdentifierVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.IdentifierVO.IdType;
import de.mpg.escidoc.services.common.valueobjects.metadata.OrganizationVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.PersonVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.TextVO;

/**
 * Presentation wrapper for CreatorVO
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class CreatorVOPresentation extends CreatorVO
{
    private List<CreatorVOPresentation> list;
    private String ouNumbers;
    
    private PersonVO surrogatePerson;
    private OrganizationVO surrogateOrganization;
    
    public CreatorVOPresentation(List<CreatorVOPresentation> list)
    {
        this.list = list;
    }
    
    public CreatorVOPresentation(List<CreatorVOPresentation> list, CreatorVO creatorVO)
    {
        this.list = list;
        if (creatorVO != null)
        {
            this.setOrganization(creatorVO.getOrganization());
            this.setPerson(creatorVO.getPerson());
            this.setRole(creatorVO.getRole());
            this.setType(creatorVO.getType());
            
            if (this.getOrganization() != null && this.getOrganization().getName() == null)
            {
                this.getOrganization().setName(new TextVO());
            }
            
            if (this.getPerson() != null && this.getPerson().getIdentifier() == null)
            {
                this.getPerson().setIdentifier(new IdentifierVO());
            }
        }
    }
    
    /**
     * Add a new creator to the list after this creator.
     * 
     * @return Always empty
     */
    public String add()
    {
        CreatorVOPresentation creatorVOPresentation = new CreatorVOPresentation(this.list);
        creatorVOPresentation.init(getType());
        int index = this.list.indexOf(this);
        this.list.add(index + 1, creatorVOPresentation);
        return "";
    }
    
    public String remove()
    {
        this.list.remove(this);
        return "";
    }
    
    public String getTypeString()
    {
        return (this.getType() == null ? "" :this.getType().toString());
    }
    
    public void setTypeString(String value)
    {
        if (value == null)
        {
            this.setType(null);
        }
        else if (getType() != CreatorType.valueOf(value))
        {
            init(CreatorType.valueOf(value));
        }
    }
    
    public String getRoleString()
    {
        return (this.getRole() == null ? "" : this.getRole().toString());
    }
    
    public void setRoleString(String value)
    {
        if (value == null)
        {
            this.setRole(null);
        }
        else
        {
            this.setRole(CreatorRole.valueOf(value));
        }
    }
    
    public boolean isPersonType()
    {
        return (this.getType() == CreatorType.PERSON);
    }
    
    public boolean isOrganizationType()
    {
        return (this.getType() == CreatorType.ORGANIZATION);
    }
    
    public boolean isLast()
    {
        return (this.equals(this.list.get(this.list.size() - 1)));
    }
    
    public boolean isSingleCreator()
    {
        return (this.list.size() == 1);
    }
    
    public String getAutoPasteValue()
    {
        // Always empty
        return "";
    }
    
    public void setAutoPasteValue(String value)
    {
        if (!"".equals(value))
        {
            String[] values = value.split(EditItem.AUTOPASTE_INNER_DELIMITER);
            EditItemSessionBean editItemSessionBean = (EditItemSessionBean) EditItemSessionBean.getSessionBean(EditItemSessionBean.class);
            List<OrganizationVOPresentation> creatorOrganizations = editItemSessionBean.getCreatorOrganizations();
            OrganizationVOPresentation newOrg = new OrganizationVOPresentation();
            newOrg.setName(new TextVO(values[1]));
            newOrg.setIdentifier(values[0]);
            newOrg.setBean(editItemSessionBean);
            creatorOrganizations.add(newOrg);
            this.ouNumbers = creatorOrganizations.size() + "";
        }

    }

    public String getOuNumbers()
    {
        if (isPersonType() && this.ouNumbers == null)
        {
            EditItemSessionBean editItemSessionBean = (EditItemSessionBean) EditItemSessionBean.getSessionBean(EditItemSessionBean.class);
            List<OrganizationVOPresentation> creatorOrganizations = editItemSessionBean.getCreatorOrganizations();
            for (OrganizationVO organization : getPerson().getOrganizations())
            {
                if (ouNumbers == null)
                {
                    ouNumbers = "";
                }
                else
                {
                    ouNumbers += ",";
                }
                if (creatorOrganizations.indexOf(organization) >= 0)
                {
                    ouNumbers += creatorOrganizations.indexOf(organization) + 1;
                }
            }
        }
        return ouNumbers;
    }

    public int[] getOus()
    {
        if (getOuNumbers() != null && !"".equals(getOuNumbers()))
        {
            String[] orgArr = getOuNumbers().split(",");
            int[] result = new int[orgArr.length];
            
            try
            {
                for (int i = 0; i < orgArr.length; i++)
                {
                    if (!"".equals(orgArr[i]))
                    {
                        int orgNr = Integer.parseInt(orgArr[i]);
                        result[i] = orgNr;
                    }
                }
            }
            catch (NumberFormatException nfe)
            {
                FacesBean.error(((EditItem) FacesBean.getRequestBean(EditItem.class)).getMessage("EntryIsNotANumber").replace("$1", getOuNumbers()));
            }
            catch (IndexOutOfBoundsException ioobe)
            {
                FacesBean.error(((EditItem) FacesBean.getRequestBean(EditItem.class)).getMessage("EntryIsNotInValidRange").replace("$1", getOuNumbers()));
            }
            catch (Exception e)
            {
                FacesBean.error(((EditItem) FacesBean.getRequestBean(EditItem.class)).getMessage("ErrorInOrganizationAssignment").replace("$1", getOuNumbers()));
            }
            return result;
        }
        else
        {
            return new int[]{};
        }
    }
    
    public void setOus(int[] values)
    {
        String result = "";
        for (int i = 0; i < values.length; i++)
        {
            if (i > 0)
            {
                result += ",";
            }
            result += values[i];
        }
        setOuNumbers(result);
    }
    
    public void setOuNumbers(String ouNumbers)
    {
        this.ouNumbers = ouNumbers;
        //EditItem editItem = (EditItem) EditItem.getRequestBean(EditItem.class);
        //editItem.bindOrganizationsToCreator(this);
    }
    
    private void init(CreatorType type)
    {
        if (CreatorType.PERSON == type)
        {
            setType(CreatorType.PERSON);
            setPerson(new PersonVO());
            getPerson().setIdentifier(new IdentifierVO());
            getPerson().getIdentifier().setType(IdType.CONE);
            getPerson().setOrganizations(new ArrayList<OrganizationVO>());
        }
        else if (CreatorType.ORGANIZATION == type)
        {
            setType(CreatorType.ORGANIZATION);
            setOrganization(new OrganizationVO());
            getOrganization().setName(new TextVO());
        }
    }

    public PersonVO getPerson()
    {
        return surrogatePerson;
    }

    public void setPerson(PersonVO surrogatePerson)
    {
        this.surrogatePerson = surrogatePerson;
    }

    public OrganizationVO getOrganization()
    {
        return surrogateOrganization;
    }

    public void setOrganization(OrganizationVO surrogateOrganization)
    {
        this.surrogateOrganization = surrogateOrganization;
    }

    @Override
    public boolean equals(Object obj)
    {
        return (this == obj);
    }
    
    
    
}
