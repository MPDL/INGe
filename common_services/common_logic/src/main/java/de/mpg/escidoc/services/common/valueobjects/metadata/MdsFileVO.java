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
    private List<IdentifierVO> identifiers = new ArrayList<IdentifierVO>();

    private List<FormatVO> formats = new ArrayList<FormatVO>();

    private int size;
    
    private String rights;
    
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
        super(other);

        this.identifiers = other.identifiers;
        this.description = other.description;
        this.contentCategory = other.contentCategory;
        this.formats = other.formats;
        this.rights = other.rights;
    }
    
    public int getSize()
    {
        return size;
    }

    public void setSize(int size)
    {
        this.size = size;
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

    public List<IdentifierVO> getIdentifiers()
    {
        return identifiers;
    }

    public List<FormatVO> getFormats()
    {
        return formats;
    }

    public String getRights()
    {
        return rights;
    }

    public void setRights(String rights)
    {
        this.rights = rights;
    }

}
