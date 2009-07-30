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

<html>
	<head>
		<title>eSciDoc DataAcquisition Service</title>
	</head>
	<body bgcolor="white">
		<h1>
			eSciDoc DataAcquisition Service
		</h1>
		<p>
			The eSciDoc DataAcquisition Service is a interface for harvesting data from external servers.
		</p>
		<p>
			<a href="http://colab.mpdl.mpg.de/mediawiki/ESciDoc_Services_DataAcquisitionHandler">[more]</a>
		</p>
		<ul>
			<b>The four steps to fetch data:</b>
				<p>
			  		1. Choose the presentation of the data </br>
					dataacquisition/view: 		Views the fetched data in the browser</br>
					dataacquisition/download:	The fetched data will be provided as a download</br>
				</p>
				<p>
			  		2. Call the unAPI service interface</br>
					dataacquisition/view/unapi</br>
					dataacquisition/download/unapi</br>
				</p>
				<p>
			  		3. Provide the identifier of the item you want to fetch</br>
					dataacquisition/view/unapi?id=escidoc:1234</br>
					dataacquisition/download/unapi?id=escidoc:1234</br>
				</p>
				<p>
			  		4. Provide the format you want the fetched item in</br>
					dataacquisition/view/unapi?id=escidoc:1234&format=bibtex</br>
					dataacquisition/download/unapi?id=escidoc:1234&format=bibtex</br>
				</p>
				
		</ul>
		<ul>
			<b>Supported Identifiers:</b>
				<p>
					1. A identifier from a supported source (explained in /dataacquisition).</br>
				</p>
				<p>
					2. A identifier = any URL (the eSciDoc DataAcquisition Service has no information about this source and can only try to call the given URL for the fetching request).</br>
					   The format has to be set to "url". The response will be a zip file of the fetched content. The view option for url identifiers is disabled.
				</p>
		</ul>


	</body>
</html>