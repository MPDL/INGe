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
@Table(name = "import_log_item_detail")
public class ImportLogItemDetailDbVO extends ImportLog {
  @ManyToOne(fetch = FetchType.EAGER, targetEntity = ImportLogItemDbVO.class)
  @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "importProcess")
  @JoinColumn(name = "parent")
  @OnDelete(action = OnDeleteAction.CASCADE)
  private ImportLogItemDbVO parent;

  @Column(name = "message")
  private String message;

  public ImportLogItemDetailDbVO(ImportLogItemDbVO importLogItemDbVO, ImportLog.ErrorLevel errorLevel, String message) {
    super();
    setStartDate(new Date());
    setErrorLevel(errorLevel);
    this.message = message;
    this.parent = importLogItemDbVO;
  }

  public ImportLogItemDbVO getParent() {
    return this.parent;
  }

  public String getMessage() {
    return this.message;
  }
}
