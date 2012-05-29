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
<xsl:stylesheet version="2.0"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:prop="http://escidoc.de/core/01/properties/"
	xmlns:dcterms="http://purl.org/dc/terms/"
	xmlns:xlink="http://www.w3.org/1999/xlink"
	xmlns:escidocItem="http://www.escidoc.de/schemas/item/0.8"
>
	
	<xsl:output method="xml" encoding="UTF-8" indent="yes" cdata-section-elements="dcterms:bibliographicCitation"/>

<!--	<xsl:param name="v1" as="item()*"/>-->
	<xsl:variable name="v1" select="document('v1')/*"/>
	
	<xsl:template match="prop:content-model-specific">
	
		
		<xsl:variable name="id" select="../../@objid" as="xs:string"/>
		<xsl:variable name="cit" select="$v1//escidocItem:item[@objid=$id]/escidocItem:properties/prop:content-model-specific/dcterms:bibliographicCitation"/>
	     <xsl:element name="{name(.)}">
	         <xsl:copy-of select="*[name(.)!='dcterms:bibliographicCitation']"/>
	         <xsl:element name="dcterms:bibliographicCitation">
	         	<xsl:value-of select="
	         		if ($id='') 
	         		then error(
						QName('http://www.escidoc.de/transformation', 'err:NoBindIdInSourceDocument'), 
						'No binding item id found in source element'
					)
					else if (not($cit)) 
	         		then error(
						QName('http://www.escidoc.de/transformation', 'err:NoCitFound'), 
						concat ('No citation found in document for id: ', $id)
					)
					else $cit
	         	"/>
	         </xsl:element>
	      </xsl:element>
	      <xsl:apply-templates />
	</xsl:template>
	

	<xsl:template match="node() | @*">
		<xsl:copy>
			<xsl:apply-templates select="node() | @*"/>
		</xsl:copy>
	</xsl:template>
		
</xsl:stylesheet>
