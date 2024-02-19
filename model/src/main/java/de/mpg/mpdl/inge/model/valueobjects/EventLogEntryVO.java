package de.mpg.mpdl.inge.model.valueobjects;

import java.util.Date;

@SuppressWarnings("serial")
public class EventLogEntryVO extends ValueObject {

  public enum EventType
  {
    CREATE,
    UPDATE,
    SUBMIT,
    RELEASE,
    WITHDRAW,
    IN_REVISION,
    ASSIGN_VERSION_PID
  }

  private EventType type;

  private Date date;

  private String comment;

  public EventType getType() {
    return this.type;
  }

  public void setType(EventType type) {
    this.type = type;
  }

  public Date getDate() {
    return this.date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public String getComment() {
    return this.comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

}
