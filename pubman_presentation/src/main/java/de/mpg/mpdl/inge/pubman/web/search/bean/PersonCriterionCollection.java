package de.mpg.mpdl.inge.pubman.web.search.bean;

import java.util.ArrayList;
import java.util.List;

import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO.CreatorRole;
import de.mpg.mpdl.inge.pubman.web.search.bean.criterion.PersonCriterion;
import de.mpg.mpdl.inge.pubman.web.util.DataModelManager;

/**
 * Bean to handle the PersonCriterionCollection on a single jsp. A PersonCriterionCollection is
 * represented by a List<PersonCriterionVO>.
 * 
 * @author Mario Wagner
 */
public class PersonCriterionCollection {
  private List<PersonCriterion> parentVO;
  private PersonCriterionManager personCriterionManager;

  /**
   * CTOR to create a new ArrayList<PersonCriterionVO> starting with one empty new PersonCriterionVO
   */
  public PersonCriterionCollection() {
    // ensure the parentVO is never null;
    final List<PersonCriterion> ctorList = new ArrayList<PersonCriterion>();
    ctorList.add(new PersonCriterion());
    this.setParentVO(ctorList);
  }

  /**
   * CTOR to refine or fill a predefined ArrayList<PersonCriterionVO>
   * 
   * @param parentVO
   */
  public PersonCriterionCollection(List<PersonCriterion> parentVO) {
    this.setParentVO(parentVO);
  }

  public List<PersonCriterion> getParentVO() {
    return this.parentVO;
  }

  public void setParentVO(List<PersonCriterion> parentVO) {
    this.parentVO = parentVO;
    // ensure proper initialization of our DataModelManager
    this.personCriterionManager = new PersonCriterionManager(parentVO);
  }

  /**
   * Specialized DataModelManager to deal with objects of type PersonCriterionBean
   * 
   * @author Mario Wagner
   */
  public class PersonCriterionManager extends DataModelManager<PersonCriterionBean> {
    List<PersonCriterion> parentVO;

    public PersonCriterionManager(List<PersonCriterion> parentVO) {
      this.setParentVO(parentVO);
    }

    @Override
    public PersonCriterionBean createNewObject() {
      final PersonCriterion newVO = new PersonCriterion();
      newVO.setCreatorRole(new ArrayList<CreatorRole>());
      // create a new wrapper pojo
      final PersonCriterionBean personCriterionBean = new PersonCriterionBean(newVO);
      // we do not have direct access to the original list
      // so we have to add the new VO on our own
      this.parentVO.add(newVO);
      return personCriterionBean;
    }

    @Override
    public void removeObjectAtIndex(int i) {
      // due to wrapped data handling
      super.removeObjectAtIndex(i);
      this.parentVO.remove(i);
    }

    public List<PersonCriterionBean> getDataListFromVO() {
      if (this.parentVO == null) {
        return null;
      }

      // we have to wrap all VO's in a nice PersonCriterionBean
      final List<PersonCriterionBean> beanList = new ArrayList<PersonCriterionBean>();
      for (final PersonCriterion personCriterionVO : this.parentVO) {
        beanList.add(new PersonCriterionBean(personCriterionVO));
      }

      return beanList;
    }

    public void setParentVO(List<PersonCriterion> parentVO) {
      this.parentVO = parentVO;
      // we have to wrap all VO's into a nice PersonCriterionBean
      final List<PersonCriterionBean> beanList = new ArrayList<PersonCriterionBean>();
      for (final PersonCriterion personCriterionVO : parentVO) {
        beanList.add(new PersonCriterionBean(personCriterionVO));
      }
      this.setObjectList(beanList);
    }

    public int getSize() {
      return this.getObjectDM().getRowCount();
    }
  }


  public PersonCriterionManager getPersonCriterionManager() {
    return this.personCriterionManager;
  }

  public void setPersonCriterionManager(PersonCriterionManager personCriterionManager) {
    this.personCriterionManager = personCriterionManager;
  }

  public void clearAllForms() {
    for (final PersonCriterionBean pcb : this.personCriterionManager.getObjectList()) {
      pcb.clearCriterion();
    }
  }

  public List<PersonCriterion> getFilledCriterion() {
    final List<PersonCriterion> returnList = new ArrayList<PersonCriterion>();
    for (final PersonCriterion vo : this.parentVO) {
      if (vo.getCreatorRole().size() > 0
          || (vo.getSearchString() != null && vo.getSearchString().length() > 0)) {
        returnList.add(vo);
      }
    }
    return returnList;
  }

}
