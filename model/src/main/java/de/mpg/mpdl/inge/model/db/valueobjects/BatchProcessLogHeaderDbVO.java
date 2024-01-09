package de.mpg.mpdl.inge.model.db.valueobjects;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "batch_log_header")
public class BatchProcessLogHeaderDbVO implements Serializable {

  public enum State
  {
    INITIALIZED,
    RUNNING,
    FINISHED
  }
  
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "batch_log_header_id")
  private long batchLogHeaderId;
  
  @OneToOne(fetch = FetchType.EAGER, targetEntity = AccountUserDbVO.class)
  private String userAccountObjectId;

  @Column(name = "state")
  @Enumerated(EnumType.STRING)
  private BatchProcessLogHeaderDbVO.State state;

  @Column(name = "start_date")
  private Date startDate;

  @Column(name = "end_date")
  private Date endDate;

  public BatchProcessLogHeaderDbVO() {
  }

  public BatchProcessLogHeaderDbVO(AccountUserDbVO accountUser, BatchProcessLogHeaderDbVO.State state, Date startDate) {
    this.userAccountObjectId = accountUser.getObjectId();
    this.state = state;
    this.startDate = startDate;
  }

  public BatchProcessLogHeaderDbVO.State getState() {
    return this.state;
  }

  public void setState(BatchProcessLogHeaderDbVO.State state) {
    this.state = state;
  }

  public Date getEndDate() {
    return this.endDate;
  }

  public void setEndDate(Date endDate) {
    this.endDate = endDate;
  }

  public long getBatchLogHeaderId() {
    return this.batchLogHeaderId;
  }

  public String getUserAccountObjectId() {
    return this.userAccountObjectId;
  }

  public Date getStartDate() {
    return this.startDate;
  }
}
