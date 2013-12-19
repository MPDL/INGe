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
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:escidocFunctions="urn:escidoc:functions" xmlns:kml="http://earth.google.com/kml/2.1" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/">
	
	<xsl:output method="html" encoding="UTF-8" media-type="text/html"/>

	<xsl:param name="subjectTagLocalName"/>
	<xsl:param name="subjectTagNamespace"/>
	<xsl:param name="subjectTagPrefix"/>
	
	
	<xsl:template match="/">
		
		<html xmlns="http://www.w3.org/1999/xhtml">
			<head>
				
				<title>CoNE - <xsl:value-of select="rdf:RDF/*[local-name()=$subjectTagLocalName and namespace-uri()=$subjectTagNamespace]/@rdf:about"/></title>
			</head>
			<body>
				<xsl:apply-templates select="rdf:RDF/*[local-name()=$subjectTagLocalName and namespace-uri()=$subjectTagNamespace]"/>
			</body>
		</html>
	</xsl:template>
	
	<xsl:template match="*[local-name()=$subjectTagLocalName and namespace-uri()=$subjectTagNamespace]" >
		<xsl:if test="exists(@rdf:about)">
			<h1>
				Resource: <xsl:value-of select="@rdf:about"/>
			</h1>
		</xsl:if>
		<ul>
			<xsl:apply-templates/>
		</ul>
	</xsl:template>
	
	<xsl:template match="*">
		<li>
			<b><xsl:value-of select="namespace-uri()"/><xsl:value-of select="local-name()"/></b>:
			<xsl:apply-templates select="*[local-name()=$subjectTagLocalName and namespace-uri()=$subjectTagNamespace]"/>
			<xsl:element name="a">
				<xsl:attribute name="href">
					<xsl:value-of select="@rdf:resource"/>
				</xsl:attribute>
				<xsl:value-of select="@rdf:resource"/>
			</xsl:element>
			<xsl:value-of select="text()"/>
		</li>
	</xsl:template>

	
</xsl:stylesheet>
