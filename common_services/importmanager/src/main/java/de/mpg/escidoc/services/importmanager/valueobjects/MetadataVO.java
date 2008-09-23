package de.mpg.escidoc.services.importmanager.valueobjects;

import java.net.URL;
/**
 * 
 * @author kleinfe1
 *
 */
public class MetadataVO 
{

	private String mdDesc;
	private URL mdUrl;
	private String mdFormat;
	private String mdLabel;
	private boolean mdDefault;
	private String fileType;
	
	
	public String getFileType() 
	{
		return this.fileType;
	}
	public void setFileType(String fileType) 
	{
		this.fileType = fileType;
	}
	public String getMdDesc() 
	{
		return this.mdDesc;
	}
	public void setMdDesc(String mdDesc) 
	{
		this.mdDesc = mdDesc;
	}
	public URL getMdUrl() 
	{
		return this.mdUrl;
	}
	public void setMdUrl(URL mdUrl) 
	{
		this.mdUrl = mdUrl;
	}
	public String getMdFormat() 
	{
		return this.mdFormat;
	}
	public void setMdFormat(String mdFormat) 
	{
		this.mdFormat = mdFormat;
	}
	public String getMdLabel() 
	{
		return this.mdLabel;
	}
	public void setMdLabel(String mdLabel) 
	{
		this.mdLabel = mdLabel;
	}
	public boolean isMdDefault() 
	{
		return this.mdDefault;
	}
	public void setMdDefault(boolean mdDefault) 
	{
		this.mdDefault = mdDefault;
	}	
}
