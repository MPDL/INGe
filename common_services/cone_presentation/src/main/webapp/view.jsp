<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="de.mpg.escidoc.services.cone.Querier" %>
<%@ page import="de.mpg.escidoc.services.cone.QuerierFactory" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="de.mpg.escidoc.services.cone.ModelList.Model" %>
<%@ page import="de.mpg.escidoc.services.cone.ModelList" %>
<%@ page import="de.mpg.escidoc.services.cone.ModelList.Predicate" %>
<%@ page import="de.mpg.escidoc.services.cone.util.LocalizedString" %>
<%
	String uri = request.getParameter("uri");
	String modelName = request.getParameter("model");
	Map<String, List<LocalizedString>> results = new HashMap<String, List<LocalizedString>>();
	ModelList.Model model = null;
	if (modelName != null && !"".equals(modelName))
	{
	    model = ModelList.getInstance().getModelByAlias(modelName);
	}
	
	if (uri != null && !"".equals(uri) && modelName != null && !"".equals(modelName))
	{
	    Querier querier = QuerierFactory.newQuerier();
	    results = querier.details(modelName, uri, "*");
	}
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">


<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<title>CoNE - View Entry</title>
	</head>
	<body>
		<h2>CoNE - View Entry</h2>
			<a href="index.jsp">Home</a>
			<% if (request.getSession().getAttribute("latestSearch") != null) { %>
				<a href="<%= request.getSession().getAttribute("latestSearch") %>">Back to Search</a>
			<% } else { %>
				<a href="search.jsp">Search</a>
			<% } %>
			<% if (request.getSession().getAttribute("logged_in") != null && ((Boolean)request.getSession().getAttribute("logged_in")).booleanValue()) { %>
				<a href="edit.jsp?model=<%= modelName %>&amp;uri=<%= uri %>">Edit</a>
			<% } %>
			<h3><%= modelName %>:
				<% if (uri != null) { %>
					<%= uri %>
				<% } %>
			</h3>

			<ul>
				<% if (model != null) { %>
					<% for (Predicate predicate : model.getPredicates()) { %>
						<% if (results.get(predicate.getId()) != null && results.get(predicate.getId()).size() > 0) { %>
							<li><%= predicate.getName() %>
								<ul>
									<% for (LocalizedString object : results.get(predicate.getId())) { %>
										<li><%= object %>
										<% if (object.getLanguage() != null) { %>
											(<%= object.getLanguage() %>)
										<% } %>
										</li>
									<% } %>
								</ul>
							</li>
						<% } %>
					<% } %>
				<% } %>
			</ul>
	</body>
</html>