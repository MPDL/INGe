<%--

 CDDL HEADER START

 The contents of this file are subject to the terms of the
 Common Development and Distribution License, Version 1.0 only
 (the "License"). You may not use this file except in compliance
 with the License.

 You can obtain a copy of the license at license/ESCIDOC.LICENSE
 or http://www.escidoc.org/license.
 See the License for the specific language governing permissions
 and limitations under the License.

 When distributing Covered Code, include this CDDL HEADER in each
 file and include the License file at license/ESCIDOC.LICENSE.
 If applicable, add the following below this CDDL HEADER, with the
 fields enclosed by brackets "[]" replaced with your own identifying
 information: Portions Copyright [yyyy] [name of copyright owner]

 CDDL HEADER END


 Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft
 für wissenschaftlich-technische Information mbH and Max-Planck-
 Gesellschaft zur Förderung der Wissenschaft e.V.
 All rights reserved. Use is subject to license terms.
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ page import="de.mpg.mpdl.inge.aa.Aa"%>
<%@ page import="de.mpg.mpdl.inge.cone.web.Login"%>
<%@ page import="de.mpg.mpdl.inge.util.PropertyReader" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.nio.charset.StandardCharsets" %>

<%
	boolean showWarning = Login.checkLogin(request, true);
%>

<style type="text/css">
	.headerLogo {background-image: none; top: 0.5em;}
</style>

<div class="full_area0 header clear">
<!-- start: header section -->
	<span id="metaMenuSkipLinkAnchor" class="full_area0 metaMenu">

		<!-- meta Menu starts here -->
			<!-- CoLab -->
			<a class="medium_area0_p8 endline" href="http://colab.mpdl.mpg.de/mediawiki/Service_for_Control_of_Named_Entities">About</a>
			<span class="seperator"></span>

			<!-- Login -->

				<% if (Login.getLoggedIn(request)) { %>
					<a class="medium_area0_p8 endline" href="logout.jsp?target=<%= URLEncoder.encode(PropertyReader.getProperty(PropertyReader.INGE_CONE_SERVICE_URL) + request.getServletPath().substring(1), StandardCharsets.UTF_8) %>">Logout</a>
				<% } else { %>
					<a class="medium_area0_p8 endline" href="<%= Aa.getLoginLink(request) %>">Login</a>
				<% } %>
				<span class="seperator"></span>

			<!-- Log out -->

		<!-- meta Menu ends here -->
	</span>

	<div class="full_area0 LogoNSearch">
		<span class="tiny_marginLExcl headerLogo">
			<img alt="CoNE" src="/cone/img/cone_logo_web.png">
		</span>
	</div>

	<div id="mainMenuSkipLinkAnchor" class="full_area0 mainMenu">
		<a href="index.jsp" class="free_area0 xTiny_marginRIncl<% if ("/index.jsp".equals(request.getServletPath())) { %> active<% } %>">Home</a>

		<% if (null != request.getSession().getAttribute("latestSearch")) { %>
			<a href="<%= request.getSession().getAttribute("latestSearch") %>" class="free_area0 xTiny_marginRIncl<% if ("/search.jsp".equals(request.getServletPath())) { %> active<% } %>">Back to Search</a>
		<% } else { %>
			<a href="search.jsp" class="free_area0 xTiny_marginRIncl<% if ("/search.jsp".equals(request.getServletPath())) { %> active<% } %>">Search</a>
		<% } %>

		<% if ((null != request.getSession() && null != request.getSession().getAttribute("edit_open_vocabulary") && (Boolean) request.getSession()
                .getAttribute("edit_open_vocabulary"))
				|| (null != request.getSession() && null != request.getSession()
                .getAttribute("edit_closed_vocabulary") && (Boolean) request.getSession()
                .getAttribute("edit_closed_vocabulary"))) { %>
			<a href="select.jsp" class="free_area0 xTiny_marginRIncl<% if ("/select.jsp".equals(request.getServletPath())) { %> active<% } %>">Enter New Entity</a>
			<a href="import.jsp" class="free_area0 xTiny_marginRIncl<% if ("/import.jsp".equals(request.getServletPath())) { %> active<% } %>">Import</a>
		<% } %>

	</div>
<!-- end: header section -->
</div>

<div class="small_marginLIncl subHeaderSection">
	<div class="subHeader">
	<% if (showWarning) { %>
		<span class="messageWarn">No sufficient privileges!</span>
	<% } %>
	</div>
</div>
