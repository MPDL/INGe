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
		<title>eSciDoc Validation Service</title>
	</head>
	<body bgcolor="white">
		<h1>
			eSciDoc Validation Service REST Interface
		</h1>
		<p>
			The following methods are available here:
			<ul>
				<li>
					<a href="/validation_presentation/rest/validateItemXml">validateItemXml (returns ValidationReportXml)</a>:
					This service expects a POST request containing the item XML that should be validated.<br/>
					Optionally, a validation point can be passed as request parameter in the form<br/>
					<code>?validation-point=&lt;validationPoint&gt;</code><br/>
					It returns an XML validation report.
				</li>
				<li>
					<a href="/validation_presentation/rest/refreshValidationSchemaCache">refreshValidationSchemaCache</a>:
					This service expects a GET request without any data provided.<br/>
					It returns nothing.
				</li>
			</ul>
		</p>
		<p>
			Find <a href="/validation_presentation/rest_sample.jsp">here</a> a sample application using AJAX to call the service.
		</p>
	</body>
</html>