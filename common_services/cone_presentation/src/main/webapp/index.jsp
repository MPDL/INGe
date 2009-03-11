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
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%
	request.setCharacterEncoding("UTF-8");
	response.setCharacterEncoding("UTF-8");
%>

<%@page import="java.util.List"%>
<%@page import="de.mpg.escidoc.services.cone.ModelList.Model"%>
<%@page import="de.mpg.escidoc.services.cone.ModelList"%>
<%@page import="java.util.Set"%>
<%@page import="de.mpg.escidoc.services.cone.Querier"%>
<%@page import="de.mpg.escidoc.services.cone.QuerierFactory"%><html xmlns="http://www.w3.org/1999/xhtml">
	<jsp:include page="header.jsp"/>
	<body>
		<jsp:include page="navigation.jsp"/>
		<%
			Set<Model> modelList = ModelList.getInstance().getList();
			Querier querier = QuerierFactory.newQuerier();
		%>
		<p>
			The purpose of this service is to provide methods to deal with controlled lists of named 
			entities to assure data quality and facilitate data access and data entry <a href="http://colab.mpdl.mpg.de/mediawiki/Service_for_Control_of_Named_Entities">[more]</a>.
		</p>
		<table align="center">
			<tr>
				<th align="right">Model \ Format</th>
				<th width="150">HTML</th>
				<th width="150">RDF/XML</th>
				<th width="150">Json</th>
				<th width="150">JQuery</th>
				<th width="150">Options</th>
			</tr>
			<% for (Model model : modelList) {
				List<String> ids = querier.getAllIds(model.getName());
				%>
				<tr>
					<th align="right"><%= model.getName() %>: <%= model.getDescription() %></th>
					<td align="center">
						<a href="/cone/html/<%= model.getName() %>/query?q=a">query</a>
						/
						<a href="/cone/html/<%= model.getName() %>/all">all</a>
						/
						<a href="/cone/html/<%= model.getName() %>/<%= ids.get(0) %>">details</a>
					</td>
					<td align="center">
						<a href="/cone/rdf/<%= model.getName() %>/query?q=a">query</a>
						/
						<a href="/cone/rdf/<%= model.getName() %>/all">all</a>
						/
						<a href="/cone/rdf/<%= model.getName() %>/<%= ids.get(0) %>">details</a>
					</td>
					<td align="center">
						<a href="/cone/json/<%= model.getName() %>/query?q=a">query</a>
						/
						<a href="/cone/json/<%= model.getName() %>/all">all</a>
						/
						<a href="/cone/json/<%= model.getName() %>/<%= ids.get(0) %>">details</a>
					</td>
					<td align="center">
						<a href="/cone/jquery/<%= model.getName() %>/query?q=a">query</a>
						/
						<a href="/cone/jquery/<%= model.getName() %>/all">all</a>
						/
						<a href="/cone/jquery/<%= model.getName() %>/<%= ids.get(0) %>">details</a>
					</td>
					<td align="center">
						<a href="/cone/options/<%= model.getName() %>/query?q=a">query</a>
						/
						<a href="/cone/options/<%= model.getName() %>/all">all</a>
						/
						<a href="/cone/options/<%= model.getName() %>/<%= ids.get(0) %>">details</a>
					</td>
				</tr>
			<% } %>
		</table>
	</body>
</html>
<%
	querier.release();
%>