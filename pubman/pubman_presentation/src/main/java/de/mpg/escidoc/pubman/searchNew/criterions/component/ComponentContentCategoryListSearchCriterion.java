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
* Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 
package de.mpg.escidoc.pubman.searchNew.criterions.component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bouncycastle.voms.VOMSAttribute;

import de.mpg.escidoc.pubman.ApplicationBean;
import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.searchNew.criterions.SearchCriterionBase;
import de.mpg.escidoc.pubman.searchNew.criterions.enums.GenreSearchCriterion;
import de.mpg.escidoc.pubman.searchNew.criterions.operators.LogicalOperator;
import de.mpg.escidoc.pubman.searchNew.criterions.operators.Parenthesis;
import de.mpg.escidoc.pubman.searchNew.criterions.standard.ComponentVisibilitySearchCriterion;
import de.mpg.escidoc.pubman.searchNew.criterions.standard.DegreeSearchCriterion;
import de.mpg.escidoc.pubman.util.InternationalizationHelper;
import de.mpg.escidoc.services.common.valueobjects.FileVO.Visibility;
import de.mpg.escidoc.services.common.valueobjects.metadata.MdsFileVO;
import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO.DegreeType;
import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO.Genre;

public class ComponentContentCategoryListSearchCriterion extends MapListSearchCriterion{

	
	
	
	
	public ComponentContentCategoryListSearchCriterion()
	{
		super(getContentCategoryMap());
	}
	
	private static Map<String, String> getContentCategoryMap()
	{
		ApplicationBean appBean = (ApplicationBean)FacesBean.getApplicationBean(ApplicationBean.class);
        
		Map<String, String> ccMap = appBean.getContentCategoryMap();
		Map<String, String> newMap = new HashMap<String, String>();
		
		for(Entry<String,String> entry: ccMap.entrySet())
		{
			newMap.put(entry.getKey().toLowerCase().replace("_", "-"), entry.getValue());	
		}
		
		
		return newMap;
	}

	

	@Override
	public SearchCriterion getSearchCriterion() {
		return SearchCriterion.COMPONENT_CONTENT_CATEGORY_LIST;
	}

	@Override
	public String[] getCqlIndexes() {
		return new String[]{"escidoc.component.content-category"};
	}
	

}
