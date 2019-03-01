<%@page import="java.util.Enumeration"%>
<%@page import="de.mpg.mpdl.inge.util.PropertyReader"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.net.URLEncoder" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<%
			response.sendRedirect("login?" + request.getQueryString() + "&target=" + URLEncoder.encode(PropertyReader.getProperty(PropertyReader.INGE_AA_INSTANCE_URL), "ISO-8859-1") + "clientLogin");
    %>
</html>
