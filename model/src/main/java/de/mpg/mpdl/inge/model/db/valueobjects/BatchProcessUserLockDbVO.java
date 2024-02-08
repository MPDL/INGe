package de.mpg.mpdl.inge.model.db.valueobjects;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@SuppressWarnings("serial")
@Entity
@Table(name = "batch_process_user_lock")
public class BatchProcessUserLockDbVO implements Serializable {

  @Id
  @Size(max = 255)
  @Column(name = "user_account_objectid", nullable = false)
  private String userAccountObjectId;

  @NotNull
  @Column(name = "lock_date", columnDefinition = "TIMESTAMP", nullable = false)
  private Date lockDate;

  public BatchProcessUserLockDbVO() {}

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
