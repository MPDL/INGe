package de.mpg.mpdl.inge.pubman.web.search.bean;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import de.mpg.mpdl.inge.pubman.web.search.bean.criterion.AnyFieldCriterion;
import de.mpg.mpdl.inge.pubman.web.search.bean.criterion.Criterion;
import de.mpg.mpdl.inge.pubman.web.search.bean.criterion.TitleCriterion;
import de.mpg.mpdl.inge.pubman.web.search.bean.criterion.TopicCriterion;
import de.mpg.mpdl.inge.search.query.MetadataSearchCriterion.LogicalOperator;

/**
 * POJO bean to deal with one AnyFieldCriterionVO, TitleCriterionVO or TopicCriterionVO. Type change
 * during runtime is supported using a integrated ValueChangeListener.
 * 
 * @author Mario Wagner
 */
@SuppressWarnings("serial")
public class AnyFieldCriterionBean extends CriterionBean {
  private Criterion criterionVO;

  // private AnyFieldCriterionVO anyFieldCriterionVO;
  // private TitleCriterionVO titleCriterionVO;
  // private TopicCriterionVO topicCriterionVO;

  // private String searchText;
  private String selectedType = "Any";
  private boolean includeFiles;

  public AnyFieldCriterionBean() {
    // ensure the parentVO is never null;
    this(new AnyFieldCriterion());
  }

  public AnyFieldCriterionBean(Criterion criterionVO) {
    this.setCriterionVO(criterionVO);
  }

  @Override
  public Criterion getCriterionVO() {
    return this.criterionVO;

    // if (selectedType != null && selectedType.equals("Title"))
    // {
    // return titleCriterionVO;
    // }
    // else if (selectedType != null && selectedType.equals("Topic"))
    // {
    // return topicCriterionVO;
    // }
    // else
    // {
    // return anyFieldCriterionVO;
    // }
  }

  public void setCriterionVO(Criterion criterionVO) {

    this.criterionVO = criterionVO;

    // if (criterionVO instanceof AnyFieldCriterionVO)
    // {
    // setAnyFieldCriterionVO((AnyFieldCriterionVO)criterionVO);
    // }
    // else if (criterionVO instanceof TitleCriterionVO)
    // {
    // setTitleCriterionVO((TitleCriterionVO)criterionVO);
    // }
    // else if (criterionVO instanceof TopicCriterionVO)
    // {
    // setTopicCriterionVO((TopicCriterionVO)criterionVO);
    // }
    // else
    // throw new IllegalArgumentException("CriterionVO type used is not supported");
  }

  // public AnyFieldCriterionVO getAnyFieldCriterionVO()
  // {
  // return anyFieldCriterionVO;
  // }
  //
  // public void setAnyFieldCriterionVO(AnyFieldCriterionVO anyFieldCriterionVO)
  // {
  // if (anyFieldCriterionVO != null)
  // {
  // this.titleCriterionVO = null;
  // this.topicCriterionVO = null;
  // searchText = anyFieldCriterionVO.getSearchString();
  // includeFiles = anyFieldCriterionVO.isIncludeFiles();
  // selectedType = "Any";
  // }
  // this.anyFieldCriterionVO = anyFieldCriterionVO;
  // this.includeFiles = anyFieldCriterionVO.isIncludeFiles();
  // }
  //
  // public TitleCriterionVO getTitleCriterionVO()
  // {
  // return titleCriterionVO;
  // }
  //
  // public void setTitleCriterionVO(TitleCriterionVO titleCriterionVO)
  // {
  // if (titleCriterionVO != null)
  // {
  // this.anyFieldCriterionVO = null;
  // this.topicCriterionVO = null;
  // searchText = titleCriterionVO.getSearchString();
  // selectedType = "Title";
  // }
  // this.titleCriterionVO = titleCriterionVO;
  // }
  //
  // public TopicCriterionVO getTopicCriterionVO()
  // {
  // return topicCriterionVO;
  // }
  //
  // public void setTopicCriterionVO(TopicCriterionVO topicCriterionVO)
  // {
  // if (topicCriterionVO != null)
  // {
  // this.anyFieldCriterionVO = null;
  // this.titleCriterionVO = null;
  // searchText = topicCriterionVO.getSearchString();
  // selectedType = "Topic";
  // }
  // this.topicCriterionVO = topicCriterionVO;
  // }

  /**
   * Action navigation call to clear the current part of the form
   * 
   * @return null
   */
  public void clearCriterion() {
    this.selectedType = "Any";
    this.setIncludeFiles(false);
    this.criterionVO = new AnyFieldCriterion();
  }

  public SelectItem[] getTypeOptions() {

    final SelectItem TYPE_TITLE = new SelectItem("Title", this.getLabel("adv_search_lblRgbTitle"));
    final SelectItem TYPE_TOPIC = new SelectItem("Topic", this.getLabel("adv_search_lblRgbTopic"));
    final SelectItem TYPE_ANY = new SelectItem("Any", this.getLabel("adv_search_lblRgbAny"));
    final SelectItem TYPE_ANY_FULLTEXT =
        new SelectItem("Any_Fulltext", this.getLabel("adv_search_lblRgbAnyFulltext"));
    final SelectItem[] TYPE_OPTIONS =
        new SelectItem[] {TYPE_TITLE, TYPE_TOPIC, TYPE_ANY, TYPE_ANY_FULLTEXT};

    return TYPE_OPTIONS;
  }

  public String getSelectedType() {
    return this.selectedType;
  }

  public void setSelectedType(String selectedType) {
    this.selectedType = selectedType;
  }

  public boolean isIncludeFiles() {
    return this.includeFiles;
  }

  public void setIncludeFiles(boolean includeFiles) {
    this.includeFiles = includeFiles;
    if (this.criterionVO instanceof AnyFieldCriterion) {
      ((AnyFieldCriterion) this.criterionVO).setIncludeFiles(includeFiles);
    }
  }

  public boolean isIncludeFilesDisabled() {
    return !(this.criterionVO instanceof AnyFieldCriterion);
  }

  /**
   * ValueChangeListener method to handle changes in the selectedType.
   * 
   * @param event
   * @throws AbortProcessingException
   */
  public void processTypeChanged(ValueChangeEvent event) {
    final String newVal = (String) event.getNewValue();

    // get current searchString and operator and move it to the new VO
    final String searchString = this.criterionVO.getSearchString();
    final LogicalOperator logicOperator = this.criterionVO.getLogicalOperator();

    this.selectedType = newVal;

    if (this.selectedType != null && this.selectedType.equals("Title")) {
      final TitleCriterion titleCriterionVO = new TitleCriterion();
      titleCriterionVO.setSearchString(searchString);
      titleCriterionVO.setLogicalOperator(logicOperator);
      // Reinitialize this POJO, because the selectedType has been changed.
      this.criterionVO = titleCriterionVO;
    } else if (this.selectedType != null && this.selectedType.equals("Topic")) {
      final TopicCriterion topicCriterionVO = new TopicCriterion();
      topicCriterionVO.setSearchString(searchString);
      topicCriterionVO.setLogicalOperator(logicOperator);
      // Reinitialize this POJO, because the selectedType has been changed.
      this.criterionVO = topicCriterionVO;
    } else if (this.selectedType != null && this.selectedType.equals("Any_Fulltext")) {
      final AnyFieldCriterion anyFieldCriterionVO = new AnyFieldCriterion();
      anyFieldCriterionVO.setSearchString(searchString);
      anyFieldCriterionVO.setLogicalOperator(logicOperator);
      anyFieldCriterionVO.setIncludeFiles(true);
      // Reinitialize this POJO, because the selectedType has been changed.
      this.criterionVO = anyFieldCriterionVO;
    } else {
      final AnyFieldCriterion anyFieldCriterionVO = new AnyFieldCriterion();
      anyFieldCriterionVO.setSearchString(searchString);
      anyFieldCriterionVO.setLogicalOperator(logicOperator);
      // Reinitialize this POJO, because the selectedType has been changed.
      this.criterionVO = anyFieldCriterionVO;
    }

    // // enforce rendering of the response
    // FacesContext context = FacesTools.getCurrentInstance();
    // context.renderResponse();
  }
}
