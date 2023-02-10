package de.mpg.mpdl.inge.model.valueobjects.metadata;

import de.mpg.mpdl.inge.model.valueobjects.ValueObject;

@SuppressWarnings("serial")
public abstract class AbstractAcquisitionDeviceVO extends ValueObject {
  protected String name;
  protected String productionComment;
  protected DeviceType type;

  protected enum DeviceType
  {
  }

  public AbstractAcquisitionDeviceVO() {
    super();
  }

  /**
   * Delivers the name of the image acquisition device, i. e. the manufacturers' name for the
   * device.
   */
  public String getName() {
    return name;
  }

  /**
   * Delivers the production comment of the image acquisition device. The production comment is a
   * short description of the production process.
   */
  public String getProductionComment() {
    return productionComment;
  }

  /**
   * Delivers the type of the image acquisition device, i. e. the type of the device which produced
   * the image.
   */
  public DeviceType getType() {
    return type;
  }

  /**
   * Sets the name of the image acquisition device, i. e. the manufacturers' name for the device.
   * 
   * @param newVal
   */
  public void setName(String newVal) {
    name = newVal;
  }

  /**
   * Sets the production comment of the image acquisition device. The production comment is a short
   * description of the production process.
   * 
   * @param newVal
   */
  public void setProductionComment(String newVal) {
    productionComment = newVal;
  }

  /**
   * Sets the type of the image acquisition device, i. e. the type of the device which produced the
   * image.
   * 
   * @param newVal
   */
  public void setType(DeviceType newVal) {
    type = newVal;
  }

}
