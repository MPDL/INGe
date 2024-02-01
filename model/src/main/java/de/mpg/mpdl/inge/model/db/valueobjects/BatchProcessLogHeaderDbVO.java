package de.mpg.mpdl.inge.model.db.valueobjects;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

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
@JsonInclude(value = Include.NON_EMPTY)
public class BatchProcessLogHeaderDbVO implements Serializable {

  public enum State
  {
    INITIALIZED,
    RUNNING,
    FINISHED
  }

  public enum Method
  {
    ADD_KEYWORDS,
    ADD_LOCALTAGS,
    ADD_SOURCE_IDENTIFIER,
    CHANGE_CONTEXT,
    CHANGE_EXTERNAL_REFERENCE_CONTENT_CATEGORY,
    CHANGE_FILE_CONTENT_CATEGORY,
    CHANGE_FILE_VISIBILITY,
    CHANGE_GENRE,
    CHANGE_KEYWORDS,
    CHANGE_LOCALTAG,
    CHANGE_REVIEW_METHOD,
    CHANGE_SOURCE_GENRE,
    CHANGE_SOURCE_IDENTIFIER,
    DELETE_PUBITEMS,
    RELEASE_PUBITEMS,
    REPLACE_FILE_AUDIENCE,
    REPLACE_KEYWORDS,
    REPLACE_ORCID,
    REPLACE_SOURCE_EDITION,
    REVISE_PUBITEMS,
    SUBMIT_PUBITEMS,
    WITHDRAW_PUBITEMS
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
  private Integer numberOfItems;

  @Column(name = "method")
  @Enumerated(EnumType.STRING)
  private BatchProcessLogHeaderDbVO.Method method;

  @Column(name = "start_date", columnDefinition = "TIMESTAMP")
  private Date startDate;

  @Column(name = "end_date", columnDefinition = "TIMESTAMP")
  private Date endDate;

  public BatchProcessLogHeaderDbVO() {}

  public BatchProcessLogHeaderDbVO(BatchProcessLogHeaderDbVO.Method method, AccountUserDbVO accountUser,
      BatchProcessLogHeaderDbVO.State state, Integer numberOfItems, Date startDate) {
    this.method = method;
    this.userAccountObjectId = accountUser.getObjectId();
    this.state = state;
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

  public Integer getNumberOfItems() {
    return this.numberOfItems;
  }

  public Date getEndDate() {
    return this.endDate;
  }

  public void setEndDate(Date endDate) {
    this.endDate = endDate;
  }

  public long getBatchLogHeaderId() {
    return this.batchProcessLogHeaderId;
  }

  public String getUserAccountObjectId() {
    return this.userAccountObjectId;
  }

  public Date getStartDate() {
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
        && Objects.equals(numberOfItems, other.numberOfItems) && Objects.equals(startDate, other.startDate) && state == other.state
        && Objects.equals(userAccountObjectId, other.userAccountObjectId);
  }
}
