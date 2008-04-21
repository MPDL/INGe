package de.mpg.escidoc.services.common.valueobjects;

import java.util.Date;

public class EventLogEntryVO extends ValueObject {

	public enum EventType
	{
		CREATE, UPDATE, SUBMIT, RELEASE, WITHDRAW
	}
	
	private EventType type;
	
	private Date date;
	
	private String comment;

	public EventType getType() {
		return type;
	}

	public void setType(EventType type) {
		this.type = type;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	
}
