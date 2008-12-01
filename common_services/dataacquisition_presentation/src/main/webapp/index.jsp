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
		<title>eSciDoc Import Service</title>
	</head>
	<body bgcolor="white">
		<h1>
			eSciDoc Import Service
		</h1>
		<p>
			The eSciDoc Import Service is a interface for harvesting data from external servers.
		</p>
		<ul>
			<b>The service provides three operations:</b>
				<li>
			  		/unapi Gives back informations about all sources the Importhandler can fetch from.
				</li>
				<li>
			  		/unapi/unapi Gives back informations about all formats of the eSciDoc source (which is default).
				</li>
				<li>
    				/unapi/unapi?id=IDENTIFIER Gives back a list of all formats for this identifier.
				</li>
				<li>
    				/unapi/unapi?id=IDENTIFIER&format=FORMAT Fetches the given format for this identifier.
				</li>
		</ul>
		<ul>
			<b>The service will support two kinds of identifiers:</b>
				<li>
					identifier from a supported source (explained in /unapi). </br>
        			The format to fetch is defined in the &format parameter. All data will return in the fetched format
				</li>
				<li>
					identifier = any URL (the eSciDoc Import Service has no information about this source and can only try to call the given URL for the fetching request).</br>
					In case of fetching from an unknown source via providing an URL the identifier is the url. 
        			As the eSciDoc Import Service can not fetch a specific format from this url the &format parameter has to be "url" to indicate that the fetching request is for an unknown source.
				</li>
		</ul>
	</body>
</html>