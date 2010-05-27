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
		to the JasperReport Data Source 
		Author: Vlad Makarenko (initial creation) $Author: $ (last changed)
		$Revision: $ $LastChangedDate: $
	-->
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:fn="http://www.w3.org/2005/xpath-functions" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/"
	xmlns:dcmitype="http://purl.org/dc/dcmitype/" xmlns:escidocItem="http://www.escidoc.de/schemas/item/0.8"
	xmlns:prop="http://escidoc.de/core/01/properties/"
	xmlns:jfunc="java:de.mpg.escidoc.services.citationmanager.utils.XsltHelper"
	>
	<xsl:output method="xml" encoding="UTF-8" indent="yes"
		cdata-section-elements="dcterms:bibliographicCitation" />


	<xsl:template match="/*">
		<xsl:call-template name="createJasperDS" />
	</xsl:template>
	<xsl:template name="createJasperDS">
		<snippets>
			<xsl:for-each
				select="//prop:content-model-specific/dcterms:bibliographicCitation">
				<xsl:element name="dcterms:bibliographicCitation">
					<xsl:value-of select="jfunc:convertSnippetToJasperStyledText(.)"/>					
				</xsl:element>

			</xsl:for-each>
		</snippets>
	</xsl:template>

		
<!--		<xsl:template match="node() | @*">-->
<!--	        <xsl:copy>-->
<!--	            <xsl:apply-templates select="@* | node ()"/>-->
<!--	        </xsl:copy>-->
<!--	    </xsl:template>-->
<!--	    		-->
<!--		<xsl:template match="prop:content-model-specific">-->
<!--		     <xsl:element name="{name(.)}">-->
<!--		         <xsl:copy-of select="child::node()[name(.)!='dcterms:bibliographicCitation']"/>-->
<!--		         <xsl:element name="dcterms:bibliographicCitation">-->
<!--		         	<xsl:value-of select="jfunc:convertSnippetToJasperStyledText(dcterms:bibliographicCitation/text())"/>-->
<!--		         </xsl:element>-->
<!--		      </xsl:element>-->
<!--		</xsl:template>-->
	
</xsl:stylesheet>
