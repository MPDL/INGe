/**
 * 
 */
package de.mpg.escidoc.pubman.search.bean;

import javax.faces.model.SelectItem;

import de.mpg.escidoc.pubman.search.bean.criterion.Criterion;
import de.mpg.escidoc.pubman.search.bean.criterion.LanguageCriterion;
import de.mpg.escidoc.pubman.util.CommonUtils;

/**
 * @author endres
 *
 */
public class LanguageCriterionBean extends CriterionBean {

public static final String BEAN_NAME = "LanguageCriterionBean";
	
	private LanguageCriterion languageCriterionVO;
	
	// collapsed by default
	protected boolean collapsed = true;
	
    public LanguageCriterionBean()
	{
		// ensure the parentVO is never null;
		this(new LanguageCriterion());
	}

	public LanguageCriterionBean(LanguageCriterion languageCriterionVO)
	{
		setLanguageCriterionVO(languageCriterionVO);
	}

	@Override
	public Criterion getCriterionVO()
	{
		return languageCriterionVO;
	}

	public LanguageCriterion getLanguageCriterionVO()
	{
		return languageCriterionVO;
	}

	public void setLanguageCriterionVO( LanguageCriterion languageCriterionVO )
	{
		this.languageCriterionVO = languageCriterionVO;
	}

	/**
	 * Action navigation call to clear the current part of the form
	 * @return null
	 */
	public String clearCriterion()
	{
		languageCriterionVO.setSearchString( null );
		
		// navigation refresh
		return null;
	}
	
	public SelectItem[] getLanguageOptions()
	{
	    return CommonUtils.getLanguageOptions();
	}
}
