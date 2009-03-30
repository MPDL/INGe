<%
/*
* CDDL HEADER START
*
* The contents of this file are subject to the terms of the
* Common Development and Distribution License, Version 1.0 only
* (the "License"). You may not use this file except in compliance
* with the License.
*
* You can obtain a copy of the license at license/ESCIDOC.LICENSE
* or http://www.escidoc.de/license.
* See the License for the specific language governing permissions
* and limitations under the License.
*
* When distributing Covered Code, include this CDDL HEADER in each
* file and include the License file at license/ESCIDOC.LICENSE.
* If applicable, add the following below this CDDL HEADER, with the
* fields enclosed by brackets "[]" replaced with your own identifying
* information: Portions Copyright [yyyy] [name of copyright owner]
*
* CDDL HEADER END
*/

/*
* Copyright 2006-2009 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/

/**
 * eSciDoc Syndication Service Web presentation.
 *
 * Recent Releases of the Organizational Units page.
 * 
 * @author Vlad Makarenko  (initial creation) 
 * @author $Author$ (last modification)
 * $Revision$
 * $LastChangedDate$ 
 */
%>
<%@page import="java.util.Arrays,java.util.TreeMap,java.util.Map,de.mpg.escidoc.services.syndication.Syndication,de.mpg.escidoc.services.syndication.Utils,de.mpg.escidoc.services.framework.PropertyReader"%>
<%
	// generation of the SELECT for Organizational Units
	String ou = request.getParameter("ou");
	
    TreeMap<String, String> outm = Utils.getOrganizationUnitTree(); 
	String selOrgUnit="";
	boolean flag = Utils.checkVal(ou);
	for( Map.Entry<String, String> entry: outm.entrySet() )
	{
		String key = entry.getKey(); 
		String value = entry.getValue();
		selOrgUnit += "<option value=\"" + value + "\""
			+ (
				flag && ou.equals(value) ? " SELECTED" :
				!flag && key.equals("External Organizations") ? " SELECTED":
				 ""
			) 
			+ " >" + key +"</option>\n";
	}

	// generation of the SELECT for feedTypes  		
	String selType="";
	Syndication synd = new Syndication();
	String[] feedTypes = synd.getFeedFormatList("${baseUrl}/syndication/feed/${feedType}/publications/organization/${organizationId}");
	Arrays.sort(feedTypes);
	for (String type: feedTypes)
	{
		selType += "<option value=\"" + type + "\" "
			+ (type.equals("rss_2.0") ? "SELECTED" : "") 
		+ ">"+ type +"</option>\n";
	}
	
	
    String urlPrefix = (request.getProtocol().contains("HTTPS") ? "https" : "http") + "://" + request.getServerName() + (request.getServerPort() != 80 ? ":" + request.getServerPort() : "") + request.getContextPath() + "/feed";

	//The only rss_2.0, atom_1.0 link/rss are presented, too many for all feed types     
	String feedLinks = "" ; 
	for ( String feedType: new String[]{"rss_2.0", "atom_1.0"} )
		for( Map.Entry<String, String> entry: outm.entrySet() )
		{
			feedLinks += "<link href=\""
				+ urlPrefix + "/"
				+ feedType + "/publications/organization/"
				+ entry.getValue() + "\""
				+ " rel=\"alternate\" type=\"application/"
				+ (feedType.toLowerCase().contains("atom") ? "atom" : "rss")
				+ "+xml\" title=\""
				+ "eSciDoc Syndication Service | Organizational Unit | "
				+ entry.getKey()
				+ " | "
				+ feedType 
				+ "\" />\n";  
		}
		
    
%>
<html>
	<head>
		<link rel="stylesheet" type="text/css" href="./resources/escidoc-css/css/main.css" />
		<%= feedLinks %>
		<title>eSciDoc Syndication Service - Organizational Units</title>
		<script type="text/javascript" id="script">

			// This function is called to send the request.
			function submitItem()
			{
				var url = '<%= urlPrefix %>';
				
				url += '/' + document.form.feed_type.options[document.form.feed_type.selectedIndex].value;
				url += '/publications/organization';
				url += '/' + document.form.org_unit.options[document.form.org_unit.selectedIndex].value;
				
//				alert(url);
				
				document.getElementById('result').innerHTML = url;   
				
				location.href = url;
			}

		</script>
	</head>
	<body>
		<div id="col3">
			<div class="content">
		<h1 class="topSpace">
			eSciDoc Syndication Service
		</h1>
		<h2 class="topSpace">
			RSS/ATOM feeds for eSciDoc Organizational Units 
		</h2>
		<div class="topSpace">
		<form name="form" method="post" action="rest">
			<div class="editItemSingleCol">
				<label class="colLbl">Comments:</label><br/>
				<pre><%= synd.getFeeds().getComments() %></pre>
					
			</div>
			<div class="editItemSingleCol">
				<label class="colLbl">Choose a Feed Type:</label><br/>
				<select size="1" style="width:120px" name="feed_type"> 
					<%= selType %>
				</select>    
			</div>
			<div class="editItemSingleCol">
				<label class="colLbl">Choose an Organizational Unit:</label><br/>
				<select size="1" style="width:480px" name="org_unit"> 
					<%= selOrgUnit %>
				</select>    
			</div>
			<div class="editItemSingleCol">
				<label class="colLbl">The service root URL:</label><br/>
				<p><%= urlPrefix %></p>
			</div>
			<div class="editItemSingleCol">
				<label class="colLbl">Then submit it here:</label><br/>
				<input type="button" class="inlineButton" value="Submit" onclick="submitItem()" />
			</div>
			<div class="editItemSingleCol">
				<label class="colLbl">The complete URI:</label><br/>
				<pre id="result" style="background-color: #EEEEEE"></pre>
			</div>
		</form>
		</div>
			</div>
		</div>
	</body>
</html>