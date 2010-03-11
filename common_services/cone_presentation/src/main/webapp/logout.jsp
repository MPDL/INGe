
<%@page import="java.net.URLEncoder"%>
<%@page import="de.mpg.escidoc.services.framework.PropertyReader"%>
<%
	request.getSession().removeAttribute("user");
	request.getSession().removeAttribute("logged_in");
	request.getSession().removeAttribute("edit_open_vocabulary");
	request.getSession().removeAttribute("edit_closed_vocabulary");
	request.getSession().removeAttribute("user_handle_exist");

	response.sendRedirect(PropertyReader.getProperty("escidoc.framework_access.framework.url") + "/aa/logout?target=" + URLEncoder.encode(request.getParameter("target"), "UTF-8"));
%>