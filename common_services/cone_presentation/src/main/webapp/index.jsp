<?xml version="1.0" encoding="UTF-8" ?>
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
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%

	request.setCharacterEncoding("UTF-8");
	response.setCharacterEncoding("UTF-8");

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
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<title>CoNE - Control of Named Entities</title>
	</head>
	<body>
		<h2>CoNE - Control of Named Entities</h2>
		<ul>
			<li><a href="search.jsp">Search</a></li>
			<li><a href="<%= PropertyReader.getProperty("escidoc.framework_access.framework.url") %>/aa/login?target=<%= URLEncoder.encode(request.getRequestURL().toString(), "UTF-8") %>">Login</a></li>
			<% if (request.getSession() != null && request.getSession().getAttribute("logged_in") != null && ((Boolean)request.getSession().getAttribute("logged_in")).booleanValue()) { %>
				<% for (Model model : ModelList.getInstance().getList()) { %>
					<li><a href="edit.jsp?model=<%= model.getName() %>">Enter new <%= model.getName() %></a></li>
				<% } %>
			<% } %>
		</ul>
	</body>
</html>
