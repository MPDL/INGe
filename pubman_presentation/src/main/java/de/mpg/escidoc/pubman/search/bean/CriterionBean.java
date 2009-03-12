package de.mpg.escidoc.pubman.search.bean;

import javax.faces.model.SelectItem;

import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.search.bean.criterion.Criterion;
import de.mpg.escidoc.pubman.util.InternationalizationHelper;
import de.mpg.escidoc.services.search.query.MetadataSearchCriterion.LogicalOperator;



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

    public abstract Criterion getCriterionVO();
    
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
		if (logicOperator.equals("LOGIC_AND"))
		{
			getCriterionVO().setLogicalOperator(LogicalOperator.AND);
		}
		else if (logicOperator.equals("LOGIC_OR"))
		{
			getCriterionVO().setLogicalOperator(LogicalOperator.OR);
		}
		else if (logicOperator.equals("LOGIC_NOT"))
		{
			getCriterionVO().setLogicalOperator(LogicalOperator.NOT);
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
