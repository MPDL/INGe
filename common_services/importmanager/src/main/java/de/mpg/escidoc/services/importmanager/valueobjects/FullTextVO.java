package de.mpg.escidoc.services.importmanager.valueobjects;

import java.net.URL;
/**
 * 
 * @author kleinfe1
 *
 */
public class FullTextVO 
{

    private String ftDesc;
	private URL ftUrl;
	private String ftFormat;
	private String ftLabel;
	private boolean ftDefault;
	private String fileType;
	
	
	public String getFileType() 
	{
		return this.fileType;
	}
	public void setFileType(String fileType) 
	{
		this.fileType = fileType;
	}
	public String getFtDesc() 
	{
		return this.ftDesc;
	}
	public void setFtDesc(String ftDesc) 	
	{
		this.ftDesc = ftDesc;
	}
	public URL getFtUrl() 
	{
		return this.ftUrl;
	}
	public void setFtUrl(URL ftUrl) 
	{
		this.ftUrl = ftUrl;
	}
	public String getFtFormat() 
	{
		return this.ftFormat;
	}
	public void setFtFormat(String ftFormat) 
	{
		this.ftFormat = ftFormat;
	}
	public String getFtLabel() 
	{
		return this.ftLabel;
	}
	public void setFtLabel(String ftLabel) 
	{
		this.ftLabel = ftLabel;
	}
	public boolean isFtDefault() 
	{
		return this.ftDefault;
	}
	public void setFtDefault(boolean ftDefault) 
	{
		this.ftDefault = ftDefault;
	}

	
	
}
