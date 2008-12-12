<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
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


 Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
 für wissenschaftlich-technische Information mbH and Max-Planck-
 Gesellschaft zur Förderung der Wissenschaft e.V.
 All rights reserved. Use is subject to license terms.
-->

<xsl:stylesheet xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema"  xmlns:escidocAdminDescriptor="http://www.escidoc.de/schemas/admindescriptor/0.3" xmlns:escidocContext="http://www.escidoc.de/schemas/context/0.3" xmlns:escidocContextList="http://www.escidoc.de/schemas/contextlist/0.3" xmlns:escidocComponents="http://www.escidoc.de/schemas/components/0.3" xmlns:escidocItem="http://www.escidoc.de/schemas/item/0.3" xmlns:escidocItemList="http://www.escidoc.de/schemas/itemlist/0.3" xmlns:escidocMetadataRecords="http://www.escidoc.de/schemas/metadatarecords/0.3" xmlns:escidocRelations="http://www.escidoc.de/schemas/relations/0.3" xmlns:escidocResources="http://www.escidoc.de/schemas/resources/0.2" xmlns:escidocSearchResult="http://www.escidoc.de/schemas/searchresult/0.3" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.escidoc.de/schemas/item/0.3 soap/item/0.3/item.xsd" version="2.0"> 

	<xsl:param name="model" select="XXX"/>

	<xsl:output indent="no" method="text"/>

	<xsl:template match="/">
	
		<xsl:for-each select="/rdf:RDF/rdf:Description">
		
			<xsl:variable name="subject" select="@rdf:about"/>
			<xsl:variable name="pos" select="position()"/>
			<xsl:variable name="newSubject">
				<xsl:choose>
					<xsl:when test="/rdf:RDF/rdf:Description[@rdf:about = $subject and position() &lt; $pos]">
						<xsl:value-of select="$subject"/>_<xsl:value-of select="count(/rdf:RDF/rdf:Description[@rdf:about = $subject and position() &lt; $pos])"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$subject"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<xsl:variable name="pos" select="position()"/>
			
			<xsl:for-each select="*">
				<xsl:variable name="predicate"><xsl:value-of select="namespace-uri()"/><xsl:value-of select="local-name()"/></xsl:variable>
				<xsl:variable name="object"><xsl:value-of select="."/></xsl:variable>
				<xsl:variable name="lang" select="@xml:lang"/>
				
				<xsl:if test="not(/rdf:RDF/rdf:Description[position() &lt; $pos and @rdf:about = $newSubject]/*[concat(namespace-uri(), local-name()) = $predicate and . = $object and not(@xml:lang != $lang)])">INSERT INTO triples VALUES ('<xsl:value-of select="$newSubject"/>', '<xsl:value-of select="$predicate"/>', '<xsl:call-template name="escape"><xsl:with-param name="value" select="$object"/></xsl:call-template>', <xsl:choose><xsl:when test="not($lang)">null</xsl:when><xsl:otherwise>'<xsl:value-of select="$lang"/>'</xsl:otherwise></xsl:choose>, '<xsl:value-of select="$model"/>');
</xsl:if>
			</xsl:for-each>
			
			<xsl:choose>
				<xsl:when test="$model = 'journals'">INSERT INTO results VALUES ('<xsl:value-of select="$newSubject"/>', '<xsl:call-template name="escape"><xsl:with-param name="value"><xsl:value-of select="dc:title"/><xsl:if test="dc:publisher or dcterms:publisher">; <xsl:value-of select="dc:publisher"/><xsl:if test="dc:publisher and dcterms:publisher">, </xsl:if><xsl:value-of select="dcterms:publisher"/></xsl:if></xsl:with-param></xsl:call-template>');
</xsl:when>
				<xsl:when test="$model = 'languages'">INSERT INTO results VALUES ('<xsl:value-of select="$newSubject"/>', '<xsl:call-template name="escape"><xsl:with-param name="value"><xsl:value-of select="dc:title"/></xsl:with-param></xsl:call-template>', '<xsl:value-of select="dc:title/@xml:lang"/>');
</xsl:when>
			</xsl:choose>
		
		</xsl:for-each>
	
	</xsl:template>
	
	<xsl:template name="escape">
		<xsl:param name="value"/>
		
		<xsl:variable name="quot">'</xsl:variable>
		<xsl:choose>
			<xsl:when test="contains($value, $quot)">
				<xsl:value-of select="substring-before($value, $quot)"/>''<xsl:call-template name="escape"><xsl:with-param name="value" select="substring-after($value, $quot)"/></xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$value"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
</xsl:stylesheet>