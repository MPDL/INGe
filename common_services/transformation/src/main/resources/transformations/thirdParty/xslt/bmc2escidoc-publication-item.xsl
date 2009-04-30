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
<xsl:stylesheet version="2.0" xmlns:pm="http://dtd.nlm.nih.gov/2.0/xsd/archivearticle"
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
		<xsl:for-each select="oaipmh:OAI-PMH/oaipmh:GetRecord/oaipmh:record/oaipmh:metadata/Article">			
			<xsl:call-template name="createItem"/>
		</xsl:for-each>
	</xsl:template>	
	
	
	<!-- CREATE ITEM -->	
	<xsl:template name="createItem">
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
					<xsl:call-template name="createMDRecord"/>	
				</mdr:md-record>
			</xsl:element>	
			<xsl:element name="ec:components">				
						
			</xsl:element>
		</xsl:element>
	</xsl:template>
	
	
	
	
	<!-- CREATE MD-RECORD -->
	<xsl:template name="createMDRecord">
		<xsl:element name="mdp:publication">			
			<xsl:attribute name="type" select="'article'"/>
			<!-- CREATOR -->
			<xsl:apply-templates select="AuthorList"/>
			<!-- TITLE -->
			<xsl:apply-templates select="ArticleTitle"/>
			<xsl:apply-templates select="VernacularTitle"/>
			<!-- IDENTIFIER -->
			<xsl:apply-templates select="ArticleIdList"/>
			
			<!-- DATES -->
			<xsl:apply-templates select="History/PubDate"/>
			<!-- TOTAL PAGES -->
			<xsl:apply-templates select="pm:page-range"/>
			<!-- ABSTRACT -->			
			<xsl:apply-templates select="Abstract"/>
			<xsl:apply-templates select="OtherAbstract"/>
			<!-- SOURCE:JOURNAL -->
			<xsl:apply-templates select="Journal"/>
			
		</xsl:element>
	</xsl:template>
	
	

	<!-- CREATOR --><!--TODO organizations-->
	<xsl:template match="AuthorList">
		<xsl:apply-templates select="Author"/>
	</xsl:template>
	
	<xsl:template match="Author">
		<xsl:element name="pub:creator">
			<xsl:attribute name="role">author</xsl:attribute>
			<xsl:element name="e:complete-name">
				<xsl:value-of select="concat(FirstName, ' ', MiddelName, ' ', LastName)"/>
			</xsl:element>
			<xsl:element name="e:given-name">
				<xsl:value-of select="concat(FirstName, ' ', MiddelName)"/>
			</xsl:element>
			<xsl:element name="e:family-name">
				<xsl:value-of select="LastName"/>
			</xsl:element>
			<xsl:apply-templates select="Affiliation"/>
		</xsl:element>
		<xsl:apply-templates select="ColectiveName"/>
	</xsl:template>
	
	<xsl:template match="Affiliation">
		<xsl:call-template name="createOrganization"/>
	</xsl:template>
	
	<xsl:template match="CollectiveName">
		<xsl:element name="pub:creator">			
			<xsl:call-template name="createOrganization"/>
		</xsl:element>
	</xsl:template>
	<xsl:template name="createOrganization">		
		<xsl:element name="e:organization">
			<xsl:element name="e:organization-name">
				<xsl:value-of select="."/>
			</xsl:element>			
		</xsl:element>		
	</xsl:template>
	
	
	<!-- TITLE -->
	<xsl:template match="ArticleTitle">
		<xsl:element name="dc:title">			
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="VernacularTitle">
		<xsl:element name="dcterms:alternative">			
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<!-- IDENTIFIER -->
	<xsl:template match="ArticleIdList">
		<xsl:apply-templates select="ArticleId"/>
	</xsl:template>
	<xsl:template match="ArticleId">
		<xsl:element name="dc:identifier">
			<xsl:choose>
				<xsl:when test="@IdType='pii'">					
					<xsl:attribute name="xsi:type">eidt:PII</xsl:attribute>					
				</xsl:when>
				<xsl:when test="@IdType='doi'">
					<xsl:attribute name="xsi:type">eidt:DOI</xsl:attribute>
				</xsl:when>
				<xsl:when test="@IdType='pmcpid'">
					<xsl:attribute name="xsi:type">eidt:OTHER</xsl:attribute>
					<xsl:value-of select="'pmcpid:'"/>
				</xsl:when>
				<xsl:when test="@IdType='pmpid'">
					<xsl:attribute name="xsi:type">eidt:OTHER</xsl:attribute>
					<xsl:value-of select="'pmpid:'"/>
				</xsl:when>
				<xsl:when test="@IdType='pmid'">
					<xsl:attribute name="xsi:type">eidt:PMID</xsl:attribute>
				</xsl:when>
				<xsl:when test="@IdType='medline'">
					<xsl:attribute name="xsi:type">eidt:OTHER</xsl:attribute>
					<xsl:value-of select="'medline:'"/>
				</xsl:when>
				<xsl:when test="@Idtype='pmcid'">
					<xsl:attribute name="xsi:type">eidt:PMC</xsl:attribute>
				</xsl:when>				
			</xsl:choose>				
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	
	
	<!-- DATES -->
	<xsl:template match="History/PubDate">		
		<xsl:choose>
			<xsl:when test="@PubStatus='received'">
				<xsl:element name="dcterms:dateSubmitted">
					<xsl:call-template name="createDate"/>
				</xsl:element>
			</xsl:when>
			<xsl:when test="@PubStatus='accepted'">
				<xsl:element name="dcterms:dateAccepted">
					<xsl:call-template name="createDate"/>
				</xsl:element>
			</xsl:when>
			<xsl:when test="@PubStatus='epublish'">
				<xsl:element name="pub:published-online">
					<xsl:call-template name="createDate"/>
				</xsl:element>
			</xsl:when>
			<xsl:when test="@PubStatus='ppublish'">
				<xsl:element name="dcterms:issued">					
					<xsl:call-template name="createDate"/>				
				</xsl:element>
			</xsl:when>
			<xsl:when test="@PubStatus='revised'">
				<xsl:element name="dcterms:modified">					
					<xsl:call-template name="createDate"/>				
				</xsl:element>
			</xsl:when>
			<xsl:when test="@PubStatus='aheadofprint'">
				<xsl:element name="pub:published-online">
					<xsl:call-template name="createDate"/>
				</xsl:element>
			</xsl:when>
		</xsl:choose>
	</xsl:template>
	
	
	<xsl:template name="createDate">
		
		<xsl:variable name="date">
			<xsl:if test="Year">
				<xsl:value-of select="Year"/>
				<xsl:if test="Month">					
					
					<xsl:choose>
						<xsl:when test="Jan"><xsl:value-of>-01</xsl:value-of></xsl:when>
						<xsl:when test="Feb"><xsl:value-of>-02</xsl:value-of></xsl:when>
						<xsl:when test="Mar"><xsl:value-of>-03</xsl:value-of></xsl:when>
						<xsl:when test="Apr"><xsl:value-of>-04</xsl:value-of></xsl:when>
						<xsl:when test="May"><xsl:value-of>-05</xsl:value-of></xsl:when>
						<xsl:when test="Jun"><xsl:value-of>-06</xsl:value-of></xsl:when>
						<xsl:when test="Jul"><xsl:value-of>-07</xsl:value-of></xsl:when>
						<xsl:when test="Aug"><xsl:value-of>-08</xsl:value-of></xsl:when>
						<xsl:when test="Sep"><xsl:value-of>-09</xsl:value-of></xsl:when>
						<xsl:when test="Oct"><xsl:value-of>-10</xsl:value-of></xsl:when>
						<xsl:when test="Nov"><xsl:value-of>-11</xsl:value-of></xsl:when>
						<xsl:when test="Dec"><xsl:value-of>-12</xsl:value-of></xsl:when>
					</xsl:choose>
				</xsl:if>
				<xsl:if test="Day">
					<xsl:value-of select="concat('-',Day)"/>
				</xsl:if>
			</xsl:if>
		</xsl:variable>
	</xsl:template>
	
	
	<!-- TOTAL NO OF PAGES  -->
	<xsl:template match="pm:page-range">
		<xsl:element name="pub:total-number-of-pages">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<!-- ABSTRACT -->
	<!--TODO delete tags -->
	<xsl:template match="Abstract">
		<xsl:element name="dcterms:abstract">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="OtherAbstract">
		<xsl:element name="dcterms:abstract">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	
	
	<!-- CREATE JOURNAL -->
	<xsl:template match="Journal">		
		<xsl:element name="pub:source">	
			<xsl:attribute name="type" select="'journal'"/>
			<!-- SOURCE TITLE -->
			<xsl:apply-templates select="JournalTitle"/>			
			<!-- SOURCE VOLUME -->
			<xsl:apply-templates select="Volume"/>
			<!-- SOURCE ISSUE -->
			<xsl:apply-templates select="Issue"/>
			<!-- SOURCE PAGES -->
			<xsl:apply-templates select="FirstPage"/>
			<xsl:apply-templates select="LastPage"/>
			<!-- SOURCE SEQ NR -->
			<xsl:apply-templates select="ELocationID"/>
			<!-- SOURCE PUBLISHINGINFO -->
			<xsl:apply-templates select="PublisherName"/>
			<!-- SOURCE IDENTIFIER -->
			<xsl:apply-templates select="Issn"/>
		</xsl:element>
	</xsl:template>
	<!-- VOLUME -->	
	<xsl:template match="Volume">
		<xsl:element name="e:volume">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<!-- ISSUE -->	
	<xsl:template match="Issue">
		<xsl:element name="e:issue">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<!-- PAGES -->
	<xsl:template match="FirstPages">
		<xsl:element name="e:start-page">
			<xsl:value-of select="."/>
		</xsl:element>		
	</xsl:template>
	<xsl:template match="LastPage">
		<xsl:element name="e:end-page">	
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<!-- SEQ NO -->
	<xsl:template match="ELocationID">
		<xsl:element name="e:sequence-number">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	
	<!-- SOURCE TITLE -->
	<xsl:template match="JournalTitle">
		<xsl:element name="dc:title">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	
	<!-- SOURCE IDENTIFIER -->
	
	<xsl:template match="Issn">
		<xsl:element name="dc:identifier">
			<xsl:attribute name="xsi:type">eidt:ISSN</xsl:attribute>
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<!-- SOURCE PUBLISHINGINFO -->
	<xsl:template match="PublisherName">
		<xsl:element name="e:publishing-info">
			<xsl:element name="dc:publisher">
				<xsl:value-of select="."/>
			</xsl:element>				
		</xsl:element>
	</xsl:template>
	
</xsl:stylesheet>	
