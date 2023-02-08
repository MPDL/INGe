package de.mpg.mpdl.inge.model.db.valueobjects;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@SuppressWarnings("serial")
@Entity(name = "audit")
@Table(name = "audit_log")
public class AuditDbVO implements Serializable {

  public enum EventType
  {
    CREATE,
    SUBMIT,
    RELEASE,
    REVISE,
    WITHDRAW,
    UPDATE;
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
  @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "item")
  @JsonSerialize(as=ItemVersionRO.class)
  private ItemVersionVO pubItem;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public Date getModificationDate() {
    return modificationDate;
  }

  public void setModificationDate(Date modificationDate) {
    this.modificationDate = modificationDate;
  }

  public EventType getEvent() {
    return event;
  }

  public void setEvent(EventType event) {
    this.event = event;
  }

  public AccountUserDbRO getModifier() {
    return modifier;
  }

  public void setModifier(AccountUserDbRO modifier) {
    this.modifier = modifier;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public ItemVersionVO getPubItem() {
    return pubItem;
  }

  public void setPubItem(ItemVersionVO pubItem) {
    this.pubItem = pubItem;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((comment == null) ? 0 : comment.hashCode());
    result = prime * result + ((event == null) ? 0 : event.hashCode());
    result = prime * result + id;
    result = prime * result + ((modificationDate == null) ? 0 : modificationDate.hashCode());
    result = prime * result + ((modifier == null) ? 0 : modifier.hashCode());
    result = prime * result + ((pubItem == null) ? 0 : pubItem.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    AuditDbVO other = (AuditDbVO) obj;
    if (comment == null) {
      if (other.comment != null)
        return false;
    } else if (!comment.equals(other.comment))
      return false;
    if (event != other.event)
      return false;
    if (id != other.id)
      return false;
    if (modificationDate == null) {
      if (other.modificationDate != null)
        return false;
    } else if (!modificationDate.equals(other.modificationDate))
      return false;
    if (modifier == null) {
      if (other.modifier != null)
        return false;
    } else if (!modifier.equals(other.modifier))
      return false;
    if (pubItem == null) {
      if (other.pubItem != null)
        return false;
    } else if (!pubItem.equals(other.pubItem))
      return false;
    return true;
  }


}
