<%@page import="de.mpg.escidoc.services.aa.Aa"%>
<%@page import="de.mpg.escidoc.services.aa.AuthenticationVO.Grant"%>
<%@page import="de.mpg.escidoc.services.aa.AuthenticationVO.Role"%>
<%@page import="de.mpg.escidoc.services.aa.AuthenticationVO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<title>eSciDoc AA Test Page</title>
		<style type="text/css">
			.key {
				font-weight: bold;
			}
			
			.value {
				color: blue;
			}
		</style>
	</head>
	<body>
		<a href="login.jsp">Login</a>
		<br>
		<h1>Authentication</h1>
		
		<% Aa aa = new Aa(request);
		AuthenticationVO auth = (AuthenticationVO) session.getAttribute("authentication"); %>
		<% if (auth != null) { %>
			<div><span class="key">Type: </span><span class="value"><%= auth.getType() %></span></div>
			<div><span class="key">Full user name: </span><span class="value"><%= auth.getFullName() %></span></div>
			<div><span class="key">User-ID: </span><span class="value"><%= auth.getUserId() %></span></div>
			<div><span class="key">Login name: </span><span class="value"><%= auth.getUsername() %></span></div>
			<div><span class="key">Used TAN: </span><span class="value"><%= auth.getTan() %></span></div>
			<div><h3>Roles</h3>
				<% for (Role role : auth.getRoles()) { %>
					<div><span class="key">Role: </span><span class="value"><%= role.getKey() %></span></div>
				<% } %>
			</div>
			<div><h3>Grants</h3>
				<% for (Grant grant : auth.getGrants()) { %>
					<div><span class="key">Grant: </span><span class="value"><%= grant.getKey() %> on <%= grant.getValue() %></span></div>
				<% } %>
			</div>
		<% } else { %>
			Not authenticated.
		<% } %>
	</body>
</html>