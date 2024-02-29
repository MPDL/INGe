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
@Table(name = "import_log_item_detail")
public class ImportLogItemDetailDbVO {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "import_log_item_detail_id_gen")
  @SequenceGenerator(name = "import_log_item_detail_id_gen", sequenceName = "import_log_id_seq", allocationSize = 1)
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

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JoinColumn(name = "parent", nullable = false)
  private ImportLogItemDbVO parent;

  @Column(name = "message", length = Integer.MAX_VALUE)
  private String message;

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

  public ImportLogItemDbVO getParent() {
    return this.parent;
  }

  public void setParent(ImportLogItemDbVO parent) {
    this.parent = parent;
  }

  public String getMessage() {
    return this.message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

}
