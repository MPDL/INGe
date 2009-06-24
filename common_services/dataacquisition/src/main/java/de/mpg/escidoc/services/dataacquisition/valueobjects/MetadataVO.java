package de.mpg.escidoc.services.dataacquisition.valueobjects;

import java.net.URL;

/**
 * The class for metadata objects.
 * 
 * @author kleinfe1
 */
public class MetadataVO
{
    private String mdDesc;
    private URL mdUrl;
    private String mdFormat;
    private String mdLabel;
    private boolean mdDefault;
    private String name;
    private String encoding;

    public MetadataVO (String mdDesc, URL mdUrl, String mdFormat, String mdLabel, boolean mdDefault, 
            String name, String encoding )
    {
        this.mdDesc = mdDesc;
        this.mdUrl = mdUrl;
        this.mdFormat = mdFormat;
        this.mdLabel = mdLabel;
        this.mdDefault = mdDefault;
        this.name = name;
        this.encoding = encoding;
    }
    
    public MetadataVO()
    {}
    
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
