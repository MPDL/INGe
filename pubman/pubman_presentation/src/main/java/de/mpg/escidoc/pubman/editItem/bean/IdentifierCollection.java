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
* Copyright 2006-20110 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.pubman.editItem.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import de.mpg.escidoc.pubman.appbase.DataModelManager;
import de.mpg.escidoc.pubman.util.InternationalizationHelper;
import de.mpg.escidoc.services.common.valueobjects.metadata.IdentifierVO;

/**
 * Bean to handle the IdentifierCollection on a single jsp.
 * A IdentifierCollection is represented by a List<IdentifierVO>.
 *
 * @author Mario Wagner
 */
public class IdentifierCollection
{
    private List<IdentifierVO> parentVO;
    private IdentifierManager identifierManager;

    public IdentifierCollection()
    {
        // ensure the parentVO is never null;
        this(new ArrayList<IdentifierVO>());
    }

    public IdentifierCollection(List<IdentifierVO> parentVO)
    {
        setParentVO(parentVO);
    }

    public List<IdentifierVO> getParentVO()
    {
        return parentVO;
    }

    public void setParentVO(List<IdentifierVO> parentVO)
    {
        this.parentVO = parentVO;
        // ensure proper initialization of our DataModelManager
        identifierManager = new IdentifierManager(parentVO);
    }

    /**
     * localized creation of SelectItems for the identifier types available
     * @return SelectItem[] with Strings representing identifier types
     */
    public SelectItem[] getIdentifierTypes()
    {
        InternationalizationHelper i18nHelper = (InternationalizationHelper)FacesContext.getCurrentInstance().getApplication().getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(), InternationalizationHelper.BEAN_NAME);
        ResourceBundle labelBundle = ResourceBundle.getBundle(i18nHelper.getSelectedLabelBundle());

        ArrayList<SelectItem> selectItemList = new ArrayList<SelectItem>();
        
        // constants for comboBoxes
        selectItemList.add(new SelectItem("", labelBundle.getString("EditItem_NO_ITEM_SET")));
        
        for (IdentifierVO.IdType type : IdentifierVO.IdType.values())
        {
            selectItemList.add(new SelectItem(type.toString(), labelBundle.getString("ENUM_IDENTIFIERTYPE_" + type.toString())));
        }
        return selectItemList.toArray(new SelectItem[]{});
    }

    /**
     * Specialized DataModelManager to deal with objects of type IdentifierVO
     * @author Mario Wagner
     */
    public class IdentifierManager extends DataModelManager<IdentifierVO>
    {
        List<IdentifierVO> parentVO;
        
        public IdentifierManager(List<IdentifierVO> parentVO)
        {
            setParentVO(parentVO);
        }
        
        public IdentifierVO createNewObject()
        {
            IdentifierVO newIdentifier = new IdentifierVO();
            return newIdentifier;
        }
        
        public List<IdentifierVO> getDataListFromVO()
        {
            return parentVO;
        }

        public void setParentVO(List<IdentifierVO> parentVO)
        {
            this.parentVO = parentVO;
            setObjectList(parentVO);
        }
        
        public int getSize()
        {
            return getObjectDM().getRowCount();
        }
    }

    public IdentifierManager getIdentifierManager()
    {
        return identifierManager;
    }

    public void setIdentifierManager(IdentifierManager identifierManager)
    {
        this.identifierManager = identifierManager;
    }

}
