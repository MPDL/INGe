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
  @ManyToOne(targetEntity = PubItemVersionDbVO.class)
  @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "item")
  private PubItemVersionDbVO pubItem;

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

  public PubItemVersionDbVO getPubItem() {
    return pubItem;
  }

  public void setPubItem(PubItemVersionDbVO pubItem) {
    this.pubItem = pubItem;
  }


}
