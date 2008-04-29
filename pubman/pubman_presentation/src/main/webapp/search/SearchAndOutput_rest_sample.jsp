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
* Gesellschaft zur Forderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/
%>
<%@page import="de.mpg.escidoc.services.framework.PropertyReader"%>
<html>
	<head>
		<link rel="stylesheet" type="text/css" href="../resources/escidoc-css/css/main.css" />
		<title>eSciDoc Search and Output Service</title>
		<script type="text/javascript" id="script">

			// This function is called to send the request.
			function submitItem()
			{
				var queryString =  '?cqlQuery=' + document.form.cqlQuery.value;
				
				queryString += '&exportFormat=' + document.form.exportFormat.options[document.form.exportFormat.selectedIndex].value;
				queryString += '&outputFormat=' + document.form.outputFormat.options[document.form.outputFormat.selectedIndex].value;
				queryString += '&language=' + document.form.language.options[document.form.language.selectedIndex].value;
				
				var req = document.form.url.value  + queryString;
				
				document.getElementById('result').innerHTML = req;   
				
				//window.open(req,'','height=100, width=100, toolbar=no, scrollbars=yes, resizable=yes');
				location.href = req;
			}
			
		</script>
	</head>
	<body bgcolor="white">
		<h1>
			eSciDoc Search And Output Service REST interface sample application
		</h1>
		<form name="form" method="post" action="rest">
			<p>
				This is the CQL search query:<br/>
				<input type="text" size="100" name="cqlQuery" value="escidoc.metadata=test"></input>
			</p>
			<p>
				<strong>Info:</strong> You can find
				<a href="<%= PropertyReader.getProperty("escidoc.framework_access.framework.url") %>/srw/search/escidoc_all?operation=explain" target="_blank" >here</a> 
				all indexes are allowed.
			</p>
			<p>
				Choose a language:<br/>
				<select size="1" name="language">
					<option value="all">all languages</option>
					<option value="en">en</option>
					<option value="de">de</option>
				</select>
			</p>
			<p>
				Choose a Export format:<br/>
				<select size="1" name="exportFormat" onchange="checkEndNote()">
					<option value="APA">APA</option>
					<option value="ENDNOTE">EndNote</option>
				</select>
			</p>
			<p>
				Choose a Output format:<br/>
				<select size="1" name="outputFormat">
					<option value="pdf">PDF</option>
					<option value="html">HTML</option>
					<option value="rtf">RTF</option>
					<option value="odt">ODT</option>
					<option value="snippet">SNIPPET</option>
				</select>
			</p>
			<p>
				This is the address where to send it:<br/>
				<input type="text" name="url" size="100" value="<%= (request.getProtocol().contains("HTTPS") ? "https" : "http") %>://<%= request.getServerName() %><%= (request.getServerPort() != 80 ? ":" + request.getServerPort() : "") %><%= request.getContextPath() %>/SearchAndOutput"/>
			</p>
			<p>
				Then submit it here:<br/>
				<input type="button" value="Submit" onclick="submitItem()"/>
			</p>
			<p>
				The complete URI:<br/>
				<pre id="result" style="background-color: #EEEEEE"></pre>
			</p>
		</form>
	</body>
</html>