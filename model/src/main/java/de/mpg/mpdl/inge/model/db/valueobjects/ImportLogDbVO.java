package de.mpg.mpdl.inge.model.db.valueobjects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "import_log")
public class ImportLogDbVO extends ImportLog {
  public static final int PERCENTAGE_ZERO = 0;
  public static final int PERCENTAGE_COMPLETED = 100;
  public static final int PERCENTAGE_DELETE_END = 89;
  public static final int PERCENTAGE_DELETE_START = 5;
  public static final int PERCENTAGE_DELETE_SUSPEND = 10;
  public static final int PERCENTAGE_IMPORT_END = 29;
  public static final int PERCENTAGE_IMPORT_PREPARE = 65;
  public static final int PERCENTAGE_IMPORT_START = 5;
  public static final int PERCENTAGE_SUBMIT_END = 89;
  public static final int PERCENTAGE_SUBMIT_START = 5;
  public static final int PERCENTAGE_SUBMIT_SUSPEND = 10;

  public enum Format
  {
    BIBTEX_STRING,
    BMC_XML,
    EDOC_XML, // TODO: remove??? was ist mit alten Datensätzen?
    ENDNOTE_STRING,
    ESCIDOC_ITEM_V3_XML,
    MARC_XML,
    RIS_STRING,
    WOS_STRING
  }

  @Column(name = "enddate", columnDefinition = "TIMESTAMP")
  private Date endDate;

  @Column(name = "userid")
  private String userId;

  @Column(name = "name")
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(name = "format")
  private Format format;

  @Column(name = "context")
  private String contextId;

  @Column(name = "percentage")
  private Integer percentage;

  public ImportLogDbVO() {
    super();
  }

  public ImportLogDbVO(String userId, Format format) {
    super();
    this.userId = userId;
    this.format = format;
  }

  public Date getEndDate() {
    return this.endDate;
  }

  public void setEndDate(Date enddate) {
    this.endDate = enddate;
  }

  public String getUserId() {
    return this.userId;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Format getFormat() {
    return this.format;
  }

  public String getContextId() {
    return this.contextId;
  }

  public void setContextId(String context) {
    this.contextId = context;
  }

  public Integer getPercentage() {
    return this.percentage;
  }

  public void setPercentage(Integer percentage) {
    this.percentage = percentage;
  }
}
