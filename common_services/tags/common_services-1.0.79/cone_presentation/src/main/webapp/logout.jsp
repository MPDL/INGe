
<%@page import="java.net.URLEncoder"%>
<%@page import="de.mpg.escidoc.services.framework.PropertyReader"%>
<%
	request.getSession().removeAttribute("user");
	request.getSession().setAttribute("logged_in",Boolean.FALSE);
	request.getSession().removeAttribute("edit_open_vocabulary");
	request.getSession().removeAttribute("edit_closed_vocabulary");
	request.getSession().removeAttribute("user_handle_exist");
	request.getSession().removeAttribute("authenticention");

	response.sendRedirect("index.jsp");
%>