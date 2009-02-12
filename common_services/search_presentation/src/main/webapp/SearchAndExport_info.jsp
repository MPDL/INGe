<%
/*
*
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
* Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/
%>
<html>
	<head>
		<link rel="stylesheet" type="text/css" href="../resources/escidoc-css/css/main.css" />
		<link rel="search" href="opensearch_apa_html_all.xml" type="application/opensearchdescription+xml" title="eSciDoc OS APA" />
		<link rel="search" href="opensearch_endnote_all.xml" type="application/opensearchdescription+xml" title="eSciDoc OS EndNote" />
		<title>eSciDoc SearchAndExport Service</title>
	</head>
	<body>
		<div id="col3">
			<div class="content">
		<h1 class="topSpace">
			eSciDoc SearchAndExport Service REST Interface
		</h1>
		<div class="topSpace">
		<div class="editItemSingleCol topSpace"> 
			<span>This service expects a GET request containing following parameters:</span><br/>
		<ul>
			<li>
				<strong>cqlQuery</strong> defines CQL search request (required).  
			</li>
			<li>
				<strong>exportFormat</strong> can be <i>APA</i>, <!-- <i>AJP,</i> -->  <i>BIBTEX</i>, <i>ENDNOTE</i> or <i>eSciDoc XML</i>. (Default: <i>ENDNOTE</i>)  
			</li>
			<li>
				<strong>language</strong> can be <i>all</i>, <i>en</i> or <i>de</i>. (Default: <i>all</i>)  
			</li>
			<li>
				<strong>outputFormat</strong> is only relevant for <i>APA</i> <!-- and <i>AJP</i> -->  exportFormat and can be <i>pdf, html, rtf, odt, snippet</i>. (Default: <i>pdf</i>). 
				<i>ENDNOTE</i> will be always returned as plain text.  
				 
			</li>
		</ul>
		</div>
		<div class="editItemSingleCol"> 
			The service returns results of the search as attached file in the defined format.<br/>
		
			Find <a href="SearchAndExport_rest_sample.jsp">here</a> a sample application to call the service with JavaScript.
		</div>
		<h2>
			OpenSearch indexes:
		</h2>
		<ul>
			<li>
				exportFormat = APA, outputFormat = html, language = all. <a href="#" onclick="javascript:window.external.AddSearchProvider( location.protocol + '//' + location.host  + '/search/opensearch_apa.xml');">Add</a>
			</li>
			<li>
				exportFormat = EndNote, language = all. <a href="#" onclick="javascript:window.external.AddSearchProvider(location.protocol + '//' + location.host  + '/search/opensearch_endnote_all.xml');">Add</a>
			</li>
		  </ul>
		  </div>
		  </div>
		  </div>
	</body>
</html>