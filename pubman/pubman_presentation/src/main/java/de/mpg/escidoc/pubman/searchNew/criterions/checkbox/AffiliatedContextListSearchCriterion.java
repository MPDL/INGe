package de.mpg.escidoc.pubman.searchNew.criterions.checkbox;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.contextList.ContextListSessionBean;
import de.mpg.escidoc.pubman.searchNew.SearchParseException;
import de.mpg.escidoc.pubman.searchNew.criterions.SearchCriterionBase;
import de.mpg.escidoc.pubman.searchNew.criterions.component.MapListSearchCriterion;
import de.mpg.escidoc.pubman.searchNew.criterions.standard.StandardSearchCriterion;
import de.mpg.escidoc.pubman.util.PubContextVOPresentation;
import de.mpg.escidoc.services.common.valueobjects.FileVO.Visibility;

public class AffiliatedContextListSearchCriterion extends MapListSearchCriterion<PubContextVOPresentation> {

	public AffiliatedContextListSearchCriterion() {
		
		super(getItemStateMap(), true);
		// TODO Auto-generated constructor stub
	}
	
	private static Map<String, PubContextVOPresentation> getItemStateMap()
	{
		
		ContextListSessionBean clsb = (ContextListSessionBean)FacesBean.getSessionBean(ContextListSessionBean.class);
		Map<String, PubContextVOPresentation> contextMap = new LinkedHashMap<String, PubContextVOPresentation>();

		
		for (PubContextVOPresentation context : clsb.getDepositorContextList())
		{
			contextMap.put(context.getReference().getObjectId(), context);
		}
		
		for (PubContextVOPresentation context : clsb.getModeratorContextList())
		{
			contextMap.put(context.getReference().getObjectId(), context);
		}
		

		
		
		
		return contextMap;
	}

	@Override
	public String[] getCqlIndexes(Index indexName, String value) {
		switch(indexName)
		{
		
			case ESCIDOC_ALL : 
			{
				return new String[] {"escidoc.context.objid"}; 
			}
			case ITEM_CONTAINER_ADMIN : 
			{
				return new String[] {"\"/properties/context/id\""}; 
			}
		}
		
		return null;
	}
	
	


	@Override
	public String getCqlValue(Index indexName, PubContextVOPresentation value) {
		
		return value.getReference().getObjectId();
	}
	
	/**
	 * List is empty if only if all are deselected
	 */
	@Override
	public boolean isEmpty() {
		
		
		boolean anySelected = getEnumMap().containsValue(true);
		boolean anyDeselected = getEnumMap().containsValue(false);

		return !anySelected;
	
	}

	

	
	

}
