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
 fÃ¼r wissenschaftlich-technische Information mbH and Max-Planck-
 Gesellschaft zur FÃ¶rderung der Wissenschaft e.V.
 All rights reserved. Use is subject to license terms.
-->

<%
	request.setCharacterEncoding("UTF-8");
	response.setCharacterEncoding("UTF-8");
	
%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="java.util.Arrays" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="de.mpg.escidoc.services.cone.QuerierFactory" %>
<%@ page import="de.mpg.escidoc.services.cone.Querier" %>
<%@ page import="de.mpg.escidoc.services.cone.ModelList.Model" %>
<%@ page import="de.mpg.escidoc.services.cone.ModelList" %>
<%@ page import="de.mpg.escidoc.services.cone.ModelList.Predicate" %>
<%@ page import="de.mpg.escidoc.services.cone.util.LocalizedString" %>
<%@ page import="de.mpg.escidoc.services.cone.util.TreeFragment" %>
<%@ page import="de.mpg.escidoc.services.cone.util.LocalizedTripleObject" %>
<%@ page import="de.mpg.escidoc.services.cone.ModelList.Predicate" %>
<%@ page import="java.io.PrintWriter" %>
<%@ page import="java.io.StringWriter" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.HashSet" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%!
	List<String> errors;
	List<String> messages;
	Querier querier = QuerierFactory.newQuerier();

	private String displayPredicates(Model model, TreeFragment results, String uri, List<Predicate> predicates, String prefix)
	{
    	StringWriter out = new StringWriter();
    
	    for (Predicate predicate : predicates)
	    {
	        out.append("<span class=\"full_area0 endline itemLine noTopBorder\">");
	        
				out.append("<b class=\"xLarge_area0_p8 endline labelLine clear\">");
	        		if (predicate.isMandatory())
	        		{
	        	    	out.append("<span class=\"mandatory\" title=\"Pflichtfeld\">*</span>");
	        		}
	        		out.append(predicate.getName()+"<span class=\"noDisplay\">: </span>");
	        	out.append("</b>");
	        	out.append("<span class=\"xDouble_area0 endline\" style=\"overflow: visible;\">");

			        if (results != null && results.get(predicate.getId()) != null && results.get(predicate.getId()).size() > 0)
			        {
	    		        int counter = 0;
	        		    for (LocalizedTripleObject object : results.get(predicate.getId()))
	            		{                
			                out.append("<span class=\"xDouble_area0 singleItem endline\">");
			                	out.append("<input type=\"");
				                if (predicate.isGenerateObject())
			    	            {
	    		    	            out.append("hidden\" class=\"noDisplay");
	        		    	    }
	            		    	else
		            		    {
		                		    out.append("text\" class=\"xLarge_txtInput");
		    	            	}
		                
								if (predicate.isResource())
								{
									out.append(" " + prefix + predicate.getId().replaceAll("[/:.]", "_"));
								}
								out.append("\" name=\"" + prefix + predicate.getId().replaceAll("[/:.]", "_") + "\" value=\"" + object + "\" ");
								out.append(" />");

								if (predicate.isResource())
								{
									out.append("<script type=\"text/javascript\">bindSuggest('" + prefix + predicate.getId().replaceAll("[/:.]", "_") + "', '" + predicate.getResourceModel() + "')</script>");
								}

			            	    if (predicate.isLocalized())
				                {
				                    out.append("<input type=\"text\" name=\"" + prefix + predicate.getId().replaceAll("[/:.]", "_") + "_lang\" value=\"" + (object.getLanguage() != null ? object.getLanguage() : "") + "\"");
									out.append(" class=\"small_txtInput " + prefix + predicate.getId().replaceAll("[/:.]", "_") + "_lang" + counter + "\"");
									out.append("/>");
									out.append("<script type=\"text/javascript\">bindSuggest('" + prefix + predicate.getId().replaceAll("[/:.]", "_") + "_lang" + counter + "', 'languages', true)</script>");
	                			}
		                
			                	if (predicate.isMultiple())
			    		        {
		    			            out.append("<input type=\"button\" class=\"min_imgBtn groupBtn add\" value=\" \" title=\"add\" onclick=\"add(this, '" + prefix + predicate.getId().replaceAll("[/:.]", "_") + "'");
	    			    	        if (predicate.isGenerateObject())
	    		    	    	    {
	    	    	    	    	    out.append(", true");
	    	        	    		}
		    	        	    	else
		    	            		{
	    		               			out.append(", false");
		    		            	}
			    	    	        if (predicate.isLocalized())
		    		        	    {
			    		                out.append(", true");
		    			            }
	    			    	        else
	    		    	    	    {
	    	    	    	    	    out.append(", false");
	    	        	    		}
			    	            	out.append(")\"/>");
				    	        }
				    	        else
				    	        {
			    		            if (predicate.isLocalized())
			    		        	{
	    			    	        	out.append("<input type=\"button\" class=\"min_imgBtn groupBtn add\" value=\" \" title=\"add language\" onclick=\"add(this, '" + prefix + predicate.getId().replaceAll("[/:.]", "_") + "'");
	    	    		    	    	if (predicate.isGenerateObject())
	    	        		    		{
	    	            				    out.append(", true");
	    	            				}
				    	            	else
				    	            	{
	    				                	out.append(", false");
	    				            	}
	    	    			        	out.append(", true)\"/>");
			    		        	}
	    	        			}
	                
			        	        if (results.get(predicate.getId()).size() > 1 || !((object.getLanguage() == null || "".equals(object.getLanguage())) && object instanceof LocalizedString && "".equals(((LocalizedString) object).getValue())))
			            	    {
				            	    out.append("<input type=\"button\" class=\"min_imgBtn groupBtn remove\" value=\" \" onclick=\"remove(this)\"/>");
	        		        	}
	                
		            	    out.append("</span>");
	                
			                if (predicate.getPredicates() != null && predicate.getPredicates().size() > 0)
			                {
	    		                out.append("<span class=\"free_area0 large_negMarginLExcl\">");
	        		            out.append(displayPredicates(model, (object instanceof TreeFragment ? (TreeFragment) object : null), uri, predicate.getPredicates(), prefix + predicate.getId().replaceAll("[/:.]", "_") + ":" + counter + ":"));
	            		        out.append("</span>");
	            	    	}
	                
	                		counter++;
	            		}
	            	}
			        else
			        {
	    		        if (predicate.getPredicates() == null || predicate.getPredicates().size() == 0)
	        			{
	            			out.append("<span class=\"xDouble_area0 singleItem endline\">");
	            				if (predicate.isGenerateObject())
	                			{
    		            		    out.append("<input type=\"hidden\" name=\"" + prefix + predicate.getId().replaceAll("[/:.]", "_") + "\" value=\"\"/>");
        		        		}
		        		        else 
		            		    {
	    	            		    if (predicate.isResource())
	    		            		{
    	    		            		out.append("<input type=\"text\" name=\"" + prefix + predicate.getId().replaceAll("[/:.]", "_") + "\" value=\"\"");
		    	    		        	out.append(" class=\"xLarge_txtInput " + prefix + predicate.getId().replaceAll("[/:.]", "_") + "\"");
			        		    	    out.append("/>");
		    	        		    	out.append("<script type=\"text/javascript\">bindSuggest('" + prefix + predicate.getId().replaceAll("[/:.]", "_") + "', '" + predicate.getResourceModel() + "')</script>");
	    	    	        		}
	    	    	    	    	else
    	    	    	    		{
        	    	    			    out.append("<input type=\"text\" class=\"xLarge_txtInput\" name=\"" + prefix + predicate.getId().replaceAll("[/:.]", "_") + "\" value=\"\" />");
            	    				}
			    	            }

					            if (predicate.isLocalized())
					            {
	    				            out.append("<input type=\"text\" name=\"" + prefix + predicate.getId().replaceAll("[/:.]", "_") + "_lang\" value=\"\"");
									out.append(" class=\"small_txtInput " + prefix + predicate.getId().replaceAll("[/:.]", "_") + "_lang\"");
									out.append("/>");
									out.append("<script type=\"text/javascript\">bindSuggest('" + prefix + predicate.getId().replaceAll("[/:.]", "_") + "_lang', 'languages', true)</script>");
		            			}
			            
//			            	  	if (predicate.getPredicates() != null && predicate.getPredicates().size() > 0)
//      		          		{
//          	          
//	          		          	out.append(displayPredicates(model, null, uri, predicate.getPredicates(), prefix + predicate.getId().replaceAll("[/:.]", "_") + ":0:"));
//  	            		  	}

								if (predicate.isMultiple())
					        	{
							    	out.append("<input type=\"button\" class=\"min_imgBtn groupBtn add\" value=\" \" title=\"add\" onclick=\"add(this, '" + prefix + predicate.getId().replaceAll("[/:.]", "_") + "'");
				    	        	if (predicate.isGenerateObject())
		    		    	    	{
		        		    		    out.append(", true");
		            				}
			            			else
			            			{
				                		out.append(", false");
				    	        	}
			            	
			    	    	    	if (predicate.isLocalized())
			        	    		{
		    	        			    out.append(", true");
			            			}
			    	        		else
			        	    		{
			        	        		out.append(", false");
		    	       	 			}
		        	   		 		out.append(")\"/>");
			        			}
					        	else
					        	{
					        	    if (predicate.isLocalized())
		    			    		{
		        			    		out.append("<input type=\"button\" class=\"min_imgBtn groupBtn add\" value=\" \" title=\"add language\" onclick=\"add(this, '" + prefix + predicate.getId().replaceAll("[/:.]", "_") + "'");
			            				if (predicate.isGenerateObject())
			        	   				{
			            	   				out.append(", true");
				           				}
			  		         			else
			    		        		{
		    	    		    	   		out.append(", false");
		        	    				}
		            					out.append(", true)\"/>");
		        					}
					        	}
	
				            out.append("</span>");
	        			}
	    		        else if (predicate.isMultiple())
			        	{
	    		            out.append("<span class=\"xDouble_area0 singleItem endline\">");
						    	out.append("<input type=\"button\" class=\"min_imgBtn groupBtn add\" value=\" \" title=\"add\" onclick=\"add(this, '" + prefix + predicate.getId().replaceAll("[/:.]", "_") + "'");
			    	        	if (predicate.isGenerateObject())
	    		    	    	{
	        		    		    out.append(", true");
	            				}
		            			else
		            			{
			                		out.append(", false");
			    	        	}
		            	
		    	    	    	if (predicate.isLocalized())
		        	    		{
	    	        			    out.append(", true");
		            			}
		    	        		else
		        	    		{
		        	        		out.append(", false");
	    	       	 			}
	        	   		 		out.append(")\"/>");
	        	   		 	out.append("</span>");
	        			}
			        	else if (predicate.isLocalized())
   			    		{
			        	    out.append("<span class=\"xDouble_area0 singleItem endline\">");
	       			    		out.append("<input type=\"button\" class=\"min_imgBtn groupBtn add\" value=\" \" title=\"add language\" onclick=\"add(this, '" + prefix + predicate.getId().replaceAll("[/:.]", "_") + "'");
	            				if (predicate.isGenerateObject())
	        	   				{
	            	   				out.append(", true");
		           				}
	  		         			else
	    		        		{
	   	    		    	   		out.append(", false");
	       	    				}
	           					out.append(", true)\"/>");
	           				out.append("</span>");
       					}
	    		   	}
		        
		        out.append("</span>");
		        
	        out.append("</span>");
	    }

	    return out.toString();
	}
	
	private void mapFormValues(Model model, List<Predicate> predicates, HttpServletRequest request, Enumeration<String> paramNames, TreeFragment results, String prefix)
	{
	    
        for (Predicate predicate : predicates)
        {
            String paramName = prefix + predicate.getId().replaceAll("[/:.]", "_");
            String[] paramValues = request.getParameterValues(paramName);
            String[] langValues = request.getParameterValues(paramName + "_lang");
            List<LocalizedTripleObject> objects = new ArrayList<LocalizedTripleObject>();
            if (paramValues != null)
            {
	            for (int i = 0; i < paramValues.length; i++)
	            {
	                String paramValue = paramValues[i];
	                String langValue = null;
	                if (langValues != null && langValues.length == paramValues.length)
	                {
	                	langValue = langValues[i];
	                }
	            	if (!"".equals(paramValue))
	            	{
	            	    if (predicate.getPredicates() != null && predicate.getPredicates().size() > 0)
	            	    {
	            	        TreeFragment fragment = new TreeFragment(paramValue, langValue);
	            	        objects.add(fragment);
	            	        mapFormValues(model, predicate.getPredicates(), request, paramNames, fragment, paramName + ":" + i + ":");
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
            	        	String generatedObject = querier.createUniqueIdentifier(null);
            	        	if (predicate.getPredicates() != null && predicate.getPredicates().size() > 0)
    	            	    {
    	            	        TreeFragment fragment = new TreeFragment(generatedObject, langValue);
    	            	        objects.add(fragment);
    	            	        mapFormValues(model, predicate.getPredicates(), request, paramNames, fragment, paramName + ":" + i + ":");
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
            if (!predicate.isMultiple() && objects.size() > 1)
            {
                if (predicate.isLocalized())
                {
                    Set<String> languages = new HashSet<String>();
                    for (LocalizedTripleObject tripleObject : objects)
                    {
                        if (languages.contains(tripleObject.getLanguage()))
                        {
                            errors.add("\"" + predicate.getName() + "\" must not have multiple values of the same language.");
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
                	errors.add("\"" + predicate.getName() + "\" must not have multiple values.");
                }
            }
            if (!model.getIdentifier().equals(predicate.getId()) && predicate.isMandatory() && objects.size() == 0)
            {
                errors.add("\"" + predicate.getName() + "\" is mandatory.");
            }
            results.put(predicate.getId(), objects);
        }
	    
	}

%>

<%

	errors = new ArrayList<String>();
	messages = new ArrayList<String>();

	String uri = request.getParameter("uri");
	String modelName = request.getParameter("model");
	Model model = null;
	TreeFragment results = new TreeFragment();

	Enumeration<String> paramNames = request.getParameterNames();
	
	if (modelName != null && !"".equals(modelName))
	{
	    model = ModelList.getInstance().getModelByAlias(modelName);
	}
    
	if ("true".equals(request.getParameter("form")))
	{
		mapFormValues(model, model.getPredicates(), request, paramNames, results, "");
	}
	
	if ((request.getParameter("delete") != null
	        || request.getParameter("save") != null)
	        && (request.getSession().getAttribute("logged_in") == null
	        || !((Boolean)request.getSession().getAttribute("logged_in")).booleanValue()))
    {
	    errors.add("Not authorized for this action.");
    }
	else if (request.getParameter("delete") != null)
	{
	    querier.delete(modelName, uri);
	    if (request.getSession().getAttribute("latestSearch") != null)
	    {
	        response.sendRedirect(request.getSession().getAttribute("latestSearch").toString());
	        return;
	    }
	}
	else if (request.getParameter("save") != null)
	{
        
        if (uri == null)
        {
            if (model.isGenerateIdentifier())
            {
                uri = model.getIdentifierPrefix() + querier.createUniqueIdentifier(modelName);
            }
            else
            {
                String identifierValue = request.getParameter("cone_identifier");
                if (identifierValue != null && !"".equals(identifierValue))
                {
                    uri = model.getIdentifierPrefix() + identifierValue;
                }
                else
                {
                    errors.add("No primary key is provided.");
                }
            }
        }
        else
        {
            if (!uri.startsWith(model.getIdentifierPrefix()))
            {
                errors.add("Identifier does not start with expected prefix '" + model.getIdentifierPrefix() + "'");
            }
//            else
//            {
//	            String identifierName = model.getIdentifier();
//	            String identifierValue = uri.substring(model.getIdentifierPrefix().length());
//	            List<LocalizedTripleObject> objects = new ArrayList<LocalizedTripleObject>();
//	            objects.add(new LocalizedString(identifierValue));
//	            results.put(identifierName, objects);
//           }
        }
        
        if (errors.size() == 0)
        {
		    querier.delete(modelName, uri);
		    querier.create(modelName, uri, results);
		    if (request.getSession().getAttribute("latestSearch") != null)
		    {
		        response.sendRedirect(request.getSession().getAttribute("latestSearch").toString());
		        return;
		    }
		    messages.add("Entry saved.");
        }
	}
	else if (uri != null && !"".equals(uri) && modelName != null && !"".equals(modelName))
	{
	    if (!"true".equals(request.getParameter("form")))
	    {
	    	results = querier.details(modelName, uri, "*");
	    }
	    errors = new ArrayList<String>();
		messages = new ArrayList<String>();
	}
%>
<html xmlns="http://www.w3.org/1999/xhtml">
	<jsp:include page="header.jsp"/>
	<body>
		<div class="full wrapper">
			<jsp:include page="navigation.jsp"/>
			<div id="content" class="full_area0 clear">
			<!-- begin: content section (including elements that visualy belong to the header (breadcrumb, headline, subheader and content menu)) -->
				<form name="editform" action="edit.jsp" accept-charset="UTF-8" method="post">
					<input type="hidden" name="form" value="true"/>
					<% if (uri != null) { %>
						<input type="hidden" name="uri" value="<%= uri %>"/>
					<% } %>
					<input type="hidden" name="model" value="<%= modelName %>"/>
					<div class="clear">
						<div id="headerSection">
							<div id="headLine" class="clear headLine">
								<!-- Headline starts here -->
								<h1>
									<% if (uri != null) { %>
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
								<% if (messages.size() > 0) { %>
									<ul class="singleMessage">
									<% for (String message : messages) { %>
										<li class="messageStatus"><%= message %></li>
									<% } %>
									</ul>
								<% } %>
								<% if (errors.size() > 0) { %>
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
					<div class="full_area0">
						<div class="full_area0 fullItem">
							<% if (uri != null) { %>
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
							<div class="full_area0 itemBlock">
								<h3 class="xLarge_area0_p8 endline blockHeader">
									Data
								</h3>
								<span class="seperator"></span>
								<div class="free_area0 itemBlockContent endline">
									<span class="free_area0 endline itemLine noTopBorder">
										<b class="xLarge_area0_p8 endline labelLine clear">
											Cone-ID<span class="noDisplay">: </span>
										</b>
										<span class="xHuge_area0 endline">
											<%
											if (uri == null)
								            {
								                if (model.isGenerateIdentifier())
								                {
								                    out.append("<label class=\"quad_label\">Will be generated</label>");
								            	}
								                else
								                {
								                    out.append("<label class=\"free_area0\">"+model.getIdentifierPrefix()+"</label>");
								                    out.append("<input type=\"text\" name=\"cone_identifier\" class=\"double_txtInput\" value=\"\" />");
								                }
								            }
											else
											{
											    out.append("<label class=\"quad_label\">"+uri+"</label>");
											}
											
											%>
										</span>
									</span>
									<% if (model != null) { %>
										<%= displayPredicates(model, results, uri, model.getPredicates(), "") %>
									<% } %>	
								</div>
								<div class="free_area0 xTiny_marginLIncl">
									<span class="mandatory">* mandatory fiefd</span>
								</div>
							</div>
						</div>
					</div>
					<div class="full_area0 formButtonArea">
						<% if (uri != null) { %>
							<input class="free_txtBtn cancelButton xLarge_marginLIncl" type="submit" name="delete" value="Delete" onclick="if (!confirm('Really delete this entry?')) return false;"/>
						<% } %>
						<input class="free_txtBtn activeButton" type="submit" name="save" value="Save"/>
					</div>
				</form>
			</div>
		</div>
	</body>
</html>