package de.mpg.mpdl.inge.pubman.web.search.bean;

import javax.faces.model.SelectItem;

import de.mpg.mpdl.inge.pubman.web.search.bean.criterion.Criterion;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.search.query.MetadataSearchCriterion.LogicalOperator;

/**
 * Abstract bean with common criterion behaviour.
 * 
 * @author Mario Wagner
 */
@SuppressWarnings("serial")
public abstract class CriterionBean extends FacesBean {
  protected boolean collapsed = false;
  private String logicOperator;

  public SelectItem LOGIC_AND = new SelectItem("And", getLabel("adv_search_logicop_and"));
  public SelectItem LOGIC_OR = new SelectItem("Or", getLabel("adv_search_logicop_or"));
  public SelectItem LOGIC_NOT = new SelectItem("Not", getLabel("adv_search_logicop_not"));
  public SelectItem[] LOGIC_OPTIONS = new SelectItem[] {LOGIC_AND, LOGIC_OR, LOGIC_NOT};

  public enum LogicOptions {
    LOGIC_AND, LOGIC_OR, LOGIC_NOT
  }

  public SelectItem[] getLogicOptions() {
    LogicOptions[] values = LogicOptions.values();
    return getI18nHelper().getSelectItemsForEnum(false, values);
  }

  public abstract Criterion getCriterionVO();

  public final String collapse() {
    setCollapsed(true);
    return null;
  }

  public final String expand() {
    setCollapsed(false);
    return null;
  }

  public final String getLogicOperator() {
    return this.logicOperator;
  }

  public final void setLogicOperator(String logicOperator) {
    this.logicOperator = logicOperator;
    if (logicOperator.equals("LOGIC_AND")) {
      getCriterionVO().setLogicalOperator(LogicalOperator.AND);
    } else if (logicOperator.equals("LOGIC_OR")) {
      getCriterionVO().setLogicalOperator(LogicalOperator.OR);
    } else if (logicOperator.equals("LOGIC_NOT")) {
      getCriterionVO().setLogicalOperator(LogicalOperator.NOT);
    }
  }

  public final boolean isCollapsed() {
    return this.collapsed;
  }

  public final void setCollapsed(boolean collapsed) {
    this.collapsed = collapsed;
  }
}
