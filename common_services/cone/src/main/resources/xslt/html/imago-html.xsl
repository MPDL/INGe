<?xml version="1.0" encoding="UTF-8"?>
<!--
 CDDL HEADER START

 The contents of this file are subject to the terms of the
 Common Development and Distribution License, Version 1.0 only
 (the "License"). You may not use this file except in compliance
 with the License.

 You can obtain a copy of the license at license/ESCIDOC.LICENSE
 or http://www.escidoc.de/license.
 See the License for the specific language governing permissions
 and limitations under the License.

 When distributing Covered Code, include this CDDL HEADER in each
 file and include the License file at license/ESCIDOC.LICENSE.
 If applicable, add the following below this CDDL HEADER, with the
 fields enclosed by brackets "[]" replaced with your own identifying
 information: Portions Copyright [yyyy] [name of copyright owner]

 CDDL HEADER END


 Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft
 für wissenschaftlich-technische Information mbH and Max-Planck-
 Gesellschaft zur Förderung der Wissenschaft e.V.
 All rights reserved. Use is subject to license terms.
-->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/">
	
	<xsl:output method="text" encoding="UTF-8" media-type="text/html"/>

	<xsl:template match="/">
		<xsl:apply-templates select="rdf:RDF/rdf:Description"/>
	</xsl:template>

	<xsl:template match="rdf:Description">
		<html xmlns="http://www.w3.org/1999/xhtml">
			<head>
				<title>CoNE - <xsl:value-of select="dc:title"/></title>
				<script type="text/javascript" language="JavaScript" src="/cone/js/jquery-1.2.6.min.js">;</script>
				<script type="text/javascript">
					requestString = '../query?dc:relation="imago/resource/<xsl:value-of select="substring-after(@rdf:about, '/resource/')"/>"&amp;f=json';
					<![CDATA[
					$(document).ready(function() {

						var ampEsc = '&amp;';
						var amp = ampEsc.substring(0,1);
						requestString = requestString.replace(/&amp;/g, '&');

						$.getJSON(requestString, printChildList);
					});
					
					function printChildList(childList)
					{
						alert('2');
						var content = '<ul>';
						for (var i = 0; i <xsl:text disable-output-escaping="yes"> childList.length)
						{
							content += '<li><a href="' + childList[i].id + '">' + childList[i].http_purl_org_dc_elements_1_1_title + '</a></li>'
						}
						if (content == '<ul>')
						{
							content = '';
							alert('leer');
						}
						else
						{
							content += '</ul>';
							$('.children').replaceWith(content);
						}
					}
					]]>
				</script>
			</head>
			<body>
				<h1>
					<xsl:apply-templates select="dc:relation"/>
					<xsl:value-of select="dc:title"/>
				</h1>
				<div class="children"></div>
			</body>
		</html>
	</xsl:template>
	
	<xsl:template match="dc:relation">
		<xsl:apply-templates select="rdf:Description/dc:relation"/>
		<a href="{rdf:Description/@rdf:about}">
			<xsl:value-of select="rdf:Description/dc:title"/>
		</a>
		<xsl:text> &gt; </xsl:text>
	</xsl:template>
	
</xsl:stylesheet>
