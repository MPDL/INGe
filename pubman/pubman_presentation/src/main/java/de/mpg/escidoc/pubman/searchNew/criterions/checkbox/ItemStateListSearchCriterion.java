package de.mpg.escidoc.pubman.searchNew.criterions.checkbox;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import de.mpg.escidoc.pubman.searchNew.SearchParseException;
import de.mpg.escidoc.pubman.searchNew.criterions.SearchCriterionBase;
import de.mpg.escidoc.pubman.searchNew.criterions.component.MapListSearchCriterion;
import de.mpg.escidoc.pubman.searchNew.criterions.standard.StandardSearchCriterion;
import de.mpg.escidoc.services.common.valueobjects.FileVO.Visibility;

public class ItemStateListSearchCriterion extends MapListSearchCriterion<String> {

	public ItemStateListSearchCriterion() {
		
		super(getItemStateMap());
		// TODO Auto-generated constructor stub
	}
	
	private static Map<String, String> getItemStateMap()
	{
		Map<String, String> itemStateMap = new LinkedHashMap<String, String>();

		itemStateMap.put("PENDING", "pending");
		itemStateMap.put("SUBMITTED", "submitted");
		itemStateMap.put("IN_REVISION", "in-revision");
		itemStateMap.put("RELEASED", "released");
		itemStateMap.put("WITHDRAWN", "withdrawn");
		
		
		return itemStateMap;
	}

	@Override
	public String[] getCqlIndexes(Index indexName, String value) {
		switch(indexName)
		{
			case ITEM_CONTAINER_ADMIN : 
				{
					if("withdrawn".equals(value))
					{
						return new String[] {"\"/properties/public-status\""}; 
					}
					else
					{
						return new String[] {"\"/properties/version/status\""};
						
					}
				}
		}
		
		return null;
	}

	@Override
	public String getCqlValue(Index indexName, String value) {
		return value;
	}

	

	
	

}
