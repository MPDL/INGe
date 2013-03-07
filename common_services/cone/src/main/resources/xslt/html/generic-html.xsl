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

	<xsl:template match="/">
		<html xmlns="http://www.w3.org/1999/xhtml">
			<head>
				<title>CoNE - <xsl:value-of select="rdf:RDF/rdf:Description/@rdf:about"/></title>
			</head>
			<body>
				<xsl:apply-templates select="rdf:RDF/rdf:Description"/>
			</body>
		</html>
	</xsl:template>
	
	<xsl:template match="rdf:Description">
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
			<xsl:apply-templates select="rdf:Description"/>
			<xsl:value-of select="text()"/>
		</li>
	</xsl:template>
	
	<xsl:template match="kml:coordinates">
		<li>
			<iframe width="300" height="300" frameborder="0" scrolling="no" marginheight="0" marginwidth="0" src="http://maps.google.de/maps?hl=de&amp;ie=UTF8&amp;t=h&amp;ll={.}&amp;spn=3.967501,6.591797&amp;z=6&amp;output=embed"></iframe>
			<ul>
				<li>
					<xsl:text>Latitude: </xsl:text>
					<xsl:choose>
						<xsl:when test="starts-with(substring-after(., ','), '-')">
							<xsl:value-of select="escidocFunctions:decimal2degree(substring-after(., ',-'))"/>
							<xsl:text> W</xsl:text>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="escidocFunctions:decimal2degree(substring-after(., ','))"/>
							<xsl:text> E</xsl:text>
						</xsl:otherwise>
					</xsl:choose>
				</li>
				<li>
					<xsl:text>Longitude: </xsl:text>
					<xsl:choose>
						<xsl:when test="starts-with(., '-')">
							<xsl:value-of select="escidocFunctions:decimal2degree(substring-after(substring-before(., ','), '-'))"/>
							<xsl:text> S</xsl:text>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="escidocFunctions:decimal2degree(substring-before(., ','))"/>
							<xsl:text> N</xsl:text>
						</xsl:otherwise>
					</xsl:choose>
				</li>
			</ul>
		</li>
	</xsl:template>
	
	<xsl:function name="escidocFunctions:decimal2degree">
		<xsl:param name="value"/>
		
		<xsl:choose>
			<xsl:when test="not(contains($value, '.'))">
				<xsl:value-of select="$value"/>
				<xsl:text>°</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="substring-before($value, '.')"/>
				<xsl:text>° </xsl:text>
				<xsl:variable name="minutes" select="round(concat('.', substring-after($value, '.')) cast as xs:float * 3600) div 60"/>
				<xsl:choose>
					<xsl:when test="not(contains($minutes cast as xs:string, '.'))">
						<xsl:value-of select="$minutes"/>
						<xsl:text>'</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="substring-before($minutes cast as xs:string, '.')"/>
						<xsl:text>' </xsl:text>
						<xsl:variable name="seconds" select="round(concat('.', substring-after($minutes cast as xs:string, '.')) cast as xs:float * 60)"/>
						<xsl:value-of select="$seconds"/>
						<xsl:text>''</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
	
</xsl:stylesheet>
