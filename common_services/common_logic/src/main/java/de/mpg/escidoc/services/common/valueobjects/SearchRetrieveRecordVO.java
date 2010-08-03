package de.mpg.escidoc.services.common.valueobjects;


public class SearchRetrieveRecordVO extends ValueObject {

	private String schema;
	private String packing;
	private int position;
	
	private ValueObject data;

	public String getSchema() 
	{
		return schema;
	}

	public void setSchema(String schema) 
	{
		this.schema = schema;
	}

	public String getPacking() 
	{
		return packing;
	}

	public void setPacking(String packing) 
	{
		this.packing = packing;
	}

	public int getPosition() 
	{
		return position;
	}

	public void setPosition(int position) 
	{
		this.position = position;
	}

	public ValueObject getData() 
	{
		return data;
	}

	public void setData(ValueObject data) 
	{
		this.data = data;
	}
}
