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


 Copyright 2006-2009 Fachinformationszentrum Karlsruhe Gesellschaft
 für wissenschaftlich-technische Information mbH and Max-Planck-
 Gesellschaft zur Förderung der Wissenschaft e.V.
 All rights reserved. Use is subject to license terms.
-->

<!-- 
	Back transformation from the eSciDoc metadata profile v2 to v1  
	Author: vmakarenko (initial creation) 
	$Author: vmakarenko $ (last changed)
	$Revision: $ 
	$LastChangedDate: $
-->

<xsl:stylesheet version="2.0"
 
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"

		xmlns:escidocItemList="http://www.escidoc.de/schemas/itemlist/0.8"		
  		xmlns:escidocItem="http://www.escidoc.de/schemas/item/0.8"
  		
        xmlns:escidocMetadataRecords="http://www.escidoc.de/schemas/metadatarecords/0.4"
		xmlns:escidocMetadataProfile="http://escidoc.mpg.de/metadataprofile/schema/0.1/"
		xmlns:escidoc="http://escidoc.mpg.de/metadataprofile/schema/0.1/types"
		  		
		xmlns:publication="http://purl.org/escidoc/metadata/profiles/0.1/publication"        
		xmlns:eterms="http://purl.org/escidoc/metadata/terms/0.1/"
		xmlns:organization="http://purl.org/escidoc/metadata/profiles/0.1/organization"
		xmlns:person="http://purl.org/escidoc/metadata/profiles/0.1/person"
		xmlns:source="http://purl.org/escidoc/metadata/profiles/0.1/source"
        xmlns:idtype="http://purl.org/escidoc/metadata/terms/0.1/"
		xmlns:event="http://purl.org/escidoc/metadata/profiles/0.1/event"		
        
        xmlns:file="http://purl.org/metadata/profiles/0.1/file"
        xmlns:dc="http://purl.org/dc/elements/1.1/"
        xmlns:dcterms="http://purl.org/dc/terms/"
        
        xmlns:xlink="http://www.w3.org/1999/xlink"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  
	>
	
	<xsl:output method="xml" encoding="UTF-8" indent="yes"/>

	<xsl:param name="is-item-list" select="true()"/>

	<xsl:variable name="vm" select="document('ves-mapping.xml')/mappings"/>

	<xsl:template match="/">
		<xsl:choose>
			<xsl:when test="$is-item-list">
				<xsl:if test="count(//escidocItemList:item-list) = 0">
					<xsl:element name="escidocItemList:item-list">
						<xsl:namespace name="escidocItemList">http://www.escidoc.de/schemas/itemlist/0.8</xsl:namespace>
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
			<xsl:element name="{name(.)}">
				<xsl:namespace name="escidocItemList">http://www.escidoc.de/schemas/itemlist/0.8</xsl:namespace>			
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
		<xsl:element name="{name(.)}">
			<xsl:if test="not($is-item-list)">
				<xsl:call-template name="item-namespaces"/>
			</xsl:if>				
			<xsl:copy-of select="@*" />
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>
	

	<xsl:template match="publication:publication">
		<xsl:variable name="v2" select="@type"/> 
		<xsl:variable name="v1" select="$vm/publication-type/v2-to-v1/map[@v2=$v2]"/>
		<xsl:element name="escidocMetadataProfile:publication" >
			<!-- publication type (genre) from the ves -->	
			<xsl:attribute name="type" select="
				if (exists($v1))  
				then $v1
				else error(
					QName('http://www.escidoc.de/transformation', 'err:NoMappingForEnum' ), 
						concat ('No mapping v2.0 to v1.0 for publication type: ', $v2 )
					)
			" />
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="eterms:creator" priority="999">
		<xsl:variable name="v2" select="@role"/>
		<xsl:variable name="v1" select="$vm/creator-role/v2-to-v1/map[@v2=$v2]"/>
		<xsl:element name="publication:creator" namespace="http://escidoc.mpg.de/metadataprofile/schema/0.1/publication">
			<xsl:copy-of select="@*[name()!='role']" />
			<!-- creator role from the ves -->	
			<xsl:attribute name="role" select="
				if (exists($v1))  
				then $v1
				else error(
					QName('http://www.escidoc.de/transformation', 'err:NoMappingForEnum' ), 
						concat ('No mapping v2.0 to v1.0 for creator role: ', $v2 )
					)
			" />
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>	
	
	<xsl:template match="person:person" priority="999">
		<xsl:element name="escidoc:person">
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>
	 
	<xsl:template match="organization:organization" priority="999">
		<xsl:element name="escidoc:organization">
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>
	
	
	<xsl:template match="organization:organization/dc:title" priority="999">
		<xsl:element name="escidoc:organization-name">
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>	
	
	<!-- organization and person identifiers  -->
	<xsl:template match="organization:organization/dc:identifier | person:person/dc:identifier" priority="999">
		<xsl:element name="escidoc:identifier">
			<xsl:copy-of select="@*[name() != 'xsi:type']"/>
			<xsl:if test="@*[name() = 'xsi:type']">
				<xsl:attribute name="xsi:type" select="concat('eidt:', local-name-from-QName(resolve-QName(@xsi:type, .)))"/>
			</xsl:if>
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>

	<!-- publication identifiers  --> 
	<xsl:template match="dc:identifier" priority="998">
		<xsl:element name="dc:identifier">
			<xsl:copy-of select="@*[name() != 'xsi:type']"/>
			<xsl:if test="@*[name() = 'xsi:type']">
				<xsl:attribute name="xsi:type" select="concat('eidt:', local-name-from-QName(resolve-QName(@xsi:type, .)))"/>
			</xsl:if>
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="eterms:published-online" priority="999">
		<xsl:element name="publication:published-online" namespace="http://escidoc.mpg.de/metadataprofile/schema/0.1/publication">
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>
	
	
	<xsl:template match="eterms:review-method" priority="999">
		<xsl:variable name="v2" select="normalize-space(.)"/>
		<xsl:variable name="v1" select="$vm/review-method/v2-to-v1/map[@v2=$v2]"/>
		<!-- review method from the ves -->	
		<xsl:element name="publication:review-method" namespace="http://escidoc.mpg.de/metadataprofile/schema/0.1/publication">
			<xsl:value-of select="
				if (exists($v1))  
				then $v1
				else error(
					QName('http://www.escidoc.de/transformation', 'err:NoMappingForEnum' ), 
						concat ('No mapping v2.0 to v1.0 for review method: ', $v1 )
					)
			" />
			<!-- skip duplicated value of the element -->
			<xsl:apply-templates select="*/*"/>
		</xsl:element>
	</xsl:template>	

	<xsl:template match="source:source">
		<xsl:variable name="v2" select="@type"/>
		<xsl:variable name="v1" select="$vm/publication-type/v2-to-v1/map[@v2=$v2]"/>
		<xsl:element name="publication:source" namespace="http://escidoc.mpg.de/metadataprofile/schema/0.1/publication">
			<xsl:copy-of select="@*[name()!='type']"/>
			<!-- source type from the ves -->	
			<xsl:attribute name="type" select="
				if (exists($v1))  
				then $v1
				else error(
					QName('http://www.escidoc.de/transformation', 'err:NoMappingForEnum' ), 
						concat ('No mapping v2.0 to v1.0 for source type: ', $v2 )
					)
			" />
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="dc:language">
		<xsl:variable name="v2" select="normalize-space(.)"/>
		<xsl:variable name="v1" select="$vm/language/v2-to-v1/map[@v2=$v2]"/>
		<xsl:element name="{name()}">
			<xsl:copy-of select="@*[name() != 'xsi:type']"/>
			<xsl:if test="@*[name() = 'xsi:type']">
				<xsl:attribute name="xsi:type" select="
					if (@xsi:type=$vm/language/@v2) 
					then $vm/language/@v1
					else @xsi:type
				"/>
				<xsl:value-of select="
					if ($v1!='')
					then $v1
					else $vm/language/v2-to-v1/@default
				"/>
			</xsl:if>
			<!-- skip duplicated value of the element -->
			<xsl:apply-templates select="*/*"/>
		</xsl:element>
	</xsl:template>	
	
	<xsl:template match="event:event">
		<xsl:element name="publication:event" namespace="http://escidoc.mpg.de/metadataprofile/schema/0.1/publication">
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>

	<xsl:template match="eterms:total-number-of-pages" priority="999">
		<xsl:element name="publication:total-number-of-pages" namespace="http://escidoc.mpg.de/metadataprofile/schema/0.1/publication">
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="eterms:degree" priority="999">
		<xsl:variable name="v2" select="normalize-space(.)"/>
		<xsl:variable name="v1" select="$vm/academic-degree/v2-to-v1/map[@v2=$v2]"/>
		<xsl:element name="publication:degree" namespace="http://escidoc.mpg.de/metadataprofile/schema/0.1/publication">
			<!-- academic degree from the ves -->	
			<xsl:value-of select="
				if (exists($v1))  
				then $v1
				else error(
					QName('http://www.escidoc.de/transformation', 'err:NoMappingForEnum' ), 
						concat ('No mapping v2.0 to v1.0 for academic degree: ', $v2 )
					)
			" />
			<!-- skip duplicated value of the element -->
			<xsl:apply-templates select="*/*"/>
		</xsl:element>
	</xsl:template>	
	
	<xsl:template match="eterms:location" priority="999">
		<xsl:element name="publication:location" namespace="http://escidoc.mpg.de/metadataprofile/schema/0.1/publication">
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="eterms:publishing-info" priority="999">
		<xsl:element name="publication:publishing-info" namespace="http://escidoc.mpg.de/metadataprofile/schema/0.1/publication">
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>

	<!-- TODO: check!!! (v1->v2 as well) -->
	<xsl:template match="dc:subject">
		<xsl:element name="dcterms:subject">
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>
	
	<!-- all eterms: prefixes to the escidoc: -->
	<xsl:template match="*[namespace-uri()='http://purl.org/escidoc/metadata/terms/0.1/']" priority="1">
		<xsl:element name="escidoc:{local-name()}" namespace="http://escidoc.mpg.de/metadataprofile/schema/0.1/types">
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>

	<xsl:template match="*[namespace-uri()='http://purl.org/metadata/profiles/0.1/file']" priority="1">
		<xsl:element name="{name()}" namespace="http://escidoc.mpg.de/metadataprofile/schema/0.1/file">
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>


	<!-- all namespaces which should be presented in item root element -->
	<xsl:template name="item-namespaces">
	
		<xsl:namespace name="xsi">http://www.w3.org/2001/XMLSchema-instance</xsl:namespace>
		<xsl:namespace name="xs">http://www.w3.org/2001/XMLSchema</xsl:namespace>
		<xsl:namespace name="dc">http://purl.org/dc/elements/1.1/</xsl:namespace>
		<xsl:namespace name="dcterms">http://purl.org/dc/terms/</xsl:namespace>
		<xsl:namespace name="xlink">http://www.w3.org/1999/xlink</xsl:namespace>
		<xsl:namespace name="escidoc">http://escidoc.mpg.de/metadataprofile/schema/0.1/types</xsl:namespace>
		<xsl:namespace name="escidocItem">http://www.escidoc.de/schemas/item/0.8</xsl:namespace>
		<xsl:namespace name="publication">http://escidoc.mpg.de/metadataprofile/schema/0.1/publication</xsl:namespace>
		<xsl:namespace name="escidocMetadataProfile">http://escidoc.mpg.de/metadataprofile/schema/0.1/</xsl:namespace>
		<xsl:namespace name="escidocMetadataRecords">http://www.escidoc.de/schemas/metadatarecords/0.4</xsl:namespace>
		<xsl:namespace name="eidt">http://escidoc.mpg.de/metadataprofile/schema/0.1/idtypes</xsl:namespace>
        
	</xsl:template>


</xsl:stylesheet>
