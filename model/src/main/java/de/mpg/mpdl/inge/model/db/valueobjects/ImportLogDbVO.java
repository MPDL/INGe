package de.mpg.mpdl.inge.model.db.valueobjects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.Date;

@Entity
@Table(name = "import_log", indexes = {@Index(name = "import_log_idx_userid", columnList = "userid")})
public class ImportLogDbVO extends ImportLog {
  public static final int PERCENTAGE_ZERO = 0;
  public static final int PERCENTAGE_COMPLETED = 100;
  public static final int PERCENTAGE_DELETE_END = 95;
  public static final int PERCENTAGE_DELETE_START = 5;
  public static final int PERCENTAGE_DELETE_SUSPEND = 10;
  public static final int PERCENTAGE_IMPORT_END = 95;
  public static final int PERCENTAGE_IMPORT_START = 5;
  public static final int PERCENTAGE_SUBMIT_END = 95;
  public static final int PERCENTAGE_SUBMIT_START = 5;
  public static final int PERCENTAGE_SUBMIT_SUSPEND = 10;


  public enum Format
  {
    BIBTEX_STRING,
    BMC_XML,
    EDOC_XML, // TODO: remove??? was ist mit alten Datensätzen?
    ENDNOTE_STRING,
    ESCIDOC_ITEM_V3_XML,
    MAB_STRING,
    MARC_XML,
    MARC_21_STRING,
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

  @Transient
  private Long anzImportedItems;
  @Transient
  private Long anzFrom;

  public ImportLogDbVO() {}

  public ImportLogDbVO(String userId, Format format, String importName, String contextId) {
    this.setErrorLevel(ErrorLevel.FINE);
    this.userId = userId;
    this.format = format;
    this.name = importName;
    this.contextId = contextId;
    this.percentage = ImportLogDbVO.PERCENTAGE_ZERO;
  }

  public Long getAnzImportedItems() {
    return this.anzImportedItems;
  }

  public void setAnzImportedItems(Long anzImportedItems) {
    this.anzImportedItems = anzImportedItems;
  }

  public Long getAnzFrom() {
    return this.anzFrom;
  }

  public void setAnzFrom(Long anzFrom) {
    this.anzFrom = anzFrom;
  }

  public String getContextId() {
    return this.contextId;
  }

  public void setContextId(String contextId) {
    this.contextId = contextId;
  }

  public Date getEndDate() {
    return this.endDate;
  }

  public void setEndDate(Date enddate) {
    this.endDate = enddate;
  }

  public Format getFormat() {
    return this.format;
  }

  public void setFormat(Format format) {
    this.format = format;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getPercentage() {
    return this.percentage;
  }

  public void setPercentage(Integer percentage) {
    this.percentage = percentage;
  }

  public String getUserId() {
    return this.userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }
}
