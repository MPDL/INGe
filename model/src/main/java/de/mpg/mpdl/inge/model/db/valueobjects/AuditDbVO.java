package de.mpg.mpdl.inge.model.db.valueobjects;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.Date;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@SuppressWarnings("serial")
@Entity(name = "audit")
@Table(name = "audit_log", indexes = {@Index(name = "audit_log_idx_pubitemobjectid", columnList = "pubitem_objectid")})
public class AuditDbVO implements Serializable {

  public enum EventType
  {
    CREATE,
    SUBMIT,
    RELEASE,
    REVISE,
    WITHDRAW,
    UPDATE,
    CONTEXT_CHANGE,
    UPDATE_LOCAL_TAGS
  }

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private int id;

  private Date modificationDate;

  @Enumerated(EnumType.STRING)
  private EventType event;

  @Embedded
  @AttributeOverrides({@AttributeOverride(name = "objectId", column = @Column(name = "modifier_objectId")),
      @AttributeOverride(name = "name", column = @Column(name = "modifier_name"))})
  private AccountUserDbRO modifier;

  @Column(columnDefinition = "TEXT")
  private String comment;

  @OnDelete(action = OnDeleteAction.CASCADE)
  @ManyToOne(targetEntity = ItemVersionVO.class)
  @JsonSerialize(as=ItemVersionRO.class)
  private ItemVersionVO pubItem;

  public int getId() {
    return this.id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public Date getModificationDate() {
    return this.modificationDate;
  }

  public void setModificationDate(Date modificationDate) {
    this.modificationDate = modificationDate;
  }

  public EventType getEvent() {
    return this.event;
  }

  public void setEvent(EventType event) {
    this.event = event;
  }

  public AccountUserDbRO getModifier() {
    return this.modifier;
  }

  public void setModifier(AccountUserDbRO modifier) {
    this.modifier = modifier;
  }

  public String getComment() {
    return this.comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public ItemVersionVO getPubItem() {
    return this.pubItem;
  }

  public void setPubItem(ItemVersionVO pubItem) {
    this.pubItem = pubItem;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((null == this.comment) ? 0 : this.comment.hashCode());
    result = prime * result + ((null == this.event) ? 0 : this.event.hashCode());
    result = prime * result + this.id;
    result = prime * result + ((null == this.modificationDate) ? 0 : this.modificationDate.hashCode());
    result = prime * result + ((null == this.modifier) ? 0 : this.modifier.hashCode());
    result = prime * result + ((null == this.pubItem) ? 0 : this.pubItem.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (null == obj)
      return false;
    if (getClass() != obj.getClass())
      return false;
    AuditDbVO other = (AuditDbVO) obj;
    if (null == this.comment) {
      if (null != other.comment)
        return false;
    } else if (!this.comment.equals(other.comment))
      return false;
    if (this.event != other.event)
      return false;
    if (this.id != other.id)
      return false;
    if (null == this.modificationDate) {
      if (null != other.modificationDate)
        return false;
    } else if (!this.modificationDate.equals(other.modificationDate))
      return false;
    if (null == this.modifier) {
      if (null != other.modifier)
        return false;
    } else if (!this.modifier.equals(other.modifier))
      return false;
    if (null == this.pubItem) {
      if (null != other.pubItem)
        return false;
    } else if (!this.pubItem.equals(other.pubItem))
      return false;
    return true;
  }


}
