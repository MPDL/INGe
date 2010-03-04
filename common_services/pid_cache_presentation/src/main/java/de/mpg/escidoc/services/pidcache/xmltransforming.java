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
		if (id == null && url == null) 
		{
			throw new RuntimeException("Error transformation: This is not a PidXml");
		}
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
		String xml = "<pid>".concat(pid.getIdentifier()).concat("</pid>");
		xml.concat("<url>").concat(pid.getUrl()).concat("</url>");
		return xml;
	}
	
}
