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
		<title>SWORD Depositing Service</title>
	</head>
	<body bgcolor="white">
		<h1>
			SWORD Depositing Service
		</h1>
		<p>
			The SWORD Depositing Service is an interface for depositing items from external servers into the eSciDoc repository.</br>
		</p>
		<p>
			<a href="http://colab.mpdl.mpg.de/mediawiki/PubMan_Sword">[more]</a>
		</p>
		<ul>
			<h2>Retrieve the Servicedocument:</h2>
				<p>
			  		The SWORD servicedocument ( /servicedocument) describes the collections a user can deposit to. (User credentials have to be provided)
				</p>
				<p>
					Attributes which can be set in the http header: <br/>
  					<b>sword:verbose</b> - Sets the verbose output</br>
  					<b>sword:noOp</b>    - Sets the test deposit option (item will be processed but not saved)</br>
				</p>
				<p style="background-color:E0EEEE">
					<b>Exemplary  Servicedocument:</b> <br/><br/>
  					&#60;service&#62; <br/>
					&#160;	&#60;sword:level&#62;0&#60;/sword:level&#62; <br/>
					&#160;	&#60;sword:verbose&#62;false&#60;/sword:verbose&#62; <br/>
					&#160;	&#60;sword:noOp&#62;true&#60;/sword:noOp&#62; <br/>

					&#160;&#160;	&#60;workspace&#62; <br/>
					&#160;&#160;&#160;		&#60;atom:title type="text"&#62;PubMan SWORD Workspace&#60;/atom:title&#62; <br/>
					&#160;&#160;&#160;&#160;		&#60;collection href="escidoc:133823"&#62; <br/>
					&#160;&#160;&#160;&#160;			&#60;atom:title type="text"&#62;Test SWORD Deposit&#60;/atom:title&#62; <br/>
					&#160;&#160;&#160;&#160;			&#60;accept&#62;application/zip&#60;/accept&#62; <br/>
					&#160;&#160;&#160;&#160;			&#60;sword:collectionPolicy&#62;Simple work flow&#60;/sword:collectionPolicy&#62; <br/>
					&#160;&#160;&#160;&#160;			&#60;dcterms:abstract&#62;This is a test collection for SWORD depositing. The policy is: no policy at this time.&#60;/dcterms:abstract&#62; <br/>
					&#160;&#160;&#160;&#160;			&#60;sword:mediation&#62;false&#60;/sword:mediation&#62; <br/>
					&#160;&#160;&#160;&#160;			&#60;sword:treatment&#62;Zip archives recognised as content packages are opened and the individual files contained in them are stored.&#60;/sword:treatment&#62; <br/>
					&#160;&#160;&#160;&#160;			&#60;sword:acceptPackaging &#62;http://www.tei-c.org/ns/1.0&#60;/sword:acceptPackaging &#62; <br/>
					&#160;&#160;&#160;&#160;			&#60;sword:acceptPackaging &#62;http://purl.org/escidoc/metadata/schemas/0.1/publication &#60;/sword:acceptPackaging &#62; <br/>
					&#160;&#160;&#160;&#160;			&#60;sword:acceptPackaging &#62;bibTex&#60;/sword:acceptPackaging &#62; <br/>
					&#160;&#160;&#160;&#160;			&#60;sword:acceptPackaging &#62;EndNote&#60;/sword:acceptPackaging &#62; <br/>
					&#160;&#160;&#160;		&#60;/collection&#62; <br/>
					&#160;&#160;	&#60;/workspace&#62; <br/>
					&#160;	&#60;/service&#62; <br/>
				</p>
				<p>
					You already have an eSciDoc account for PubMan? Check out your Servicedocument <a href="/pubman/faces/sword-app/servicedocument" target="_blank"> here.</a>
				</p>
				
		</ul>
		<ul>
			<h2>Deposit data to PubMan:</h2>
			<p>
			  	One can deposit ( /deposit?collection=ID) publication data to PubMan by calling the deposit servlet. (User credentials have to be provided)
			</p>
			<p>
			  	For detailed information about parameters, error codes etc. please check out the <a href="http://colab.mpdl.mpg.de/mediawiki/PubMan_Sword">PubMan SWORD description in CoLab.</a>
			</p>
		</ul>


	</body>
</html>