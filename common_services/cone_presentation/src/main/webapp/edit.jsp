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
<%

	String uri = request.getParameter("uri");
	String modelName = request.getParameter("model");
	Model model = null;
	Map<String, List<LocalizedString>> results = new HashMap<String, List<LocalizedString>>();
	
	List<String> errors = new ArrayList<String>();
	List<String> messages = new ArrayList<String>();
	
	Querier querier = QuerierFactory.newQuerier();
	
	if (modelName != null && !"".equals(modelName))
	{
	    model = ModelList.getInstance().getModelByAlias(modelName);
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
	    Enumeration<String> paramNames = request.getParameterNames();
	    
        for (Predicate predicate : model.getPredicates())
        {
            String paramName = predicate.getId().replaceAll("[/:.]", "_");
            String[] paramValues = request.getParameterValues(paramName);
            String[] langValues = request.getParameterValues(paramName + "_lang");
            List<LocalizedString> objects = new ArrayList<LocalizedString>();
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
	            	    objects.add(new LocalizedString(paramValue, langValue));
	            	}
	            }
            }
            if (!predicate.isMultiple() && objects.size() > 1)
            {
                errors.add("\"" + predicate.getName() + "\" must not have multiple values.");
            }
            if (!model.getIdentifier().equals(predicate.getId()) && predicate.isMandatory() && objects.size() == 0)
            {
                errors.add("\"" + predicate.getName() + "\" is mandatory.");
            }
            results.put(predicate.getId(), objects);
        }
        
        if (uri == null)
        {
            if (model.isGenerateIdentifier())
            {
                uri = model.getIdentifierPrefix() + querier.createUniqueIdentifier(modelName);
            }
            else
            {
                String identifierName = model.getIdentifier().replaceAll("[/:.]", "_");
                String identifierValue = request.getParameter(identifierName);
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
            else
            {
	            String identifierName = model.getIdentifier();
	            String identifierValue = uri.substring(model.getIdentifierPrefix().length());
	            List<LocalizedString> objects = new ArrayList<LocalizedString>();
	            objects.add(new LocalizedString(identifierValue));
	            results.put(identifierName, objects);
            }
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
	    results = querier.details(modelName, uri, "*");
	}
%>




<html xmlns="http://www.w3.org/1999/xhtml">
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

			function add(element, predicate)
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
				input.type = 'text';
				input.size = '50';
				input.value = '';
				li.appendChild(input);

				var space = document.createTextNode(' ');
				li.appendChild(space);
				
				var inputLang = document.createElement('input');
				inputLang.name = predicate + '_lang';
				inputLang.type = 'text';
				inputLang.size = '3';
				inputLang.value = '';
				li.appendChild(inputLang);

				var space2 = document.createTextNode(' ');
				li.appendChild(space2);
				
				var button = document.createElement('input');
				button.value = 'delete';
				button.type = 'button';
				button.onclick = new Function('remove(this)');
				li.appendChild(button);
			}
		
		</script>
	</head>
	<body>
		<h2>CoNE - Edit Entry</h2>
		<form name="editform" action="edit.jsp" accept-charset="UTF-8" method="post">
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

			<ul>
				<% if (model != null) { %>
					<% for (Predicate predicate : model.getPredicates()) { %>
						<li><%= predicate.getName() %>
							<% if (predicate.isMandatory()) { %>*<% } %>
							<% if (predicate.isMultiple()) { %>
								<input type="button" value="add" onclick="add(this, '<%= predicate.getId().replaceAll("[/:.]", "_") %>')"/>
							<% } %>
							<ul>
								<% if (predicate.getId().equals(model.getIdentifier())) { %>
									<% if (uri == null) { %>
										<% if (model.isGenerateIdentifier()) { %>
											<li>Will be generated</li>
										<% } else { %>
											<li><input type="text" name="<%= predicate.getId().replaceAll("[/:.]", "_") %>" value="" size="50"/>
											</li>
										<% } %>
									<% } else if (results.get(predicate.getId()) != null) { %>
										<% for (LocalizedString object : results.get(predicate.getId())) { %>
											<li><%= object %></li>
										<% } %>
									<% } else { %>

									<% } %>
								<% } else if (results.get(predicate.getId()) != null && results.get(predicate.getId()).size() > 0) { %>
									<% for (LocalizedString object : results.get(predicate.getId())) { %>
										<li><input type="text" name="<%= predicate.getId().replaceAll("[/:.]", "_") %>" value="<%= object %>" size="50"/>
											<% if (predicate.isLocalized()) { %>
												<input type="text" size="3" name="<%= predicate.getId().replaceAll("[/:.]", "_") %>_lang" value="<%= (object.getLanguage() != null ? object.getLanguage() : "") %>"/>
											<% } %>
											<input type="button" value="delete" onclick="remove(this)"/>
										</li>
									<% } %>
								<% } else { %>
									<li><input type="text" name="<%= predicate.getId().replaceAll("[/:.]", "_") %>" value="" size="50"/>
										<% if (predicate.isLocalized()) { %>
											<input type="text" size="3" name="<%= predicate.getId().replaceAll("[/:.]", "_") %>_lang" value=""/>
										<% } %>
									</li>
								<% } %>
							</ul>
						</li>
					<% } %>
				<% } %>
			</ul>
			<% if (uri != null) { %>
				<input type="submit" name="delete" value="Delete" onclick="if (!confirm('Really delete this entry?')) return false;"/>
			<% } %>
			<input type="submit" name="save" value="Save"/>
		</form>
	</body>
</html>