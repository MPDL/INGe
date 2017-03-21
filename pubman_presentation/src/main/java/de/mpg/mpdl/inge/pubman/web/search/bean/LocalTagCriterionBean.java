package de.mpg.mpdl.inge.pubman.web.search.bean;

import de.mpg.mpdl.inge.pubman.web.search.bean.criterion.Criterion;
import de.mpg.mpdl.inge.pubman.web.search.bean.criterion.LocalTagCriterion;

/**
 * POJO bean to deal with one LocalTagCriterionVO.
 * 
 * @author Thomas Endres
 */
@SuppressWarnings("serial")
public class LocalTagCriterionBean extends CriterionBean {
  private LocalTagCriterion localTagCriterionVO;

  // collapsed by default
  protected boolean collapsed = true;

  public LocalTagCriterionBean() {
    // ensure the parentVO is never null;
    this(new LocalTagCriterion());
  }

  public LocalTagCriterionBean(LocalTagCriterion localTagCriterionVO) {
    setLocalTagCriterionVO(localTagCriterionVO);
  }

  @Override
  public Criterion getCriterionVO() {
    return localTagCriterionVO;
  }

  public LocalTagCriterion getLocalTagCriterionVO() {
    return localTagCriterionVO;
  }

  public void setLocalTagCriterionVO(LocalTagCriterion localTagCriterionVO) {
    this.localTagCriterionVO = localTagCriterionVO;
  }


  /**
   * Action navigation call to clear the current part of the form
   * 
   * @return null
   */
  public String clearCriterion() {
    localTagCriterionVO.setSearchString("");

    // navigation refresh
    return null;
  }

}
