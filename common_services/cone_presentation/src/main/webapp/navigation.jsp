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

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="de.mpg.escidoc.services.cone.ModelList.Model" %>
<%@ page import="de.mpg.escidoc.services.cone.ModelList" %>
<%@ page import="de.mpg.escidoc.services.framework.PropertyReader" %>
<%@ page import="java.net.URLEncoder" %>
<%@page import="org.apache.commons.codec.binary.Base64"%>
<%@ page import="de.mpg.escidoc.services.framework.ServiceLocator" %>
<%@ page import="de.escidoc.www.services.aa.UserAccountHandler" %>
<%@ page import="de.mpg.escidoc.services.common.valueobjects.AccountUserVO" %>
<%@ page import="de.mpg.escidoc.services.common.XmlTransforming" %>
<%@ page import="de.mpg.escidoc.services.common.xmltransforming.XmlTransformingBean" %>
<%@ page import="java.util.List" %>
<%@ page import="de.mpg.escidoc.services.common.valueobjects.GrantVO" %>
<%@ page import="de.mpg.escidoc.services.cone.web.Login"%>

<%

	boolean showWarning = Login.checkLogin(request, true);
	
%>


<div class="full_area0 header clear">
<!-- start: header section -->
	<span id="metaMenuSkipLinkAnchor" class="full_area0 metaMenu">
		<!-- logo alternate area starts here -->
		<div class="free_area0 small_marginLExcl logoAlternate">
			<a href="">
				<span>eSciDoc.</span>
				<span>CoNE</span>
			</a>
		</div>
		<!-- logo alternate area starts here -->
		<!-- meta Menu starts here -->
			<!-- CoLab -->
			<a class="medium_area0_p8 endline" href="http://colab.mpdl.mpg.de/mediawiki/Service_for_Control_of_Named_Entities">About</a>
			<span class="seperator"></span>

			<!-- Login -->
		
				<% if (Login.getLoggedIn(request)) { %>
					<a class="medium_area0_p8 endline" href="logout.jsp?target=<%= URLEncoder.encode(PropertyReader.getProperty("escidoc.cone.service.url") + request.getServletPath().substring(1), "UTF-8") %>">Logout</a>
				<% } else { %>
					<a class="medium_area0_p8 endline" href="/auth/login.jsp">Login</a>
				<% } %>
				<span class="seperator"></span>
		
			<!-- Log out -->

		<!-- meta Menu ends here -->
	</span>
	<% if (showWarning) { %>
		<div>
			No sufficient privileges!
		</div>
			
	<% } %>
	
	<div id="mainMenuSkipLinkAnchor" class="full_area0 mainMenu">
		<a href="index.jsp" class="free_area0 xTiny_marginRIncl">Home</a>
		
		<% if (request.getSession().getAttribute("latestSearch") != null) { %>
			<a href="<%= request.getSession().getAttribute("latestSearch") %>" class="free_area0 xTiny_marginRIncl">Back to Search</a>
		<% } else { %>
			<a href="search.jsp" class="free_area0 xTiny_marginRIncl">Search</a>
		<% } %>
		
		<% if ((request.getSession() != null && request.getSession().getAttribute("edit_open_vocabulary") != null && ((Boolean)request.getSession().getAttribute("edit_open_vocabulary")).booleanValue())
				|| (request.getSession() != null && request.getSession().getAttribute("edit_closed_vocabulary") != null && ((Boolean)request.getSession().getAttribute("edit_closed_vocabulary")).booleanValue())) { %>
			<a href="select.jsp" class="free_area0 xTiny_marginRIncl">Enter New Entity</a>
			<a href="import.jsp" class="free_area0 xTiny_marginRIncl">Import</a>
		<% } %>
		
	</div>
<!-- end: header section -->
</div>