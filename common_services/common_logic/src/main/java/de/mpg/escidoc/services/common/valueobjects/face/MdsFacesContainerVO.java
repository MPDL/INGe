package de.mpg.escidoc.services.common.valueobjects.face;

import de.mpg.escidoc.services.common.valueobjects.MetadataSetVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO;

/**
 * Metadata content of a Faces container (album).
 *
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */

public class MdsFacesContainerVO extends MetadataSetVO
{
    private String name;
    private String description;
    private java.util.List<CreatorVO> creators = new java.util.ArrayList<CreatorVO>();
    
    public String getName()
    {
        return name;
    }
    public void setName(String name)
    {
        this.name = name;
    }
    public String getDescription()
    {
        return description;
    }
    public void setDescription(String description)
    {
        this.description = description;
    }
    public java.util.List<CreatorVO> getCreators()
    {
        return creators;
    }
    
   
}
