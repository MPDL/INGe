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

<!-- 
	Back transformation from the eSciDoc xml coreservice 1.3 to 1.2  
	Author: mwalter (initial creation) 
	$Author: mwalter $ (last changed)
	$Revision: $ 
	$LastChangedDate: $
-->

<xsl:stylesheet version="2.0"
 
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"

		xmlns:escidocItemList="${xsd.soap.item.itemlist}"		
  		xmlns:escidocItem="${xsd.soap.item.item}"
  		
        xmlns:escidocMetadataRecords="${xsd.soap.common.mdrecords}"
		xmlns:escidocMetadataProfile="http://escidoc.mpg.de/metadataprofile/schema/0.1/"
		xmlns:escidoc="http://escidoc.mpg.de/metadataprofile/schema/0.1/types"
		  		
		xmlns:publication="${xsd.metadata.publication}"        
		xmlns:eterms="${xsd.metadata.escidocprofile.types}"
		xmlns:organization="${xsd.metadata.organization}"
		xmlns:person="${xsd.metadata.person}"
		xmlns:source="${xsd.metadata.source}"
        xmlns:idtype="${xsd.metadata.escidocprofile.types}"
		xmlns:event="${xsd.metadata.event}"		

        xmlns:file="http://purl.org/metadata/profiles/0.1/file"
        
        xmlns:prop="${xsd.soap.common.prop}"
        
        xmlns:dc="http://purl.org/dc/elements/1.1/"
        xmlns:dcterms="http://purl.org/dc/terms/"
        
        xmlns:xlink="http://www.w3.org/1999/xlink"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  
	>
	
	<xsl:output method="xml" encoding="UTF-8" indent="yes" cdata-section-elements="dcterms:bibliographicCitation"/>

	<xsl:param name="is-item-list" select="true()"/>

	<xsl:variable name="vm" select="document('ves-mapping.xml')/mappings"/>

	<xsl:template match="/">
		<xsl:choose>
			<xsl:when test="$is-item-list">
				<xsl:apply-templates select="escidocItemList:item-list" />
			</xsl:when>
			<xsl:when test="count(//escidocItem:item) = 1">
				<xsl:apply-templates />
			</xsl:when>
			<xsl:when test="count(//escidocItem:item) = 0">
				<xsl:value-of select="error(QName('http://www.escidoc.de/transformation', 'err:NoSourceForSingleTarget' ), 'Single item was selected as target, but the source contained no items')"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="error(QName('http://www.escidoc.de/transformation', 'err:MultipleSourceForSingleTarget' ), 'Single item was selected as target, but the source contained multiple items')"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="node() | @*">
		<xsl:copy copy-namespaces="no">
			<xsl:apply-templates select="node() | @*"/>
		</xsl:copy>
	</xsl:template>

	<xsl:template match="escidocItemList:item-list">
		<xsl:if test="$is-item-list">
			<xsl:element name="escidocItemList:item-list" namespace="http://www.escidoc.de/schemas/itemlist/0.9">
				<xsl:call-template name="item-namespaces"/>
				<xsl:apply-templates />
			</xsl:element>
		</xsl:if>
		<xsl:if test="not($is-item-list)">
			<!-- skip creation of the root item-list element and start with the item -->
			<xsl:apply-templates/>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="escidocItem:item">
		<xsl:element name="escidocItem:item" namespace="http://www.escidoc.de/schemas/item/0.9">
			<xsl:if test="not($is-item-list)">
				<xsl:call-template name="item-namespaces"/>
			</xsl:if>				
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="escidocItem:properties">
		<xsl:element name="escidocItem:properties" namespace="http://www.escidoc.de/schemas/item/0.9">
			<xsl:apply-templates select="node() | @*"/>
		</xsl:element>
	</xsl:template>
	

	<!-- all namespaces which should be presented in item root element -->
	<xsl:template name="item-namespaces">
	
        <xsl:namespace name="escidocContext">http://www.escidoc.de/schemas/context/0.7</xsl:namespace>
        <xsl:namespace name="escidocContextList">http://www.escidoc.de/schemas/contextlist/0.7</xsl:namespace>
        <xsl:namespace name="escidocComponents">http://www.escidoc.de/schemas/components/0.9</xsl:namespace>
        <xsl:namespace name="escidocItem">http://www.escidoc.de/schemas/item/0.9</xsl:namespace>
        <xsl:namespace name="escidocItemList">http://www.escidoc.de/schemas/itemlist/0.9</xsl:namespace>
        <xsl:namespace name="escidocMetadataRecords">http://www.escidoc.de/schemas/metadatarecords/0.5</xsl:namespace>
        <xsl:namespace name="escidocRelations">http://www.escidoc.de/schemas/relations/0.3</xsl:namespace>
        <xsl:namespace name="escidocSearchResult">http://www.escidoc.de/schemas/searchresult/0.8</xsl:namespace>
        <xsl:namespace name="xlink">http://www.w3.org/1999/xlink</xsl:namespace>
        <xsl:namespace name="xsi">http://www.w3.org/2001/XMLSchema-instance</xsl:namespace>
        <xsl:namespace name="prop">http://escidoc.de/core/01/properties/</xsl:namespace>
        <xsl:namespace name="srel">http://escidoc.de/core/01/structural-relations/</xsl:namespace>
        <xsl:namespace name="version">http://escidoc.de/core/01/properties/version/</xsl:namespace>
        <xsl:namespace name="release">http://escidoc.de/core/01/properties/release/</xsl:namespace>
        <xsl:namespace name="member-list">http://www.escidoc.de/schemas/memberlist/0.9</xsl:namespace>
        <xsl:namespace name="container">http://www.escidoc.de/schemas/container/0.8</xsl:namespace>
        <xsl:namespace name="container-list">http://www.escidoc.de/schemas/containerlist/0.8</xsl:namespace>
        <xsl:namespace name="struct-map">http://www.escidoc.de/schemas/structmap/0.4</xsl:namespace>
        <xsl:namespace name="mods-md">http://www.loc.gov/mods/v3</xsl:namespace>
        <xsl:namespace name="file">http://purl.org/escidoc/metadata/profiles/0.1/file</xsl:namespace>
        <xsl:namespace name="publication">http://purl.org/escidoc/metadata/profiles/0.1/publication</xsl:namespace>
        <xsl:namespace name="face">http://purl.org/escidoc/metadata/profiles/0.1/face</xsl:namespace>
        <xsl:namespace name="jhove">http://hul.harvard.edu/ois/xml/ns/jhove</xsl:namespace>
        
	</xsl:template>
</xsl:stylesheet>
