package de.mpg.mpdl.inge.pubman.web.search.bean;

import java.util.ArrayList;
import java.util.List;

import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;
import de.mpg.mpdl.inge.pubman.web.search.bean.criterion.GenreCriterion;
import de.mpg.mpdl.inge.pubman.web.util.DataModelManager;

/**
 * Bean to handle the GenreCriterionCollection on a single jsp. A GenreCriterionCollection is
 * represented by a List<GenreCriterionVO>.
 * 
 * @author Mario Wagner
 */
public class GenreCriterionCollection {
  private List<GenreCriterion> parentVO;
  private GenreCriterionManager genreCriterionManager;

  /**
   * CTOR to create a new ArrayList<GenreCriterionVO> starting with one empty new GenreCriterionVO
   */
  public GenreCriterionCollection() {
    // ensure the parentVO is never null;
    final List<GenreCriterion> ctorList = new ArrayList<GenreCriterion>();
    ctorList.add(new GenreCriterion());
    this.setParentVO(ctorList);
  }

  /**
   * CTOR to refine or fill a predefined ArrayList<GenreCriterionVO>
   * 
   * @param parentVO
   */
  public GenreCriterionCollection(List<GenreCriterion> parentVO) {
    this.setParentVO(parentVO);
  }

  public List<GenreCriterion> getParentVO() {
    return this.parentVO;
  }

  public void setParentVO(List<GenreCriterion> parentVO) {
    this.parentVO = parentVO;
    // ensure proper initialization of our DataModelManager
    this.genreCriterionManager = new GenreCriterionManager(parentVO);
  }

  /**
   * Specialized DataModelManager to deal with objects of type GenreCriterionBean
   * 
   * @author Mario Wagner
   */
  public class GenreCriterionManager extends DataModelManager<GenreCriterionBean> {
    List<GenreCriterion> parentVO;

    public GenreCriterionManager(List<GenreCriterion> parentVO) {
      this.setParentVO(parentVO);
    }

    @Override
    public GenreCriterionBean createNewObject() {
      final GenreCriterion newVO = new GenreCriterion();
      newVO.setGenre(new ArrayList<MdsPublicationVO.Genre>());
      // create a new wrapper pojo
      final GenreCriterionBean genreCriterionBean = new GenreCriterionBean(newVO);
      // we do not have direct access to the original list
      // so we have to add the new VO on our own
      this.parentVO.add(newVO);
      return genreCriterionBean;
    }

    @Override
    public void removeObjectAtIndex(int i) {
      // due to wrapped data handling
      super.removeObjectAtIndex(i);
      this.parentVO.remove(i);
    }

    public List<GenreCriterionBean> getDataListFromVO() {
      if (this.parentVO == null) {
        return null;
      }

      // we have to wrap all VO's in a nice GenreCriterionBean
      final List<GenreCriterionBean> beanList = new ArrayList<GenreCriterionBean>();
      for (final GenreCriterion genreCriterionVO : this.parentVO) {
        beanList.add(new GenreCriterionBean(genreCriterionVO));
      }
      return beanList;
    }

    public void setParentVO(List<GenreCriterion> parentVO) {
      this.parentVO = parentVO;
      // we have to wrap all VO's into a nice GenreCriterionBean
      final List<GenreCriterionBean> beanList = new ArrayList<GenreCriterionBean>();
      for (final GenreCriterion genreCriterionVO : parentVO) {
        beanList.add(new GenreCriterionBean(genreCriterionVO));
      }
      this.setObjectList(beanList);
    }

    public int getSize() {
      return this.getObjectDM().getRowCount();
    }
  }


  public GenreCriterionManager getGenreCriterionManager() {
    return this.genreCriterionManager;
  }

  public void setGenreCriterionManager(GenreCriterionManager genreCriterionManager) {
    this.genreCriterionManager = genreCriterionManager;
  }

  public void clearAllForms() {
    for (final GenreCriterionBean gcb : this.genreCriterionManager.getObjectList()) {
      gcb.clearCriterion();
    }
  }

  public List<GenreCriterion> getFilledCriterion() {
    final List<GenreCriterion> returnList = new ArrayList<GenreCriterion>();
    for (final GenreCriterion vo : this.parentVO) {
      if (vo.getGenre().size() > 0
          || (vo.getSearchString() != null && vo.getSearchString().length() > 0)) {
        returnList.add(vo);
      }
    }
    return returnList;
  }

}
