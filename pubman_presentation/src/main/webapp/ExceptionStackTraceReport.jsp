<%@page session="true"%>
<%@page import="java.io.PrintStream"%>

<jsp:useBean id="ErrorPage" scope="session" class="de.mpg.mpdl.inge.pubman.web.ErrorPage" />

<%
	Exception e = ErrorPage.getException();

	String errorType = "text/html";
	response.setContentType(errorType);

    String contentDisposition = "ExceptionStackTraceReport;" + "filename=\"" + "ExceptionStackTraceReport.txt" + "\"";
	response.setHeader("Content-Disposition", contentDisposition);
	response.setHeader("Pragma", "public");
	response.setHeader("Cache-Control", "max-age=0");
	response.setHeader("Expires", "11 Februar 2222 12:34:56 CET");

	e.printStackTrace(new PrintStream(response.getOutputStream()));
%>
