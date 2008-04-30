package de.mpg.escidoc.pubman.search.bean;

import javax.faces.model.SelectItem;

import de.mpg.escidoc.pubman.ApplicationBean;
import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.util.InternationalizationHelper;
import de.mpg.escidoc.services.common.valueobjects.FileVO;
import de.mpg.escidoc.services.pubman.valueobjects.CriterionVO;
import de.mpg.escidoc.services.pubman.valueobjects.CriterionVO.LogicOperator;



/**
 * Abstract bean with common criterion behaviour.
 * 
 * @author Mario Wagner
 */
public abstract class CriterionBean extends FacesBean
{
	protected boolean collapsed = false;
	private String logicOperator;
	
    public SelectItem LOGIC_AND = new SelectItem("And", this.getLabel("adv_search_logicop_and"));
    public SelectItem LOGIC_OR = new SelectItem("Or", this.getLabel("adv_search_logicop_or"));
    public SelectItem LOGIC_NOT = new SelectItem("Not", this.getLabel("adv_search_logicop_not"));
    public SelectItem[] LOGIC_OPTIONS = new SelectItem[]{LOGIC_AND, LOGIC_OR, LOGIC_NOT};

    public enum LogicOptions
    {
    	LOGIC_AND, LOGIC_OR, LOGIC_NOT
    }
    
    public SelectItem[] getLogicOptions()
    {
    	LogicOptions[] values = LogicOptions.values();
    	return ((InternationalizationHelper)getSessionBean(InternationalizationHelper.class)).getSelectItemsForEnum(false, values);
    }

    public abstract CriterionVO getCriterionVO();
    
	public final String collapse()
	{
		setCollapsed(true);
		return null;
	}

	public final String expand()
	{
		setCollapsed(false);
		return null;
	}

	public final String getLogicOperator()
	{
		return logicOperator;
	}

	public final void setLogicOperator(String logicOperator)
	{
		this.logicOperator = logicOperator;
		if (logicOperator.equals("And"))
		{
			getCriterionVO().setLogicOperator(LogicOperator.AND);
		}
		else if (logicOperator.equals("Or"))
		{
			getCriterionVO().setLogicOperator(LogicOperator.OR);
		}
		else if (logicOperator.equals("Not"))
		{
			getCriterionVO().setLogicOperator(LogicOperator.NOT);
		}
	}

	public final boolean isCollapsed()
	{
		return collapsed;
	}

	public final void setCollapsed(boolean collapsed)
	{
		this.collapsed = collapsed;
	}

}
