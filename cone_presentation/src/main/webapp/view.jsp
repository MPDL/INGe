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

<%
	request.setCharacterEncoding("UTF-8");
	response.setCharacterEncoding("UTF-8");
%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ page import="de.mpg.mpdl.inge.cone.ModelList" %>
<%@ page import="de.mpg.mpdl.inge.cone.Querier" %>
<%@ page import="de.mpg.mpdl.inge.cone.QuerierFactory" %>
<%@ page import="de.mpg.mpdl.inge.cone.LocalizedTripleObject" %>
<%@ page import="de.mpg.mpdl.inge.cone.TreeFragment" %>
<%@ page import="de.mpg.mpdl.inge.cone.web.Login"%>
<%@ page import="de.mpg.mpdl.inge.cone.web.util.HtmlUtils" %>
<%@ page import="de.mpg.mpdl.inge.cone.web.UrlHelper"%>
<%@ page import="java.io.StringWriter" %>
<%@ page import="java.util.List" %>
<%@ page import="org.apache.logging.log4j.LogManager" %>
<%@ page import="org.apache.logging.log4j.Logger" %>

<%!
	private String printPredicates(List<ModelList.Predicate> predicates, TreeFragment resultNode, boolean loggedIn) throws Exception
	{
		StringWriter writer = new StringWriter();
		for (ModelList.Predicate predicate : predicates) {
			if (null != resultNode.get(predicate.getId())) {
				List<LocalizedTripleObject> nodeList = resultNode.get(predicate.getId());
				for (LocalizedTripleObject node : nodeList) {
					if(!predicate.isRestricted() || loggedIn) {
						writer.append("<span class=\"full_area0 endline itemLine noTopBorder\">");
						writer.append("<b class=\"xLarge_area0_p8 endline labelLine clear\">");
						writer.append(predicate.getName());
						writer.append("<span class=\"noDisplay\">: </span>");
						writer.append("</b>");
						writer.append("<span class=\"xDouble_area0 endline\" style=\"overflow: visible;\">");
						if (null != predicate.getPredicates() && !predicate.getPredicates().isEmpty() && node instanceof TreeFragment) {
							writer.append("<span class=\"xDouble_area0 singleItem endline\">");
							writer.append("<span class=\"xDouble_area0\">");
							writer.append("&#160;");
							writer.append("</span>");
							writer.append("</span>");
							writer.append("<span class=\"free_area0 large_negMarginLExcl\">");
							writer.append(printPredicates(predicate.getPredicates(), (TreeFragment) node, loggedIn));
							writer.append("</span>");
						} else {
							writer.append("<span class=\"xDouble_area0 singleItem endline\">");
							writer.append("<span class=\"xDouble_area0\">");
							writer.append(HtmlUtils.escapeHtml(node.toString()));
							writer.append("</span>");
							writer.append("</span>");
						}
						writer.append("</span>");
						writer.append("</span>");
					}
				}
			}
		}
		return writer.toString();
	}
%>

<%
	Logger logger = LogManager.getLogger("CoNE view.jsp");

	String uri = request.getParameter("uri");
	String modelName = request.getParameter("model");

	if (null == uri || uri.trim().isEmpty() || null == modelName || modelName.trim().isEmpty()) {
		String error = "uri and/or model may not be null";
		logger.error(error);
		throw new RuntimeException(error);
	}

	if (!UrlHelper.isValidParam(uri)) {
		String error = "uri " + uri + " not valid";
		logger.error(error);
		throw new RuntimeException(error);
	}

	if (!UrlHelper.isValidParam(modelName)) {
		String error = "model " + modelName + " not valid";
		logger.error(error);
		throw new RuntimeException(error);
	}

	ModelList.Model model = ModelList.getInstance().getModelByAlias(modelName);
	boolean loggedIn = Login.getLoggedIn(request);

	Querier querier = QuerierFactory.newQuerier(loggedIn);
	TreeFragment results = querier.details(modelName, uri, "*");

	querier.release();
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
	<jsp:include page="header.jsp"/>
	<body>
		<div class="full wrapper">
			<jsp:include page="navigation.jsp"/>
			<div id="content" class="full_area0 clear">
			<!-- begin: content section (including elements that visually belong to the header (breadcrumb, headline, subheader and content menu)) -->
				<div class="clear">
					<div id="headerSection">
						<div id="headLine" class="clear headLine">
							<!-- Headline starts here -->
							<h1>
								View <%= modelName %>
							</h1>
							<!-- Headline ends here -->
						</div>
					</div>
					<div class="small_marginLIncl subHeaderSection">
						<div class="contentMenu">
							<div class="free_area0 sub">
								<% if (Login.getLoggedIn(request)) { %>
									<% if (model.isOpen() &&
										(null != request.getSession().getAttribute("edit_open_vocabulary") && (Boolean) request.getSession()
                                                .getAttribute("edit_open_vocabulary"))) { %>
										<a href="edit.jsp?model=<%= modelName %>&amp;uri=<%= uri %>">
											Edit Entity
										</a>
									<% } %>
									<% if (!model.isOpen() &&
										(null != request.getSession().getAttribute("edit_closed_vocabulary") && (Boolean) request.getSession()
                                                .getAttribute("edit_closed_vocabulary"))) { %>
										<a href="edit.jsp?model=<%= modelName %>&amp;uri=<%= uri %>">
											Edit Entity
										</a>
									<% } %>
								<% } %>
							</div>
						</div>
					</div>
				</div>
				<div class="full_area0">
					<div class="full_area0 fullItem">
						<div class="full_area0 itemBlock noTopBorder">
							<h3 class="xLarge_area0_p8 endline blockHeader">
								Data
							</h3>
							<span class="seperator"></span>
							<div class="free_area0 itemBlockContent endline">
								<% if (null != uri) { %>
									<span class="free_area0 endline itemLine noTopBorder">
										<b class="xLarge_area0_p8 endline labelLine clear">
											URI<span class="noDisplay">: </span>
										</b>
										<span class="xHuge_area0 endline">
											<%= uri %>
										</span>
									</span>
								<% } %>
								<% if (null != model) { %>
									<%= printPredicates(model.getPredicates(), results, loggedIn) %>
								<% } %>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</body>
</html>
