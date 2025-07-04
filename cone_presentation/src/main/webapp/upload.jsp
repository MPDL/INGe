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
<%@ page import="org.apache.commons.fileupload2.core.DiskFileItemFactory" %>
<%@ page import="org.apache.commons.fileupload2.core.FileItem" %>
<%@ page import="org.apache.commons.fileupload2.core.FileItemFactory" %>
<%@ page import="org.apache.commons.fileupload2.core.FileItemInputIterator" %>
<%@ page import="org.apache.commons.io.build.AbstractStreamBuilder" %>
<%@ page import="org.apache.commons.fileupload2.jakarta.servlet6.JakartaServletFileUpload" %>

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
							<h1>Importing...</h1>
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
									logger.info("Importing...");

									if (!loggedIn)
									{
									    errors.add("You are not logged in!");
									}
									else
									{
										boolean isMigrateNamespace = true;

										// boolean isMultipart = JakartaServletFileUpload.isMultipartContent(request);
										// Create a factory for disk-based file items
										DiskFileItemFactory fileItemfactory = DiskFileItemFactory.builder().get();

										// Create a new file upload handler
										JakartaServletFileUpload upload = new JakartaServletFileUpload(fileItemfactory);

										logger.info("Parsing request...");
										// Parse the request
										List<FileItem> items = upload.parseRequest(request);
										logger.info("Parsing request done. Length: " + items.size());

										InputStream uploadedStream = null;
										ModelList.Model model = null;
										String workflow = "SKIP";

										for (FileItem item : items)
										{
											if (item.isFormField())
											{
												logger.info("Item is formField " + item.getFieldName());
												if ("model".equals(item.getFieldName()))
												{
													model = ModelList.getInstance().getModelByAlias(item.getString());
												}
												else if ("workflow".equals(item.getFieldName()))
												{
													workflow = item.getString();
												}
											}
											else
											{
												logger.info("Item is NO formField");
												uploadedStream = item.getInputStream();
											}
										}

										logger.info("model: " + model);
										logger.info("workflow: " + workflow);

										SAXParserFactory spf = SAXParserFactory.newInstance();
										spf.setNamespaceAware(true);
										SAXParser parser = spf.newSAXParser();
										RDFHandler rdfHandler = new RDFHandler(loggedIn, model);
										try {
											logger.info("Parsing uploadedStream...");
											parser.parse(uploadedStream, rdfHandler);
											logger.info("Parsing uploadedStream done.");
										}
										catch(Exception e)
										{
											logger.error("Error while parsing RDF import file", e);
											errors.add("Invalid RDF file!<br/>" + e.getMessage());
										}

										Querier querier = QuerierFactory.newQuerier(loggedIn);

										List<LocalizedTripleObject> results = rdfHandler.getResult();

										logger.info("RDF size: " + results.size());
										int i = 0;
										for (LocalizedTripleObject result : results)
										{
											i++;
											if (result instanceof TreeFragment)
											{
												out.println("Importing item " + i + "/" + results.size());
												logger.info("Importing item " + i + "/" + results.size());
												String id;
												if ((null == ((TreeFragment) result).getSubject()) && model.isGenerateIdentifier())
												{
													id = model.getSubjectPrefix() + querier.createUniqueIdentifier(model.getName());
													out.println(PropertyReader.getProperty(PropertyReader.INGE_CONE_SERVICE_URL) + id + " (generated)");
													logger.info(PropertyReader.getProperty(PropertyReader.INGE_CONE_SERVICE_URL) + id + " (generated)");
												}
												else if (null != ((TreeFragment) result).getSubject())
												{
												    if (((TreeFragment) result).getSubject().startsWith(PropertyReader.getProperty(PropertyReader.INGE_CONE_SERVICE_URL)))
												    {
												        id = ((TreeFragment) result).getSubject().substring(PropertyReader.getProperty(PropertyReader.INGE_CONE_SERVICE_URL).length());
												        TreeFragment existingObject = querier.details(model.getName(), id, "*");
												        out.println(PropertyReader.getProperty(PropertyReader.INGE_CONE_SERVICE_URL) + id);
														logger.info(PropertyReader.getProperty(PropertyReader.INGE_CONE_SERVICE_URL) + id);

												        if (null != existingObject && !existingObject.isEmpty() && "skip".equals(workflow))
												        {
											        		out.println(" (skipped)<br/>");
															logger.info(" (skipped)<br/>");
											        		continue;
												        }
												        else if (null != existingObject && !existingObject.isEmpty() && "overwrite".equals(workflow))
														{
												        	out.println(" ... deleting existing object ...");
															logger.info(" ... deleting existing object ...");
															querier.delete(model.getName(), id);
															out.println(" (replaced)");
															logger.info(" (replaced)");
														}
												        else if (null != existingObject && "update-overwrite".equals(workflow))
														{
												        	out.println(" ... updating existing object ...");
															logger.info(" ... updating existing object ...");
												        	existingObject.merge((TreeFragment) result, true);
												        	result = existingObject;
															querier.delete(model.getName(), id);
															out.println(" (updated)");
															logger.info(" (updated)");
														}
												        else if (null != existingObject && "update-add".equals(workflow))
														{
												        	out.println(" ... updating existing object ...");
															logger.info(" ... updating existing object ...");
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
															logger.info(" (updated)");
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
																logger.info(" (skipped)<br/>");
												        		continue;
													        }
													        else if (null != existingObject && !existingObject.isEmpty() && "overwrite".equals(workflow))
															{
													        	out.println(" ... deleting existing object ...");
																logger.info(" ... deleting existing object ...");
																querier.delete(model.getName(), id);
																out.println(" (replaced)");
																logger.info(" (replaced)");
															}
													        else if (null != existingObject && "update-overwrite".equals(workflow))
															{
													        	out.println(" ... updating existing object ...");
																logger.info(" ... updating existing object ...");
													        	existingObject.merge((TreeFragment) result, true);
													        	result = existingObject;
																querier.delete(model.getName(), id);
																out.println(" (updated)");
																logger.info(" (updated)");
															}
													        else if (null != existingObject && "update-add".equals(workflow))
															{
													        	out.println(" ... updating existing object ...");
																logger.info(" ... updating existing object ...");
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
																logger.info(" (updated)");
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
													logger.info(" ...done!<br/>");
												}
												catch (Exception e)
												{
													out.println("<li class=\"messageError\"><b>Error: </b> "+e.getMessage() + "</li>");
													logger.info("<li class=\"messageError\"><b>Error: </b> "+e.getMessage() + "</li>");
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
