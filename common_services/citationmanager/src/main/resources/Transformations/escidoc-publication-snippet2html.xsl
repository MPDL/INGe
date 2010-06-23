<?xml version="1.0" encoding="UTF-8"?>
	<!--
		CDDL HEADER START The contents of this file are subject to the terms
		of the Common Development and Distribution License, Version 1.0 only
		(the "License"). You may not use this file except in compliance with
		the License. You can obtain a copy of the license at
		license/ESCIDOC.LICENSE or http://www.escidoc.de/license. See the
		License for the specific language governing permissions and
		limitations under the License. When distributing Covered Code, include
		this CDDL HEADER in each file and include the License file at
		license/ESCIDOC.LICENSE. If applicable, add the following below this
		CDDL HEADER, with the fields enclosed by brackets "[]" replaced with
		your own identifying information: Portions Copyright [yyyy] [name of
		copyright owner] CDDL HEADER END Copyright 2006-2010
		Fachinformationszentrum Karlsruhe Gesellschaft für
		wissenschaftlich-technische Information mbH and Max-Planck-
		Gesellschaft zur Förderung der Wissenschaft e.V. All rights reserved.
		Use is subject to license terms.
	-->
	<!--
		Transformations from eSciDoc Citation Style Snippet format 
		into to the plain HTML snippets 
		Author: Vlad Makarenko (initial creation) $Author: $ (last changed)
		$Revision: $ $LastChangedDate: $
	-->
	
	
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:fn="http://www.w3.org/2005/xpath-functions" 
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:xlink="http://www.w3.org/1999/xlink"
	xmlns:func="http://www.escidoc.de/citationstyle/functions" 
	xmlns:jfunc="java:de.mpg.escidoc.services.citationmanager.utils.XsltHelper"
	xmlns:dc="http://purl.org/dc/elements/1.1/"
	xmlns:dcterms="http://purl.org/dc/terms/"
	xmlns:prop="${xsd.soap.common.prop}"
	xmlns:escidocComponents="${xsd.soap.item.components}"
	xmlns:escidocItem="${xsd.soap.item.item}"
	>
	<xsl:output method="html" encoding="UTF-8" indent="yes"/>
	
	<xsl:param name="pubman_instance"/>
	
	<xsl:param name="html_linked" select="false()"/>

	<xsl:template match="/">
		<xsl:element name="html">
			<xsl:element name="head">
				<xsl:element name="meta">
					<xsl:attribute name="http-equiv" select="'Content-Type'"/>
					<xsl:attribute name="content" select="'text/html; charset=utf-8'"/>
				</xsl:element>
			</xsl:element>
			<xsl:element name="body">
				<xsl:element name="div">
					<xsl:attribute name="class" select="'Pubman'"/>
					<xsl:apply-templates select="//dcterms:bibliographicCitation"/>
				</xsl:element>
			</xsl:element>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="dcterms:bibliographicCitation">
		<xsl:element name="p">
			<xsl:value-of select="jfunc:convertSnippetToHtml(.)" disable-output-escaping="yes"/>
			<xsl:variable name="item" select="../../.."/>			
			<xsl:if test="$html_linked">
				<xsl:text disable-output-escaping="yes">&lt;br&gt;</xsl:text>
				 <xsl:element name="a">
					<xsl:attribute name="href" select="
						concat(
							$pubman_instance,
		    				'/item/', 
		    				$item/escidocItem:properties/prop:version/@objid
			    			)"
			    		/>
					<xsl:attribute name="class" select="'Item'"/>[Item] </xsl:element>
				<xsl:variable name="con" select="$item/escidocComponents:components/escidocComponents:component/escidocComponents:content"/>				
				<xsl:for-each select="$con[@storage='internal-managed']">
					<xsl:element name="a"><xsl:attribute name="href" select="@xlink:href"/><xsl:attribute name="class" select="'File'"/><xsl:value-of select="concat('[File ', position(), ']')"/></xsl:element>
				</xsl:for-each>
				<xsl:for-each select="$con[@storage!='internal-managed']">
					<xsl:element name="a"><xsl:attribute name="href" select="@xlink:href"/><xsl:attribute name="class" select="'Locator'"/><xsl:value-of select="concat('[Locator ', position(), ']')"/></xsl:element>
				</xsl:for-each>
			</xsl:if>
		</xsl:element>
	</xsl:template>
	
</xsl:stylesheet>
