<%@ page import="de.mpg.mpdl.inge.util.PropertyReader"%>
<%@ page import="de.mpg.mpdl.inge.cone.web.Login"%>
<%@ page import="de.mpg.mpdl.inge.cone.ModelList"%>

<%
	if (Login.getLoggedIn(request))
	{
		PropertyReader.loadProperties();
		ModelList.reload();
		out.println("...reloaded!");
	}
	else
	{
	    out.println("Not logged in, sorry!");
	}
%>