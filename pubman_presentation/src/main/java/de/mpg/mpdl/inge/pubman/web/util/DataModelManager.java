package de.mpg.mpdl.inge.pubman.web.util;

import java.util.ArrayList;
import java.util.List;

import jakarta.faces.model.DataModel;
import jakarta.faces.model.ListDataModel;

public abstract class DataModelManager<T> {
  protected List<T> objectList = null;
  protected DataModel<?> objectDM = null;

  // //////////////////////////////////////////////////////////////////////////
  //
  // Abstract enforcement section
  //
  // //////////////////////////////////////////////////////////////////////////

  /**
   * I do not really know how to create and initialize a new object of type T, but you should be
   * able to do so.
   */
  public abstract T createNewObject();


  /**
   * Tell me where in your value object or class there is a list of data to be managed and give it
   * to me.
   *
   * @return List containing data object of type T
   */
  // public abstract List<T> getDataListFromVO();

  // //////////////////////////////////////////////////////////////////////////
  //
  // Class implementation section
  //
  // //////////////////////////////////////////////////////////////////////////

  public List<T> getObjectList() {
    return this.objectList;
  }

  public void setObjectList(List<T> objectList) {
    this.objectList = objectList;
    if (null == this.objectDM) {
      this.objectDM = new ListDataModel<>();
    }
    this.objectDM.setWrappedData(objectList);
  }

  /**
   * The DataModel is always created by wrapping the data objects contained in the objectList. If
   * this objectList is empty, there will be no rows to be displayed.
   *
   * @return DataModel
   */
  public DataModel<?> getObjectDM() {
    if (null == this.objectList) {
      this.objectList = new ArrayList<>();
    }
    if (null == this.objectDM) {
      this.objectDM = new ListDataModel<>();
      this.objectDM.setWrappedData(this.objectList);
    }
    return this.objectDM;
  }



  /**
   * Simple setter, not really used yet
   *
   * @param objectDM new DataModel
   */
  public void setObjectDM(DataModel<?> objectDM) {
    this.objectDM = objectDM;
  }

  /**
   * Adds a object of type T to the list (and therefore to the UI model)
   */
  public void addObject() {
    T elem = this.createNewObject();
    int i = this.objectDM.getRowIndex();

    if (null != elem) {
      this.objectList.add(i + 1, elem);
    }
  }

  public void addObjectAtIndex(int i) {
    T elem = this.createNewObject();

    if (null != elem) {
      this.objectList.add(i + 1, elem);
    }
  }

  /**
   * Removes object of type T from the list (and therefore from the UI model)
   */
  public void removeObject() {
    int i = this.objectDM.getRowIndex();
    this.removeObjectAtIndex(i);
  }

  public void removeObjectAtIndex(int i) {
    this.objectList.remove(i);
  }
}
