
<%@page import="de.mpg.escidoc.services.framework.PropertyReader"%>
<%@page import="de.mpg.escidoc.services.cone.web.Login"%>
<%@page import="de.mpg.escidoc.services.cone.ModelList"%>

<%
	if (Login.getLoggedIn(request))
	{
		PropertyReader.loadProperties();
		ModelList.reload();
	}
%>