package de.mpg.mpdl.inge.pubman.web.yearbook;

import java.util.Date;

import de.mpg.mpdl.inge.inge_validation.data.ValidationReportVO;
import de.mpg.mpdl.inge.model.referenceobjects.ItemRO;

@SuppressWarnings("serial")
public class YearbookInvalidItemRO extends ItemRO implements Comparable<ItemRO> {

  private ValidationReportVO validationReport;
  private Date lastModificationDate;

  public YearbookInvalidItemRO(String objectId, ValidationReportVO report, Date lmd) {
    super(objectId);
    this.validationReport = report;
    this.lastModificationDate = lmd;

  }

  public void setValidationReport(ValidationReportVO validationReport) {
    this.validationReport = validationReport;
  }

  public ValidationReportVO getValidationReport() {
    return this.validationReport;

  }

  public void setLastModificationDate(Date lastModificationDate) {
    this.lastModificationDate = lastModificationDate;
  }

  public Date getLastModificationDate() {
    return this.lastModificationDate;
  }

  public int compareTo(YearbookInvalidItemRO arg0) {
    return 0;
  }

  @Override
  public int compareTo(ItemRO arg0) {
    return this.getObjectId().compareTo(arg0.getObjectId());
  }



}
