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

import javax.faces.model.SelectItem;

import de.mpg.escidoc.pubman.search.bean.criterion.Criterion;
import de.mpg.escidoc.pubman.search.bean.criterion.FileCriterion;

/**
 * POJO bean to deal with one FileCriterionVO.
 * @author endres
 *
 */
public class FileCriterionBean extends CriterionBean
{

/**
     * 
     */
    private static final long serialVersionUID = 1L;

public static final String BEAN_NAME = "FileCriterionBean";
    
    private FileCriterion fileCriterionVO;
    
    public FileCriterionBean()
    {
        // ensure the parentVO is never null;
        this(new FileCriterion());
    }

    public FileCriterionBean(FileCriterion fileCriterionVO)
    {
        setFileCriterionVO(fileCriterionVO);
    }

    @Override
    public Criterion getCriterionVO()
    {
        return fileCriterionVO;
    }

    public FileCriterion getFileCriterionVO()
    {
        return fileCriterionVO;
    }

    public void setFileCriterionVO(FileCriterion fileCriterionVO)
    {
        this.fileCriterionVO = fileCriterionVO;
    }
    
    
    /**
     * Action navigation call to clear the current part of the form
     * @return null
     */
    public String clearCriterion()
    {
        fileCriterionVO.setExcludeCategory(false);
        fileCriterionVO.setComponentVisibility(null);
        fileCriterionVO.setComponentAvailability(null);
        fileCriterionVO.setContentCategory(null);
        
        // navigation refresh
        return null;
    }
    
    /**
     * Returns all options for content category.
     * 
     * @return all options for content category.
     */
    public SelectItem[] getContentCategories()
    {
        return this.i18nHelper.getSelectItemsContentCategory(true);
    }
    
    /**
     * Returns all options for component visibility.
     * 
     * @return all options for content category.
     */
    public SelectItem[] getComponentVisibilityOptions()
    {
        return this.i18nHelper.getSelectedItemsComponentVisibility(true);
    }
    
    /**
     * Returns all options for component availability.
     * 
     * @return all options for content category.
     */
    public SelectItem[] getComponentAvailabilityOptions()
    {
        return this.i18nHelper.getSelectedItemsComponentAvailability(true);
    }
}
