<%--

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


 Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft
 für wissenschaftlich-technische Information mbH and Max-Planck-
 Gesellschaft zur Förderung der Wissenschaft e.V.
 All rights reserved. Use is subject to license terms.
--%>

<%@page import="java.util.List"%>
<%@page import="de.mpg.escidoc.services.cone.ModelList.Model"%>
<%@page import="de.mpg.escidoc.services.cone.ModelList"%>
<%@page import="java.util.Set"%>
<%@page import="de.mpg.escidoc.services.cone.Querier"%>
<%@page import="de.mpg.escidoc.services.cone.QuerierFactory"%>
<%@page import="de.mpg.escidoc.services.cone.web.Login"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
	request.setCharacterEncoding("UTF-8");
	response.setCharacterEncoding("UTF-8");
%>

<html>
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
						boolean loggedIn = Login.getLoggedIn(request);
						boolean editOpen = (request.getSession().getAttribute("edit_open_vocabulary") != null && ((Boolean)request.getSession().getAttribute("edit_open_vocabulary")).booleanValue());
						boolean editClosed = (request.getSession().getAttribute("edit_closed_vocabulary") != null && ((Boolean)request.getSession().getAttribute("edit_closed_vocabulary")).booleanValue());
						
						Querier querier = QuerierFactory.newQuerier(loggedIn);
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
								<% if (loggedIn && ((model.isOpen() && editOpen) || (!model.isOpen() && editClosed))) { %>
									<span class="large_area0_p8 lineToolSection">
										<a class="min_imgBtn add sectionTool" href="edit.jsp?model=<%= modelName %>" class="free_area0 xTiny_marginRIncl" title="add new <%= modelName %>">&#160;</a>
									</span>
								<% } %>
								<span class="free_area0 endline itemLine noTopBorder">
									<b class="xLarge_area0_p8 endline labelLine clear">
										HTML<span class="noDisplay">: </span>
									</b>
									<span class="xHuge_area0 xTiny_marginLIncl endline">
										<a class="free_area0 xTiny_marginRIncl" href="<%= model.getName() %>/query?format=html&q=a">query</a>
										<a class="free_area0 xTiny_marginRIncl" href="<%= model.getName() %>/all?format=html">all</a>
										<a class="free_area0 xTiny_marginRIncl" href="./<%= ids.size() > 0 ? ids.get(0) : "" %>?format=html">details</a>
									</span>
								</span>
								<span class="free_area0 endline itemLine noTopBorder">
									<b class="xLarge_area0_p8 endline labelLine clear">
										RDF/XML<span class="noDisplay">: </span>
									</b>
									<span class="xHuge_area0 xTiny_marginLIncl endline">
										<a class="free_area0 xTiny_marginRIncl" href="<%= model.getName() %>/query?format=rdf&q=a">query</a>
										<a class="free_area0 xTiny_marginRIncl" href="<%= model.getName() %>/all?format=rdf">all</a>
										<a class="free_area0 xTiny_marginRIncl" href="./<%= ids.size() > 0 ? ids.get(0) : "" %>?format=rdf">details</a>
									</span>
								</span>
								<span class="free_area0 endline itemLine noTopBorder">
									<b class="xLarge_area0_p8 endline labelLine clear">
										JSON<span class="noDisplay">: </span>
									</b>
									<span class="xHuge_area0 xTiny_marginLIncl endline">
										<a class="free_area0 xTiny_marginRIncl" href="<%= model.getName() %>/query?format=json&q=a">query</a>
										<a class="free_area0 xTiny_marginRIncl" href="<%= model.getName() %>/all?format=json">all</a>
										<a class="free_area0 xTiny_marginRIncl" href="./<%= ids.size() > 0 ? ids.get(0) : "" %>?format=json">details</a>
									</span>
								</span>
								<span class="free_area0 endline itemLine noTopBorder">
									<b class="xLarge_area0_p8 endline labelLine clear">
										Options<span class="noDisplay">: </span>
									</b>
									<span class="xHuge_area0 xTiny_marginLIncl endline">
										<a class="free_area0 xTiny_marginRIncl" href="<%= model.getName() %>/query?format=options&q=a">query</a>
										<a class="free_area0 xTiny_marginRIncl" href="<%= model.getName() %>/all?format=options">all</a>
										<a class="free_area0 xTiny_marginRIncl" href="./<%= ids.size() > 0 ? ids.get(0) : "" %>?format=options">details</a>
									</span>
								</span>
								<span class="free_area0 endline itemLine noTopBorder">
									<b class="xLarge_area0_p8 endline labelLine clear">
										JQuery<span class="noDisplay">: </span>
									</b>
									<span class="xHuge_area0 xTiny_marginLIncl endline">
										<a class="free_area0 xTiny_marginRIncl" href="<%= model.getName() %>/query?format=jquery&q=a">query</a>
										<a class="free_area0 xTiny_marginRIncl" href="<%= model.getName() %>/all?format=jquery">all</a>
										<a class="free_area0 xTiny_marginRIncl" href="./<%= ids.size() > 0 ? ids.get(0) : "" %>?format=jquery">details</a>
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