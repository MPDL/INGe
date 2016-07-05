<%@page import="de.mpg.mpdl.inge.aa.Aa"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="de.mpg.mpdl.inge.util.PropertyReader"%>
<%@ page import="java.net.URLEncoder" %>
<%
	request.getSession().removeAttribute("user");
	request.getSession().setAttribute("logged_in",Boolean.FALSE);
	request.getSession().removeAttribute("edit_open_vocabulary");
	request.getSession().removeAttribute("edit_closed_vocabulary");
	request.getSession().removeAttribute("user_handle_exist");

	response.sendRedirect(PropertyReader.getProperty("escidoc.aa.instance.url") + "logout?target=" + URLEncoder.encode(PropertyReader.getProperty("escidoc.cone.service.url")));
%>