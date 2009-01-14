<%
	request.setCharacterEncoding("UTF-8");
	response.setCharacterEncoding("UTF-8");
	
%><?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.Map" %>
<%@ page import="de.mpg.escidoc.services.cone.ModelList" %>
<%@ page import="java.util.List" %>
<%@ page import="de.mpg.escidoc.services.cone.util.Pair" %>
<%@ page import="de.mpg.escidoc.services.cone.QuerierFactory" %>
<%@ page import="de.mpg.escidoc.services.cone.Querier" %>
<%@ page import="de.mpg.escidoc.services.cone.ModelList.Model" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.util.Enumeration" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%
	
	List<Pair> results = null;
	String path = "search.jsp";
	String queryString = "?";
	Enumeration params = request.getParameterNames();
	
	while (params.hasMoreElements())
	{
	    String param = params.nextElement().toString();
	    queryString += param + "=" + URLEncoder.encode(request.getParameter(param), "UTF-8");
	    if (params.hasMoreElements())
	    {
	        queryString += "&";
	    }
	}
	if (!"?".equals(queryString))
	{
	    path += queryString;
	    
	}
	request.getSession().setAttribute("latestSearch", path);
	
	if (request.getParameter("searchterm") != null && !"".equals(request.getParameter("searchterm")))
	{
	    Querier querier = QuerierFactory.newQuerier();
	    if (request.getParameter("lang") != null && !"".equals(request.getParameter("lang")))
	    {
	    	results = querier.query(request.getParameter("model"), request.getParameter("searchterm"), request.getParameter("lang"));
	    }
	    else
	    {
	        results = querier.query(request.getParameter("model"), request.getParameter("searchterm"));
	    }
	}
%>

<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<title>CoNE - Search entries</title>
	</head>
	<body>
		<form name="searchform" action="search.jsp" accept-charset="UTF-8" method="post">
			<h2>CoNE - Search entries</h2>
			<a href="index.jsp">Home</a>
			<br/>
			<center>
				<select size="1" name="model">
					<% for (ModelList.Model model : ModelList.getInstance().getList()) { %>
							<option value="<%= model.getName() %>" <%= (model.getName().equals(request.getParameter("model")) ? "selected" : "") %>
							<% if (model.getName().equals(request.getParameter("model"))) { %>selected<% } %>><%= model.getName() %></option>
					<% } %>
				</select>
				<input type="text" name="searchterm" value="<%= (request.getParameter("searchterm") != null ? request.getParameter("searchterm") : "") %>" size="50"/>
				<select size="1" name="lang">
					<option value="">--</option>
					<option value="de" <% if ("de".equals(request.getParameter("lang"))) { %>selected<% } %>>german</option>
					<option value="en" <% if ("en".equals(request.getParameter("lang"))) { %>selected<% } %>>english</option>
					<option value="fr" <% if ("fr".equals(request.getParameter("lang"))) { %>selected<% } %>>french</option>
				</select>
				<input type="submit" value="Search"/>
			</center>
			<% if (results != null) { %>
				<br/>
				<% if (results.size() == 0) { %>
					No results found
				<% } else { %>
					<% for (Pair pair : results) { %>
						<%= pair.getValue() %> (<%= pair.getKey() %>).
						<a href="view.jsp?model=<%= request.getParameter("model") %>&amp;uri=<%= pair.getKey() %>">View</a>
						<% if (request.getSession().getAttribute("logged_in") != null && ((Boolean)request.getSession().getAttribute("logged_in")).booleanValue()) { %>
							| <a href="edit.jsp?model=<%= request.getParameter("model") %>&amp;uri=<%= pair.getKey() %>">Edit</a>
						<% } %>
						<br/>
					<% } %>
				<% } %>
			<% } %>
		</form>
	</body>
</html>