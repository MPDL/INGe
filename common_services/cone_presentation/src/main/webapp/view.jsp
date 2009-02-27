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
<%@ page import="de.mpg.escidoc.services.cone.util.TreeFragment" %>
<%@ page import="de.mpg.escidoc.services.cone.util.LocalizedTripleObject" %>
<%@ page import="java.io.StringWriter" %>
<%!

	String uri;
	TreeFragment results;
	ModelList.Model model;

	private String printPredicates(List<Predicate> predicates, TreeFragment resultNode) throws Exception
	{
	    StringWriter writer = new StringWriter();
	    
	    writer.append("<ul>");
    	for (Predicate predicate : predicates)
    	{
    	    if (resultNode.get(predicate.getId()) != null)
    	    {
    	        List<LocalizedTripleObject> nodeList = resultNode.get(predicate.getId());
    	        
	    	    for (LocalizedTripleObject node : nodeList)
	    	    {
	    	    
	    	        writer.append("<li><b>");
		    	    writer.append(predicate.getName());
		    	    writer.append("</b>: ");
		    	    if (predicate.getPredicates() != null && predicate.getPredicates().size() > 0 && node instanceof TreeFragment)
		    	    {
		    	        writer.append(printPredicates(predicate.getPredicates(), (TreeFragment) node));
		    	    }
		    	    else
		    	    {
		    	        writer.append(node.toString());
		    	    }
		    	    writer.append("</li>");
	    	    }
    	    }
    	}
    	writer.append("</ul>");
    	return writer.toString();
	}

%>
<%
	uri = request.getParameter("uri");
	String modelName = request.getParameter("model");
	results = new TreeFragment();

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

			<% if (model != null) { %>
				<%= printPredicates(model.getPredicates(), results) %>
			<% } %>

	</body>
</html>