package de.mpg.escidoc.services.common.valueobjects.publication;

import org.apache.log4j.Logger;

import de.mpg.escidoc.services.common.valueobjects.ItemVO;
import de.mpg.escidoc.services.framework.PropertyReader;


public class PubItemVO extends ItemVO
{
    
    private static Logger logger = Logger.getLogger(PubItemVO.class);
    private String descriptionMetaTag;

    
    /**
     * Default constructor.
     */
    public PubItemVO()
    {
        try
        {
            this.setContentModel(PropertyReader.getProperty("escidoc.framework_access.content-model.id.publication"));
        }
        catch (Exception e)
        {
            logger.error("Unable to set publication content model", e);
        }
    }
    
    /**
     * Clone constructor.
     * 
     * @param itemVO The item to be copied.
     */
    public PubItemVO(ItemVO itemVO)
    {
        super(itemVO);
    }
    
    /**
     * {@inheritDoc}
     * 
     * @author Tobias Schraut
     */
    @Override
    public Object clone()
    {
        return new PubItemVO(this);
    }
    
    
    public MdsPublicationVO getMetadata()
    {
        if (getMetadataSets() != null && getMetadataSets().size() > 0 && getMetadataSets().get(0) instanceof MdsPublicationVO)
        {
            return (MdsPublicationVO) getMetadataSets().get(0);
        }
        else
        {
            return null;
        }
    }

    public void setMetadata(MdsPublicationVO mdsPublicationVO)
    {
        if (getMetadataSets().size() > 0 && getMetadataSets().get(0) instanceof MdsPublicationVO)
        {
            getMetadataSets().set(0, mdsPublicationVO);
        }
        else if (getMetadataSets() != null)
        {
            getMetadataSets().add(mdsPublicationVO);
        }
    }
    public String getDescriptionMetaTag()
	{
    	//add first creator to meta tag
    	descriptionMetaTag = getMetadata().getCreators().get(0).getRoleString() + ": " ;
    	if(getMetadata().getCreators().get(0).getPerson() != null)
    		descriptionMetaTag+= getMetadata().getCreators().get(0).getPerson().getGivenName() +" " + getMetadata().getCreators().get(0).getPerson().getFamilyName();
		else
			descriptionMetaTag += getMetadata().getCreators().get(0).getOrganization().getName();
    	//add genre information
    	descriptionMetaTag += ", Gerne: " + getMetadata().getGenre() ;
		//add published print date
    	if(getMetadata().getDatePublishedInPrint()!= null && getMetadata().getDatePublishedInPrint()!="")
    		descriptionMetaTag += ", Published in Print: "+getMetadata().getDatePublishedInPrint();
    	//add published online date
    	if(getMetadata().getDatePublishedOnline()!= null && getMetadata().getDatePublishedOnline()!="")
    		descriptionMetaTag += ", Published online: "+getMetadata().getDatePublishedOnline();
    	//add keywords
    	if(getMetadata().getFreeKeywords().getValue() != null && getMetadata().getFreeKeywords().getValue()!="")
    		descriptionMetaTag += ", keyword: " + getMetadata().getFreeKeywords().getValue() ;
    	System.err.println(descriptionMetaTag);
		return descriptionMetaTag;
	}

	public void setDescriptionMetaTag(String descriptionMetaTag) 
	{
		this.descriptionMetaTag = descriptionMetaTag;
	}

    

}
