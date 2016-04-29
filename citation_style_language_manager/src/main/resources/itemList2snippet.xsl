<?xml version="1.0" encoding="UTF-8"?>
<!--
 CDDL HEADER START

 The contents of this file are subject to the terms of the
 Common Development and Distribution License, Version 1.0 only
 (the "License"). You may not use this file except in compliance
 with the License.

 You can obtain a copy of the license at license/ESCIDOC.LICENSE
 or http://www.escidoc.org/license.
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
<!-- 
	Transformation from itemList to snippet format
	Author: walter (initial creation) 
	$Author: walter $ (last changed)
	$Revision: 5346 $ 
	$LastChangedDate:  $
-->
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:fn="http://www.w3.org/2005/xpath-functions"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	
	xmlns:dcterms="${xsd.metadata.dcterms}"
	xmlns:escidocItem="${xsd.soap.item.item}" 
	xmlns:escidocItemList="${xsd.soap.item.itemlist}"
	xmlns:prop="${xsd.soap.common.prop}"
>
	
	<xsl:output method="xml" encoding="UTF-8" indent="yes"/>
	
	<xsl:param name="citations" as="xs:string*"/>
	
	<xsl:template match="/">
		<xsl:apply-templates/>
	</xsl:template>
	
	<!-- IdentityTransform -->
	<xsl:template match="@* | node()">
		<xsl:copy>
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates select="@* | node()" />
		</xsl:copy>
	</xsl:template>
	
	<xsl:template match="escidocItem:item/escidocItem:properties/prop:content-model-specific">  
		<xsl:copy>
			<xsl:copy-of select="@*"/>
            <xsl:apply-templates select="@*|node()"/>
            <!--Add bibliographicCitation-->
            <xsl:variable name="item-position" select="count(../../preceding-sibling::escidocItem:item) + 1"/>
            <dcterms:bibliographicCitation><xsl:text disable-output-escaping="yes">&lt;![CDATA[</xsl:text><xsl:value-of disable-output-escaping="yes" select="$citations[$item-position]"/><xsl:text disable-output-escaping="yes">]]&gt;</xsl:text></dcterms:bibliographicCitation>
        </xsl:copy>
    </xsl:template>

</xsl:stylesheet>
