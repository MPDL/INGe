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


 Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
 für wissenschaftlich-technische Information mbH and Max-Planck-
 Gesellschaft zur Förderung der Wissenschaft e.V.
 All rights reserved. Use is subject to license terms.
--%>
<%@page session="true"%>
<%@page import="java.io.PrintStream"%>

<jsp:useBean id="ErrorPage" scope="session" class="de.mpg.escidoc.pubman.ErrorPage" />

<%
	Exception e = ErrorPage.getException();
	
	String errorType = "text/html";
	response.setContentType(errorType);

	StringBuffer contentDisposition = new StringBuffer(64);
	
	contentDisposition.append("ExceptionStackTraceReport;");
	contentDisposition.append("filename=\"");
	contentDisposition.append("ExceptionStackTraceReport.txt");
	contentDisposition.append("\"");	
	response.setHeader("Content-Disposition", contentDisposition.toString());
	response.setHeader("Pragma", "public");
	response.setHeader("Cache-Control", "max-age=0");
	response.setHeader("Expires", "11 Februar 2222 12:34:56 CET");

	e.printStackTrace(new PrintStream(response.getOutputStream()));	
%>
