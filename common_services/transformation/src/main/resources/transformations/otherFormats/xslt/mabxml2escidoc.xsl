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
	Transformations from WoS Item to eSciDoc PubItem 
	Author: Julia Kurt (initial creation) 
	$Author: jkurt $ (last changed)
	$Revision: 2310 $ 
	$LastChangedDate: 2009-09-15 18:09:23 +0200 (Di, 15 Sep 2009) $
-->
<xsl:stylesheet version="2.0"
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
   xmlns:xs="http://www.w3.org/2001/XMLSchema"
   xmlns:fn="http://www.w3.org/2005/xpath-functions"
   xmlns:xlink="http://www.w3.org/1999/xlink"
   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
   xmlns:dc="${xsd.metadata.dc}"
   xmlns:dcterms="${xsd.metadata.dcterms}"    
   xmlns:eidt="${xsd.metadata.escidocprofile.idtypes}"
   xmlns:srel="${xsd.soap.common.srel}"   
   xmlns:oaipmh="http://www.openarchives.org/OAI/2.0/"   
   xmlns:file="${xsd.metadata.file}"
   xmlns:pub="${xsd.metadata.publication}"
   xmlns:AuthorDecoder="java:de.mpg.escidoc.services.common.util.creators.AuthorDecoder"
   xmlns:escidoc="urn:escidoc:functions"
   xmlns:ei="${xsd.soap.item.item}"
   xmlns:mdr="${xsd.soap.common.mdrecords}"
   xmlns:mdp="${xsd.metadata.escidocprofile}"
   xmlns:e="${xsd.metadata.escidocprofile.types}"
   xmlns:ec="${xsd.soap.item.components}"
   xmlns:prop="${xsd.soap.common.prop}">
 

	<xsl:output method="xml" encoding="UTF-8" indent="yes"/>
	
	<xsl:param name="user" select="'dummy-user'"/>
	<xsl:param name="context" select="'escidoc:31013'"/>
	<xsl:param name="is-item-list" select="true()"/>
	<!--
		DC XML  Header
	-->
	
		
	<!-- VARIABLEN -->
	
	
	<xsl:variable name="genre"/>		
	
	<xsl:template match="/">
		<xsl:choose>
			<xsl:when test="$is-item-list">
				<item-list>
					<xsl:apply-templates select="//item"/>
				</item-list>
			</xsl:when>
			<xsl:when test="count(//item) = 1">
				<xsl:apply-templates select="//item"/>
			</xsl:when>
			
			<xsl:when test="count(//item) = 0">
				<xsl:value-of select="error(QName('http://www.escidoc.de', 'err:NoSourceForSingleTarget' ), 'Single item was selected as target, but the source contained no items')"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="error(QName('http://www.escidoc.de', 'err:MultipleSourceForSingleTarget' ), 'Single item was selected as target, but the source contained multiple items')"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="item">
		<xsl:element name="ei:item">
			<xsl:element name="ei:properties">
				<srel:content-model objid="escidoc:persistent4"/>
				<xsl:element name="prop:content-model-specific"/>				
			</xsl:element>
			<xsl:element name="mdr:md-records">
				<mdr:md-record name="escidoc">
					<xsl:call-template name="itemMetadata"/>
				</mdr:md-record>
			</xsl:element>
			<xsl:element name="ec:components"></xsl:element>
		</xsl:element>
	</xsl:template>
	
	<!-- GENRE -->
	<xsl:template name="itemMetadata">
			<xsl:choose>
				<xsl:when test="mab029_m='B'">
					<xsl:call-template name="createEntry">
						<xsl:with-param name="gen" select="'book'"/>
					</xsl:call-template>
				</xsl:when>				
				<xsl:when test="mab029_m='P'">
					<xsl:call-template name="createEntry">
						<xsl:with-param name="gen" select="'article'"/>
					</xsl:call-template>
				</xsl:when>					
				<xsl:otherwise>
					<xsl:call-template name="createEntry">
						<xsl:with-param name="gen" select="'other'"/>
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
	</xsl:template>
	
	<!-- Create eSciDoc Entry -->
	<xsl:template name="createEntry">
		<xsl:param name="gen"/>
		
		<xsl:element name="mdp:publication">
			<xsl:attribute name="type">
				<xsl:value-of select="$gen"/>
			</xsl:attribute>
			<!-- CREATOR -->			
			<xsl:apply-templates select="mab100"/>
			<!-- TITLE -->
			<xsl:apply-templates select="mab331"/>
			<!-- LANGUAGE -->			
			
			<!-- IDENTIFIER -->
			<xsl:apply-templates select="mab088"/>			
			
			<!-- DATES -->
			
			<!-- SOURCE -->			
					
			<!-- EVENT -->
			
			<!-- PAGES -->
			
			<!-- ABSTRACT -->
			
			<!-- SUBJECT -->
			
			<!--end publication-->
		</xsl:element>
	</xsl:template>
	<!-- CREATOR -->
	<xsl:template match="mab100">
		<xsl:call-template name="createCreator">
			<xsl:with-param name="role">author</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	<xsl:template name="createCreator">
		<xsl:param name="role"/>
		<xsl:element name="pub:creator">
			<xsl:element name="e:person">
				<xsl:attribute name="role" select="$role"/>
				<xsl:element name="e:complete-name">
					<xsl:value-of select="."/>
				</xsl:element>
			</xsl:element>
		</xsl:element>
	</xsl:template>
	<!-- TITLE -->
	<xsl:template match="mab331">
		<xsl:element name="dc:title">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<!-- IDENTIFIER -->
	<xsl:template match="mab088">
		<xsl:call-template name="createID">
			<xsl:with-param name="idtype">eidt:URI</xsl:with-param>
		</xsl:call-template>
	</xsl:template>	
	<xsl:template name="createID">
		<xsl:param name="idtype"/>
		<xsl:element name="dc:identifier">
			<xsl:attribute name="xsi:type" select="$idtype"/>
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	
	
	
</xsl:stylesheet>