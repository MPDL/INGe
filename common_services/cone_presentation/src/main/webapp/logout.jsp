
<%@page import="java.net.URLEncoder"%>
<%@page import="de.mpg.escidoc.services.framework.PropertyReader"%>
<%
	request.getSession().removeAttribute("user");
	request.getSession().removeAttribute("logged_in");
	request.getSession().removeAttribute("edit");

	response.sendRedirect(PropertyReader.getProperty("escidoc.framework_access.framework.url") + "/aa/logout?target=" + URLEncoder.encode(request.getParameter("target"), "UTF-8"));
%>