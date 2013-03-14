package de.mpg.escidoc.services.cone.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultEntry extends LocalizedString{
	

	private String sortResult;
	
	private String type;

	
	
	
	private Map<String, List<LocalizedTripleObject>> valueMap = new HashMap<String, List<LocalizedTripleObject>>();
	
	

	public ResultEntry(String value)
	{
		super(value);
	}
	
	public ResultEntry(String value, String lang, String type, String sortResult) {
		super();
		super.setValue(value);
		super.setLanguage(lang);
		this.sortResult = sortResult;
		this.type = type;
	}




	public Map<String, List<LocalizedTripleObject>> getValueMap() {
		return valueMap;
	}

	public void setValueMap(Map<String, List<LocalizedTripleObject>> valueMap) {
		this.valueMap = valueMap;
	}
	
	/*
	@Override
	public boolean equals(Object obj)
	{
		 if (obj == this) {
	           return true;
	     }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }

        ResultEntry res = (ResultEntry) obj;

		return (getResult()!=null && getResult().equals(res.getResult()));
	}
	
	@Override
	public String toString()
	{
		return getResult();
				
	}
	*/

	public String getSortResult() {
		return sortResult;
	}

	public void setSortResult(String sortResult) {
		this.sortResult = sortResult;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
