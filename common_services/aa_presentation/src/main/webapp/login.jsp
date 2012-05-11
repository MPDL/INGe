<%@page import="java.util.Enumeration"%>
<%@page import="de.mpg.escidoc.services.aa.AaServerConfiguration"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.net.URLEncoder" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<%
		AaServerConfiguration configuration = new AaServerConfiguration();
		String from = request.getParameter("from");
		if (from == null)
		{
		    from = "";
		}
	
		if (configuration.getMap().size() == 1)
		{
			%>
			<head>
				<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
				<meta http-equiv="refresh" content="0; login?from=<%= from %>&target=<%= URLEncoder.encode(configuration.getMap().values().iterator().next()) %>clientLogin">
				<!-- <title>Insert title here</title> -->
			</head>
		<% } else { %>
			<head>
				<title>Select Login Mechanism</title>
			</head>
			<body style="text-align: center;">
				<h1>Select Login Mechanism</h1>
				<% for (String key : configuration.getMap().keySet()) { %>
					<div><a href="login?from=<%= URLEncoder.encode(from) %>&target=<%= URLEncoder.encode(configuration.getMap().get(key)) %>clientLogin"><%= key %></a></div>
				<% } %>
			</body>
	<% } %>
</html>
