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
 * Recent Releases of Repository page.
 * 
 * @author Vlad Makarenko  (initial creation) 
 * @author $Author$ (last modification)
 * $Revision$
 * $LastChangedDate$ 
 */

%>
<%@page import="java.util.Arrays,de.mpg.escidoc.services.syndication.Syndication,de.mpg.escidoc.services.framework.PropertyReader"%>
<%

	Syndication synd = new Syndication();
	String urlTempl = "%s%s/releases";
	
    String urlPrefix = (request.getProtocol().contains("HTTPS") ? "https" : "http") + "://" + request.getServerName() + (request.getServerPort() != 80 ? ":" + request.getServerPort() : "") + request.getContextPath() + "/feed/";

	String[] feedTypes = synd.getFeedFormatList( String.format(urlTempl, urlPrefix, "${feedType}") );
	Arrays.sort(feedTypes);

	String feedLinks = "";
	for ( String feedType: feedTypes )
	{
		feedLinks +=
			synd.getFeedRelLink(
				String.format(urlTempl, urlPrefix, feedType)
			)
			+ "\n";
	}    
	
	String feedImage = "<img src=\"./resources/Live_bookmarks.png\" />";
%>
<html>
	<head>
		<link rel="stylesheet" type="text/css" href="./resources/escidoc-css/css/main.css" />
		<%= feedLinks %>
		<title>eSciDoc Syndication Service - Recent Releases</title>
	</head>
	<body>
		<div id="col3">
			<div class="content">
				<h1 class="topSpace">
					eSciDoc Syndication Service
				</h1>
				<h2 class="topSpace">
					RSS/ATOM feeds for eSciDoc Recent Releases
				</h2>				
				<div class="topSpace">
					<div class="editItemSingleCol">
						<label class="colLbl">Comments:</label><br/>
						<pre><%= synd.getFeeds().getComments() %></pre>
					</div>
					<div class="editItemSingleCol topSpace">
						<label class="colLbl">The following feeds are available:</label><br/>
						<ul>
							<li>
								<a href="<%= urlPrefix %>rss_0.9/releases">RSS, version 0.9 <%= feedImage %></a>
							</li>
							<li>
								<a href="<%= urlPrefix %>rss_0.91N/releases">RSS, version 0.91N <%= feedImage %></a>  
							</li>
							<li>
								<a href="<%= urlPrefix %>rss_0.91U/releases">RSS, version 0.91U <%= feedImage %></a>  
							</li>
							<li>
								<a href="<%= urlPrefix %>rss_0.92/releases">RSS, version 0.92 <%= feedImage %></a>  
							</li>
							<li>
								<a href="<%= urlPrefix %>rss_0.93/releases">RSS, version 0.93 <%= feedImage %></a>  
							</li>
							<li>
								<a href="<%= urlPrefix %>rss_0.94/releases">RSS, version 0.94 <%= feedImage %></a>  
							</li>
							<li>
								<a href="<%= urlPrefix %>rss_1.0/releases">RSS, version 1.0 <%= feedImage %></a>  
							</li>
							<li>
								<a href="<%= urlPrefix %>rss_2.0/releases">RSS, version 2.0 <%= feedImage %></a>  
							</li>
							<li>
								<a href="<%= urlPrefix %>atom_0.3/releases">Atom, version 0.3 <%= feedImage %></a>  
							</li>
							<li>
								<a href="<%= urlPrefix %>atom_1.0/releases">Atom, version 1.0 <%= feedImage %></a>  
							</li>
						</ul>
					</div>
				</div>
			</div>
		</div>
	</body>	
</html>