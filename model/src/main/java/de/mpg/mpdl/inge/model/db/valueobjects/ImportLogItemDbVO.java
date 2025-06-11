package de.mpg.mpdl.inge.model.db.valueobjects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.Date;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table( //
    name = "import_log_item", //
    indexes = { //
        @Index(name = "import_log_item_idx_parent", columnList = "parent"), //
        @Index(name = "import_log_item_idx_itemid", columnList = "item_id")})
public class ImportLogItemDbVO extends ImportLog {
  @Column(name = "enddate", columnDefinition = "TIMESTAMP")
  private Date endDate;

  @ManyToOne(fetch = FetchType.EAGER, targetEntity = ImportLogDbVO.class)
  @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "importProcess")
  @JoinColumn(name = "parent")
  @OnDelete(action = OnDeleteAction.CASCADE)
  private ImportLogDbVO parent;

  @Column(name = "message")
  private String message;
  @Column(name = "item_id")
  private String itemId;

  @Transient
  private Long anzDetails;

  public ImportLogItemDbVO() {}

  public ImportLogItemDbVO(ImportLogDbVO importLogDbVO, ImportLog.ErrorLevel errorLevel, String message) {
    this.setErrorLevel(errorLevel);
    this.message = message;
    this.parent = importLogDbVO;
  }

  public Long getAnzDetails() {
    return this.anzDetails;
  }

  public void setAnzDetails(Long anzDetails) {
    this.anzDetails = anzDetails;
  }

  public Date getEndDate() {
    return this.endDate;
  }

  public void setEndDate(Date enddate) {
    this.endDate = enddate;
  }

  public String getItemId() {
    return this.itemId;
  }

  public void setItemId(String itemId) {
    this.itemId = itemId;
  }

  public String getMessage() {
    return this.message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public ImportLogDbVO getParent() {
    return this.parent;
  }

  public void setParent(ImportLogDbVO importLogDbVO) {
    this.parent = importLogDbVO;
  }
}
