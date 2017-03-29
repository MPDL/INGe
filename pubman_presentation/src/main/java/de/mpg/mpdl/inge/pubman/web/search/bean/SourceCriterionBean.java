package de.mpg.mpdl.inge.pubman.web.search.bean;

import de.mpg.mpdl.inge.pubman.web.search.bean.criterion.Criterion;
import de.mpg.mpdl.inge.pubman.web.search.bean.criterion.SourceCriterion;

/**
 * POJO bean to deal with one SourceCriterionVO.
 * 
 * @author Mario Wagner
 */
@SuppressWarnings("serial")
public class SourceCriterionBean extends CriterionBean {
  private SourceCriterion sourceCriterionVO;

  // collapsed by default
  protected boolean collapsed = true;

  public SourceCriterionBean() {
    // ensure the parentVO is never null;
    this(new SourceCriterion());
  }

  public SourceCriterionBean(SourceCriterion sourceCriterionVO) {
    this.setSourceCriterionVO(sourceCriterionVO);
  }

  @Override
  public Criterion getCriterionVO() {
    return this.sourceCriterionVO;
  }

  public SourceCriterion getSourceCriterionVO() {
    return this.sourceCriterionVO;
  }

  public void setSourceCriterionVO(SourceCriterion sourceCriterionVO) {
    this.sourceCriterionVO = sourceCriterionVO;
  }

  /**
   * Action navigation call to clear the current part of the form
   * 
   * @return null
   */
  public void clearCriterion() {
    this.sourceCriterionVO.setSearchString("");
  }
}
