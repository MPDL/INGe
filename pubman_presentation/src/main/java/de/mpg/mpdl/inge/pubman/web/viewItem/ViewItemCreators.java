package de.mpg.mpdl.inge.pubman.web.viewItem;

import de.mpg.mpdl.inge.pubman.web.util.FacesBean;

/**
 *
 * @author yu
 *
 */
@SuppressWarnings("serial")
public class ViewItemCreators extends FacesBean {
  private String creatorType;
  private Object creatorObj;
  private String creatorRole;

  public enum Type
  {
    PERSON, ORGANIZATION
  }

  public ViewItemCreators() {}

  public String getCreatorType() {
    return this.creatorType;
  }

  public void setCreatorType(String creatorType) {
    this.creatorType = creatorType;
  }

  public Object getCreatorObj() {
    return this.creatorObj;
  }

  public void setCreatorObj(Object creatorObj) {
    this.creatorObj = creatorObj;
  }

  public void setCreatorRole(String creatorRole) {
    this.creatorRole = creatorRole;
  }

  public String getCreatorRole() {
    return this.getLabel("ENUM_CREATORROLE_" + this.creatorRole);
  }
}
