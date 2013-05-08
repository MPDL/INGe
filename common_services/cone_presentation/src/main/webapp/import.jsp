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


 Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft
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

<%@page import="de.mpg.escidoc.services.cone.ModelList.Model"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
	<jsp:include page="header.jsp"/>
	<body>
		<form name="form" action="upload.jsp" enctype="multipart/form-data" method="post">
			<div class="full wrapper">
				<jsp:include page="navigation.jsp"/>
				<div id="content" class="full_area0 clear">
				<!-- begin: content section (including elements that visualy belong to the header (breadcrumb, headline, subheader and content menu)) -->
					<div class="clear">
						<div id="headerSection">
							<div id="headLine" class="clear headLine">
								<!-- Headline starts here -->
								<h1>Import RDF data</h1>
								<!-- Headline ends here -->
							</div>
						</div>
					</div>
					<div class="full_area0">
						<div class="small_marginLExcl">
							<% if (request.getSession() != null && Login.getLoggedIn(request)) {
								
								boolean editOpen = (request.getSession().getAttribute("edit_open_vocabulary") != null && ((Boolean)request.getSession().getAttribute("edit_open_vocabulary")).booleanValue());
								boolean editClosed = (request.getSession().getAttribute("edit_closed_vocabulary") != null && ((Boolean)request.getSession().getAttribute("edit_closed_vocabulary")).booleanValue());
	
								%>
								Model:
								<br/>
								<select name="model" size="1">
									<% for (ModelList.Model model : ModelList.getInstance().getList()) { %>
										<% if ((model.isOpen() && editOpen) || (!model.isOpen() && editClosed)) { %>
											<option value="<%= model.getName() %>"><%= model.getName() %></option>
										<% } %>
									<% } %>
								</select>
								<br/>
								<br/>
								RDF File:
								<br/>
								<input type="file" name="file"/>
								<br/>
								<br/>
								If an object already exists:
								<br/>
								<input type="radio" name="workflow" value="overwrite"/>
								Replace it with the imported object
								<br/>
								<input type="radio" name="workflow" value="update-overwrite"/>
								Update it with the imported object (overwrite matching predicates)
								<br/>
								<input type="radio" name="workflow" value="update-add"/>
								Update it with the imported object (add matching predicates where possible)
								<br/>
								<input type="radio" name="workflow" value="skip" checked=""/>
								Do not import it
								<br/>
								<br/>
								<input type="submit" name="submit"/>
							<% } %>
						</div>
					</div>
				</div>
			</div>
		</form>
	</body>
</html>
