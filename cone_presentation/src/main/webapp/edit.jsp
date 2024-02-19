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
	this.request = request;
	response.setCharacterEncoding("UTF-8");
%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ page import="de.mpg.mpdl.inge.aa.AuthenticationVO" %>
<%@ page import="de.mpg.mpdl.inge.cone.ConeException"%>
<%@ page import="de.mpg.mpdl.inge.cone.LocalizedString" %>
<%@ page import="de.mpg.mpdl.inge.cone.LocalizedTripleObject" %>
<%@ page import="de.mpg.mpdl.inge.cone.ModelList" %>
<%@ page import="de.mpg.mpdl.inge.cone.Querier" %>
<%@ page import="de.mpg.mpdl.inge.cone.QuerierFactory" %>
<%@ page import="de.mpg.mpdl.inge.cone.TreeFragment" %>
<%@ page import="de.mpg.mpdl.inge.cone.web.Login"%>
<%@ page import="de.mpg.mpdl.inge.cone.web.UrlHelper"%>
<%@ page import="de.mpg.mpdl.inge.cone.web.util.HtmlUtils" %>
<%@ page import="java.io.PrintWriter" %>
<%@ page import="java.io.StringWriter" %>
<%@ page import="java.nio.charset.Charset" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Arrays" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.HashSet" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Set" %>
<%@ page import="org.apache.log4j.Logger"%>
<%@ page import="java.nio.charset.StandardCharsets" %>

<%!
	static final Logger logger = LogManager.getLogger("CoNE edit.jsp");
	List<String> errors;
	List<String> messages;
	boolean warning;
	HttpServletRequest request = null;
	Querier querier = null;
	String sessionAttributePrefix = "coneSubSession_";

	private String displayPredicates(ModelList.Model model, TreeFragment results, String uri, List<ModelList.Predicate> predicates, String prefix, String path, boolean loggedIn) throws ConeException
	{
		StringWriter out = new StringWriter();
		for (ModelList.Predicate predicate : predicates)
		{
				//Display name of predicate and mandatory status
				out.append("\n<span class=\"free_area0 endline itemLine noTopBorder\">");
				out.append("<b class=\"xLarge_area0_p8 endline labelLine clear\">");
				if (predicate.isMandatory())
				{
					out.append("\n<span class=\"mandatory\" title=\"Pflichtfeld\">*</span>");
				}
				out.append(predicate.getName()+"<span class=\"noDisplay\">: </span>");
				out.append("</b>");
				out.append("\n<span class=\"xHuge_area0 singleItem endline\" style=\"overflow: visible;\">");
				//If this predicate has the same identifier as the models primary identifier, display the primary identifier as value (or a message if no identifier is available yet for new entries)
				if (null != model && predicate.getId().equals(model.getIdentifier()) && prefix.isEmpty())
				{
					List<LocalizedTripleObject> resList = results.get(predicate.getId());
					if (null != results.get(predicate.getId()) && !resList.isEmpty())
					{
						for (LocalizedTripleObject object : resList)
						{
							out.append(HtmlUtils.escapeHtml(object.toString()));
							if (object instanceof TreeFragment)
							{
                                this.request.getSession().setAttribute(
                                        this.sessionAttributePrefix + prefix + predicate.getId().replaceAll("[/:. ]", "_"), ((TreeFragment) object).getSubject());
							}
							else if (object instanceof LocalizedString)
							{
                                this.request.getSession().setAttribute(
                                        this.sessionAttributePrefix + prefix + predicate.getId().replaceAll("[/:. ]", "_"), ((LocalizedString) object).getValue());
							}
							else
							{
                                this.request.getSession().setAttribute(
                                        this.sessionAttributePrefix + prefix + predicate.getId().replaceAll("[/:. ]", "_"), object.toString());
							}
						}
					}
					else
					{
						out.append("Will be the same value as Cone-ID");
					}
				}
				//Otherwise (predicates that are not equal to the model's primary identifier)
				else
				{
					//If a value for this predicate already exists
					if (null != results && null != results.get(predicate.getId()) && !results.get(predicate.getId())
                            .isEmpty())
					{
						int counter = 0;
						boolean multiValues = 1 < results.get(predicate.getId()).size();
						for (LocalizedTripleObject object : results.get(predicate.getId()))
						{
							out.append("\n<span class=\"xHuge_area0 endline inputField\" style=\"overflow: visible;\">");
								//Value for predicate exists and predicate is modifyable
								if (predicate.isModify())
								{
									StringBuilder value = new StringBuilder();
									if (null != predicate.getDefaultValue() && ModelList.Event.ONLOAD == predicate.getEvent() && predicate.isOverwrite())
									{
										value.append(HtmlUtils.escapeHtml(predicate.getDefault(this.request)));
									}
									else
									{
										if (object instanceof TreeFragment)
										{
											value.append(HtmlUtils.escapeHtml(((TreeFragment) object).getSubject()));
										}
										else if (object instanceof LocalizedString)
										{
											value.append(HtmlUtils.escapeHtml(((LocalizedString) object).getValue()));
										}
										else
										{
											value.append(HtmlUtils.escapeHtml(object.toString()));
										}
									}
									String name = prefix + predicate.getId().replaceAll("[/:. ]", "_");
									String onChangeSnippet = (" onchange=\"checkField(this, '" + model.getName() + "', '" + path + predicate.getId() + "', '" + prefix + predicate.getId().replaceAll("[/:. ]", "_") + "', " + (multiValues ? counter + "" : "null") + ", false, " + predicate.isShouldBeUnique() + ")\"");
									String nameSnippet = (" name=\"" + name + "\"");
									String cssSnippet = predicate.isResource() ? (" " + prefix + predicate.getId().replaceAll("[/:. ]", "_")) : "";
									if (predicate.isGenerateObject())
									{
										out.append("\n<input type=\"hidden\" class=\"noDisplay\"" + nameSnippet + " value=\"" + value + "\"/>");
									}
									else if(null != predicate.getType() && ModelList.Type.XML == predicate.getType())
									{
										out.append("\n<input type=\"file\" name=\"" + name +"_file\" enctype=\"multipart/form-data\" accept=\".xml,.csl\" />");
										out.append("\n<textarea rows=\"30\" class=\"half_txtArea inputTextArea" + cssSnippet + "\"" + nameSnippet  + ">" + value + "</textarea>");
										out.append("<script>$(document).ready(function() {$('[name=\"" + name + "_file\"]').bind('change', {txtArea: '" + name + "'}, readCslFile)});</script>");
									}
									else
									{
										out.append("\n<input type=\"text\" class=\"huge_txtInput" + cssSnippet + "\"" + nameSnippet + onChangeSnippet + " value=\"" + value + "\"/>");
									}
									if (predicate.isResource())
									{
										out.append("\n<script type=\"text/javascript\">bindSuggest('" + prefix + predicate.getId().replaceAll("[/:. ]", "_") + "', '" + predicate.getResourceModel() + "')</script>");
									}
									if(null != predicate.getSuggestUrl() && !predicate.getSuggestUrl().trim().isEmpty())
									{
										out.append("\n<script type=\"text/javascript\">bindExternalSuggest('" + prefix + predicate.getId().replaceAll("[/:. ]", "_") + "', '" + predicate.getSuggestUrl() + "')</script>");
									}
									if (predicate.isLocalized())
									{
										out.append("<input title=\"Language\" type=\"text\" name=\"" + prefix + predicate.getId().replaceAll("[/:. ]", "_") + "_lang\"  value=\"" + (
                                                null != object.getLanguage()
                                                        ? HtmlUtils.escapeHtml(object.getLanguage()) : "") + "\"");
										out.append(" class=\"small_txtInput " + prefix + predicate.getId().replaceAll("[/:. ]", "_") + "_lang" + counter + "\"");
										out.append("/>");
										out.append("<script type=\"text/javascript\">bindSuggest('" + prefix + predicate.getId().replaceAll("[/:. ]", "_") + "_lang', 'iso639-1', true)</script>");
									}
									if (predicate.isMultiple())
									{
										out.append("<input type=\"button\" class=\"min_imgBtn groupBtn add \" value=\" \" title=\"add\" onclick=\"add(this, '" + prefix + predicate.getId().replaceAll("[/:. ]", "_") + "'," + predicate.isGenerateObject() +", " + predicate.isLocalized()+ ", " + (null != predicate.getPredicates() && !predicate.getPredicates()
                                                .isEmpty()) + ")\"/>");
									}
									else
									{
										if (predicate.isLocalized())
										{
											out.append("<input type=\"button\" class=\"min_imgBtn groupBtn add\" value=\" \" title=\"add language\" onclick=\"add(this, '" + prefix + predicate.getId().replaceAll("[/:. ]", "_") + "'," + predicate.isGenerateObject() +", true, " + (null != predicate.getPredicates() && !predicate.getPredicates()
                                                    .isEmpty()) + ")\"/>"  );
										}
									}
									if (1 < results.get(predicate.getId()).size() || !((null == object.getLanguage() || "".equals(object.getLanguage())) && object instanceof LocalizedString && "".equals(((LocalizedString) object).getValue())))
									{
										out.append("<input type=\"button\" class=\"min_imgBtn groupBtn remove \" value=\" \" onclick=\"removeLine(this, " + (null != predicate.getPredicates() && !predicate.getPredicates()
                                                .isEmpty()) + ");\"/>");
									}
									if (null == predicate.getPredicates() || predicate.getPredicates().isEmpty() || predicate.isResource())
									{
										out.append("<span style='visibility:hidden' class='tiny_area0 tiny_marginRExcl inputInfoBox' onclick=\"checkField($(this).siblings('input').first()[0], '" + model.getName() + "', '" + path + predicate.getId() + "', '" + prefix + predicate.getId().replaceAll("[/:. ]", "_") + "', " + (multiValues ? counter + "" : "null") + ", true, " + predicate.isShouldBeUnique() + ");return false;\">i</span>");
									}
								}
								//Value for predicate exists and it is not modifyable
								else
								{
									if (null != predicate.getDefaultValue() && ModelList.Event.ONLOAD == predicate.getEvent() && predicate.isOverwrite())
									{
										String defaultValue = predicate.getDefault(this.request);
										out.append(predicate.getDefault(this.request));
                                        this.request.getSession().setAttribute(
                                                this.sessionAttributePrefix + prefix + predicate.getId().replaceAll("[/:. ]", "_"), defaultValue);
									}
									else
									{
										out.append(object.toString());
										if (object instanceof TreeFragment)
										{
                                            this.request.getSession().setAttribute(
                                                    this.sessionAttributePrefix + prefix + predicate.getId().replaceAll("[/:. ]", "_"), ((TreeFragment) object).getSubject());
										}
										else if (object instanceof LocalizedString)
										{
                                            this.request.getSession().setAttribute(
                                                    this.sessionAttributePrefix + prefix + predicate.getId().replaceAll("[/:. ]", "_"), ((LocalizedString) object).getValue());
										}
										else
										{
                                            this.request.getSession().setAttribute(
                                                    this.sessionAttributePrefix + prefix + predicate.getId().replaceAll("[/:. ]", "_"), object.toString());
										}
									}
								}
							//Predicate has child predicates
							if (null != predicate.getPredicates() && !predicate.getPredicates().isEmpty())
							{
								out.append("<br/>");
								out.append("\n<span class=\"free_area0 clear\">");
								out.append(displayPredicates(model, (object instanceof TreeFragment ? (TreeFragment) object : null), uri, predicate.getPredicates(), prefix + predicate.getId().replaceAll("[/:. ]", "_") + "_" + counter + "|", path + predicate.getId() + "/", Login.getLoggedIn(
                                        this.request)));
								out.append("</span>");
							}
							out.append("</span>");
							counter++;
						}
					}
					//A value for this predicate does not exist yet, it is modifyable
					else if (predicate.isModify() && !(null != predicate.getDefaultValue() && ModelList.Event.ONSAVE == predicate.getEvent()))
					{
						if (null == predicate.getPredicates() || predicate.getPredicates().isEmpty())
						{
								out.append("\n<span class=\"xHuge_area0 singleItem inputField endline\">");
								String name = prefix + predicate.getId().replaceAll("[/:. ]", "_");
								String nameSnippet = " name=\"" + name + "\"";
								String onChangeSnippet = " onchange=\"checkField(this, '" + model.getName() + "', '" + path + predicate.getId() + "', '" + prefix + predicate.getId().replaceAll("[/:. ]", "_") + "', null, false, " + predicate.isShouldBeUnique() + ");\"";
								String cssSnippet = predicate.isResource() ? (" " + prefix + predicate.getId().replaceAll("[/:. ]", "_")) : "";
								if (predicate.isGenerateObject())
								{
									out.append("<input type=\"hidden\"" + nameSnippet +  "value=\"\"/>");
								}
								else if(null != predicate.getType() && ModelList.Type.XML == predicate.getType())
								{
									out.append("\n<input type=\"file\" name=\"" + name +"_file\" enctype=\"multipart/form-data\" accept=\".xml,.csl\" />");
									out.append("\n<textarea rows=\"30\" class=\"half_txtArea inputTextArea" + cssSnippet + "\"" + nameSnippet + onChangeSnippet + ">" +  "</textarea>");
									out.append("<script>$(document).ready(function() {$('[name=\"" + name + "_file\"]').bind('change', {txtArea: '" + name + "'}, readCslFile)});</script>");
								}
								else
								{
									out.append("\n<input type=\"text\" class=\"huge_txtInput" + cssSnippet + "\"" + nameSnippet + onChangeSnippet + " value=\"\"/>");
								}
								if (predicate.isResource())
								{
									out.append("\n<script type=\"text/javascript\">bindSuggest('" + prefix + predicate.getId().replaceAll("[/:. ]", "_") + "', '" + predicate.getResourceModel() + "')</script>");
								}
								if(null != predicate.getSuggestUrl() && !predicate.getSuggestUrl().trim().isEmpty())
								{
									out.append("\n<script type=\"text/javascript\">bindExternalSuggest('" + prefix + predicate.getId().replaceAll("[/:. ]", "_") + "', '" + predicate.getSuggestUrl() + "')</script>");
								}
								if (predicate.isLocalized())
								{
									out.append("<input type=\"text\" title=\"Language\" name=\"" + prefix + predicate.getId().replaceAll("[/:. ]", "_") + "_lang\" value=\"\"");
									out.append(" class=\"small_txtInput " + prefix + predicate.getId().replaceAll("[/:. ]", "_") + "_lang\"");
									out.append("/>");
									out.append("\n<script type=\"text/javascript\">bindSuggest('" + prefix + predicate.getId().replaceAll("[/:. ]", "_") + "_lang', 'iso639-1', true)</script>");
								}
								if (predicate.isMultiple())
								{
									out.append("<input type=\"button\" class=\"min_imgBtn groupBtn add\" value=\" \" title=\"add\" onclick=\"add(this, '" + prefix + predicate.getId().replaceAll("[/:. ]", "_") + "'," + predicate.isGenerateObject() +", " + predicate.isLocalized()+ ", " + (null != predicate.getPredicates() && !predicate.getPredicates()
                                            .isEmpty()) + ")\"/>");
								}
								else
								{
									if (predicate.isLocalized())
									{
										out.append("\n<input type=\"button\" class=\"min_imgBtn groupBtn add\" value=\" \" title=\"add language\" onclick=\"add(this, '" + prefix + predicate.getId().replaceAll("[/:. ]", "_") + "'," + predicate.isGenerateObject() +", true, " + (null != predicate.getPredicates() && !predicate.getPredicates()
                                                .isEmpty()) + ")\"/>");
									}
								}
								out.append("<span style='visibility:hidden' class='tiny_area0 tiny_marginRExcl inputInfoBox' onclick=\"checkField($(this).siblings('input').first()[0], '" + model.getName() + "', '" + path + predicate.getId() + "', '" + prefix + predicate.getId().replaceAll("[/:. ]", "_") + "', null, true, " + predicate.isShouldBeUnique() + ");return false;\">i</span>");
							out.append("</span>");
						}
						else if (predicate.isMultiple())
						{
							out.append("\n<span class=\"xDouble_area0 singleItem endline\">");
							out.append("\n<input type=\"button\" class=\"min_imgBtn groupBtn add\" value=\" \" title=\"add\" onclick=\"add(this, '" + prefix + predicate.getId().replaceAll("[/:. ]", "_") + "'," + predicate.isGenerateObject() +", " + predicate.isLocalized()+ ", " + (null != predicate.getPredicates() && !predicate.getPredicates()
                                    .isEmpty()) + ")\"/>");
							out.append("</span>");
						}
						else if (predicate.isLocalized())
						{
							out.append("\n<span class=\"xDouble_area0 singleItem endline\">");
							out.append("\n<input type=\"button\" class=\"min_imgBtn groupBtn add\" value=\" \" title=\"add language\" onclick=\"add(this, '" + prefix + predicate.getId().replaceAll("[/:. ]", "_") + "'," + predicate.isGenerateObject() +", " + predicate.isLocalized()+ ", " + (null != predicate.getPredicates() && !predicate.getPredicates()
                                    .isEmpty()) + ")\"/>");
							out.append("</span>");
						}
					}
					//A value for this predicate does not exist yet and is automatically generated on load of this page
					else if (null != predicate.getDefaultValue() && ModelList.Event.ONLOAD == predicate.getEvent())
					{
						String defaultValue = predicate.getDefault(this.request);
						out.append(predicate.getDefault(this.request));
                        this.request.getSession().setAttribute(
                                this.sessionAttributePrefix + prefix + predicate.getId().replaceAll("[/:. ]", "_"), defaultValue);
					}
					//A value for this predicate does not exist yet and is automatically generated on save of this page
					else if (null != predicate.getDefaultValue() && ModelList.Event.ONSAVE == predicate.getEvent())
					{
						//remove old session attributes which are not modifiable
						if(!predicate.isModify())
						{
                            this.request.getSession().removeAttribute(prefix + predicate.getId().replaceAll("[/:. ]", "_"));
						}
						out.append("Will be generated automatically");
					}
					else
					{
						out.append("---");
					}
				}
				out.append("\n</span>");
			out.append("\n</span>");
		}
		return out.toString();
	}

	private void mapFormValues(ModelList.Model model, List<ModelList.Predicate> predicates, HttpServletRequest request, Enumeration<String> paramNames, TreeFragment results, String prefix) throws ConeException
	{
		for (ModelList.Predicate predicate : predicates)
		{
			String paramName = prefix + predicate.getId().replaceAll("[/:. ]", "_");
			String[] paramValues = request.getParameterValues(paramName);
			if (!predicate.isModify() && null != predicate.getDefaultValue() && null != request.getSession()
                    .getAttribute(this.sessionAttributePrefix + paramName))
			{
				paramValues = new String[]{(String) request.getSession().getAttribute(this.sessionAttributePrefix + paramName)};
			}
			else if (null != model.getIdentifier() && model.getIdentifier().equals(predicate.getId()) && predicate.isMandatory() && null != request.getSession()
                    .getAttribute(this.sessionAttributePrefix + paramName))
			{
				paramValues = new String[]{(String) request.getSession().getAttribute(this.sessionAttributePrefix + paramName)};
			}
			String[] langValues = request.getParameterValues(paramName + "_lang");
			List<LocalizedTripleObject> objects = new ArrayList<>();
			if (null != paramValues)
			{
				for (int i = 0; i < paramValues.length; i++)
				{
					String paramValue = paramValues[i];
					if (null != predicate.getDefaultValue() && predicate.isOverwrite() && ModelList.Event.ONSAVE == predicate.getEvent())
					{
						paramValue = predicate.getDefault(request);
					}
					String langValue = null;
					if (null != langValues && langValues.length == paramValues.length)
					{
						langValue = langValues[i];
					}
					if (!"".equals(paramValue))
					{
						if (null != predicate.getPredicates() && !predicate.getPredicates().isEmpty())
						{
							TreeFragment fragment = new TreeFragment(paramValue, langValue);
							objects.add(fragment);
							mapFormValues(model, predicate.getPredicates(), request, paramNames, fragment, paramName + "_" + i + "|");
						}
						else if (predicate.isResource())
						{
							TreeFragment fragment = new TreeFragment(paramValue, langValue);
							objects.add(fragment);
						}
						else
						{
							objects.add(new LocalizedString(paramValue, langValue));
						}
					}
					else if (predicate.isGenerateObject())
					{
						try
						{
							String generatedObject = this.querier.createUniqueIdentifier(null);
							//System.out.println("Generating new identifier")
							if (null != predicate.getPredicates() && !predicate.getPredicates().isEmpty())
							{
								TreeFragment fragment = new TreeFragment(generatedObject, langValue);
								objects.add(fragment);
								mapFormValues(model, predicate.getPredicates(), request, paramNames, fragment, paramName + "_" + i + "|");
							}
							else
							{
								objects.add(new LocalizedString(paramValue, langValue));
							}
						}
						catch (Exception e)
						{
							throw new RuntimeException(e);
						}
					}
					else
					{
						objects.add(new LocalizedString(paramValue, langValue));
					}
				}
			}
			else if (null != predicate.getDefaultValue() && ModelList.Event.ONSAVE == predicate.getEvent())
			{
				if (null != predicate.getPredicates() && !predicate.getPredicates().isEmpty())
				{
					TreeFragment fragment = new TreeFragment(predicate.getDefault(request), null);
					objects.add(fragment);
				}
				else
				{
					objects.add(new LocalizedString(predicate.getDefault(request), null));
				}
			}
			if (!predicate.isMultiple() && 1 < objects.size())
			{
				if (predicate.isLocalized())
				{
					Set<String> languages = new HashSet<>();
					for (LocalizedTripleObject tripleObject : objects)
					{
						if (languages.contains(tripleObject.getLanguage()))
						{
                            this.errors.add("\"" + predicate.getName() + "\" must not have multiple values of the same language.");
							break;
						}
						else
						{
							languages.add(tripleObject.getLanguage());
						}
					}
				}
				else
				{
                    this.errors.add("\"" + predicate.getName() + "\" must not have multiple values.");
				}
			}
			if ((null == model.getIdentifier() || !model.getIdentifier().equals(predicate.getId())) && predicate.isMandatory())
			{
				if (objects.isEmpty())
				{
                    this.errors.add("\"" + predicate.getName() + "\" is mandatory.");
				}
				else
				{
					boolean empty = true;
					for (LocalizedTripleObject object : objects)
					{
						if (object.hasValue())
						{
							empty = false;
							break;
						}
					}
					if (empty)
					{
                        this.errors.add("\"" + predicate.getName() + "\" is mandatory.");
					}
				}
			}
			results.put(predicate.getId(), objects);
		}
	}
%>

<%
    this.errors = new ArrayList<>();
    this.messages = new ArrayList<>();
    this.warning = false;
	String uri = request.getParameter("uri");
	String modelName = request.getParameter("model");
	if (null == modelName || modelName.trim().isEmpty()) {
	  String error = "model may not be null";
	  logger.error(error);
	  throw new RuntimeException(error);
	}
	if (null != uri && !uri.trim().isEmpty() && !UrlHelper.isValidParam(uri)) {
	  String error = "uri " + uri + " not valid";
	  logger.error(error);
	  throw new RuntimeException(error);
	}
	if (!UrlHelper.isValidParam(modelName)) {
	  String error = "model " + modelName + " not valid";
	  logger.error(error);
	  throw new RuntimeException(error);
	}
	AuthenticationVO user = (AuthenticationVO) request.getSession().getAttribute("user");
	ModelList.Model model = null;
	TreeFragment results = new TreeFragment();
	Enumeration<String> paramNames = request.getParameterNames();
	boolean loggedIn = Login.getLoggedIn(request);
    this.querier = QuerierFactory.newQuerier(loggedIn);
	if (null != modelName && !modelName.isEmpty())
	{
		model = ModelList.getInstance().getModelByAlias(modelName);
	}
	if ("true".equals(request.getParameter("form")))
	{
		if (!model.isGenerateIdentifier() && null != request.getParameter("cone_identifier"))
		{
			results.setSubject(request.getParameter("cone_identifier"));
		}
		mapFormValues(model, model.getPredicates(), request, paramNames, results, "");
	}
	//First call edit.jsp (just GET request, no form submission)
	else
	{
		//remove old session attributes
		for(Enumeration e = request.getSession().getAttributeNames(); e.hasMoreElements(); )
		{
			String attrName = (String)e.nextElement();
			if(attrName.startsWith(this.sessionAttributePrefix))
			{
				request.getSession().removeAttribute(attrName);
			}
		}
	}
	boolean form = ("true".equals(request.getParameter("form")));
	if (null != request.getParameter("workflow"))
	{
        this.errors = new ArrayList<>();
        this.messages = new ArrayList<>();
		results = (TreeFragment) session.getAttribute("currentObject");
		if ("change".equals(request.getParameter("workflow")))
		{
			results = this.querier.details(modelName, uri, "*");
		}
		else if ("overwrite".equals(request.getParameter("workflow")))
		{
			logger.info("Overwrite existing CoNE entry " + uri + " by user " + user.getUsername() +" (" + user.getUserId() + ")");
            this.querier.delete(modelName, uri);
            this.querier.create(modelName, uri, results);
			if (null != request.getSession().getAttribute("latestSearch"))
			{
				response.sendRedirect(request.getSession().getAttribute("latestSearch").toString());
				return;
			}
            this.messages.add("Entry saved.");
			logger.info("CoNE entry " + uri + " overwritten successfully by user " + user.getUsername() +" (" + user.getUserId() + ")");
		}
		else if ("update-overwrite".equals(request.getParameter("workflow")))
		{
			logger.info("Merge existing CoNE entry " + uri + " by user " + user.getUsername() +" (" + user.getUserId() + ")");
			TreeFragment existingObject = this.querier.details(modelName, uri, "*");
			existingObject.merge(results, true);
			results = existingObject;
            this.querier.delete(modelName, uri);
            this.querier.create(modelName, uri, results);
			if (null != request.getSession().getAttribute("latestSearch"))
			{
				response.sendRedirect(request.getSession().getAttribute("latestSearch").toString());
				return;
			}
            this.messages.add("Entry saved.");
			logger.info("CoNE entry " + uri + " merged successfully by user " + user.getUsername() +" (" + user.getUserId() + ")");
		}
	}
	else if ((null != request.getParameter("delete")
			|| null != request.getParameter("save"))
			&& ((null == request.getSession().getAttribute("edit_open_vocabulary"))
				&& (null != model && model.isOpen())
			))
	{
        this.errors.add("Not authorized for this action.");
	}
	else if ((null != request.getParameter("delete")
			|| null != request.getParameter("save"))
			&& ((null == request.getSession().getAttribute("edit_closed_vocabulary"))
			&& (null != model && !(Boolean)model.isOpen())))
	{
        this.errors.add("Not authorized for this action.");
	}
	else if (null != request.getParameter("delete"))
	{
		logger.info("Deleting existing CoNE entry " + uri + " by user " + user.getUsername() +" (" + user.getUserId() + ")");
        this.querier.delete(modelName, uri);
		logger.info("CoNE entry " + uri + " deleted successfully by user " + user.getUsername() +" (" + user.getUserId() + ")");
		uri = null;
        this.messages.add("Entry deleted successfully.");
	}
	else if (null != request.getParameter("save"))
	{
		 if (this.errors.isEmpty())
		 {
			if (null == uri)
			{
				String identifierValue;
				if (model.isGenerateIdentifier())
				{
					identifierValue = this.querier.createUniqueIdentifier(modelName);
					uri = model.getSubjectPrefix() + identifierValue;
				}
				else
				{
					identifierValue = request.getParameter("cone_identifier");
					//Check if identifier is null or not ASCII compatible
					if (null != identifierValue && !identifierValue.isEmpty() && UrlHelper.isValidParam(identifierValue) && StandardCharsets.US_ASCII.newEncoder().canEncode(identifierValue))
					{
						identifierValue = identifierValue.trim();
						uri = model.getSubjectPrefix() + identifierValue;
						TreeFragment result = this.querier.details(modelName, uri, "*");
						if (result.exists())
						{
                            this.warning = true;
							session.setAttribute("currentObject", results);
                            this.errors.add("This resource already exists.");
						}
					}
					else
					{
                        this.errors.add("No primary key is provided or the key is invalid. Please do not use special characters or umlauts.");
					}
				}
				if (null != model.getIdentifier())
				{
					List<LocalizedTripleObject> idList = new ArrayList<>();
					idList.add(new LocalizedString(model.getIdentifierPrefix() + identifierValue));
					results.put(model.getIdentifier(), idList);
				}
				logger.info("Creating new CoNE entry " + uri + " by user " + user.getUsername() +" (" + user.getUserId() + ")");
			}
			else
			{
				if (!uri.startsWith(model.getSubjectPrefix()))
				{
                    this.errors.add("Identifier does not start with expected prefix '" + model.getSubjectPrefix() + "'");
				}
				logger.info("Modifying existing CoNE entry " + uri + " by user " + user.getUsername() +" (" + user.getUserId() + ")");
			}
			if (this.errors.isEmpty() && !this.warning)
			{
                this.querier.delete(modelName, uri);
                this.querier.create(modelName, uri, results);
                this.messages.add("Entry saved.");
				logger.info("CoNE entry " + uri + " saved successfully by user " + user.getUsername() +" (" + user.getUserId() + ")");
				response.sendRedirect("view.jsp?model=" + modelName + "&uri=" + uri);
				return;
			}
		 }
	}
	//Edit existing entity
	else if (null != uri && !uri.isEmpty() && null != modelName && !modelName.isEmpty())
	{
		//First call of edit existing entity (just GET request, no form submission)
		if (!form)
		{
			results = this.querier.details(modelName, uri, "*");
		}
        this.errors = new ArrayList<>();
        this.messages = new ArrayList<>();
	}
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
	<jsp:include page="header.jsp"/>
	<body onload="self.pageLoaded = true;checkFields()">
		<div class="full wrapper" id="fullItem">
			<jsp:include page="navigation.jsp"/>
			<div id="content" class="full_area0 clear">
			<!-- begin: content section (including elements that visually belong to the header (breadcrumb, headline, subheader and content menu)) -->
				<form name="editform" action="edit.jsp" accept-charset="UTF-8" method="post">
					<input type="hidden" name="form" value="true"/>
					<% if (null != uri) { %>
						<input type="hidden" name="uri" value="<%= uri %>"/>
					<% } %>
					<input type="hidden" name="model" value="<%= modelName %>"/>
					<div class="clear">
						<div id="headerSection">
							<div id="headLine" class="clear headLine">
								<!-- Headline starts here -->
								<h1>
									<% if (null != uri) { %>
										Edit <%= modelName %>
									<% } else { %>
										New <%= modelName %>
									<% } %>
								</h1>
								<!-- Headline ends here -->
							</div>
						</div>
						<div class="small_marginLIncl subHeaderSection">
							<div class="contentMenu">
								<div class="free_area0 sub">
									&nbsp;
								</div>
							</div>
							<div class="subHeader">
								<% if (!this.messages.isEmpty()) { %>
									<ul class="singleMessage">
									<% for (String message : this.messages) { %>
										<li class="messageStatus"><%= message %></li>
									<% } %>
									</ul>
								<% } %>
								<% if (null != request.getParameter("save") && !this.errors.isEmpty()) { %>
									<ul>
										<% for (String error : this.errors) { %>
											<li class="messageError"><b>Error: </b><%= error %></li>
										<% } %>
									</ul>
								<% } %>
								&nbsp;
							</div>
						</div>
					</div>
					<div class="full_area0">
						<div class="full_area0 fullItem">
							<% if (null != uri) { %>
							<div class="full_area0 itemHeader">
								<span class="xLarge_area0 endline">
									&nbsp;
								</span>
								<span class="seperator"></span>
								<span class="free_area0_p8 endline itemHeadline">
									<b>
										<%= modelName %>:<%= uri %>
									</b>
								</span>
							</div>
							<% } %>
							<% if (this.warning) { %>
								<div class="full_area0 itemBlock">
									<h3 class="xLarge_area0_p8 endline blockHeader">
										What do you want to do?
									</h3>
									<span class="seperator"></span>
									<div class="free_area0 itemBlockContent endline">
										<input type="radio" name="workflow" value="overwrite"/>
										Replace the existing object with mine.
										<br/>
										<input type="radio" name="workflow" value="update-overwrite"/>
										Update the existing object with mine (overwrite matching predicates).
										<br/>
										<input type="radio" name="workflow" value="update-add"/>
										Update the existing object with mine (add matching predicates where possible).
										<br/>
										<input type="radio" name="workflow" value="change" checked=""/>
										Switch to the existing object.
										<br/><br/><br/>
									</div>
								</div>
							<% } else { %>
								<div class="full_area0 itemBlock">
									<h3 class="xLarge_area0_p8 endline blockHeader">
										Data
									</h3>
									<span class="seperator"></span>
									<div class="free_area0 itemBlockContent endline">
										<span class="free_area0 endline itemLine noTopBorder">
											<b class="xLarge_area0_p8 endline labelLine clear">
												*Cone-ID<span class="noDisplay">: </span>
											</b>
											<span class="xHuge_area0 endline">
												<%
												if (null == uri)
												{
													if (model.isGenerateIdentifier())
													{
														out.append("<label class=\"quad_label\">Will be generated automatically</label>");
													}
													else
													{
														out.append("<label class=\"free_area0\">"+model.getSubjectPrefix()+"</label>");
														out.append("<input type=\"hidden\" name=\"cone_subject_prefix\" value=\""+model.getSubjectPrefix()+"\"/>");
														String subject = "";
														if (null != results.getSubject())
														{
															subject = results.getSubject();
														}

														out.append("<input type=\"text\" name=\"cone_identifier\" id='cone_identifier' class=\"double_txtInput\" onchange=\"checkId('" + model.getName() + "', false)\" value=\"" + subject + "\" />");
														out.append("<span style='visibility:hidden' class='tiny_area0 tiny_marginRExcl inputInfoBox' id='idInfo' onclick=\"checkId('" + model.getName() + "', true);return false;\">i</span>");
													}
												}
												else
												{
													out.append("<label class=\"quad_label\">"+uri+"</label>");
												}
												%>
											</span>
										</span>
										<% if (null != model) { %>
											<%= displayPredicates(model, results, uri, model.getPredicates(), "", "", Login.getLoggedIn(request)) %>
										<% } %>
									</div>
									<div class="free_area0 xTiny_marginLIncl">
										<span class="mandatory">* mandatory field</span>
									</div>
								</div>
							<% } %>
						</div>
					</div>
					<div class="full_area0 formButtonArea">
						<input class="free_txtBtn activeButton" type="submit" name="save" value="Save">
						<% if (null != uri) { %>
							<input class="free_txtBtn cancelButton xLarge_marginLIncl" type="submit" name="delete" value="Delete" onclick="if (!confirm('Really delete this entry?')) return false;"/>
						<% } %>

					</div>
				</form>
			</div>
		</div>
		<div class="xHuge_area2_p8 messageArea noDisplay" style="height: 28.37em; overflow-y: auto;">
			<input type="button" id="btnClose" onclick="closeDialog()" value=" " class="min_imgBtn quad_marginLIncl fixMessageBlockBtn"/>
		</div>
	</body>
</html>
