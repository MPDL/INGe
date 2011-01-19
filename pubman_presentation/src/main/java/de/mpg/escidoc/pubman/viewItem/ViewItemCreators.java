package de.mpg.escidoc.pubman.viewItem;
/**
 * 
 * @author yu
 *
 */
public class ViewItemCreators {
	private String creatorType;
	private Object creatorObj;
	
	public enum Type
	{
		PERSON, ORGANIZATION;
	}
	
	public ViewItemCreators()
	{
		
	}
	
	public String getCreatorType()
	{
		return creatorType;
	}
	public void setCreatorType(String creatorType) 
	{
		this.creatorType = creatorType;
	}
	public Object getCreatorObj() 
	{
		return creatorObj;
	}
	public void setCreatorObj(Object creatorObj) 
	{
		this.creatorObj = creatorObj;
	}
	
	

}
