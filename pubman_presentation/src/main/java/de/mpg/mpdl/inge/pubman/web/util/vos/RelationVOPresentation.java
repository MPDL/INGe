package de.mpg.mpdl.inge.pubman.web.util.vos;

import de.mpg.mpdl.inge.model.valueobjects.RelationVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;

@SuppressWarnings("serial")
public class RelationVOPresentation extends RelationVO {
  private ItemVersionVO sourceItem;
  private ItemVersionVO targetItem;

  public ItemVersionVO getTargetItem() {
    return this.targetItem;
  }

  public void setTargetItem(ItemVersionVO targetItem) {
    this.targetItem = targetItem;
  }

  public RelationVOPresentation(RelationVO relation) {
    super(relation);
  }

  public ItemVersionVO getSourceItem() {
    return this.sourceItem;
  }

  public void setSourceItem(ItemVersionVO item) {
    this.sourceItem = item;
  }
}
