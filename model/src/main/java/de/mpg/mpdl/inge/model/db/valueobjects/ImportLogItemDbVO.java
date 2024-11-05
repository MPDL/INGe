package de.mpg.mpdl.inge.model.db.valueobjects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Date;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "import_log_item")
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

  public ImportLogItemDbVO(ImportLogDbVO importLogDbVO, String message) {
    super();
    setStartDate(new Date());
    setErrorLevel(ImportLog.ErrorLevel.FINE);
    this.message = message;
    this.parent = importLogDbVO;
  }

  public Date getEndDate() {
    return this.endDate;
  }

  public void setEndDate(Date enddate) {
    this.endDate = enddate;
  }

  public ImportLogDbVO getParent() {
    return this.parent;
  }

  public String getMessage() {
    return this.message;
  }

  public String getItemId() {
    return this.itemId;
  }

  public void setItemId(String itemId) {
    this.itemId = itemId;
  }
}
