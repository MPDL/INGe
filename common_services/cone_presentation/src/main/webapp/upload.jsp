<?xml version="1.0" encoding="UTF-8" ?>
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


 Copyright 2006-2009 Fachinformationszentrum Karlsruhe Gesellschaft
 für wissenschaftlich-technische Information mbH and Max-Planck-
 Gesellschaft zur Förderung der Wissenschaft e.V.
 All rights reserved. Use is subject to license terms.
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
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

<%@page import="org.apache.commons.fileupload.FileUpload"%>
<%@page import="org.apache.commons.fileupload.servlet.ServletFileUpload"%>
<%@page import="org.apache.commons.fileupload.FileItemFactory"%>
<%@page import="org.apache.commons.fileupload.disk.DiskFileItemFactory"%>
<%@page import="org.apache.commons.fileupload.FileItem"%>
<%@page import="java.io.InputStream"%>
<%@page import="javax.xml.parsers.SAXParser"%>
<%@page import="javax.xml.parsers.SAXParserFactory"%>
<%@page import="de.mpg.escidoc.services.cone.rdfimport.RDFHandler"%>
<%@page import="de.mpg.escidoc.services.cone.util.LocalizedTripleObject"%>
<%@page import="de.mpg.escidoc.services.cone.util.TreeFragment"%>
<%@page import="de.mpg.escidoc.services.cone.ModelList.Model"%><html xmlns="http://www.w3.org/1999/xhtml">
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
							<h1>Importing...</h1>
							<!-- Headline ends here -->
						</div>
					</div>
				</div>
				<div class="full_area0">
					<%
						boolean isMultipart = ServletFileUpload.isMultipartContent(request);
						// Create a factory for disk-based file items
						FileItemFactory factory = new DiskFileItemFactory();
						
						// Create a new file upload handler
						ServletFileUpload upload = new ServletFileUpload(factory);
						
						// Parse the request
						List<FileItem> items = upload.parseRequest(request);
						InputStream uploadedStream = null;
						ModelList.Model model = null;
						for (FileItem item : items)
						{
							if (item.isFormField())
							{
								if ("model".equals(item.getFieldName()))
								{
									model = ModelList.getInstance().getModelByAlias(item.getString());
								}
							}
							else
							{
								uploadedStream = item.getInputStream();
							}
						}
						
						SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
						RDFHandler rdfHandler = new RDFHandler();
						parser.parse(uploadedStream, rdfHandler);
						
						Querier querier = QuerierFactory.newQuerier();
						List<LocalizedTripleObject> results = rdfHandler.getResult();
						
						for (LocalizedTripleObject result : results)
						{
							if (result instanceof TreeFragment)
							{
								String id;
								if (model.isGenerateIdentifier())
								{
									id = model.getIdentifierPrefix() + querier.createUniqueIdentifier(model.getName());
								}
								else if (((TreeFragment) result).getSubject() != null)
								{
									id = ((TreeFragment) result).getSubject();
								}
								else
								{
									throw new RuntimeException("Identifier expected");
								}
								querier.create(model.getName(), id, (TreeFragment) result);
							}
							else
							{
								throw new RuntimeException("Wrong RDF structure at " + result);
							}
						}

					%>
				</div>
			</div>
		</div>
	</body>
</html>
