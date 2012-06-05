package de.mpg.escidoc.pubman.searchNew.criterions.standard;

import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.searchNew.criterions.SearchCriterionBase.SearchCriterion;
import de.mpg.escidoc.pubman.searchNew.criterions.stringOrHiddenId.StringOrHiddenIdSearchCriterion;
import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.pubman.util.InternationalizationHelper;

public class LanguageSearchCriterion extends StandardSearchCriterion {

	@Override
	public String[] getCqlIndexes() {
		return new String[] {"escidoc.publication.language"};
	}
	
	public String getAlternativeValue() throws Exception
    {
        String locale = ((InternationalizationHelper) FacesBean.getSessionBean(InternationalizationHelper.class)).getLocale();
        return CommonUtils.getConeLanguageName(getSearchString(), locale);
    }
	
	@Override
	public SearchCriterion getSearchCriterion() {
		return SearchCriterion.LANG;
	}


}
