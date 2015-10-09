package de.mpg.escidoc.services.common.valueobjects.statistics;

import java.util.List;

import de.mpg.escidoc.services.common.valueobjects.ValueObject;

/**
 * VO Class representing an aggregation definition
 * 
 * @author Markus Haarländer
 */
public class AggregationDefinitionVO extends ValueObject {

	
	private String objectId;
	
	private String scopeId;
	
	private String name;
	
	private List<AggregationTableVO> aggregationTables;
	
	private String statisticDataXPath;

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public String getScopeId() {
		return scopeId;
	}

	public void setScopeId(String scopeId) {
		this.scopeId = scopeId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<AggregationTableVO> getAggregationTables() {
		return aggregationTables;
	}

	public void setAggregationTables(List<AggregationTableVO> aggregationTables) {
		this.aggregationTables = aggregationTables;
	}

	public String getStatisticDataXPath() {
		return statisticDataXPath;
	}

	public void setStatisticDataXPath(String statisticDataXPath) {
		this.statisticDataXPath = statisticDataXPath;
	}
}
