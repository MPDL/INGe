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


 Copyright 2006-2008 Fachinformationszentrum Karlsruhe Gesellschaft
 für wissenschaftlich-technische Information mbH and Max-Planck-
 Gesellschaft zur Förderung der Wissenschaft e.V.
 All rights reserved. Use is subject to license terms.
-->
<!-- 
	Transformations from BioMedCentral to eSciDoc PubItem 
	Author: Julia Kurt (initial creation) 
	$Author: kurt $ (last changed)
	$Revision: 747 $ 
	$LastChangedDate: 2008-07-21 19:15:26 +0200 (Mo, 21 Jul 2008) $
-->
<xsl:stylesheet version="2.0" xmlns:pm="http://dtd.nlm.nih.gov/2.0/xsd/archivearticle" xmlns:bmc="http://www.biomedcentral.com/xml/schemas/oai/2.0/"
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
   xmlns:xs="http://www.w3.org/2001/XMLSchema"
   xmlns:fns="http://www.w3.org/2005/02/xpath-functions"
   xmlns:fn="http://www.w3.org/2005/xpath-functions"
   xmlns:xlink="http://www.w3.org/1999/xlink"
   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
   xmlns:dc="http://purl.org/dc/elements/1.1/"
   xmlns:dcterms="http://purl.org/dc/terms/"
   xmlns:mdr="http://www.escidoc.de/schemas/metadatarecords/0.4"
   xmlns:mdp="http://escidoc.mpg.de/metadataprofile/schema/0.1/"
   xmlns:e="http://escidoc.mpg.de/metadataprofile/schema/0.1/types"
   xmlns:ei="http://www.escidoc.de/schemas/item/0.7"
   xmlns:eidt="http://escidoc.mpg.de/metadataprofile/schema/0.1/idtypes"
   xmlns:srel="http://escidoc.de/core/01/structural-relations/"
   xmlns:prop="http://escidoc.de/core/01/properties/"
   xmlns:oaipmh="http://www.openarchives.org/OAI/2.0/"   
   xmlns:ec="http://www.escidoc.de/schemas/components/0.7"
   xmlns:file="http://escidoc.mpg.de/metadataprofile/schema/0.1/file"
   xmlns:pub="http://escidoc.mpg.de/metadataprofile/schema/0.1/publication"
   xmlns:escidoc="urn:escidoc:functions">
   
 <!--  xmlns:ei="${xsd.soap.item.item}"
   xmlns:mdr="${xsd.soap.common.mdrecords}"
   xmlns:mdp="${xsd.metadata.escidocprofile}"
   xmlns:e="http://escidoc.mpg.de/metadataprofile/schema/0.1/types"
   xmlns:ec="${xsd.soap.item.components}"
   xmlns:prop="${xsd.soap.common.prop}"
> -->

	<xsl:param name="user" select="'dummy-user'"/>
	<xsl:param name="context" select="'escidoc:31013'"/>	

	<xsl:output method="xml" encoding="UTF-8" indent="yes"/>	
	
	<xsl:template match="/">		
		<xsl:apply-templates select="results"/>
	</xsl:template>	
	
	
	<xsl:template match="results">
		<xsl:apply-templates select="document"/>
	</xsl:template>
	
	<xsl:template match="document">
		<xsl:call-template name="createItem">
			<xsl:with-param name="genre">conference-paper</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="journal">
		<xsl:call-template name="createItem">
			<xsl:with-param name="genre">journal</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	
	<!-- CREATE ITEM -->	
	<xsl:template name="createItem">
		<xsl:param name="genre"/>
		<xsl:element name="ei:item">
			<xsl:element name="ei:properties">
				<srel:context objid="escidoc:persistent3" />
				<!--<xsl:element name="srel:context">
					<xsl:attribute name="xlink:href" select="concat('/ir/context/', $context)"/>
				</xsl:element>-->
				<srel:content-model objid="escidoc:persistent4"/>
				<xsl:element name="prop:content-model-specific"/>
			</xsl:element>
			<xsl:element name="mdr:md-records">
				<mdr:md-record name="escidoc">
					<xsl:call-template name="createMDRecord">
						<xsl:with-param name="genre" select="$genre"/>
					</xsl:call-template>
				</mdr:md-record>
			</xsl:element>	
			<xsl:element name="ec:components">				
						
			</xsl:element>
		</xsl:element>
	</xsl:template>
	
	
	
	
	<!-- CREATE MD-RECORD -->
	<xsl:template name="createMDRecord">
		<xsl:param name="genre"/>
		<xsl:element name="mdp:publication">			
			<xsl:attribute name="type">conference-paper</xsl:attribute>
			<!-- CREATOR -->
			<xsl:apply-templates select="authaffgrp"/>
			<!-- TITLE -->
			<xsl:apply-templates select="title"/>
			
			<!-- IDENTIFIER -->
			<xsl:apply-templates select="doi"/>
			<xsl:apply-templates select="eprint"/>
			<xsl:apply-templates select="spires_key"/>
			
			<!-- DATES -->
			<xsl:apply-templates select="date"/>
			<!-- EVENT -->
			<xsl:apply-templates select="conference"/>
			<!-- No PAGES -->
			<xsl:apply-templates select="pages"/>
			<!-- SUBJECT -->			
			<xsl:apply-templates select="report_num"/>
			
			<!-- SOURCE:JOURNAL -->
			<xsl:apply-templates select="journal"/>
			
		</xsl:element>
	</xsl:template>
	

	<!-- CREATOR -->
	<xsl:template match="authaffgrp">
		<xsl:apply-templates select="author"/>		
	</xsl:template>
	
	<xsl:template match="author">		
		<xsl:element name="pub:creator">
			<xsl:attribute name="role">author</xsl:attribute>
			<xsl:call-template name="createPerson"/>
		</xsl:element>		
	</xsl:template>
	<xsl:template name="createPerson">
		<xsl:element name="e:person">
			
			<xsl:element name="e:complete-name">
				<xsl:value-of select="."/>
			</xsl:element>			
			<xsl:if test="../aff">
				<xsl:call-template name="createOrganization"/>
			</xsl:if>
		</xsl:element>
	</xsl:template>
	
	
	<xsl:template name="createOrganization">		
		<xsl:element name="e:organization">
			<xsl:element name="e:organization-name">
				<xsl:value-of select="../aff"/>
			</xsl:element>			
		</xsl:element>		
	</xsl:template>
	
	
	<!-- TITLE -->
	<xsl:template match="title">
		<xsl:element name="dc:title">			
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	
	<!-- IDENTIFIER -->	
	<xsl:template match="doi">
		<xsl:element name="dc:identifier">
			<xsl:attribute name="xsi:type">eidt:DOI</xsl:attribute>							
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="eprint">
		<xsl:element name="dc:identifier">
			<xsl:attribute name="xsi:type">eidt:URI</xsl:attribute>							
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="spires_key">
		<xsl:element name="dc:identifier">
			<xsl:attribute name="xsi:type">eidt:OTHER</xsl:attribute>							
			<xsl:value-of select="concat('spires:',.)"/>
		</xsl:element>
	</xsl:template>
	
	<!-- DATES -->
	<xsl:template match="date">		
		<xsl:element name="pub:published-online">
			<xsl:call-template name="createDate"/>
		</xsl:element>		
	</xsl:template>
	
	<xsl:template name="createDate">
		<xsl:variable name="year" select="substring(.,1,4)"/>
		<xsl:variable name="month" select="substring(.,5,2)"/>
		<xsl:variable name="day" select="substring(.,7,8)"/>
		<xsl:value-of select="concat($year,'-',$month,'-',$day)"/>
	</xsl:template>
	
	<xsl:template name="createEventDate">
		<xsl:variable name="d" select="substring-before(., ' ')"/>
		<xsl:variable name="sd" select="substring-before(.,'-')"/>
		<xsl:variable name="ed" select="substring-after($d,'-')"/>
		<xsl:variable name="m" select="substring-before(substring-after(.,' '),' ')"/>
		<xsl:variable name="year" select="substring(normalize-space(substring-after(.,' ')),4,7)"/>
		<xsl:variable name="month">
		
				<xsl:choose>
					<xsl:when test="$m='Jan'">01</xsl:when>
					<xsl:when test="$m='Feb'">02</xsl:when>
					<xsl:when test="$m='Mar'">03</xsl:when>
					<xsl:when test="$m='Apr'">04</xsl:when>
					<xsl:when test="$m='May'">05</xsl:when>
					<xsl:when test="$m='Jun'">06</xsl:when>
					<xsl:when test="$m='Jul'">07</xsl:when>
					<xsl:when test="$m='Aug'">08</xsl:when>
					<xsl:when test="$m='Sep'">09</xsl:when>
					<xsl:when test="$m='Oct'">10</xsl:when>
					<xsl:when test="$m='Nov'">11</xsl:when>
					<xsl:when test="$m='Dec'">12</xsl:when>					
				</xsl:choose>
			
		</xsl:variable>
		<xsl:variable name="day">	
			<xsl:choose>
				<xsl:when test="string-length($sd)=1">	
					<xsl:value-of select="concat('0',$sd)"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$sd"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="date">
			<xsl:value-of select="normalize-space($year)"/>
			<xsl:value-of select="concat('-',$month)"/>
			<xsl:value-of select="concat('-',$day)"/>
		</xsl:variable>
		<xsl:value-of select="$date"/>
	</xsl:template>
	
	<!-- EVENT -->
	<xsl:template match="conference">
		<xsl:element name="pub:event">
			<xsl:apply-templates select="name"/>			
			<xsl:choose>
				<xsl:when test="dater">
					<xsl:apply-templates select="dater"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates select="dates"/>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:apply-templates select="address"/>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="address">
		<xsl:element name="e:place">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	
	<!-- EVENT DATE -->
	<xsl:template match="dates">
			
			<xsl:element name="e:start-date">
				<xsl:variable name="year" select="substring(.,1,4)"/>
				<xsl:variable name="month" select="substring(.,5,2)"/>
				<xsl:variable name="day" select="substring(.,7,2)"/>
				<xsl:value-of select="concat($year,'-',$month,'-',$day)"/>
			</xsl:element>
		
	</xsl:template>
	<xsl:template match="dater">
		<xsl:element name="e:start-date">
			<xsl:call-template name="createEventDate"/>
		</xsl:element>
	</xsl:template>
	
	<!-- SUBJECT -->
	
	<xsl:template match="report_num">
		<xsl:element name="dcterms:subject">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<!-- PAGES -->
	<xsl:template match="pages">
		<xsl:element name="pub:total-number-of-pages">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	
	<!-- CREATE JOURNAL -->
	<xsl:template match="journal">		
		<xsl:element name="pub:source">	
			<xsl:attribute name="type">journal</xsl:attribute>
			<!-- SOURCE TITLE -->
			<xsl:apply-templates select="name"/>			
			<!-- SOURCE VOLUME -->
			<xsl:apply-templates select="volume"/>
			<!-- SOURCE PAGES -->
			<xsl:apply-templates select="page"/>			
		</xsl:element>
	</xsl:template>
	<!-- VOLUME -->	
	<xsl:template match="volume">
		<xsl:element name="e:volume">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	
	<!-- PAGES -->
	<xsl:template match="page">
		<xsl:element name="e:start-page">
			<xsl:value-of select="."/>
		</xsl:element>		
	</xsl:template>
	
	
	<!-- SOURCE TITLE -->
	<xsl:template match="name">
		<xsl:element name="dc:title">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	
	
	
</xsl:stylesheet>	

