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

package de.mpg.escidoc.pubman.editItem.bean;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.appbase.DataModelManager;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.OrganizationVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.PersonVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.TextVO;

/**
 * Bean to handle the CreatorCollection on a single jsp.
 * A CreatorCollection is represented by a List&lt;CreatorVO>.
 * 
 * @author Mario Wagner
 */
public class CreatorCollection
{
    private static Logger logger = Logger.getLogger(CreatorCollection.class);
    
    private List<CreatorVO> parentVO;
    private CreatorManager creatorManager;
    
    public CreatorCollection()
    {
        // ensure the parentVO is never null;
        this(new ArrayList<CreatorVO>());
    }

    public CreatorCollection(List<CreatorVO> parentVO)
    {
        setParentVO(parentVO);
    }

    public List<CreatorVO> getParentVO()
    {
        return parentVO;
    }

    public void setParentVO(List<CreatorVO> parentVO)
    {
        this.parentVO = parentVO;
        // ensure proper initialization of our DataModelManager
        creatorManager = new CreatorManager(parentVO);
    }
    
    /**
     * Specialized DataModelManager to deal with objects of type CreatorBean
     * @author Mario Wagner
     */
    public class CreatorManager extends DataModelManager<CreatorBean>
    {
        protected List<CreatorVO> parentVO;
        
        public CreatorManager(List<CreatorVO> parentVO)
        {
            setParentVO(parentVO);
        }
        
        public CreatorBean createNewObject()
        {
            int i = objectDM.getRowIndex();
            CreatorVO newVO = new CreatorVO();
            newVO.setPerson(new PersonVO());
            // create a new Organization for this person
            OrganizationVO newPersonOrganization = new OrganizationVO();

            newPersonOrganization.setName(new TextVO());
            newVO.getPerson().getOrganizations().add(newPersonOrganization);

            CreatorBean creatorBean = new CreatorBean(newVO);
            // we do not have direct access to the original list
            // so we have to add the new VO on our own
            if (parentVO.size() > 0)
            {
                parentVO.add(i + 1, newVO);
            }
            else
            {
                parentVO.add(newVO);
            }
            return creatorBean;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        protected void removeObjectAtIndex(int i)
        {
            // due to wrapped data handling
            super.removeObjectAtIndex(i);
            parentVO.remove(i);
        }

        
        //public List<CreatorBean> getDataListFromVO()
        //{
            /*
            if (parentVO == null) return null;
            // we have to wrap all VO's in a nice CreatorBean
            List<CreatorBean> beanList = new ArrayList<CreatorBean>();
            for (CreatorVO creatorVO : parentVO)
            {
                beanList.add(new CreatorBean(creatorVO));
            }
            return beanList;
            */
        //}

        
        public void setParentVO(List<CreatorVO> parentVO)
        {
            this.parentVO = parentVO;
            // we have to wrap all VO's into a nice CreatorBean
            List<CreatorBean> beanList = new ArrayList<CreatorBean>();
            for (CreatorVO creatorVO : parentVO)
            {
                beanList.add(new CreatorBean(creatorVO));
            }
            setObjectList(beanList);
        }
        
        public int getSize()
        {
            return getObjectDM().getRowCount();
        }
    }


    public CreatorManager getCreatorManager()
    {
        return creatorManager;
    }

    public void setCreatorManager(CreatorManager creatorManager)
    {
        this.creatorManager = creatorManager;
    }

    
}
