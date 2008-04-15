package de.mpg.escidoc.services.common.valueobjects;


public class PubItemVO extends ItemVO {
	
	/**
	 * Default constructor.
	 */
	public PubItemVO()
	{
		
	}
	
	/**
	 * Clone constructor.
	 * 
	 * @param itemVO The item to be copied.
	 */
	public PubItemVO(PubItemVO itemVO)
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
