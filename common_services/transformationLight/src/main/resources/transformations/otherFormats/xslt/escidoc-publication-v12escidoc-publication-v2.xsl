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


 Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft
 für wissenschaftlich-technische Information mbH and Max-Planck-
 Gesellschaft zur Förderung der Wissenschaft e.V.
 All rights reserved. Use is subject to license terms.
-->

<!-- 
	Transformation from the eSciDoc metadata profile v1 to v2  
	Author: vmakarenko (initial creation) 
	$Author: mfranke $ (last changed)
	$Revision: 3827 $ 
	$LastChangedDate: 2011-01-12 11:50:54 +0100 (Mi, 12 Jan 2011) $
-->

<xsl:stylesheet version="2.0"    
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:eterms="http://purl.org/escidoc/metadata/terms/0.1/"
	xmlns:person="http://purl.org/escidoc/metadata/profiles/0.1/person"
	xmlns:source="http://purl.org/escidoc/metadata/profiles/0.1/source"
	xmlns:event="http://purl.org/escidoc/metadata/profiles/0.1/event"
	xmlns:organization="http://purl.org/escidoc/metadata/profiles/0.1/organization"
	xmlns:dc="http://purl.org/dc/elements/1.1/"
	xmlns:dcterms="http://purl.org/dc/terms/"
	xmlns:xlink="http://www.w3.org/1999/xlink"
	xmlns:escidoc="http://escidoc.mpg.de/metadataprofile/schema/0.1/types"
	xmlns:escidocItemList="http://www.escidoc.de/schemas/itemlist/0.8"
	xmlns:escidocItem="http://www.escidoc.de/schemas/item/0.8"
	xmlns:publication="http://escidoc.mpg.de/metadataprofile/schema/0.1/publication"
	xmlns:pub="http://purl.org/escidoc/metadata/profiles/0.1/publication"
	xmlns:escidocMetadataProfile="http://escidoc.mpg.de/metadataprofile/schema/0.1/"
	xmlns:file="http://escidoc.mpg.de/metadataprofile/schema/0.1/file"
	xmlns:prop="http://escidoc.de/core/01/properties/" 
	>
	
	<xsl:output method="xml" encoding="UTF-8" indent="yes"/>

	<xsl:param name="is-item-list" select="true()"/>

	<xsl:variable name="vm" select="document('ves-mapping.xml')/mappings"/>
	
	<!-- Temporarily OUs mapping -->
	<!-- xsl:variable name="ous">
		<map v1="escidoc:4001">escidoc:274892</map>
		<map v1="escidoc:4002">escidoc:274893</map>
		<map v1="escidoc:4005">escidoc:274894</map>
		<map v1="escidoc:4003">escidoc:274895</map>
		<map v1="escidoc:4007">escidoc:274896</map>
	</xsl:variable -->

	<xsl:template match="/">
		<xsl:choose>
			<xsl:when test="$is-item-list">
				<xsl:if test="count(//escidocItemList:item-list) = 0">
					<xsl:element name="escidocItemList:item-list" namespace="${xsd.soap.item.itemlist}">
						<xsl:call-template name="item-namespaces"/>
						<xsl:apply-templates select="escidocItem:item" />
					</xsl:element>
				</xsl:if>
				<xsl:if test="count(//escidocItemList:item-list) > 0">
					<xsl:apply-templates select="escidocItemList:item-list"/>
				</xsl:if>
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
			<xsl:element name="escidocItemList:item-list" namespace="${xsd.soap.item.itemlist}">
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
		<xsl:element name="escidocItem:item" namespace="${xsd.soap.item.item}">
			<xsl:if test="not($is-item-list)">
				<xsl:call-template name="item-namespaces"/>
			</xsl:if>				
			<xsl:copy-of select="@*" />
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>
	

	<xsl:template match="escidocMetadataProfile:publication">
		<xsl:variable name="objid" select="../../../@objid"/>
		<xsl:variable name="v1" select="@type"/> 
		<xsl:variable name="v2" select="$vm/publication-type/v1-to-v2/map[@v1=$v1]"/>
		<xsl:element name="pub:publication" namespace="http://purl.org/escidoc/metadata/profiles/0.1/publication">
			<xsl:copy-of select="@*[name()!='type']" />
			<!-- publication type (genre) from the ves -->	
			<xsl:attribute name="type" select="
				if (exists($v2))  
				then $v2
				else error(
					QName('http://www.escidoc.de/transformation', 'err:NoMappingForEnum' ), 
						concat ('No mapping v1.0 to v2.0 for publication type: ', $v1, ', item id: ', $objid)
					)
			" />
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="publication:creator | escidoc:creator" priority="999" >
		<xsl:variable name="objid" select="
			if (namespace-uri()='http://escidoc.mpg.de/metadataprofile/schema/0.1/publication') 
			then ../../../@objid 
			else ../../../../@objid
		 "/>
		<xsl:variable name="v1" select="@role"/>
		<xsl:variable name="v2" select="$vm/creator-role/v1-to-v2/map[@v1=$v1]"/>
		<xsl:element name="eterms:creator">
			<xsl:copy-of select="@*[name()!='role']" />
			<!-- creator role from the ves -->
			<!-- TODO: NON STRICT for the moment!!! -->
			<xsl:if test="$v2!=''">
				<xsl:attribute name="role" select="
					if (exists($v2))  
					then $v2
					else error(
						QName('http://www.escidoc.de/transformation', 'err:NoMappingForEnum' ), 
							concat ('No mapping v1.0 to v2.0 for creator role: ', $v1, ', item id: ', $objid)
						)
				" />
				</xsl:if>
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>
	
 	
	<xsl:template match="escidoc:person" priority="999">
		<xsl:element name="person:person">
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>
	 
	<xsl:template match="escidoc:organization" priority="999">
		<xsl:element name="organization:organization">
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="escidoc:organization/escidoc:organization-name" priority="999">
		<xsl:element name="dc:title">
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>
	
	
	<!-- person and organization identifiers, see http://colab.mpdl.mpg.de/mediawiki/Checklist_for_Metadata_Changes#Identifiers_from_CONE_in_Publication_Metadata  -->
	<xsl:template match="escidoc:identifier" priority="999">
		<xsl:element name="dc:identifier">
			<xsl:copy-of select="@*[name() != 'xsi:type']"/>
			<xsl:if test="@*[name() = 'xsi:type']">
				<xsl:attribute name="xsi:type" select="concat('eterms:', local-name-from-QName(resolve-QName(@xsi:type, .)))"/>
				<xsl:value-of select="
					if (contains(@xsi:type, 'CONE'))
					then concat('http://cone.mpdl.mpg.de/persons/resource/', substring-after(., 'urn:cone:'))
					else .
				"/>
			</xsl:if>
			<xsl:if test="not(@xsi:type)">
				<!-- xsl:variable name="ou" select="."/>
				<xsl:value-of select="
					if ($ous/map[@v1=$ou]) then $ous/map[@v1=$ou]
					else . 
				"/ -->
				<xsl:value-of select="."/>
			</xsl:if>
			<xsl:apply-templates select="*/*"/>
		</xsl:element>
	</xsl:template>
	
	<!-- person, organization and publication identifiers  --> 
	<xsl:template match="dc:identifier" priority="999">
		<xsl:element name="dc:identifier">
			<xsl:copy-of select="@*[name() != 'xsi:type']"/>
			<xsl:if test="@*[name() = 'xsi:type']">
				<xsl:attribute name="xsi:type" select="concat('eterms:', local-name-from-QName(resolve-QName(@xsi:type, .)))"/>
			</xsl:if>
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>

	<xsl:template match="publication:published-online">
		<xsl:element name="eterms:published-online">
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="publication:review-method">
		<xsl:variable name="objid" select="../../../../@objid"/>
		<xsl:variable name="v1" select="."/>
		<xsl:variable name="v2" select="$vm/review-method/v1-to-v2/map[@v1=$v1]"/>
		<!-- review method from the ves -->	
		<xsl:element name="eterms:review-method">
			<xsl:value-of select="
				if (exists($v2))  
				then $v2
				else error(
					QName('http://www.escidoc.de/transformation', 'err:NoMappingForEnum' ), 
						concat ('No mapping v1.0 to v2.0 for review method: ', $v1, ', item id: ', $objid)
					)
			" />
			<!-- skip duplicated value of the element -->
			<xsl:apply-templates select="*/*"/>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="publication:source | escidoc:source" priority="999">
		<xsl:element name="source:source">
			<xsl:copy-of select="@*[name()!='type']"/>
			<!-- source type from the ves -->
			<xsl:if test="@type">
				<xsl:variable name="objid" select="
					if (namespace-uri()='http://escidoc.mpg.de/metadataprofile/schema/0.1/publication') 
					then ../../../../@objid 
					else ../../../../../@objid
				 "/>
				<xsl:variable name="v1" select="@type"/>
				<!-- no constrains for the source type:  mapping is taken from the publication type list-->
				<xsl:variable name="v2" select="$vm/publication-type/v1-to-v2/map[@v1=$v1]"/>
				<xsl:attribute name="type" select="
					if (exists($v2))  
					then $v2
					else error(
						QName('http://www.escidoc.de/transformation', 'err:NoMappingForEnum' ), 
							concat ('No mapping v1.0 to v2.0 for source type: ', $v1, ', item id: ', $objid)
						)
				" />
			</xsl:if>
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>

	<!-- see http://colab.mpdl.mpg.de/mediawiki/Checklist_for_Metadata_Changes#Identifiers_from_CONE_in_Publication_Metadata  -->	
	<xsl:template match="dc:language">
		<xsl:variable name="v1" select="."/>
		<xsl:variable name="v2" select="$vm/language/v1-to-v2/map[@v1=$v1]"/>
		<xsl:element name="{name()}">
			<xsl:copy-of select="@*[name() != 'xsi:type']"/>
			<xsl:if test="@*[name() = 'xsi:type']">
				<xsl:attribute name="xsi:type" select="
					if (@xsi:type=$vm/language/@v1) 
					then $vm/language/@v2
					else @xsi:type
				"/>
				<xsl:value-of select="
					if ($v2!='')
					then $v2
					else $vm/language/v1-to-v2/@default
				"/>
			</xsl:if>
			<!-- skip duplicated value of the element -->
			<xsl:apply-templates select="*/*"/>
		</xsl:element>
	</xsl:template>	

	<xsl:template match="publication:event">
		<xsl:element name="event:event">
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="publication:total-number-of-pages">
		<xsl:element name="eterms:total-number-of-pages">
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="publication:degree">
		<xsl:variable name="objid" select="../../../../@objid"/>
		<xsl:variable name="v1" select="."/>
		<xsl:variable name="v2" select="$vm/academic-degree/v1-to-v2/map[@v1=$v1]"/>
		<xsl:element name="eterms:degree">
			<!-- academic degree from the ves -->	
			<xsl:value-of select="
				if (exists($v2))  
				then $v2
				else error(
					QName('http://www.escidoc.de/transformation', 'err:NoMappingForEnum' ), 
						concat ('No mapping v1.0 to v2.0 for academic degree: ', $v1, ', item id: ', $objid)
					)
			" />
			<!-- skip duplicated value of the element -->
			<xsl:apply-templates select="*/*"/>
		</xsl:element>
	</xsl:template>
	
	
	<xsl:template match="publication:location">
		<xsl:element name="eterms:location">
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="publication:publishing-info">
		<xsl:element name="eterms:publishing-info">
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>

	<xsl:template match="dcterms:subject">
		<xsl:element name="dc:subject">
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>
	

	<xsl:template match="file:content-category" priority="999">
		<xsl:variable name="v1" select="normalize-space(lower-case(translate(.,'_', '-')))"/>
		<xsl:variable name="v2" select="$vm/content-category/v1-to-v2/map[@v1=$v1]"/>
		<xsl:element name="eterms:content-category" namespace="http://purl.org/escidoc/metadata/terms/0.1/">
			<xsl:value-of select="
				if ($v2!='')
				then $v2
				else .
			"/>
			<xsl:apply-templates select="*/*" />
		</xsl:element>
	</xsl:template>
		
	<xsl:template match="prop:content-category" priority="999">
		<xsl:variable name="v1" select="normalize-space(lower-case(translate(.,'_', '-')))"/>
		<xsl:variable name="v2" select="$vm/content-category/v1-to-v2/map[@v1=$v1]"/>
		<xsl:element name="prop:content-category">
			<xsl:value-of select="
				if ($v2!='')
				then $v2
				else .
			"/>
			<xsl:apply-templates select="*/*" />
		</xsl:element>
	</xsl:template>
	
	<!-- all escidoc: prefixes to the eterms: 
		Note: escidoc:identifier for person and organization has own processing  
	-->
	<xsl:template match="*[namespace-uri()='http://escidoc.mpg.de/metadataprofile/schema/0.1/types']" priority="1">
		<xsl:element name="eterms:{local-name()}">
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>
	
	<!-- changes file namespace from http://escidoc.mpg.de/metadataprofile/schema/0.1/file to ${xsd.metadata.file} -->
	<xsl:template match="*[namespace-uri()='http://escidoc.mpg.de/metadataprofile/schema/0.1/file']" priority="1">
		<xsl:element name="{name()}" namespace="${xsd.metadata.file}">
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="*[namespace-uri()='http://www.escidoc.de/schemas/metadatarecords/0.4']" priority="1">
		<xsl:element name="{name()}" namespace="${xsd.soap.common.mdrecords}">
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="*[namespace-uri()='http://www.escidoc.de/schemas/components/0.8']" priority="1">
		<xsl:element name="{name()}" namespace="${xsd.soap.item.components}">
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="*[namespace-uri()='http://www.escidoc.de/schemas/item/0.8']" priority="1">
		<xsl:element name="{name()}" namespace="${xsd.soap.item.item}">
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>

	
	
	

	<!-- all namespaces which should be presented in item root element -->
	<xsl:template name="item-namespaces">
	
		<xsl:namespace name="escidocItem">${xsd.soap.item.item}</xsl:namespace>
		<xsl:namespace name="escidocContext">${xsd.soap.context.context}</xsl:namespace>
        <xsl:namespace name="escidocContextList">${xsd.soap.context.contextlist}</xsl:namespace>
        <xsl:namespace name="escidocComponents">${xsd.soap.item.components}</xsl:namespace>
        <xsl:namespace name="escidocMetadataRecords">${xsd.soap.common.mdrecords}</xsl:namespace>
        <xsl:namespace name="prop">${xsd.soap.common.prop}</xsl:namespace>
        <xsl:namespace name="srel">${xsd.soap.common.srel}</xsl:namespace>
        <xsl:namespace name="version">${xsd.soap.common.version}</xsl:namespace>
        <xsl:namespace name="release">${xsd.soap.common.release}</xsl:namespace>
        
		<xsl:namespace name="eterms">${xsd.metadata.escidocprofile.types}</xsl:namespace>
		<xsl:namespace name="organization">${xsd.metadata.organization}</xsl:namespace>
		<xsl:namespace name="person">${xsd.metadata.person}</xsl:namespace>
		<xsl:namespace name="source">${xsd.metadata.source}</xsl:namespace>
        <xsl:namespace name="idtype">${xsd.metadata.escidocprofile.types}</xsl:namespace>
		<xsl:namespace name="event">${xsd.metadata.event}</xsl:namespace>		
        
        <xsl:namespace name="file">${xsd.metadata.file}</xsl:namespace>
        <xsl:namespace name="dc">${xsd.metadata.dc}</xsl:namespace>
        <xsl:namespace name="dcterms">${xsd.metadata.dcterms}</xsl:namespace>
        
        <xsl:namespace name="xlink">http://www.w3.org/1999/xlink</xsl:namespace>
        <xsl:namespace name="xsi">http://www.w3.org/2001/XMLSchema-instance</xsl:namespace>
        
	</xsl:template>

</xsl:stylesheet>
