package de.mpg.escidoc.services.common.valueobjects.metadata;

import java.util.ArrayList;
import java.util.List;

import de.mpg.escidoc.services.common.valueobjects.MetadataSetVO;

public class MdsFileVO extends MetadataSetVO
{

    private String contentCategory;
    private String description;
    
    /**
     * Identifier of an external resource.
     */
    private List<String> identifiers = new ArrayList<String>();

    /**
     * The unique name of the affiliation in the organizational structure.
     */
    private String name;
    private List<String> formats = new ArrayList<String>();

    /**
     * Default constructor.
     */
    public MdsFileVO()
    {
        
    }
    
    /**
     * Clone constructor.
     * 
     * @param other The {@link MdsFileVO} to be cloned.
     */
    public MdsFileVO(MdsFileVO other)
    {
        this.name = other.name;
        this.identifiers = other.identifiers;
        this.description = other.description;
        this.contentCategory = other.contentCategory;
        this.formats = other.formats;
    }

    public String getContentCategory()
    {
        return contentCategory;
    }

    public void setContentCategory(String contentCategory)
    {
        this.contentCategory = contentCategory;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public List<String> getIdentifiers()
    {
        return identifiers;
    }

    public List<String> getFormats()
    {
        return formats;
    }

}
