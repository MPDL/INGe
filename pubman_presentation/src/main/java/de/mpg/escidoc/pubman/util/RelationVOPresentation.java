package de.mpg.escidoc.pubman.util;

import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.common.valueobjects.RelationVO;

public class RelationVOPresentation extends RelationVO
{

	private PubItemVO sourceItem;
	
	private PubItemVO targetItem;
	
	
	
	public PubItemVO getTargetItem()
    {
        return targetItem;
    }

    public void setTargetItem(PubItemVO targetItem)
    {
        this.targetItem = targetItem;
    }

    public RelationVOPresentation(RelationVO relation)
	{
		super(relation);
	}

	public PubItemVO getSourceItem() {
		return sourceItem;
	}

	public void setSourceItem(PubItemVO item) {
		this.sourceItem = item;
	}
	
}
