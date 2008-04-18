/**
 * 
 */
package de.mpg.escidoc.pubman.search.bean;

import de.mpg.escidoc.services.pubman.valueobjects.CriterionVO;
import de.mpg.escidoc.services.pubman.valueobjects.LanguageCriterionVO;
import de.mpg.escidoc.pubman.util.CommonUtils;
import javax.faces.model.SelectItem;

/**
 * @author endres
 *
 */
public class LanguageCriterionBean extends CriterionBean {

public static final String BEAN_NAME = "LanguageCriterionBean";
	
	private LanguageCriterionVO languageCriterionVO;
	
	// collapsed by default
	protected boolean collapsed = true;
	
    public LanguageCriterionBean()
	{
		// ensure the parentVO is never null;
		this(new LanguageCriterionVO());
	}

	public LanguageCriterionBean(LanguageCriterionVO languageCriterionVO)
	{
		setLanguageCriterionVO(languageCriterionVO);
	}

	@Override
	public CriterionVO getCriterionVO()
	{
		return languageCriterionVO;
	}

	public LanguageCriterionVO getLanguageCriterionVO()
	{
		return languageCriterionVO;
	}

	public void setLanguageCriterionVO( LanguageCriterionVO languageCriterionVO )
	{
		this.languageCriterionVO = languageCriterionVO;
	}

	/**
	 * Action navigation call to clear the current part of the form
	 * @return null
	 */
	public String clearCriterion()
	{
		languageCriterionVO.setSearchString("");
		
		// navigation refresh
		return null;
	}
	
	public SelectItem[] getLanguageOptions()
	{
	    return CommonUtils.getLanguageOptions();
	}
}
