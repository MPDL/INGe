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
<%@ page import="de.mpg.escidoc.services.cone.ModelList.Model" %>
<%@ page import="de.mpg.escidoc.services.cone.ModelList" %>
<%@ page import="de.mpg.escidoc.services.framework.PropertyReader" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="org.apache.axis.encoding.Base64" %>
<%@ page import="de.mpg.escidoc.services.framework.ServiceLocator" %>
<%@ page import="de.escidoc.www.services.aa.UserAccountHandler" %>
<%@ page import="de.mpg.escidoc.services.common.valueobjects.AccountUserVO" %>
<%@ page import="de.mpg.escidoc.services.common.XmlTransforming" %>
<%@ page import="de.mpg.escidoc.services.common.xmltransforming.XmlTransformingBean" %>
<%@ page import="java.util.List" %>
<%@ page import="de.mpg.escidoc.services.common.valueobjects.GrantVO" %>

<%
	XmlTransforming xmlTransforming = new XmlTransformingBean();
	
	if (request.getParameter("eSciDocUserHandle") != null)
	{
	    String userHandle = new String(Base64.decode(request.getParameter("eSciDocUserHandle")), "UTF-8");
	    UserAccountHandler userAccountHandler = ServiceLocator.getUserAccountHandler(userHandle);
	    String xmlUser = ServiceLocator.getUserAccountHandler(userHandle).retrieve(userHandle);
	    
	    AccountUserVO accountUser = xmlTransforming.transformToAccountUser(xmlUser);
	    // add the user handle to the transformed account user
	    accountUser.setHandle(userHandle);
	    
	    String userGrantXML = ServiceLocator.getUserAccountHandler(userHandle).retrieveCurrentGrants(accountUser.getReference().getObjectId());
	    List<GrantVO> grants = xmlTransforming.transformToGrantVOList(userGrantXML);
	    
	    for (GrantVO grant : grants)
	    {
	        accountUser.getGrants().add(grant);
	        if ("escidoc:role-administrator".equals(grant.getRole()))
	        {
	    		request.getSession().setAttribute("logged_in", Boolean.TRUE);
	        }
	    }
	}
%>
<h2>CoNE - Control of Named Entities</h2>
<span>
	<span class="nav"><a href="index.jsp">Home</a></span>
	<span class="nav"><% if (request.getSession().getAttribute("latestSearch") != null) { %>
		<a href="<%= request.getSession().getAttribute("latestSearch") %>">Back to Search</a>
	<% } else { %>
		<a href="search.jsp">Search</a>
	<% } %></span>
	<span class="nav"><a href="<%= PropertyReader.getProperty("escidoc.framework_access.framework.url") %>/aa/login?target=<%= URLEncoder.encode(request.getRequestURL().toString(), "UTF-8") %>">Login</a></span>
	<% if (request.getSession() != null && request.getSession().getAttribute("logged_in") != null && ((Boolean)request.getSession().getAttribute("logged_in")).booleanValue()) { %>
		<% for (Model model : ModelList.getInstance().getList()) { %>
			<span class="nav"><a href="edit.jsp?model=<%= model.getName() %>">Enter new <%= model.getName() %></a></span>
		<% } %>
	<% } %>
</span>