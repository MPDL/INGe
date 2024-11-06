package de.mpg.mpdl.inge.model.db.valueobjects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.SequenceGenerator;
import java.util.Date;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class ImportLog {
  // - FINE: everything is alright
  // - WARNING: import worked, but something could have been done better
  // - PROBLEM: some item was not imported because validation failed
  // - ERROR: some items were not imported because there were system errors during the import
  // - FATAL: the import was interrupted completely due to system errors
  public enum ErrorLevel
  {
    ERROR,
    FATAL,
    FINE,
    PROBLEM,
    WARNING
  }

  public enum Status
  {
    FINISHED,
    PENDING,
    SUSPENDED
  }

  public enum SubmitModus
  {
    SUBMIT,
    SUBMIT_AND_RELEASE,
    RELEASE
  }

  public enum Messsage
  {
    import_process_delete_failed,
    import_process_delete_finished,
    import_process_delete_item,
    import_process_delete_items,
    import_process_delete_successful,
    import_process_initialize_delete_process,
    import_process_initialize_release_process,
    import_process_initialize_submit_process,
    import_process_initialize_submit_release_process,
    import_process_relase_item,
    import_process_release_failed,
    import_process_release_finished,
    import_process_release_items,
    import_process_release_successful,
    import_process_remove_identifier,
    import_process_schedule_delete,
    import_process_schedule_release,
    import_process_schedule_submit,
    import_process_schedule_submit_release,
    import_process_submit_failed,
    import_process_submit_finished,
    import_process_submit_item,
    import_process_submit_items,
    import_process_submit_relase_item,
    import_process_submit_release_failed,
    import_process_submit_release_finished,
    import_process_submit_release_items,
    import_process_submit_release_successful,
    import_process_submit_successful
  }

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "import_log_id_gen")
  @SequenceGenerator(name = "import_log_id_gen", sequenceName = "import_log_id_seq", allocationSize = 1)
  @Column(name = "id", nullable = false)
  private Integer id;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private ImportLog.Status status;

  @Enumerated(EnumType.STRING)
  @Column(name = "errorlevel", nullable = false)
  private ImportLog.ErrorLevel errorLevel;

  @Column(name = "startdate", columnDefinition = "TIMESTAMP", nullable = false)
  private Date startDate;

  public ImportLog() {
    this.startDate = new Date();
    this.status = ImportLog.Status.PENDING;
  }

  public Integer getId() {
    return this.id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public ImportLog.Status getStatus() {
    return this.status;
  }

  public void setStatus(ImportLog.Status status) {
    this.status = status;
  }

  public ImportLog.ErrorLevel getErrorLevel() {
    return this.errorLevel;
  }

  public void setErrorLevel(ImportLog.ErrorLevel errorLevel) {
    if (null == this.errorLevel //
        || ErrorLevel.FATAL == errorLevel //
        || (ErrorLevel.ERROR == errorLevel && ErrorLevel.FATAL != this.errorLevel) //
        || (ErrorLevel.PROBLEM == errorLevel && ErrorLevel.FATAL != this.errorLevel && ErrorLevel.ERROR != this.errorLevel) //
        || (ErrorLevel.WARNING == errorLevel && ErrorLevel.FATAL != this.errorLevel && ErrorLevel.ERROR != this.errorLevel
            && ErrorLevel.PROBLEM != this.errorLevel)) {
      this.errorLevel = errorLevel;
    }
  }

  public Date getStartDate() {
    return this.startDate;
  }

  public void setStartDate(Date startdate) {
    this.startDate = startdate;
  }
}
