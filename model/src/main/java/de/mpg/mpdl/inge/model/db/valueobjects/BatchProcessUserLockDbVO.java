package de.mpg.mpdl.inge.model.db.valueobjects;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "batch_lock")
public class BatchProcessUserLockDbVO implements Serializable {

  @Id
  @Column(name = "user_account_objectid")
  private String userAccountObjectId;

  @Column(name = "lock_date")
  private Date lockDate;

  public BatchProcessUserLockDbVO() {
  }

  public BatchProcessUserLockDbVO(AccountUserDbVO accountUser, Date lockDate) {
    this.userAccountObjectId = accountUser.getObjectId();
    this.lockDate = lockDate;
  }

  public String getUserAccountObjectId() {
    return this.userAccountObjectId;
  }

  public Date getLockDate() {
    return this.lockDate;
  }
}
