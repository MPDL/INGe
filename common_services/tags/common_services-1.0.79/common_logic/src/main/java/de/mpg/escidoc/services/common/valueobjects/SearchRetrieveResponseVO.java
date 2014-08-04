package de.mpg.escidoc.services.common.valueobjects;

import java.util.List;


public class SearchRetrieveResponseVO extends ValueObject {
	
	private String version;
	private int numberOfRecords;
	private List<SearchRetrieveRecordVO> records;
	
	
	public String getVersion()
	{
		return version;
	}
	
	public void setVersion(String version)
	{
		this.version = version;
	}
	
	public int getNumberOfRecords()
	{
		return numberOfRecords;
	}
	
	public void setNumberOfRecords(int numberOfRecords)
	{
		this.numberOfRecords = numberOfRecords;
	}
	
	public List<SearchRetrieveRecordVO> getRecords()
	{
		return records;
	}
	
	public void setRecords(List<SearchRetrieveRecordVO> records)
	{
		this.records = records;
	}
	

}
