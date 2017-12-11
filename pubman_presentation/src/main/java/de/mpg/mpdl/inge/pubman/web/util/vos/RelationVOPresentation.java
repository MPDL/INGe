package de.mpg.mpdl.inge.pubman.web.util.vos;

import de.mpg.mpdl.inge.model.valueobjects.RelationVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;

@SuppressWarnings("serial")
public class RelationVOPresentation extends RelationVO {
  private PubItemVO sourceItem;
  private PubItemVO targetItem;

  public PubItemVO getTargetItem() {
    return this.targetItem;
  }

  public void setTargetItem(PubItemVO targetItem) {
    this.targetItem = targetItem;
  }

  public RelationVOPresentation(RelationVO relation) {
    super(relation);
  }

  public PubItemVO getSourceItem() {
    return this.sourceItem;
  }

  public void setSourceItem(PubItemVO item) {
    this.sourceItem = item;
  }
}
