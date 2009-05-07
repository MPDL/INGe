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
		<div class="full wrapper">
			<jsp:include page="navigation.jsp"/>
			<div id="content" class="full_area0 clear">
			<!-- begin: content section (including elements that visualy belong to the header (breadcrumb, headline, subheader and content menu)) -->
				<div class="clear">
					<div id="headerSection">
						<div id="headLine" class="clear headLine">
							<!-- Headline starts here -->
							<h1>CoNE - Control of Named Entities</h1>
							<!-- Headline ends here -->
						</div>
					</div>
				</div>
				<div class="full_area0">
					<%
						Set<Model> modelList = ModelList.getInstance().getList();
						Querier querier = QuerierFactory.newQuerier();
					%>
					<div class="full_area0 fullItem">
					<div class="full_area0 itemBlock noTopBorder">
						<h3 class="xLarge_area0_p8 endline blockHeader">
							About
						</h3>
						<span class="seperator"></span>
						<div class="free_area0 itemBlockContent endline">
							<span class="free_area0_p8 endline firstLine noTopBorder">
									The purpose of this service is to provide methods to deal with controlled lists of named 
									entities to assure data quality and facilitate data access and data entry.
							</span>
						</div>
					</div>
					<% for (Model model : modelList) {
							List<String> ids = querier.getAllIds(model.getName());
							%>
						<div class="full_area0 itemBlock">
							<h3 class="xLarge_area0_p8 endline blockHeader">
								<% String modelName = model.getName(); %>
								<%= modelName %>
							</h3>
							<span class="seperator"></span>
							<div class="free_area0 itemBlockContent endline">
								<span class="free_area0 endline itemLine firstLine noTopBorder">
									<b class="xLarge_area0_p8 endline labelLine clear">
										Description<span class="noDisplay">: </span>
									</b>
									<span class="xHuge_area0 xTiny_marginLIncl endline">
										<%= model.getDescription() %>
									</span>
								</span>
								<% if (request.getSession() != null && request.getSession().getAttribute("logged_in") != null && ((Boolean)request.getSession().getAttribute("logged_in")).booleanValue()) { %>
									<span class="large_area0_p8 lineToolSection">
										<a class="min_imgBtn add sectionTool" href="edit.jsp?model=<%= modelName %>" class="free_area0 xTiny_marginRIncl" title="add new <%= modelName %>">&#160;</a>
									</span>
								<% } %>
								<span class="free_area0 endline itemLine noTopBorder">
									<b class="xLarge_area0_p8 endline labelLine clear">
										HTML<span class="noDisplay">: </span>
									</b>
									<span class="xHuge_area0 xTiny_marginLIncl endline">
										<a class="free_area0 xTiny_marginRIncl" href="/cone/html/<%= model.getName() %>/query?q=a">query</a>
										<a class="free_area0 xTiny_marginRIncl" href="/cone/html/<%= model.getName() %>/all">all</a>
										<a class="free_area0 xTiny_marginRIncl" href="/cone/html/<%= model.getName() %>/<%= ids.size() > 0 ? ids.get(0) : "" %>">details</a>
									</span>
								</span>
								<span class="free_area0 endline itemLine noTopBorder">
									<b class="xLarge_area0_p8 endline labelLine clear">
										RDF/XML<span class="noDisplay">: </span>
									</b>
									<span class="xHuge_area0 xTiny_marginLIncl endline">
										<a class="free_area0 xTiny_marginRIncl" href="/cone/rdf/<%= model.getName() %>/query?q=a">query</a>
										<a class="free_area0 xTiny_marginRIncl" href="/cone/rdf/<%= model.getName() %>/all">all</a>
										<a class="free_area0 xTiny_marginRIncl" href="/cone/rdf/<%= model.getName() %>/<%= ids.size() > 0 ? ids.get(0) : "" %>">details</a>
									</span>
								</span>
								<span class="free_area0 endline itemLine noTopBorder">
									<b class="xLarge_area0_p8 endline labelLine clear">
										JSON<span class="noDisplay">: </span>
									</b>
									<span class="xHuge_area0 xTiny_marginLIncl endline">
										<a class="free_area0 xTiny_marginRIncl" href="/cone/json/<%= model.getName() %>/query?q=a">query</a>
										<a class="free_area0 xTiny_marginRIncl" href="/cone/json/<%= model.getName() %>/all">all</a>
										<a class="free_area0 xTiny_marginRIncl" href="/cone/json/<%= model.getName() %>/<%= ids.size() > 0 ? ids.get(0) : "" %>">details</a>
									</span>
								</span>
								<span class="free_area0 endline itemLine noTopBorder">
									<b class="xLarge_area0_p8 endline labelLine clear">
										jQuery<span class="noDisplay">: </span>
									</b>
									<span class="xHuge_area0 xTiny_marginLIncl endline">
										<a class="free_area0 xTiny_marginRIncl" href="/cone/jquery/<%= model.getName() %>/query?q=a">query</a>
										<a class="free_area0 xTiny_marginRIncl" href="/cone/jquery/<%= model.getName() %>/all">all</a>
										<a class="free_area0 xTiny_marginRIncl" href="/cone/jquery/<%= model.getName() %>/<%= ids.size() > 0 ? ids.get(0) : "" %>">details</a>
									</span>
								</span>
								<span class="free_area0 endline itemLine noTopBorder">
									<b class="xLarge_area0_p8 endline labelLine clear">
										Options<span class="noDisplay">: </span>
									</b>
									<span class="xHuge_area0 xTiny_marginLIncl endline">
										<a class="free_area0 xTiny_marginRIncl" href="/cone/options/<%= model.getName() %>/query?q=a">query</a>
										<a class="free_area0 xTiny_marginRIncl" href="/cone/options/<%= model.getName() %>/all">all</a>
										<a class="free_area0 xTiny_marginRIncl" href="/cone/options/<%= model.getName() %>/<%= ids.size() > 0 ? ids.get(0) : "" %>">details</a>
									</span>
								</span>
							</div>
						</div>
					<% } %>
					</div>
				</div>
			</div>
		</div>
	</body>
</html>
<%
	querier.release();
%>