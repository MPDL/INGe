package de.mpg.escidoc.pubman.search.bean;

import java.util.ResourceBundle;

import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import de.mpg.escidoc.pubman.appbase.Internationalized;
import de.mpg.escidoc.pubman.appbase.InternationalizedImpl;
import de.mpg.escidoc.pubman.util.InternationalizationHelper;
import de.mpg.escidoc.services.pubman.valueobjects.CriterionVO;
import de.mpg.escidoc.services.pubman.valueobjects.CriterionVO.LogicOperator;

/**
 * Abstract bean with common criterion behaviour.
 * 
 * @author Mario Wagner
 */
public abstract class CriterionBean extends InternationalizedImpl
{
	private boolean collapsed = false;
	private String logicOperator;
	
    public static final SelectItem LOGIC_AND = new SelectItem("And", "And");
    public static final SelectItem LOGIC_OR = new SelectItem("Or", "Or");
    public static final SelectItem LOGIC_NOT = new SelectItem("Not", "Not");
    public static final SelectItem[] LOGIC_OPTIONS = new SelectItem[]{LOGIC_AND, LOGIC_OR, LOGIC_NOT};

    public SelectItem[] getLogicOptions()
    {
        return LOGIC_OPTIONS;
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
