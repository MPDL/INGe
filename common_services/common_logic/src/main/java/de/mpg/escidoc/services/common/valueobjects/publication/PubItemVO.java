package de.mpg.escidoc.services.common.valueobjects.publication;

import org.apache.log4j.Logger;

import de.mpg.escidoc.services.common.valueobjects.ItemVO;
import de.mpg.escidoc.services.framework.PropertyReader;


public class PubItemVO extends ItemVO
{
    
    private static Logger logger = Logger.getLogger(PubItemVO.class);
    
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
}
