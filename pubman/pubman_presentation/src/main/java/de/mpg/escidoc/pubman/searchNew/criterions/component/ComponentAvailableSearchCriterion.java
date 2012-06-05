package de.mpg.escidoc.pubman.searchNew.criterions.component;

import de.mpg.escidoc.pubman.searchNew.criterions.SearchCriterionBase;

public abstract class ComponentAvailableSearchCriterion extends SearchCriterionBase {

	private ComponentAvailability selectedAvailability = ComponentAvailability.WHATEVER;
	
	private String forcedOperator;
	
	public enum ComponentAvailability
	{
		YES, NO, WHATEVER
	}
	
	
	
	@Override
	public String toCqlString() {
		switch (selectedAvailability)
		{
			case YES : 	{
							return "escidoc.component.content.storage=\"" + escapeForCql(getStorageType()) + "\""; 
						}
			
			case NO : 	{
							return "escidoc.component.content.storage<>\"" + escapeForCql(getStorageType()) + "\"";
						}
			
			case WHATEVER : return null;
		}
		return null;
	}
	
	public abstract String getStorageType();

	@Override
	public String toQueryString() {
		return getSearchCriterion() + "=\"" + getSelectedAvailability() + "\"";
	}

	@Override
	public void parseQueryStringContent(String content) {
		this.selectedAvailability = ComponentAvailability.valueOf(content);
	}
	
	

	@Override
	public boolean isEmpty() {
		return ComponentAvailability.WHATEVER.equals(selectedAvailability);
	}

	public ComponentAvailability getSelectedAvailability() {
		return selectedAvailability;
	}

	public void setSelectedAvailability(ComponentAvailability selectedAvailability) {
		this.selectedAvailability = selectedAvailability;
	}

	public String getForcedOperator() {
		return forcedOperator;
	}

	public void setForcedOperator(String forcedOperator) {
		this.forcedOperator = forcedOperator;
	}

}
