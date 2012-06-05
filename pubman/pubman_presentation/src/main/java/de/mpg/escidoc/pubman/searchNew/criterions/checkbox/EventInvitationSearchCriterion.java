package de.mpg.escidoc.pubman.searchNew.criterions.checkbox;

import de.mpg.escidoc.pubman.searchNew.criterions.SearchCriterionBase;

public class EventInvitationSearchCriterion extends SearchCriterionBase {

	private boolean invited = false;
	
	@Override
	public String toCqlString() {
		if(isInvited())
		{
			return "escidoc.publication.event.invitation-status\"invited\"";
		}
		else
		{
			return null;
		}
	}

	@Override
	public String toQueryString() {
		return getSearchCriterion() + "=\"" + invited + "\"";
	}

	@Override
	public void parseQueryStringContent(String content) {
		this.invited = Boolean.parseBoolean(content);
	}

	@Override
	public boolean isEmpty() {
		return !isInvited();
	}

	public boolean isInvited() {
		return invited;
	}

	public void setInvited(boolean invited) {
		this.invited = invited;
	}

	@Override
	public SearchCriterion getSearchCriterion() {
		return searchCriterion.EVENT_INVITATION;
	}


}
