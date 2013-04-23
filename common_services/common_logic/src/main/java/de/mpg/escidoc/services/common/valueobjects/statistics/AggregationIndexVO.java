package de.mpg.escidoc.services.common.valueobjects.statistics;

import java.util.List;

public class AggregationIndexVO {

	private String name;
	
	private List<String> fields;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getFields() {
		return fields;
	}

	public void setFields(List<String> fields) {
		this.fields = fields;
	}

	
}
