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

<%@ page import="de.mpg.mpdl.inge.cone.ModelList" %>
<%@ page import="de.mpg.mpdl.inge.cone.Querier" %>
<%@ page import="de.mpg.mpdl.inge.cone.QuerierFactory" %>
<%@ page import="de.mpg.mpdl.inge.cone.TreeFragment" %>
<%@ page import="de.mpg.mpdl.inge.cone.web.Login"%>
<%@ page import="de.mpg.mpdl.inge.util.PropertyReader"%>
<%@ page import="java.sql.Connection"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.List" %>

<%
	response.setHeader("Content-Type", "text/plain");

	boolean loggedIn = Login.getLoggedIn(request);

	if (loggedIn)
	{
		Querier querier = QuerierFactory.newQuerier(loggedIn);

		out.println("Reset started...");
		out.flush();

		List<String> models = new ArrayList<>();

		models.add(request.getParameter("model"));

		for (String modelName : models)
		{

		    ModelList.Model model = ModelList.getInstance().getModelByAlias(modelName);

		    List<String> ids = querier.getAllIds(model.getName());
		    for (String id : ids)
		    {
		        TreeFragment details = querier.details(model.getName(), id, "*");
		        querier.delete(model.getName(), id);
		        querier.create(model.getName(), id, details);
		        out.println("resetting resource " + id);
		        out.flush();
		    }
		}

		out.println("...finished");

		querier.release();
	}
	else
	{
		out.println("Not logged in!");
	}
%>
