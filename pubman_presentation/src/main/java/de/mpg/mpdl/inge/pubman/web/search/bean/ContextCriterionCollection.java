package de.mpg.mpdl.inge.pubman.web.search.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.faces.model.SelectItem;

import de.mpg.mpdl.inge.model.valueobjects.ContextVO;
import de.mpg.mpdl.inge.pubman.PubItemService;
import de.mpg.mpdl.inge.pubman.web.search.bean.criterion.ContextCriterion;
import de.mpg.mpdl.inge.pubman.web.search.bean.criterion.Criterion;
import de.mpg.mpdl.inge.pubman.web.util.DataModelManager;
import de.mpg.mpdl.inge.pubman.web.util.converter.SelectItemComparator;

public class ContextCriterionCollection {
  private List<ContextVO> contexts;
  private ContextCriterionBean contextCriterionBean;
  private List<SelectItem> contextList;

  private ContextCriterionManager contextCriterionManager;

  /**
   * CTOR to create a new ArrayList<CriterionVO> starting with one empty new ContextCriterionVO
   */
  public ContextCriterionCollection() {
    try {
      this.getContextList();
    } catch (final Exception e) {
      e.printStackTrace();
    }
    this.contextCriterionBean = new ContextCriterionBean(this.contexts);
    this.contextCriterionManager = new ContextCriterionManager();

    System.out.println();
  }

  public void getContexts() throws Exception {
    this.contexts = PubItemService.getPubCollectionListForDepositing();
  }

  public List<SelectItem> getContextList() throws Exception {
    this.getContexts();
    this.contextList = new ArrayList<SelectItem>();

    for (final ContextVO c : this.contexts) {
      if (c.getReference() != null) {
        this.contextList.add(new SelectItem(c.getReference().getObjectId(), c.getName()));
      }
    }

    Collections.sort(this.contextList, new SelectItemComparator());
    this.contextList.add(0, new SelectItem("", "--"));
    return this.contextList;
  }

  public void clearAllForms() {
    for (final ContextCriterionBean bean : this.contextCriterionManager.getObjectList()) {
      bean.clearCriterion();
    }
  }

  public List<Criterion> getFilledCriterion() {
    final List<Criterion> returnList = new ArrayList<Criterion>();
    for (final ContextCriterionBean bean : this.contextCriterionManager.getObjectList()) {
      final Criterion vo = bean.getCriterionVO();
      if ((vo != null && vo.getSearchString() != null && vo.getSearchString().length() > 0)) {
        returnList.add(vo);
      }
    }
    return returnList;
  }

  public ContextCriterionBean getContextCriterionBean() {
    return this.contextCriterionBean;
  }

  public void setContextCriterionBean(ContextCriterionBean contextCriterionBean) {
    this.contextCriterionBean = contextCriterionBean;
  }

  public void setContexts(List<ContextVO> contexts) {
    this.contexts = contexts;
  }

  public void setContextList(List<SelectItem> contextList) {
    this.contextList = contextList;
  }

  public ContextCriterionManager getContextCriterionManager() {
    return this.contextCriterionManager;
  }

  public void setContextCriterionManager(ContextCriterionManager contextCriterionManager) {
    this.contextCriterionManager = contextCriterionManager;
  }


  /**
   * Specialized DataModelManager to deal with objects of type ContextCriterionBean
   */
  public class ContextCriterionManager extends DataModelManager<ContextCriterionBean> {
    // List<CriterionVO> parentVO;

    // public AnyFieldCriterionManager(List<CriterionVO> parentVO)
    // {
    // setParentVO(parentVO);
    // }

    public ContextCriterionManager() {
      if (this.getSize() == 0) {
        final List<ContextCriterionBean> beanList = new ArrayList<ContextCriterionBean>();
        beanList.add(this.createNewObject());
        this.setObjectList(beanList);
      }
    }

    @Override
    public ContextCriterionBean createNewObject() {
      final ContextCriterion newVO = new ContextCriterion();
      // create a new wrapper pojo
      final ContextCriterionBean contextCriterionBean = new ContextCriterionBean(newVO);
      // we do not have direct access to the original list
      // so we have to add the new VO on our own
      // parentVO.add(newVO);
      return contextCriterionBean;
    }

    @Override
    public void removeObjectAtIndex(int i) {
      // due to wrapped data handling
      super.removeObjectAtIndex(i);
      // parentVO.remove(i);
    }

    public List<ContextCriterionBean> getDataListFromVO() {
      return this.objectList;
    }

    public int getSize() {
      return this.getObjectDM().getRowCount();
    }
  }
}
