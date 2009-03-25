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
* Copyright 2006-2009 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Forderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/
%>
<%@page import="de.mpg.escidoc.services.common.util.ResourceUtil"%>
<html>
	<head>
		<title>eSciDoc Validation Service</title>
		<script type="text/javascript" id="script">

			// The Object doing the request to the service.
			var xmlhttp;
		
			// This function is called to send the request.
			function submitItem()
			{
			
				if (navigator.appName.indexOf('MSIE') >= 0)
				{
					xmlhttp=new ActiveXObject("Microsoft.XMLHTTP")
				}
				else
				{
					xmlhttp=new XMLHttpRequest();
				}
				if (xmlhttp!=null)
				{
				
					var queryString = '';
					if (document.form.validationPoint.selectedIndex > 0)
					{
						queryString = '?validation-point=' + document.form.validationPoint.options[document.form.validationPoint.selectedIndex].value;
						if (document.form.validationSchema.selectedIndex > 0)
						{
							queryString += '&validation-schema=' + document.form.validationSchema.options[document.form.validationSchema.selectedIndex].value;
						}
					}
				
					xmlhttp.onreadystatechange = requestDone;
					xmlhttp.open("POST", document.form.url.value + document.form.method.value + queryString, true);
					xmlhttp.send(document.form.content.value);
				}
				else
				{
					alert('Sorry, seems like your browser does not support XMLHttp!');
				}
			}
			
			// This function is called when there is a response from the service.
			function requestDone()
			{
				if (xmlhttp.readyState == 4)
				{
					// if "OK"
					if (xmlhttp.status==200)
					{
						document.getElementById('result').innerHTML = formatXml(xmlhttp.responseText);
					}
					else
					{
						alert("Problem retrieving XML data:\n" +
							"Status: " + xmlhttp.status + "\n" +
							xmlhttp.responseText);
					}
				}
			}
			
			// This function tidies the response XML a little bit. Could be improved.
			function formatXml(xml)
			{
				
				xml = xml.replace(/</g, '<br/{gt}<font color="blue"{gt}&lt;</font{gt}');
				xml = xml.replace(/>/g, '<font color="blue">&gt;</font>');
				xml = xml.replace(/\{gt\}/g, '>');
				
				return xml;
			}
		
			// This function displays this script block in a new browser window.
			function showScript()
			{
				var w = window.open('', 'showScript');
				w.document.open();
				w.document.write('<html><body><pre>');
				w.document.write(document.getElementById('script').innerHTML.replace(/</g, '&lt;'));
				w.document.write('</pre></body></html>');
				w.document.close();
				w.focus();
			}


			// This function decides which method should be called
			function selectMethod(element)
			{
				if (element.options[element.selectedIndex].value == '')
				{
					document.form.method.value = 'validateItemXml';
				}
				else
				{
					if (document.form.validationPoint.selectedIndex == 0)
					{
						alert('Please choose a validation point first.');
						element.selectedIndex = 0;
					}
					else
					{
						document.form.method.value = 'validateItemXmlBySchema';
					}
				}
			}

			// This function resets schema and method when validation point is set to 'none'.
			function checkSchema(element)
			{
				if (element.selectedIndex == 0)
				{
					document.form.method.value = 'validateItemXml';
					document.form.validationSchema.selectedIndex = 0;
				}
			}
			
		</script>
	</head>
	<body bgcolor="white">
		<h1>
			eSciDoc Validation Service REST interface sample application
		</h1>
		<form name="form" method="post" action="rest">
			<p>
				Paste your item XML here:<br/>
				<textarea name="content" rows="10" cols="100"><%= ResourceUtil.getResourceAsString("example/invalidItem.xml") %></textarea>
			</p>
			<p>
				Choose a validation point:<br/>
				<select size="1" name="validationPoint" onchange="checkSchema(this)">
					<option value="">None (Default)</option>
					<option value="default">Default</option>
					<option value="submit_item">Submit</option>
					<option value="accept_item">Accept</option>
				</select>
			</p>
			<p>
				Choose a validation schema:<br/>
				<select size="1" name="validationSchema" onchange="selectMethod(this)">
					<option value="">None (Pick the validation schema from the context provided with the item)</option>
					<option value="simple">"simple": Checks only the very basics (Title, Creator)</option>
					<option value="publication">"publication": default PubMan validation rules</option>
					<option value="greymaterial">"greymaterial": Grey material of the MPDL</option>
					<option value="greymaterialexternal">"greymaterialexternal": Grey material validation rules without MPDL relations</option>
				</select>
			</p>
			<p>
				This is the address where to send it:<br/>
				<input type="text" name="url" size="100" value="<%= (request.getProtocol().contains("HTTPS") ? "https" : "http") %>://<%= request.getServerName() %><%= (request.getServerPort() != 80 ? ":" + request.getServerPort() : "") %><%= request.getContextPath() %>/rest/"/>
			</p>
			<p>
				This is the method to be invoked:<br/>
				<input type="text" name="method" size="100" value="validateItemXml" readonly="true"/>
			</p>
			<p>
				Then submit it here:<br/>
				<input type="button" value="Submit" onclick="submitItem()"/>
			</p>
			<p>
				Click <a href="#" onclick="showScript()">here</a> for the JS script now being executed.<br/>
			</p>
			<p>
				The result:<br/>
				<pre id="result" style="background-color: #EEEEEE"></pre>
			</p>
		</form>
	</body>
</html>