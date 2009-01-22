package de.mpg.escidoc.services.dataacquisition.valueobjects;

import java.net.URL;

/**
 * The full text Object class.
 * 
 * @author kleinfe1
 */
public class FullTextVO
{
    private String ftDesc;
    private URL ftUrl;
    private String ftFormat;
    private String ftLabel;
    private boolean ftDefault;
    private String fileType;
    private String contentCategory;
    private String visibility;
    private String name;
    private String encoding;

    public String getName()
    {
        return this.name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getEncoding()
    {
        return this.encoding;
    }

    public void setEncoding(String encoding)
    {
        this.encoding = encoding;
    }

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

    public String getContentCategory()
    {
        return this.contentCategory;
    }

    public void setContentCategory(String contentCategory)
    {
        this.contentCategory = contentCategory;
    }

    public String getVisibility()
    {
        return this.visibility;
    }

    public void setVisibility(String visibility)
    {
        this.visibility = visibility;
    }
}
