package de.mpg.escidoc.pubman.searchNew.criterions.standard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.naming.InitialContext;

import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.searchNew.criterions.SearchCriterionBase.SearchCriterion;
import de.mpg.escidoc.pubman.searchNew.criterions.stringOrHiddenId.StringOrHiddenIdSearchCriterion;
import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.pubman.util.InternationalizationHelper;
import de.mpg.escidoc.pubman.util.SelectItemComparator;
import de.mpg.escidoc.services.common.valueobjects.ContextVO;
import de.mpg.escidoc.services.pubman.PubItemDepositing;

public class CollectionSearchCriterion extends StandardSearchCriterion {

	@Override
	public String[] getCqlIndexes() {
		return new String[] {"escidoc.context.objid"};
	}
	
	
	@Override
	public SearchCriterion getSearchCriterion() {
		return SearchCriterion.COLLECTION;
	}

	

}
