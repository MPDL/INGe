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

import java.util.List;

import javax.faces.model.SelectItem;

import de.mpg.escidoc.pubman.appbase.DataModelManager;
import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.services.common.valueobjects.interfaces.TitleIF;
import de.mpg.escidoc.services.common.valueobjects.metadata.TextVO;
import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO;

/**
 * Bean to deal with one title and many alternative titles on a single jsp.
 * 
 * @author Mario Wagner
 */
public class TitleCollection
{
	private TitleIF titleIF;
	private AlternativeTitleManager alternativeTitleManager;
	
	public TitleCollection()
	{
		// ensure the parentVO is never null;
		this(new MdsPublicationVO());
	}

	public TitleCollection(TitleIF titleIF)
	{
		setTitleIF(titleIF);
	}

	public String removeTitle()
	{
		// when the title is removed, the first alternative title will become the title
		TextVO newTitle = getTitleIF().getAlternativeTitles().remove(0);
		getTitleIF().setTitle(newTitle);
		return null;
	}

	public String addTitle()
	{
		// a new title will be added at the end of the list of alternative titles
		TextVO newTitle = new TextVO();
		this.titleIF.getAlternativeTitles().add(newTitle);
		return null;
	}
	
	public TitleIF getTitleIF()
	{
		return titleIF;
	}

	public void setTitleIF(TitleIF title)
	{
		this.titleIF = title;
		if( titleIF == null )
        {
           titleIF = new MdsPublicationVO();
           titleIF.setTitle(new TextVO());
        }
		else if (titleIF.getTitle() == null)
		{
			titleIF.setTitle(new TextVO());
		}
		// ensure proper initialization of our DataModelManager
		alternativeTitleManager = new AlternativeTitleManager(titleIF);
	}
	
    public SelectItem[] getLanguageOptions()
    {
    	return CommonUtils.getLanguageOptions();
    }

	/**
	 * Specialized DataModelManager to deal with objects of type TextVO
	 * @author Mario Wagner
	 */
	public class AlternativeTitleManager extends DataModelManager<TextVO>
	{
		TitleIF titleIFimpl;
		
		public AlternativeTitleManager(TitleIF parentVO)
		{
			setTitleIF(parentVO);
		}
		
		public TextVO createNewObject()
		{
			TextVO newTitle = new TextVO();
			return newTitle;
		}
		
		public List<TextVO> getDataListFromVO()
		{
			if (titleIFimpl == null) return null;
			return titleIFimpl.getAlternativeTitles();
		}

		public void setTitleIF(TitleIF titleIFimpl)
		{
			this.titleIFimpl = titleIFimpl;
			setObjectList(titleIF.getAlternativeTitles());
		}
		
		public int getSize()
		{
			return getObjectDM().getRowCount();
		}
	}

	public AlternativeTitleManager getAlternativeTitleManager()
	{
		return alternativeTitleManager;
	}

	public void setAlternativeTitleManager(AlternativeTitleManager alternativeTitleManager)
	{
		this.alternativeTitleManager = alternativeTitleManager;
	}
	
}
