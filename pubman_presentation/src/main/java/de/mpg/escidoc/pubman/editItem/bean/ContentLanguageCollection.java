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

import javax.faces.model.SelectItem;

import de.mpg.escidoc.pubman.appbase.DataModelManager;
import de.mpg.escidoc.pubman.util.CommonUtils;

/**
 * Bean to handle the ContentLanguageCollection on a single jsp.
 * A ContentLanguageCollection is represented by a List<String>.
 * 
 * @author Mario Wagner
 */
public class ContentLanguageCollection
{
    private List<String> parentVO;
    private ContentLanguageManager contentLanguageManager;
    
    public ContentLanguageCollection()
    {
        // ensure the parentVO is never null;
        this(new ArrayList<String>());
    }

    public ContentLanguageCollection(List<String> parentVO)
    {
        setParentVO(parentVO);
    }

    public List<String> getParentVO()
    {
        return parentVO;
    }

    public void setParentVO(List<String> parentVO)
    {
        this.parentVO = parentVO;
        // ensure proper initialization of our DataModelManager
        contentLanguageManager = new ContentLanguageManager(parentVO);
    }
    
    public SelectItem[] getLanguageOptions()
    {
        return CommonUtils.getLanguageOptions();
    }

    /**
     * Specialized DataModelManager to deal with objects of type String
     * @author Mario Wagner
     */
    public class ContentLanguageManager extends DataModelManager<String>
    {
        List<String> parentVO;
        
        public ContentLanguageManager(List<String> parentVO)
        {
            setParentVO(parentVO);
        }
        
        public String createNewObject()
        {
            String newLanguage = new String();
            return newLanguage;
        }
        
        public List<String> getDataListFromVO()
        {
            if (parentVO == null) return null;
            return parentVO;
        }

        public void setParentVO(List<String> parentVO)
        {
            this.parentVO = parentVO;
            setObjectList(parentVO);
        }
        
        public int getSize()
        {
            return getObjectDM().getRowCount();
        }
    }

    public ContentLanguageManager getContentLanguageManager()
    {
        return contentLanguageManager;
    }

    public void setContentLanguageManager(ContentLanguageManager contentLanguageManager)
    {
        this.contentLanguageManager = contentLanguageManager;
    }

}
