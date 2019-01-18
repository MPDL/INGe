package de.mpg.mpdl.inge.pubman.web.search.criterions.enums;

import de.mpg.mpdl.inge.model.valueobjects.ItemVO;
import de.mpg.mpdl.inge.service.pubman.impl.PubItemServiceDbImpl;

@SuppressWarnings("serial")
public class StateSearchCriterion extends EnumSearchCriterion<ItemVO.State> {

  public StateSearchCriterion() {
    super(ItemVO.State.class);
  }

  @Override
  public String getSearchString(ItemVO.State selectedEnum) {
    return selectedEnum.name();
  }

  @Override
  public String[] getElasticIndexes() {
    return new String[] {PubItemServiceDbImpl.INDEX_PUBLIC_STATE, PubItemServiceDbImpl.INDEX_VERSION_STATE};
  }

  @Override
  public String getElasticSearchNestedPath() {
    return null;
  }

}
