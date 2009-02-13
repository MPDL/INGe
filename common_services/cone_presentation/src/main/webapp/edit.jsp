<%
	request.setCharacterEncoding("UTF-8");
	response.setCharacterEncoding("UTF-8");
	
%><?xml version="1.0" encoding="UTF-8" ?>
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
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%!
	List<String> errors;
	List<String> messages;
	Querier querier = QuerierFactory.newQuerier();

	private String displayPredicates(Model model, TreeFragment results, String uri, List<Predicate> predicates, String prefix)
	{
    	StringWriter out = new StringWriter();
    
    	out.append("<ul>");
    	
	    for (Predicate predicate : predicates)
	    {
	        out.append("<li>");
	        out.append(predicate.getName());
	        if (predicate.isMandatory())
	        {
	            out.append("*");
	        }
	        if (predicate.isMultiple())
	        {
	            out.append("<input type=\"button\" value=\"add\" onclick=\"add(this, '" + prefix + predicate.getId().replaceAll("[/:.]", "_") + "'");
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
	        else if (predicate.isLocalized())
	        {
	            out.append("<input type=\"button\" value=\"add different language\" onclick=\"add(this, '" + prefix + predicate.getId().replaceAll("[/:.]", "_") + "'");
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
	        out.append("<ul>");

	        if (results != null && results.get(predicate.getId()) != null && results.get(predicate.getId()).size() > 0)
	        {
	            int counter = 0;
	            for (LocalizedTripleObject object : results.get(predicate.getId()))
	            {
	                
	                out.append("<li><input type=\"");
	                if (predicate.isGenerateObject())
	                {
	                    out.append("hidden");
	                }
	                else
	                {
	                    out.append("text");
	                }
	                out.append("\" name=\"" + prefix + predicate.getId().replaceAll("[/:.]", "_") + "\" value=\"" + object + "\" size=\"50\"/>");
	                if (predicate.isLocalized())
	                {
	                    out.append("<input type=\"text\" size=\"3\" name=\"" + prefix + predicate.getId().replaceAll("[/:.]", "_") + "_lang\" value=\"" + (object.getLanguage() != null ? object.getLanguage() : "") + "\"/>");
	                }
	                out.append("<input type=\"button\" value=\"delete\" onclick=\"remove(this)\"/>");
	                
	                if (predicate.getPredicates() != null && predicate.getPredicates().size() > 0)
	                {
	                    
	                    out.append(displayPredicates(model, (object instanceof TreeFragment ? (TreeFragment) object : null), uri, predicate.getPredicates(), prefix + predicate.getId().replaceAll("[/:.]", "_") + ":" + counter + ":"));
	                }
	                
	                out.append("</li>");
	                
	                counter++;
	            }
	        }
	        else if (predicate.getPredicates() == null || predicate.getPredicates().size() == 0)
	        {
	            if (predicate.isGenerateObject())
                {
                    out.append("<li><input type=\"hidden\" name=\"" + prefix + predicate.getId().replaceAll("[/:.]", "_") + "\" value=\"\"/>");
                }
                else
                {
                    out.append("<li><input type=\"text\" name=\"" + prefix + predicate.getId().replaceAll("[/:.]", "_") + "\" value=\"\" size=\"50\"/>");
                }

	            if (predicate.isLocalized())
	            {
	                out.append("<input type=\"text\" size=\"3\" name=\"" + prefix + predicate.getId().replaceAll("[/:.]", "_") + "_lang\" value=\"\"/>");
	            }
//	              if (predicate.getPredicates() != null && predicate.getPredicates().size() > 0)
//                {
//                    
//                    out.append(displayPredicates(model, null, uri, predicate.getPredicates(), prefix + predicate.getId().replaceAll("[/:.]", "_") + ":0:"));
//                }
	            out.append("</li>");
	        }
	        out.append("</ul>");
	        out.append("</li>");
	    }
	    
	    out.append("</ul>");
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
	    //querier.delete(modelName, uri);
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





<%@page import="de.mpg.escidoc.services.cone.util.TreeFragment"%>
<%@page import="de.mpg.escidoc.services.cone.util.LocalizedTripleObject"%>
<%@page import="de.mpg.escidoc.services.cone.ModelList.Predicate"%>
<%@page import="java.io.PrintWriter"%>
<%@page import="java.io.StringWriter"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.HashSet"%><html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<title>CoNE - Edit Entry</title>
		<script type="text/javascript">

			function remove(element)
			{
				var parent = element.parentNode;
				var listSize = parent.parentNode.getElementsByTagName('li').length;
				if (listSize > 1)
				{
					parent.parentNode.removeChild(parent);
				}
				else
				{
					parent.getElementsByTagName('input')[0].value = '';
					parent.getElementsByTagName('input')[1].value = '';
					parent.removeChild(element);
				}
			}

			function add(element, predicate, hidden, lang)
			{
				var parent = element.parentNode;
				var ul = parent.getElementsByTagName('ul')[0];

				if (ul.getElementsByTagName('li').length == 1 && ul.getElementsByTagName('li')[0].getElementsByTagName('input').length == 1)
				{
					var newButton = document.createElement('input');
					newButton.value = 'delete';
					newButton.type = 'button';
					newButton.onclick = new Function('remove(this)');
					ul.getElementsByTagName('li')[0].appendChild(newButton);
				}
				
				var li = document.createElement("li");
				ul.appendChild(li);
				
				var input = document.createElement('input');
				input.name = predicate;
				input.type = 'hidden';
				input.value = '';
				li.appendChild(input);

				if (lang)
				{
					var inputLang = document.createElement('input');
					inputLang.name = predicate + '_lang';
					inputLang.type = 'hidden';
					inputLang.size = '3';
					inputLang.value = '';
					li.appendChild(inputLang);
				}
				
//				var button = document.createElement('input');
//				button.value = 'delete';
//				button.type = 'button';
//				button.onclick = new Function('remove(this)');
//				li.appendChild(button);

				element.form.submit();
				
			}
		
		</script>
	</head>
	<body>
		<h2>CoNE - Edit Entry</h2>
		<form name="editform" action="edit.jsp" accept-charset="UTF-8" method="post">
			<input type="hidden" name="form" value="true"/>
			<% if (uri != null) { %>
				<input type="hidden" name="uri" value="<%= uri %>"/>
			<% } %>
			<input type="hidden" name="model" value="<%= modelName %>"/>
			<a href="index.jsp">Home</a>
			<% if (request.getSession().getAttribute("latestSearch") != null) { %>
				<a href="<%= request.getSession().getAttribute("latestSearch") %>">Back to Search</a>
			<% } else { %>
				<a href="search.jsp">Search</a>
			<% } %>
			<h3><%= modelName %>:
				<% if (uri != null) { %>
					<%= uri %>
				<% } else { %>
					New entry
				<% } %>
			</h3>

			<% if (messages.size() > 0) { %>
				<% for (String message : messages) { %>
					<p style="color: green"><%= message %></p>
				<% } %>
			<% } %>

			<% if (errors.size() > 0) { %>
				<ul>
					<% for (String error : errors) { %>
						<li style="color: red"><b>Error: </b><%= error %></li>
					<% } %>
				</ul>
			<% } %>

			Cone-ID: <%
			
			if (uri == null)
            {
                if (model.isGenerateIdentifier())
                {
                    out.append("Will be generated");
            	}
                else
                {
                    out.append("<input type=\"text\" name=\"cone_identifier\" value=\"\" size=\"50\"/>");
                }
            }
			else
			{
			    out.append(uri);
			}
			
			%>

			<% if (model != null) { %>
				<%= displayPredicates(model, results, uri, model.getPredicates(), "") %>
			<% } %>

			<% if (uri != null) { %>
				<input type="submit" name="delete" value="Delete" onclick="if (!confirm('Really delete this entry?')) return false;"/>
			<% } %>
			<input type="submit" name="save" value="Save"/>
		</form>
	</body>
</html>