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

import de.mpg.escidoc.pubman.appbase.DataModelManager;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.IdentifierVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.SourceVO;

/**
 * Bean to handle the SourceCollection on a single jsp.
 * A SourceCollection is represented by a List<SourceVO>.
 * 
 * @author Mario Wagner
 */
public class SourceCollection
{
    private List<SourceVO> parentVO;
    private SourceManager sourceManager;
    
    public SourceCollection()
    {
        // ensure the parentVO is never null;
        this(new ArrayList<SourceVO>());
    }

    public SourceCollection(List<SourceVO> parentVO)
    {
        setParentVO(parentVO);
    }

    public List<SourceVO> getParentVO()
    {
        return parentVO;
    }

    public void setParentVO(List<SourceVO> parentVO)
    {
        this.parentVO = parentVO;
        // ensure proper initialization of our DataModelManager
        sourceManager = new SourceManager(parentVO);
    }
    
    /**
     * Specialized DataModelManager to deal with objects of type SourceBean
     * @author Mario Wagner
     */
    public class SourceManager extends DataModelManager<SourceBean>
    {
        List<SourceVO> parentVO;
        
        public SourceManager(List<SourceVO> parentVO)
        {
            setParentVO(parentVO);
        }
        
        public SourceBean createNewObject()
        {
            SourceVO newVO = new SourceVO();
            // add creator additionally
            CreatorVO newCreator = new CreatorVO();
            newVO.getCreators().add(newCreator);
            // add new identifier amnually
            IdentifierVO newIdentifier = new IdentifierVO();
            newVO.getIdentifiers().add(newIdentifier);
            SourceBean sourceBean = new SourceBean(newVO);
            // we do not have direct access to the original list
            // so we have to add the new VO on our own
            parentVO.add(newVO);
            return sourceBean;
        }
        
        @Override
        protected void removeObjectAtIndex(int i)
        {
            // due to wrapped data handling
            super.removeObjectAtIndex(i);
            parentVO.remove(i);
        }

        public List<SourceBean> getDataListFromVO()
        {
            if (parentVO == null) return null;
            // we have to wrap all VO's in a nice SourceBean
            List<SourceBean> beanList = new ArrayList<SourceBean>();
            for (SourceVO sourceVO : parentVO)
            {
                beanList.add(new SourceBean(sourceVO));
            }
            return beanList;
        }

        public void setParentVO(List<SourceVO> parentVO)
        {
            this.parentVO = parentVO;
            // we have to wrap all VO's into a nice SourceBean
            List<SourceBean> beanList = new ArrayList<SourceBean>();
            for (SourceVO sourceVO : parentVO)
            {
                beanList.add(new SourceBean(sourceVO));
            }
            setObjectList(beanList);
        }
        
        public int getSize()
        {
            return getObjectDM().getRowCount();
        }
    }


    public SourceManager getSourceManager()
    {
        return sourceManager;
    }

    public void setSourceManager(SourceManager sourceManager)
    {
        this.sourceManager = sourceManager;
    }

    
}
