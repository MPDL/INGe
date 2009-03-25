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
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/
%>
<%
	//response.sendRedirect("services");
%>
<html>
	<head>
		<title>eSciDoc Validation Service</title>
	</head>
	<body bgcolor="white">
		<h1>
			eSciDoc Validation Service
		</h1>
		<p>
			The eSciDoc Validation Service checks XML metadata for syntactical and semantical correctness.
			This is done by a <a href="http://www.schematron.com/" target="_blank">Schematron</a> validation based on a given 
			validation template that is selected by the validation schema id and the given validation point <a href="http://colab.mpdl.mpg.de/mediawiki/ESciDoc_Services_ValidationService">[more]</a>.
		</p>
		<p>
			There are several ways to access the service. Below are links to the REST frontend and the SOAP frontend.
			There is also an EJB3 interface called <code>ejb.de.mpg.escidoc.services.validation.ItemValidating</code>.
		</p>
		<ul>
			<li>
				<a href="services">SOAP Interface</a>
				<a href="soap_sample.jsp">sample</a>
			</li>
			<li>
				<a href="rest">REST Interface</a>
				<a href="rest_sample.jsp">sample</a>
			</li>
		</ul>
	</body>
</html>