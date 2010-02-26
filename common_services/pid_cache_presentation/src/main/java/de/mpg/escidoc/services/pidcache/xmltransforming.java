package de.mpg.escidoc.services.pidcache;

public class xmltransforming 
{
	public xmltransforming() 
	{
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * DUMMY CLASS FOR TESTING PURPOSE
	 * 
	 * @param pidXml
	 * @return
	 */
	public Pid transFormToPid(String pidXml)
	{
		Pid pid = new Pid();
		
		String id = pidXml.split("<pid>")[1].split("</pid>")[0];
		String url = pidXml.split("<url>")[1].split("</url>")[0];
    	
    	pid.setIdentifier(id);
    	pid.setUrl(url);
    	
    	return pid;
	}

	/**
	 * DUMMY, JUSt FOR TESTING
	 * @param pid
	 * @return
	 */
	public String transformtoPidXml(Pid pid)
	{
		return "this is the xml of your PID with id= " + pid.getIdentifier() + " and url=" + pid.getUrl();
	}
	
}
