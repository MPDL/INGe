package de.mpg.mpdl.inge.model.db.valueobjects;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "batch_process_user_lock")
public class BatchProcessUserLockDbVO implements Serializable {

  @Id
  @Column(name = "user_account_objectid")
  private String userAccountObjectId;

  @Column(name = "lock_date", columnDefinition = "TIMESTAMP")
  private LocalDateTime lockDate;

  public BatchProcessUserLockDbVO() {}

  public BatchProcessUserLockDbVO(AccountUserDbVO accountUser, LocalDateTime lockDate) {
    this.userAccountObjectId = accountUser.getObjectId();
    this.lockDate = lockDate;
  }

  public String getUserAccountObjectId() {
    return this.userAccountObjectId;
  }

  public LocalDateTime getLockDate() {
    return this.lockDate;
  }

  @Override
  public int hashCode() {
    return Objects.hash(lockDate, userAccountObjectId);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    BatchProcessUserLockDbVO other = (BatchProcessUserLockDbVO) obj;
    return Objects.equals(lockDate, other.lockDate) && Objects.equals(userAccountObjectId, other.userAccountObjectId);
  }
}
