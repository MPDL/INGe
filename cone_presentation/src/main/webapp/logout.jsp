<%@ page import="de.mpg.mpdl.inge.util.PropertyReader"%>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.net.URLEncoder"%>

<%
	request.getSession().removeAttribute("user");
	request.getSession().setAttribute("logged_in",Boolean.FALSE);
	request.getSession().removeAttribute("edit_open_vocabulary");
	request.getSession().removeAttribute("edit_closed_vocabulary");
	request.getSession().removeAttribute("user_handle_exist");

	response.sendRedirect(PropertyReader.getProperty(PropertyReader.INGE_AA_INSTANCE_URL) + "logout?target=" + URLEncoder.encode(PropertyReader.getProperty(PropertyReader.INGE_CONE_SERVICE_URL)));
%>
