package de.mpg.mpdl.inge.model.db.valueobjects;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "import_log")
public class ImportLogDbVO {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "import_log_id_gen")
  @SequenceGenerator(name = "import_log_id_gen", sequenceName = "import_log_id_seq", allocationSize = 1)
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

  @Size(max = 255)
  @Column(name = "userid")
  private String userid;

  @Column(name = "name", length = Integer.MAX_VALUE)
  private String name;

  @Size(max = 255)
  @Column(name = "format")
  private String format;

  @Size(max = 255)
  @Column(name = "context")
  private String context;

  @Column(name = "percentage")
  private Integer percentage;

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

  public String getUserid() {
    return this.userid;
  }

  public void setUserid(String userid) {
    this.userid = userid;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getFormat() {
    return this.format;
  }

  public void setFormat(String format) {
    this.format = format;
  }

  public String getContext() {
    return this.context;
  }

  public void setContext(String context) {
    this.context = context;
  }

  public Integer getPercentage() {
    return this.percentage;
  }

  public void setPercentage(Integer percentage) {
    this.percentage = percentage;
  }

}
