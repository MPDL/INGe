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

<%@ page import="org.apache.logging.log4j.LogManager" %>
<%@ page import="org.apache.logging.log4j.Logger" %>
<%@ page import="jakarta.servlet.http.Part"%>
<%@ page import="java.util.Collection"%>
<%@ page import="javax.xml.parsers.SAXParserFactory"%>
<%@ page import="javax.xml.parsers.SAXParser"%>
<%@ page import="java.util.regex.Pattern"%>
<%@ page import="java.util.regex.Matcher"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.io.InputStream"%>
<%@ page import="de.mpg.mpdl.inge.util.PropertyReader"%>
<%@ page import="de.mpg.mpdl.inge.cone.web.Login"%>
<%@ page import="de.mpg.mpdl.inge.cone.TreeFragment"%>
<%@ page import="de.mpg.mpdl.inge.cone.LocalizedTripleObject"%>
<%@ page import="de.mpg.mpdl.inge.cone.RDFHandler"%>
<%@ page import="de.mpg.mpdl.inge.cone.QuerierFactory"%>
<%@ page import="de.mpg.mpdl.inge.cone.Querier"%>
<%@ page import="de.mpg.mpdl.inge.cone.ModelList"%>
<%@ page import="java.util.Map" %>

<%!
	private void removeIdentifierPrefixes(TreeFragment fragment, ModelList.Model model) throws Exception
	{
		for (Map.Entry<String, List<LocalizedTripleObject>> entry : fragment.entrySet())
		{
            String nodeName = entry.getKey();
            if (null == model.getPredicate(nodeName))
			{
				throw new RuntimeException("Predicate for node \"" + nodeName + "\" not found in model " + model.getName());
			}
			if (model.getPredicate(nodeName).isResource())
			{
				for (LocalizedTripleObject listItem : entry.getValue())
				{
					if (listItem instanceof TreeFragment && ((TreeFragment) listItem).getSubject().startsWith(PropertyReader.getProperty(PropertyReader.INGE_CONE_SERVICE_URL)))
				    {
						((TreeFragment) listItem).setSubject(((TreeFragment) listItem).getSubject().substring(PropertyReader.getProperty(PropertyReader.INGE_CONE_SERVICE_URL).length()));
				    }
				}
			}
		}
	}
%>

<% List<String> errors = new ArrayList<>(); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
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
						<div class="small_marginLIncl subHeaderSection">
							<div class="contentMenu">
								<div class="free_area0 sub">
									&nbsp;
								</div>
							</div>
							<div class="subHeader">
								<%
									errors = new ArrayList<>();
									boolean loggedIn = Login.getLoggedIn(request);
									Logger logger = LogManager.getLogger( "upload.jsp" );

									if (!loggedIn)
									{
									    errors.add("You are not logged in!");
									}
									else
									{
										boolean isMigrateNamespace = true;

										// Check if the request is multipart
										boolean isMultipart = request.getContentType() != null && request.getContentType().toLowerCase().startsWith("multipart/");

										// Using Jakarta EE's Part API for file uploads
										request.setAttribute("jakarta.servlet.context.tempdir", application.getAttribute("jakarta.servlet.context.tempdir"));

										// Parse the request
										Collection<Part> parts = request.getParts();
										InputStream uploadedStream = null;
										ModelList.Model model = null;
										String workflow = "SKIP";
										// boolean createRelations = false;

										for (Part part : parts)
										{
											String fieldName = part.getName();
											if (part.getSubmittedFileName() == null)
											{
												// This is a form field
												String value = request.getParameter(fieldName);
												if ("model".equals(fieldName))
												{
													model = ModelList.getInstance().getModelByAlias(value);
												}
												else if ("workflow".equals(fieldName))
												{
													workflow = value;
												}
												// else if ("create-relations".equals(fieldName))
												// {
												//	createRelations = ("true".equals(value));
												// }
											}
											else
											{
												// This is a file upload
												uploadedStream = part.getInputStream();
											}
										}

										SAXParserFactory spf = SAXParserFactory.newInstance();
										spf.setNamespaceAware(true);
										SAXParser parser = spf.newSAXParser();
										RDFHandler rdfHandler = new RDFHandler(loggedIn, model);
										try {
											parser.parse(uploadedStream, rdfHandler);
										}
										catch(Exception e)
										{
											logger.error("Error while parsing RDF import file", e);
											errors.add("Invalid RDF file!<br/>" + e.getMessage());
										}

										Querier querier = QuerierFactory.newQuerier(loggedIn);

										List<LocalizedTripleObject> results = rdfHandler.getResult();

										for (LocalizedTripleObject result : results)
										{
											if (result instanceof TreeFragment)
											{
												out.println("Importing item");
												String id;
												if ((null == ((TreeFragment) result).getSubject()) && model.isGenerateIdentifier())
												{
													id = model.getSubjectPrefix() + querier.createUniqueIdentifier(model.getName());
													out.println(PropertyReader.getProperty(PropertyReader.INGE_CONE_SERVICE_URL) + id + " (generated)");
												}
												else if (null != ((TreeFragment) result).getSubject())
												{
												    if (((TreeFragment) result).getSubject().startsWith(PropertyReader.getProperty(PropertyReader.INGE_CONE_SERVICE_URL)))
												    {
												        id = ((TreeFragment) result).getSubject().substring(PropertyReader.getProperty(PropertyReader.INGE_CONE_SERVICE_URL).length());
												        TreeFragment existingObject = querier.details(model.getName(), id, "*");
												        out.println(PropertyReader.getProperty(PropertyReader.INGE_CONE_SERVICE_URL) + id);

												        if (null != existingObject && !existingObject.isEmpty() && "skip".equals(workflow))
												        {
											        		out.println(" (skipped)<br/>");
											        		continue;
												        }
												        else if (null != existingObject && !existingObject.isEmpty() && "overwrite".equals(workflow))
														{
												        	out.println(" ... deleting existing object ...");
															querier.delete(model.getName(), id);
															out.println(" (replaced)");
														}
												        else if (null != existingObject && "update-overwrite".equals(workflow))
														{
												        	out.println(" ... updating existing object ...");
												        	existingObject.merge((TreeFragment) result, true);
												        	result = existingObject;
															querier.delete(model.getName(), id);
															out.println(" (updated)");
														}
												        else if (null != existingObject && "update-add".equals(workflow))
														{
												        	out.println(" ... updating existing object ...");
												        	for (ModelList.Predicate predicate : model.getPredicates())
												        	{
												        	    if (!predicate.isMultiple() && ((TreeFragment) result).containsKey(predicate.getId()) && existingObject.containsKey(predicate.getId()))
												        	    {
												        	        for (LocalizedTripleObject res : ((TreeFragment) result).get(predicate.getId()))
												        	        {
												        	            for (LocalizedTripleObject existing : existingObject.get(predicate.getId()))
													        	        {
												        	                if ((null == existing.getLanguage() && null == res.getLanguage()) || existing.getLanguage().equals(res.getLanguage()))
												        	                {
												        	                    existingObject.get(predicate.getId()).remove(existing);
												        	                    break;
												        	                }
													        	        }
												        	        }
												        	    }
												        	}
												        	existingObject.merge((TreeFragment) result, false);
												        	result = existingObject;
															querier.delete(model.getName(), id);
															out.println(" (updated)");
														}

												    }
												    else if (isMigrateNamespace)
												    {
												        Pattern pattern = Pattern.compile("[^/]+/resource/.+$");
												        Matcher matcher = pattern.matcher(((TreeFragment) result).getSubject());
												        if (matcher.find())
												        {
												            id = matcher.group();
													        TreeFragment existingObject = querier.details(model.getName(), id, "*");

													        out.println(PropertyReader.getProperty(PropertyReader.INGE_CONE_SERVICE_URL) + id);

													        if (null != existingObject && !existingObject.isEmpty() && "skip".equals(workflow))
													        {
												        		out.println(" (skipped)<br/>");
												        		continue;
													        }
													        else if (null != existingObject && !existingObject.isEmpty() && "overwrite".equals(workflow))
															{
													        	out.println(" ... deleting existing object ...");
																querier.delete(model.getName(), id);
																out.println(" (replaced)");
															}
													        else if (null != existingObject && "update-overwrite".equals(workflow))
															{
													        	out.println(" ... updating existing object ...");
													        	existingObject.merge((TreeFragment) result, true);
													        	result = existingObject;
																querier.delete(model.getName(), id);
																out.println(" (updated)");
															}
													        else if (null != existingObject && "update-add".equals(workflow))
															{
													        	out.println(" ... updating existing object ...");
													        	for (ModelList.Predicate predicate : model.getPredicates())
													        	{
													        	    if (!predicate.isMultiple() && ((TreeFragment) result).containsKey(predicate.getId()) && existingObject.containsKey(predicate.getId()))
													        	    {
													        	        for (LocalizedTripleObject res : ((TreeFragment) result).get(predicate.getId()))
													        	        {
													        	            for (LocalizedTripleObject existing : existingObject.get(predicate.getId()))
														        	        {
													        	                if ((null == existing.getLanguage() && null == res.getLanguage()) || existing.getLanguage().equals(res.getLanguage()))
													        	                {
													        	                    existingObject.get(predicate.getId()).remove(existing);
													        	                    break;
													        	                }
														        	        }
													        	        }
													        	    }
													        	}
													        	existingObject.merge((TreeFragment) result, false);
													        	result = existingObject;
																querier.delete(model.getName(), id);
																out.println(" (updated)");
															}
												        }
												        else
												        {
												            throw new RuntimeException("Identifier '" + ((TreeFragment) result).getSubject() + "' does not match required format");
												        }
												    }
												    else
												    {
												        throw new RuntimeException("Identifier '" + ((TreeFragment) result).getSubject() + "' is no local URL, but migration is no allowed");
												    }
												}
												else
												{
													throw new RuntimeException("Identifier expected");
												}

												try
												{
													removeIdentifierPrefixes((TreeFragment) result, model);
													querier.create(model.getName(), id, (TreeFragment) result);
													out.println(" ...done!<br/>");
												}
												catch (Exception e)
												{
													out.println("<li class=\"messageError\"><b>Error: </b> "+e.getMessage() + "</li>");
												}



											}
											else
											{
												throw new RuntimeException("Wrong RDF structure at " + result);
											}
										}

									}
								%>
								<hr/>
								<%

									if (null != errors && !errors.isEmpty()) { %>
									<ul>
										<% for (String error : errors) { %>
											<li class="messageError"><b>Error: </b><%= error %></li>
										<% } %>
									</ul>
								<% } %>
								&nbsp;
							</div>
						</div>
					</div>
				</div>
				<div class="full_area0">


				</div>
			</div>
		</div>
	</body>
</html>
