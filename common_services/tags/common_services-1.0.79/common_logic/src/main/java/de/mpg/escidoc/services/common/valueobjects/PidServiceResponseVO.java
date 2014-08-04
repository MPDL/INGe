package de.mpg.escidoc.services.common.valueobjects;

/**
 * Response of the PID services (GWDG service and PID cache service).
 * 
 * @author saquet
 *
 */
public class PidServiceResponseVO extends ValueObject
{
	private String action;
	protected String identifier;
	protected String url;
	private String creator;
	private String userUid;
	private String message;
	private String institute;
	private String contact;
	
	/**
	 * Default constructor.
	 */
	public PidServiceResponseVO() 
	{
		super();
	}
	
	public String getAction() 
	{
		return action;
	}
	
	public void setAction(String action) 
	{
		this.action = action;
	}
	
	public String getUrl() 
	{
		return url;
	}

	public void setUrl(String url) 
	{
		this.url = url;
	}
	
	public String getIdentifier() 
	{
		return identifier;
	}
	
	public void setIdentifier(String identifier) 
	{
		this.identifier = identifier;
	}
	
	public String getCreator() 
	{
		return creator;
	}
	
	public void setCreator(String creator) 
	{
		this.creator = creator;
	}
	
	public String getUserUid() 
	{
		return userUid;
	}
	
	public void setUserUid(String userUid) 
	{
		this.userUid = userUid;
	}
	
	public String getMessage() 
	{
		return message;
	}

	public void setMessage(String message) 
	{
		this.message = message;
	}

	public String getInstitute() 
	{
		return institute;
	}

	public void setInstitute(String institute) 
	{
		this.institute = institute;
	}

	public String getContact() 
	{
		return contact;
	}

	public void setContact(String contact) 
	{
		this.contact = contact;
	}

}
