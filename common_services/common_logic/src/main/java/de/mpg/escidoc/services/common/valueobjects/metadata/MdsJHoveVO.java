package de.mpg.escidoc.services.common.valueobjects.metadata;

import java.util.Date;

import org.dom4j.Element;

import de.mpg.escidoc.services.common.valueobjects.MetadataSetVO;

public class MdsJHoveVO extends MetadataSetVO
{

    private Date date;
    private Element repInfo;
    
    public Date getDate()
    {
        return date;
    }
    public void setDate(Date date)
    {
        this.date = date;
    }
    public Element getRepInfo()
    {
        return repInfo;
    }
    public void setRepInfo(Element repInfo)
    {
        this.repInfo = repInfo;
    }

}
