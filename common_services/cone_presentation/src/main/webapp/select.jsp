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

<%@page import="de.mpg.escidoc.services.cone.web.Login"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
	request.setCharacterEncoding("UTF-8");
	response.setCharacterEncoding("UTF-8");
%>

<%@page import="java.util.List"%>
<%@page import="de.mpg.escidoc.services.cone.ModelList.Model"%>
<%@page import="de.mpg.escidoc.services.cone.ModelList"%>
<%@page import="java.util.Set"%>
<%@page import="de.mpg.escidoc.services.cone.Querier"%>
<%@page import="de.mpg.escidoc.services.cone.QuerierFactory"%>

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
							<h1>Select Entity Type</h1>
							<!-- Headline ends here -->
						</div>
					</div>
				</div>
				<div class="full_area0">
					<%
						Set<Model> modelList = ModelList.getInstance().getList();
						boolean loggedIn = Login.getLoggedIn(request);
					
						Querier querier = QuerierFactory.newQuerier(loggedIn);
					%>

					<div class="full_area0 fullItem">
						
						<% if (loggedIn) {
						
							boolean editOpen = (request.getSession().getAttribute("edit_open_vocabulary") != null && ((Boolean)request.getSession().getAttribute("edit_open_vocabulary")).booleanValue());
							boolean editClosed = (request.getSession().getAttribute("edit_closed_vocabulary") != null && ((Boolean)request.getSession().getAttribute("edit_closed_vocabulary")).booleanValue());
							
							%>
							<% for (Model model : ModelList.getInstance().getList()) { %>
								<% if ((model.isOpen() && editOpen) || (!model.isOpen() && editClosed)) { %>
									<div class="full_area0 itemHeader">
										<span class="xLarge_area0 endline">
											&nbsp;
										</span>
										<span class="seperator"></span>
										<span class="free_area0_p8 endline itemHeadline">
											<b>
												<a href="edit.jsp?model=<%= model.getName() %>" class="free_area0 xTiny_marginRIncl"><%= model.getName() %></a>
											</b>
										</span>
									</div>
								<% } %>
							<% } %>
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
