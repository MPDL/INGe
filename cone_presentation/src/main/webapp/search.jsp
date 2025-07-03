<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<!--

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
-->
<%
	request.setCharacterEncoding("UTF-8");
	response.setCharacterEncoding("UTF-8");
%>


<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ page import="de.mpg.mpdl.inge.cone.ModelList" %>
<%@ page import="de.mpg.mpdl.inge.cone.Querier" %>
<%@ page import="de.mpg.mpdl.inge.cone.QuerierFactory" %>
<%@ page import="de.mpg.mpdl.inge.cone.Describable"%>
<%@ page import="de.mpg.mpdl.inge.cone.Pair" %>
<%@ page import="de.mpg.mpdl.inge.cone.web.Login"%>
<%@ page import="de.mpg.mpdl.inge.cone.web.UrlHelper"%>
<%@ page import="de.mpg.mpdl.inge.cone.web.util.HtmlUtils" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="java.util.List" %>
<%@ page import="java.nio.charset.StandardCharsets" %>

<%
	List<? extends Describable> results = null;
	String path = "search.jsp";
	String queryString = "?";
	Enumeration params = request.getParameterNames();
	while (params.hasMoreElements()) {
		String param = params.nextElement().toString();
		if ("searchterm".equals(param) || "model".equals(param) || "lang".equals(param)) {
			queryString += param + "=" + URLEncoder.encode(UrlHelper.fixURLEncoding(request.getParameter(param)),
                    StandardCharsets.UTF_8);
			if (params.hasMoreElements()) {
				queryString += "&";
			}
		}
	}
	if (!"?".equals(queryString)) {
		path += queryString;
	}
	request.getSession().setAttribute("latestSearch", path);
	boolean loggedIn = Login.getLoggedIn(request);
	String searchterm = UrlHelper.fixURLEncoding(request.getParameter("searchterm"));
	pageContext.setAttribute("searchterm", searchterm);
	if (null != searchterm && !searchterm.isEmpty()) {
		Querier querier = QuerierFactory.newQuerier(loggedIn);
		if (null != request.getParameter("lang") && !"".equals(request.getParameter("lang"))) {
			results = querier.query(request.getParameter("model"), searchterm, request.getParameter("lang"), Querier.ModeType.FAST);
		} else {
			results = querier.query(request.getParameter("model"), searchterm, Querier.ModeType.FAST);
		}
		querier.release();
	}
%>

<html>
	<jsp:include page="header.jsp"/>
	<body>
		<div class="full wrapper">
			<jsp:include page="navigation.jsp"/>
			<div id="content" class="full_area0 clear">
			<!-- begin: content section (including elements that visualy belong to the header (breadcrumb, headline, subheader and content menu)) -->
				<form name="searchform" action="search.jsp" accept-charset="UTF-8" method="post">
					<div class="clear">
						<div id="headerSection">
							<div id="headLine" class="clear headLine">
								<!-- Headline starts here -->
								<h1>CoNE - Search</h1>
								<!-- Headline ends here -->
							</div>
						</div>
					</div>
					<div class="full_area0">
						<div class="full_area0 fullItem">
							<div class="full_area0 itemBlock noTopBorder">
								<h3 class="xLarge_area0_p8 endline blockHeader">
									Search term
								</h3>
								<span class="seperator"></span>
								<div class="free_area0 itemBlockContent endline">
									<div class="free_area0 endline itemLine firstline noTopBorder" style="margin-top:0.81em;">
										<select class="medium_select xSmall_marginLExcl" size="1" name="model">
										<%	for (ModelList.Model model : ModelList.getInstance().getList()) { %>
											<option value="<%= model.getName() %>" <%= (model.getName().equals(request.getParameter("model")) ? "selected" : "") %>
											<% if (model.getName().equals(request.getParameter("model"))) { %>selected<% } %>><%= model.getName() %></option>
										<% } %>
										</select>
          <input type="text" class="half_txtInput" name="searchterm" value="<%= searchterm != null ? HtmlUtils.escapeHtml(searchterm) : "" %>" />
										<select class="small_select" size="1" name="lang">
											<option value="">--</option>
											<option value="de" <% if ("de".equals(request.getParameter("lang"))) { %>selected<% } %>>german</option>
											<option value="en" <% if ("en".equals(request.getParameter("lang"))) { %>selected<% } %>>english</option>
											<option value="fr" <% if ("fr".equals(request.getParameter("lang"))) { %>selected<% } %>>french</option>
										</select>
										<div class="small_area0 formButtonArea">
											<input type="submit" value="Search" class="free_txtBtn activeButton endline" style="padding-left:0.36em; padding-right:0.36em;"/>
										</div>
									</div>
								</div>
							</div>
							<% if (null != results) { %>
							<div class="full_area0 itemBlock">
								<h3 class="xLarge_area0_p8 endline blockHeader">
									Search results
								</h3>
								<span class="seperator"></span>
								<div class="free_area0 itemBlockContent endline">
									<div class="free_area0 endline itemLine noTopBorder">
										<% if (results.isEmpty()) { %>
											<div class="free_area0 endline itemLine noTopBorder">
												<b class="xLarge_area0_p8 endline labelLine clear">
													&#160;<span class="noDisplay">: </span>
												</b>
												<span class="xHuge_area0 xTiny_marginLExcl endline">
													<span class="xHuge_area0">No results found</span>
												</span>
											</div>
										<% } else { %>
											<% int i = 0; %>
											<% for (Describable desc : results) {
												Pair pair = (Pair) desc; %>
												<% if(0 == i) { %>
													<div class="free_area0 endline itemLine noTopBorder">
												<% } else { %>
													<div class="free_area0 endline itemLine">
												<% }; i++; %>
														<b class="xHuge_area0 large_marginLIncl endline clear">
															<a href="view.jsp?model=<%= request.getParameter("model") %>&amp;uri=<%= pair.getKey() %>"><%= HtmlUtils.escapeHtml(pair.getValue().toString()) %></a>
														</b>

													<%	for (ModelList.Model model : ModelList.getInstance().getList()) {
														 if (model.getName().equals(request.getParameter("model"))){
															request.getSession().setAttribute("open_model",
                                                                    model.isOpen());
															//System.out.print("model "+model.getName()+" open: "+Boolean.toString(model.isOpen()));
															break;
														 }
													 } %>
														<span class="large_area0_p8 lineToolSection">
															<% if (loggedIn) { %>
																<%
																if((Boolean)request.getSession().getAttribute("open_model") &&
																		(null != request.getSession()
                                                                                .getAttribute("edit_open_vocabulary") && (Boolean) request.getSession()
                                                                                .getAttribute("edit_open_vocabulary"))) { %>
																	<a class="free_txtBtn groupBtn sectionTool" href="edit.jsp?model=<%= request.getParameter("model") %>&amp;uri=<%= pair.getKey() %>">Edit</a>
																<% } %>

																<%
																if(!(Boolean)request.getSession().getAttribute("open_model") &&
																		(null != request.getSession().getAttribute(
                                                                                "edit_closed_vocabulary") && (Boolean) request.getSession()
                                                                                .getAttribute("edit_closed_vocabulary"))) { %>
																	<a class="free_txtBtn groupBtn sectionTool" href="edit.jsp?model=<%= request.getParameter("model") %>&amp;uri=<%= pair.getKey() %>">Edit</a>
																<% } %>

															<% } %>
														</span>
													</div>
											<% } %>
										<% } %>
									</div>
								</div>
							</div>
							<% } %>
						</div>
						</div>
					</div>
				</form>
			</div>
		</div>
	</body>
</html>
