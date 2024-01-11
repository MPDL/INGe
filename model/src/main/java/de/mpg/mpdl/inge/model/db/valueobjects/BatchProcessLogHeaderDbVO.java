package de.mpg.mpdl.inge.model.db.valueobjects;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "batch_process_log_header")
public class BatchProcessLogHeaderDbVO implements Serializable {

  public enum State
  {
    INITIALIZED,
    RUNNING,
    FINISHED
  }

  public enum Method
  {
    DELETE_PUBITEMS
  }

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "batch_process_log_header_id")
  private long batchProcessLogHeaderId;

  @Column(name = "user_account_objectid")
  private String userAccountObjectId;

  @Column(name = "state")
  @Enumerated(EnumType.STRING)
  private BatchProcessLogHeaderDbVO.State state;

  @Column(name = "number_of_items")
  private int numberOfItems;

  @Column(name = "method")
  @Enumerated(EnumType.STRING)
  private BatchProcessLogHeaderDbVO.Method method;

  @Column(name = "start_date", columnDefinition = "TIMESTAMP")
  private LocalDateTime startDate;

  @Column(name = "end_date", columnDefinition = "TIMESTAMP")
  private LocalDateTime endDate;

  public BatchProcessLogHeaderDbVO() {}

  public BatchProcessLogHeaderDbVO(AccountUserDbVO accountUser, BatchProcessLogHeaderDbVO.State state,
      BatchProcessLogHeaderDbVO.Method method, int numberOfItems, LocalDateTime startDate) {
    this.userAccountObjectId = accountUser.getObjectId();
    this.state = state;
    this.method = method;
    this.numberOfItems = numberOfItems;
    this.startDate = startDate;
  }

  public BatchProcessLogHeaderDbVO.State getState() {
    return this.state;
  }

  public void setState(BatchProcessLogHeaderDbVO.State state) {
    this.state = state;
  }

  public BatchProcessLogHeaderDbVO.Method getMethod() {
    return this.method;
  }

  public int getNumberOfItems() {
    return this.numberOfItems;
  }

  public LocalDateTime getEndDate() {
    return this.endDate;
  }

  public void setEndDate(LocalDateTime endDate) {
    this.endDate = endDate;
  }

  public long getBatchLogHeaderId() {
    return this.batchProcessLogHeaderId;
  }

  public String getUserAccountObjectId() {
    return this.userAccountObjectId;
  }

  public LocalDateTime getStartDate() {
    return this.startDate;
  }

  @Override
  public int hashCode() {
    return Objects.hash(batchProcessLogHeaderId, endDate, method, numberOfItems, startDate, state, userAccountObjectId);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    BatchProcessLogHeaderDbVO other = (BatchProcessLogHeaderDbVO) obj;
    return batchProcessLogHeaderId == other.batchProcessLogHeaderId && Objects.equals(endDate, other.endDate) && method == other.method
        && numberOfItems == other.numberOfItems && Objects.equals(startDate, other.startDate) && state == other.state
        && Objects.equals(userAccountObjectId, other.userAccountObjectId);
  }
}
