<?xml version="1.0" encoding="UTF-8" ?>
<!--

 CDDL HEADER START

 The contents of this file are subject to the terms of the
 Common Development and Distribution License, Version 1.0 only
 (the "License"). You may not use this file except in compliance
 with the License.

 You can obtain a copy of the license at license/ESCIDOC.LICENSE
 or http://www.escidoc.de/license.
 See the License for the specific language governing permissions
 and limitations under the License.

 When distributing Covered Code, include this CDDL HEADER in each
 file and include the License file at license/ESCIDOC.LICENSE.
 If applicable, add the following below this CDDL HEADER, with the
 fields enclosed by brackets "[]" replaced with your own identifying
 information: Portions Copyright [yyyy] [name of copyright owner]

 CDDL HEADER END


 Copyright 2006-2009 Fachinformationszentrum Karlsruhe Gesellschaft
 für wissenschaftlich-technische Information mbH and Max-Planck-
 Gesellschaft zur Förderung der Wissenschaft e.V.
 All rights reserved. Use is subject to license terms.
-->

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
		querier.release();
	}
%>

<html xmlns="http://www.w3.org/1999/xhtml">
	<jsp:include page="header.jsp"/>
	<body>
		<jsp:include page="navigation.jsp"/>
		<form name="searchform" action="search.jsp" accept-charset="UTF-8" method="post">
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
				<input type="submit" value="Search" class="searchMenu"/>
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