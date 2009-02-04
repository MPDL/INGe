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

package de.mpg.escidoc.pubman.search.bean;

import java.util.ArrayList;
import java.util.List;

import de.mpg.escidoc.pubman.appbase.DataModelManager;
import de.mpg.escidoc.pubman.search.bean.criterion.FileCriterion;

/**
 * Bean to handle the FileCriterionCollection on a single jsp.
 * A FileCriterionCollection is represented by a List<FileCriterionVO>.
 * 
 * @author Mario Wagner
 */
public class FileCriterionCollection
{
public static final String BEAN_NAME = "FileCriterionCollection";
    
    private List<FileCriterion> parentVO;
    private FileCriterionManager fileCriterionManager;
    
    /**
     * CTOR to create a new ArrayList<FileCriterionVO> 
     * starting with one empty new GenreCriterionVO
     */
    public FileCriterionCollection()
    {
        // ensure the parentVO is never null;
        List<FileCriterion> ctorList = new ArrayList<FileCriterion>();
        ctorList.add(new FileCriterion());
        setParentVO(ctorList);
    }

    /**
     * CTOR to refine or fill a predefined ArrayList<FileCriterionVO>
     * @param parentVO
     */
    public FileCriterionCollection(List<FileCriterion> parentVO)
    {
        setParentVO(parentVO);
    }

    public List<FileCriterion> getParentVO()
    {
        return parentVO;
    }

    public void setParentVO(List<FileCriterion> parentVO)
    {
        this.parentVO = parentVO;
        // ensure proper initialization of our DataModelManager
        fileCriterionManager = new FileCriterionManager(parentVO);
    }
    
    /**
     * Specialized DataModelManager to deal with objects of type FileCriterionBean
     * @author Mario Wagner
     */
    public class FileCriterionManager extends DataModelManager<FileCriterionBean>
    {
        List<FileCriterion> parentVO;
        
        public FileCriterionManager(List<FileCriterion> parentVO)
        {
            setParentVO(parentVO);
        }
        
        public FileCriterionBean createNewObject()
        {
            FileCriterion newVO = new FileCriterion();
            // create a new wrapper pojo
            FileCriterionBean fileCriterionBean = new FileCriterionBean(newVO);
            // we do not have direct access to the original list
            // so we have to add the new VO on our own
            parentVO.add(newVO);
            return fileCriterionBean;
        }
        
        @Override
        protected void removeObjectAtIndex(int i)
        {
            // due to wrapped data handling
            super.removeObjectAtIndex(i);
            parentVO.remove(i);
        }

        public List<FileCriterionBean> getDataListFromVO()
        {
            if (parentVO == null) return null;
            // we have to wrap all VO's in a nice FileCriterionBean
            List<FileCriterionBean> beanList = new ArrayList<FileCriterionBean>();
            for (FileCriterion fileCriterionVO : parentVO)
            {
                beanList.add(new FileCriterionBean(fileCriterionVO));
            }
            return beanList;
        }

        public void setParentVO(List<FileCriterion> parentVO)
        {
            this.parentVO = parentVO;
            // we have to wrap all VO's into a nice FileCriterionBean
            List<FileCriterionBean> beanList = new ArrayList<FileCriterionBean>();
            for (FileCriterion fileCriterionVO : parentVO)
            {
                beanList.add(new FileCriterionBean(fileCriterionVO));
            }
            setObjectList(beanList);
        }
        
        public int getSize()
        {
            return getObjectDM().getRowCount();
        }
    }


    public FileCriterionManager getFileCriterionManager()
    {
        return fileCriterionManager;
    }

    public void setFileCriterionManager(FileCriterionManager fileCriterionManager)
    {
        this.fileCriterionManager = fileCriterionManager;
    }

    public void clearAllForms()
    {        
        for (FileCriterionBean gcb : fileCriterionManager.getObjectList())
        {
            gcb.clearCriterion();
        }
    }

    public List<FileCriterion> getFilledCriterion()
    {
        List<FileCriterion> returnList = new ArrayList<FileCriterion>();
        for (FileCriterion vo : parentVO)
        {
            if (!(vo.getContentCategory() == null && vo.getComponentVisibility() == null 
                    && vo.getComponentAvailability() == null))
            {
                returnList.add(vo);
            }
        }
        return returnList;
    }
}
