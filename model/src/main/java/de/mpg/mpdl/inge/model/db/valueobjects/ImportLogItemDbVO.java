package de.mpg.mpdl.inge.model.db.valueobjects;

import java.time.Instant;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "import_log_item")
public class ImportLogItemDbVO {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "import_log_item_id_gen")
  @SequenceGenerator(name = "import_log_item_id_gen", sequenceName = "import_log_id_seq", allocationSize = 1)
  @Column(name = "id", nullable = false)
  private Integer id;

  @Size(max = 255)
  @NotNull
  @Column(name = "status", nullable = false)
  private String status;

  @Size(max = 255)
  @NotNull
  @Column(name = "errorlevel", nullable = false)
  private String errorlevel;

  @NotNull
  @Column(name = "startdate", nullable = false)
  private Instant startdate;

  @Column(name = "enddate")
  private Instant enddate;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JoinColumn(name = "parent", nullable = false)
  private ImportLogDbVO parent;

  @Column(name = "message", length = Integer.MAX_VALUE)
  private String message;

  @Size(max = 255)
  @Column(name = "item_id")
  private String itemId;

  public Integer getId() {
    return this.id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getStatus() {
    return this.status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getErrorlevel() {
    return this.errorlevel;
  }

  public void setErrorlevel(String errorlevel) {
    this.errorlevel = errorlevel;
  }

  public Instant getStartdate() {
    return this.startdate;
  }

  public void setStartdate(Instant startdate) {
    this.startdate = startdate;
  }

  public Instant getEnddate() {
    return this.enddate;
  }

  public void setEnddate(Instant enddate) {
    this.enddate = enddate;
  }

  public ImportLogDbVO getParent() {
    return this.parent;
  }

  public void setParent(ImportLogDbVO parent) {
    this.parent = parent;
  }

  public String getMessage() {
    return this.message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getItemId() {
    return this.itemId;
  }

  public void setItemId(String itemId) {
    this.itemId = itemId;
  }

}
