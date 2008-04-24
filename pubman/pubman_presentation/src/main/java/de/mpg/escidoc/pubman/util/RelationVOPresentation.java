package de.mpg.escidoc.pubman.util;

import de.mpg.escidoc.services.common.valueobjects.PubItemVO;
import de.mpg.escidoc.services.common.valueobjects.RelationVO;

public class RelationVOPresentation extends RelationVO
{

	private PubItemVO sourceItem;
	
	
	
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
