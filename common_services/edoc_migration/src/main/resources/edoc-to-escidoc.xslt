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
	Transformations from eDoc Item to eSciDoc PubItem 
	Author: Julia Kurt (initial creation) 
	$Author: kurt $ (last changed)
	$Revision: 747 $ 
	$LastChangedDate: 2008-07-21 19:15:26 +0200 (Mo, 21 Jul 2008) $
-->
<xsl:stylesheet version="2.0"
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
   xmlns:xs="http://www.w3.org/2001/XMLSchema"
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
   xmlns:publ="http://escidoc.mpg.de/metadataprofile/schema/0.1/publication"
   xmlns:escidoc="urn:escidoc:functions">
 <!--  xmlns:ei="${xsd.soap.item.item}"
   xmlns:mdr="${xsd.soap.common.mdrecords}"
   xmlns:mdp="${xsd.metadata.escidocprofile}"
   xmlns:e="http://escidoc.mpg.de/metadataprofile/schema/0.1/types"
   xmlns:ec="${xsd.soap.item.components}"
   xmlns:prop="${xsd.soap.common.prop}"
> -->

	<xsl:output method="xml" encoding="UTF-8" indent="yes"/>
	
	<xsl:param name="useAuthorList" select="false()"/>
	<xsl:param name="removeSpacesInInitials" select="true()"/>
	
	<xsl:param name="user" select="'dummy-user'"/>
	<xsl:param name="context" select="'escidoc:31013'"/>
	
	<!--
		DC XML  Header
	-->
	<xsl:include href="src/main/resources/languages.xml"/>
	<xsl:include href="src/main/resources/functions.xslt"/>
	<xsl:include href="src/main/resources/mpipl_authors.xml"/>
	<xsl:include href="src/main/resources/mpipl_ous.xml"/>
	<xsl:include href="src/main/resources/mpipl_collections.xml"/>
	
	<xsl:variable name="dependentGenre">
			<type>Article</type>
			<type>Conference-Paper</type>
			<type>Conference-Report</type>
			<type>InBook</type>
			<type>Issue</type>
			<type>Paper</type>
			<type>Poster</type>
			<type>Talk at Event</type>
		</xsl:variable>
	
	<xsl:template match="/*">
		<item-list>
			<xsl:apply-templates select="record/metadata"/>		
		</item-list>
		<!-- CREATE YEARBOOK 2009 COLLECTION -->
		<xsl:result-document href="yb-2009-collection.xml" method="xml" encoding="UTF-8" indent="yes">
			<xsl:element name="yearbook-collection">
				<xsl:for-each select="record/MPGyearbook[.='2009']">
					<xsl:element name="item">
						<xsl:attribute name="edoc-id" select="../@id"/>
						<xsl:copy-of select="@status"/>
					</xsl:element>
				</xsl:for-each>
			</xsl:element>
		</xsl:result-document>
	</xsl:template>

	<xsl:template match="record/metadata">
		<xsl:element name="ei:item">
			<xsl:element name="ei:properties">
				<xsl:element name="srel:context">
					<xsl:attribute name="xlink:href" select="concat('/ir/context/', $context)"/>
				</xsl:element>
				<srel:content-model xlink:href="/cmm/content-model/escidoc:persistent4"/>
				<xsl:element name="prop:content-model-specific"/>
			</xsl:element>
			<xsl:element name="mdr:md-records">
				<mdr:md-record name="escidoc">
					<xsl:apply-templates select="basic"/>	
				</mdr:md-record>
			</xsl:element>	
			<xsl:element name="ec:components">
				<xsl:for-each select="basic/fturl">
					<xsl:call-template name="createComponent"/>
				</xsl:for-each>				
			</xsl:element>
		</xsl:element>
	</xsl:template>
	
	<xsl:template name="createComponent">		
		<xsl:element name="ec:component">
			<ec:properties>
				<!-- <prop:valid-status>valid</prop:valid-status> -->
				<prop:visibility>private</prop:visibility>
				<prop:content-category>any-fulltext</prop:content-category>
				<prop:mime-type>application/pdf</prop:mime-type>
			</ec:properties>
			<xsl:element name="ec:content">					
				<xsl:attribute name="xlink:href" select="."/>
				<xsl:attribute name="storage" select="'internal-managed'"/>
			</xsl:element>				
			<xsl:element name="mdr:md-records">
				<mdr:md-record name="escidoc">
					<xsl:element name="file:file">
						<xsl:element name="dc:title">
							<xsl:value-of select="@filename"/>
						</xsl:element>
						<xsl:element name="dc:identifier">
							<xsl:attribute name="xsi:type" select="'eidt:URI'"/>
							<xsl:value-of select="."/>
						</xsl:element>
						<xsl:element name="file:content-category">any-fulltext</xsl:element>
						<dc:format xsi:type="dcterms:IMT">application/pdf</dc:format>
						<dcterms:extent><xsl:value-of select="@size"/></dcterms:extent>
						<xsl:element name="dc:format">
							<xsl:value-of select="concat('eDoc_access: ', @viewftext)"/>
						</xsl:element>
					</xsl:element>
				</mdr:md-record>
			</xsl:element>
		</xsl:element>	
		<xsl:element name="ec:component">
			<ec:properties>
				<!-- <prop:valid-status>valid</prop:valid-status> -->
				<prop:visibility>private</prop:visibility>
				<prop:content-category>any-fulltext</prop:content-category>
			</ec:properties>
			<xsl:element name="ec:content">		
				<xsl:attribute name="xlink:href" select="."/>
				<xsl:attribute name="storage" select="'external-url'"/>
			</xsl:element>	
			<xsl:element name="mdr:md-records">
				<mdr:md-record name="escidoc">
					<xsl:element name="file:file">
						<xsl:element name="dc:title">
							<xsl:value-of select="@filename"/>
						</xsl:element>
						<xsl:element name="dc:identifier">
							<xsl:attribute name="xsi:type" select="'eidt:URI'"/>
							<xsl:value-of select="."/>
						</xsl:element>
						<xsl:element name="dc:format">
							<xsl:value-of select="concat('eDoc_access: ', @viewftext)"/>
						</xsl:element>
					</xsl:element>
				</mdr:md-record>
			</xsl:element>
		</xsl:element>	
	</xsl:template>
	
	<!-- BASIC -->
	<xsl:template match="basic">
		<xsl:variable name="genre"/>					
			<xsl:choose>
				<xsl:when test="genre='Article'">
					<xsl:variable name="genre" select="'article'"/>					
					<xsl:call-template name="createEntry">
						<xsl:with-param name="gen" select="'article'"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:when test="genre='Report'">
					<xsl:variable name="genre" select="'report'"/>					
					<xsl:call-template name="createEntry">
						<xsl:with-param name="gen" select="'report'"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:when test="genre='Book'">
					<xsl:variable name="genre" select="'book'"/>					
					<xsl:call-template name="createEntry">
						<xsl:with-param name="gen" select="'book'"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:when test="genre='Conference-Paper'">
					<xsl:variable name="genre" select="'conference-paper'"/>					
					<xsl:call-template name="createEntry">
						<xsl:with-param name="gen" select="'conference-paper'"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:when test="genre='Conference-Report'">
					<xsl:variable name="genre" select="'conference-report'"/>					
					<xsl:call-template name="createEntry">
						<xsl:with-param name="gen" select="'conference-report'"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:when test="genre='Habilitation'">
					<xsl:variable name="genre" select="'thesis'"/>					
					<xsl:call-template name="createEntry">
						<xsl:with-param name="gen" select="'thesis'"/>
					</xsl:call-template>					
				</xsl:when>
				<xsl:when test="genre='InBook'">
					<xsl:variable name="genre" select="'book-item'"/>					
					<xsl:call-template name="createEntry">
						<xsl:with-param name="gen" select="'book-item'"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:when test="genre='Issue'">
					<xsl:variable name="genre" select="'issue'"/>					
					<xsl:call-template name="createEntry">
						<xsl:with-param name="gen" select="'issue'"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:when test="genre='Interactive Resource'">
					<xsl:variable name="genre" select="'other'"/>					
					<xsl:call-template name="createEntry">
						<xsl:with-param name="gen" select="'other'"/>
					</xsl:call-template>
				</xsl:when>				
				<xsl:when test="genre='Journal'">
					<xsl:variable name="genre" select="'journal'"/>					
					<xsl:call-template name="createEntry">
						<xsl:with-param name="gen" select="'journal'"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:when test="genre='Lecture / Courseware'">
					<xsl:variable name="genre" select="'courseware-lecture'"/>					
					<xsl:call-template name="createEntry">
						<xsl:with-param name="gen" select="'courseware-lecture'"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:when test="genre='Other'">
					<xsl:variable name="genre" select="'other'"/>					
					<xsl:call-template name="createEntry">
						<xsl:with-param name="gen" select="'other'"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:when test="genre='Paper'">
					<xsl:variable name="genre" select="'paper'"/>					
					<xsl:call-template name="createEntry">
						<xsl:with-param name="gen" select="'paper'"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:when test="genre='PhD-Thesis'">
					<xsl:variable name="genre" select="'thesis'"/>					
					<xsl:call-template name="createEntry">
						<xsl:with-param name="gen" select="'thesis'"/>
					</xsl:call-template>					
				</xsl:when>
				<xsl:when test="genre='Poster'">
					<xsl:variable name="genre" select="'poster'"/>					
					<xsl:call-template name="createEntry">
						<xsl:with-param name="gen" select="'poster'"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:when test="genre='Proceedings'">
					<xsl:variable name="genre" select="'proceedings'"/>					
					<xsl:call-template name="createEntry">
						<xsl:with-param name="gen" select="'proceedings'"/>
					</xsl:call-template>					
				</xsl:when>
				<xsl:when test="genre='Series'">
					<xsl:variable name="genre" select="'series'"/>					
					<xsl:call-template name="createEntry">
						<xsl:with-param name="gen" select="'series'"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:when test="genre='Software'">
					<xsl:variable name="genre" select="'other'"/>					
					<xsl:call-template name="createEntry">
						<xsl:with-param name="gen" select="'other'"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:when test="genre='Talk at Event'">
					<xsl:variable name="genre" select="'talk-at-event'"/>					
					<xsl:call-template name="createEntry">
						<xsl:with-param name="gen" select="'talk-at-event'"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:when test="genre='Thesis'">
					<xsl:variable name="genre" select="'thesis'"/>					
					<xsl:call-template name="createEntry">
						<xsl:with-param name="gen" select="'thesis'"/>
					</xsl:call-template>					
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="error(QName('http://www.escidoc.de', 'err:UnknownGenre' ), concat(genre, ' is not mapped to an eSciDoc publication genre'))"/>
				</xsl:otherwise>
			</xsl:choose>	
	</xsl:template>
	
	<!-- Create eSciDoc Entry -->
	<xsl:template name="createEntry">
		<xsl:param name="gen"/>
		
		<xsl:element name="mdp:publication">		
			<xsl:attribute name="type" select="$gen"/>	
			<!-- creator -->
			<xsl:for-each select="../creators/creator">				
				<xsl:element name="publ:creator">
					<xsl:call-template name="createCreator"/>					
				</xsl:element>
			</xsl:for-each>
			<xsl:apply-templates select="corporatebody"/>
			<!-- TITLE -->
			<xsl:element name="dc:title">
				<xsl:value-of select="title"/>
			</xsl:element>
			<!--ALTTITLE --> 
			<xsl:apply-templates select="titlealt"/>
			<!-- LANGUAGE -->
			<xsl:apply-templates select="language"/>
			<!-- IDENTIFIER -->
			<xsl:call-template name="createIdentifier"/>			
			<!-- PUBLISHING-INFO -->
			<xsl:choose>				
				<xsl:when test="$gen='book' or $gen='proceedings' or $gen='thesis'">
					<!-- case: book or proceedings -->
					<xsl:element name="publ:publishing-info">				
						<xsl:call-template name="createPublishinginfo"/>
					</xsl:element>					
				</xsl:when>
				<xsl:when test="$gen='book-item'">
					<!-- case: book-item without source book -->
					<xsl:if test="not(exists(booktitle))">
						<xsl:element name="publ:publishing-info">
							<xsl:call-template name="createPublishinginfo"/>
						</xsl:element>
					</xsl:if>
				</xsl:when>	
							
			</xsl:choose>
			
			<!-- DATES -->
			<xsl:apply-templates select="datemodified"/>
			<xsl:apply-templates select="datesubmitted"/>
			<xsl:apply-templates select="dateaccepted"/>			
			<xsl:apply-templates select="datepublished"/>		
			<!-- DEGREE -->	
			<xsl:if test="genre='PhD-Thesis'">
				<xsl:element name="publ:degree">	
					<xsl:value-of select="'phd'"/>
				</xsl:element>
			</xsl:if>
			<xsl:if test="genre='Habilitation'">
				<xsl:element name="mdp:degree">	
					<xsl:value-of select="'habilitation'"/>
				</xsl:element>
			</xsl:if>
			<!-- EVENT -->
			<xsl:if test="exists(nameofevent)">
				<xsl:call-template name="createEvent"/>
			</xsl:if>
			
			<!-- TOTAL NUMBER OF PAGES -->
			<xsl:choose>
				<xsl:when test="$gen='book-item' and not(exists(booktitle))">
					<xsl:call-template name="phydescPubl"/>
				</xsl:when>
				<xsl:when test="$gen='conference-paper' and not(exists(titleofproceedings)) and exists(phydesc)">
					<xsl:call-template name="phydescPubl"/>
				</xsl:when>
				<xsl:when test="$gen=$dependentGenre/type">
					<xsl:if test="not(exists(titleofproceedings)) and not(exists(booktitle)) and not(exists(issuetitle)) and not(exists(journaltitle)) and not(exists(titleodseries))">
						<xsl:call-template name="phydescPubl"/>	
					</xsl:if>					
				</xsl:when>				
			</xsl:choose>			
			<!-- ABSTRACT -->
			<xsl:apply-templates select="abstract"/>
			<!-- SUBJECT -->
			<xsl:apply-templates select="discipline"/>
			<xsl:apply-templates select="keywords"/>
			<!-- TOC -->
			<xsl:apply-templates select="toc"/>
			<!-- REVIEW METHOD -->
			<xsl:apply-templates select="reviewType"/>
			<!-- SOURCE -->			
			<xsl:choose>
				<xsl:when test="journaltitle">
					<xsl:element name="publ:source">
						<xsl:call-template name="createJournal"/>
					</xsl:element>
					<xsl:if test="issuetitle">
						<xsl:element name="publ:source">
							<xsl:call-template name="createIssue"/>
						</xsl:element>
					</xsl:if>
				</xsl:when>
				<xsl:when test="issuetitle">
					<xsl:element name="publ:source">
						<xsl:call-template name="createIssue"/>
					</xsl:element>
				</xsl:when>
				<xsl:when test="booktitle">
					<xsl:element name="publ:source">
						<xsl:call-template name="createBook"/>
					</xsl:element>
					<xsl:if test="titleofseries">
						<xsl:element name="publ:source">
							<xsl:call-template name="createSeries"/>
						</xsl:element>
					</xsl:if>
				</xsl:when>
				<xsl:when test="titleofproceedings">
					<xsl:element name="publ:source">
						<xsl:call-template name="createProceedings"/>
					</xsl:element>
				</xsl:when>
				<xsl:when test="titleofseries">
					<xsl:element name="publ:source">
						<xsl:call-template name="createSeries"/>
					</xsl:element>
				</xsl:when>
			</xsl:choose>
			<!-- isPartOf RELATION -->	
			<xsl:if test="../relations/relation[@reltype='ispartof']">
				<xsl:element name="publ:source">
					<xsl:attribute name="type" select="'series'"/>
					<xsl:element name="dc:title">
						<xsl:value-of select="."/>
					</xsl:element>
				</xsl:element>
			</xsl:if>
			<xsl:apply-templates select="phydesc"/>
			<!--end publication-->	
		</xsl:element>				
	</xsl:template>
	
	<xsl:template name="createPublishinginfo">
		<xsl:apply-templates select="publisher"/>
		<xsl:apply-templates select="publisheradd"/>
		<xsl:apply-templates select="editiondescription"/>
	</xsl:template>
	
	<xsl:template match="corporatebody">
		<xsl:call-template name="createPublCreatorOrga"/>
	</xsl:template>
	<xsl:template match="issuecorporatebody">
		<xsl:call-template name="createSourceCreatorOrga"/>
	</xsl:template>
	<xsl:template match="seriescorporatebody">
		<xsl:call-template name="createSourceCreatorOrga"/>
	</xsl:template>
	<xsl:template match="bookcorporatebody">
		<xsl:call-template name="createSourceCreatorOrga"/>
	</xsl:template>
	
	<xsl:template name="createPublCreatorOrga">
		<xsl:element name="publ:creator">
			<xsl:attribute name="role" select="'editor'"/>
			<xsl:element name="e:organization">
				<xsl:element name="e:organization-name">
					<xsl:value-of select="."/>
				</xsl:element>
				<e:identifier><xsl:value-of select="$organizational-units/ou[@name = .]/@id"/></e:identifier>
			</xsl:element>
		</xsl:element>
	</xsl:template>
	
	<xsl:template name="createSourceCreatorOrga">
		<xsl:element name="e:creator">
			<xsl:attribute name="role" select="'editor'"/>
			<xsl:element name="e:organization">
				<xsl:element name="e:organization-name">
					<xsl:value-of select="."/>
				</xsl:element>
				<e:identifier><xsl:value-of select="$organizational-units/ou[@name = .]/@id"/></e:identifier>
			</xsl:element>
		</xsl:element>
	</xsl:template>
	
	<xsl:template name="createIdentifier">
		<!-- eDoc ID -->
		<xsl:element name="dc:identifier">
			<xsl:attribute name="xsi:type" select="'eidt:EDOC'"/>
			<xsl:value-of select="../../@id"/>
		</xsl:element>		
		<xsl:for-each select="../identifiers/identifier">
			<xsl:call-template name="createOtherIDs"/>
		</xsl:for-each>
	</xsl:template>
	
	<xsl:template name="createOtherIDs">
		<xsl:element name="dc:identifier">
			<xsl:choose>
				<xsl:when test="@type='doi'">
					<xsl:attribute name="xsi:type" select="'eidt:DOI'"/>
					<xsl:value-of select="."/>
				</xsl:when>
				<xsl:when test="@type='issn'">
					<xsl:attribute name="xsi:type" select="'eidt:ISSN'"/>
					<xsl:value-of select="."/>
				</xsl:when>
				<xsl:when test="@type='isbn'">
					<xsl:attribute name="xsi:type" select="'eidt:ISBN'"/>
					<xsl:value-of select="."/>
				</xsl:when>
				<xsl:when test="@type='uri'">
					<xsl:attribute name="xsi:type" select="'eidt:URI'"/>
					<xsl:value-of select="."/>
				</xsl:when>									
				<xsl:when test="@type='isi'">
					<xsl:attribute name="xsi:type" select="'eidt:ISI'"/>
					<xsl:value-of select="."/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:attribute name="xsi:type" select="'eidt:OTHER'"/>
					<xsl:value-of select="."/>
				</xsl:otherwise>				
			</xsl:choose>
		</xsl:element>
	</xsl:template>
	
<!-- ***********************************************SOURCE TEMPLATES ***************************************************************** -->	


	<!-- JOURNAL TEMPLATE -->	
	<xsl:template name="createJournal">
		<!-- TITLE -->		
		<xsl:if test="journaltitle">			
			<xsl:attribute name="type" select="'journal'"/>
			<xsl:element name="dc:title">
				<xsl:value-of select="journaltitle"/>						
			</xsl:element>
		</xsl:if>		
		<!-- ALTERNATIVE TITLE -->
		<xsl:apply-templates select="journalabbreviation"/>
		
		<!-- VOLUME -->
		<xsl:apply-templates select="volume"/>		
		<xsl:if test="not(exists(issuetitle))">
			<!-- ISSUE -->
			<xsl:apply-templates select="issuenr"/>
			<!-- START_PAGE -->
			<xsl:apply-templates select="spage"/>
			<!-- END-PAGE -->
			<xsl:apply-templates select="epage"/>
			<!-- SEQUENCE_NR -->
			<xsl:apply-templates select="artnum"/>
		</xsl:if>	
		<!-- PUBLISHININFO -->
		<xsl:if test="not(exists(issuetitle))">
			<xsl:element name="e:publishing-info">
				<xsl:call-template name="createPublishinginfo"/>
			</xsl:element>
		</xsl:if>		
	</xsl:template>
	
	<!-- ISSUE TEMPLATE -->	
	<xsl:template name="createIssue">
		<!-- TITLE -->		
		<xsl:if test="issuetitle">			
			<xsl:attribute name="type" select="'issue'"/>
			<xsl:element name="dc:title">
				<xsl:value-of select="issuetitle"/>						
			</xsl:element>			
		</xsl:if>
		<!-- CREATOR -->
		<xsl:for-each select="creators/creator">				
			<xsl:element name="e:creator">
				<xsl:call-template name="createCreator"/>					
			</xsl:element>
		</xsl:for-each>		
		<xsl:apply-templates select="issuecorporatebody"/>
		<!-- START_PAGE -->
		<xsl:apply-templates select="spage"/>
		<!-- END-PAGE -->
		<xsl:apply-templates select="epage"/>
		<!-- SEQUENCE_NR -->
		<xsl:apply-templates select="artnum"/>			
	</xsl:template>
	
	<!-- BOOK TEMPLATE -->	
	<xsl:template name="createBook">
		<!-- TITLE -->		
		<xsl:if test="booktitle">					
			<xsl:attribute name="type" select="'book'"/>
			<xsl:element name="dc:title">
				<xsl:value-of select="booktitle"/>						
			</xsl:element>		
		</xsl:if>		
		<!-- CREATOR -->	
		<xsl:for-each select="creators/creator">				
			<xsl:element name="e:creator">
				<xsl:call-template name="createCreator"/>					
			</xsl:element>
		</xsl:for-each>
		<xsl:apply-templates select="bookcorporatebody"/>		
		<!-- VOLUME -->
		<xsl:apply-templates select="volume"/>
		<!-- START_PAGE -->
		<xsl:apply-templates select="spage"/>
		<!-- END-PAGE -->
		<xsl:apply-templates select="epage"/>
		<!-- SEQUENCE_NR -->
		<xsl:apply-templates select="artnum"/>
		<!--NUMBER OF PAGES -->		
		<xsl:if test="phydesc and exists(booktitle)"> 			
			<xsl:call-template name="phydescSource"/>					
		</xsl:if>		
		<xsl:if test="exists(publisher) or exists(editiondescription)">
			<xsl:element name="e:publishing-info">
				<xsl:call-template name="createPublishinginfo"/>
			</xsl:element>
		</xsl:if>
	</xsl:template>
	
	<xsl:template name="phydescPubl">
		<xsl:element name="publ:total-number-of-pages">
			<xsl:value-of select="phydesc"/>
		</xsl:element>
	</xsl:template>	
	
	<xsl:template name="phydescSource">
		<xsl:element name="publ:total-number-of-pages">
			<xsl:value-of select="phydesc"/>
		</xsl:element>
	</xsl:template>	
	
	<xsl:template match="phydesc">
		<xsl:element name="publ:total-number-of-pages">
			<xsl:value-of select="."/>
		</xsl:element>		
	</xsl:template>
	
	<xsl:template match="publisheradd">
		<xsl:element name="e:place">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="editiondescription">
		<xsl:element name="e:edition">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	
	<!-- SERIES TEMPLATE -->	
	<xsl:template name="createSeries">
		<!-- TITLE -->			
		<xsl:if test="titleofseries">					
			<xsl:attribute name="type" select="'series'"/>
			<xsl:element name="dc:title">
				<xsl:value-of select="titleofseries"/>						
			</xsl:element>
		</xsl:if>		
		<!-- CREATOR -->
		<xsl:for-each select="creators/creator">				
			<xsl:element name="e:creator">
				<xsl:call-template name="createCreator"/>					
			</xsl:element>
		</xsl:for-each>
		<xsl:apply-templates select="seriescorporatebody"/>		
		<!-- VOLUME -->
		<xsl:apply-templates select="volume"/>
	</xsl:template>
	
	<!-- PROCEEDINGS TEMPLATE -->
	<xsl:template name="createProceedings">
		<!-- TITLE -->				
		<xsl:if test="titleofproceedings">
			<xsl:attribute name="type" select="'proceedings'"/>
			<xsl:element name="dc:title">
				<xsl:value-of select="titleofproceedings"/>						
			</xsl:element>			
			<xsl:if test="editiondescrition">
				<xsl:element name="e:volume">
					<xsl:value-of select="editiondescription"/>
				</xsl:element>
			</xsl:if>
			<xsl:if test="phydesc"> 
				<xsl:call-template name="phydescSource"/>
			</xsl:if>
			<xsl:if test="exists(publisher) or exists(editiondescription)">
				<xsl:element name="e:publishing-info">
					<xsl:call-template name="createPublishinginfo"/>
				</xsl:element>
			</xsl:if>
		</xsl:if>
		<!-- CREATOR -->
		<xsl:for-each select="creators/creator">				
			<xsl:element name="e:creator">
				<xsl:call-template name="createCreator"/>					
			</xsl:element>
		</xsl:for-each>
		<!-- START_PAGE -->
		<xsl:apply-templates select="spage"/>
		<!-- END-PAGE -->
		<xsl:apply-templates select="epage"/>
		
	</xsl:template>
	
	
	<xsl:template match="volume">
		<e:volume>
			<xsl:value-of select="."/>
		</e:volume>
	</xsl:template>
	
	
	<xsl:template name="createCreator">
		<!-- CREATOR ROLE -->
		<xsl:choose>
			<xsl:when test="@role='advisor'">
				<xsl:attribute name="role" select="'advisor'"/>
			</xsl:when>
			<xsl:when test="@role='artist'">
				<xsl:attribute name="role" select="'artist'"/>
			</xsl:when>
			<xsl:when test="@role='author'">
				<xsl:attribute name="role" select="'author'"/>
			</xsl:when>
			<xsl:when test="@role='contributor'">
				<xsl:attribute name="role" select="'contributor'"/>
			</xsl:when>
			<xsl:when test="@role='editor'">
				<xsl:attribute name="role" select="'editor'"/>
			</xsl:when>			
			<xsl:when test="@role='painter'">
				<xsl:attribute name="role" select="'painter'"/>
			</xsl:when>
			<xsl:when test="@role='referee'">
				<xsl:attribute name="role" select="'referee'"/>
			</xsl:when>
			<xsl:when test="@role='translator'">
				<xsl:attribute name="role" select="'translator'"/>
			</xsl:when>			
			<xsl:otherwise>				
				<xsl:value-of select="error(QName('http://www.escidoc.de', 'err:CreatorRoleNotMapped' ), concat(@role, ' is not mapped to an eSciDoc creator role'))"/>
			</xsl:otherwise>
		</xsl:choose>
		<!-- CREATOR -->
		<xsl:variable name="creatornfamily" select="creatornfamily"/>
		<xsl:variable name="creatorngiven" select="creatorngiven"/>
		<xsl:variable name="creatorngivenNew"><xsl:choose><xsl:when test="$removeSpacesInInitials"><xsl:value-of select="replace(creatorngiven, '([A-Z][a-z]*\.) ([A-Z][a-z]*\.) ?([A-Z][a-z]*\.)? ?([A-Z][a-z]*\.)? ?([A-Z][a-z]*\.)? ?([A-Z][a-z]*\.)? ?([A-Z][a-z]*\.)? ?([A-Z][a-z]*\.)?', '$1$2$3$4$5$6$7$8')"/></xsl:when><xsl:otherwise><xsl:value-of select="creatorngiven"/></xsl:otherwise></xsl:choose></xsl:variable>
		<xsl:variable name="creatoriniNew"><xsl:choose>
				<xsl:when test="$removeSpacesInInitials"><xsl:value-of select="replace(creatorini, '([A-Z][a-z]*\.) ([A-Z][a-z]*\.) ?([A-Z][a-z]*\.)? ?([A-Z][a-z]*\.)? ?([A-Z][a-z]*\.)? ?([A-Z][a-z]*\.)? ?([A-Z][a-z]*\.)? ?([A-Z][a-z]*\.)?', '$1$2$3$4$5$6$7$8')"/></xsl:when>
				<xsl:otherwise><xsl:value-of select="creatorini"/></xsl:otherwise>
			</xsl:choose></xsl:variable>
		<xsl:choose>
			<xsl:when test="@creatorType='individual'">
				<xsl:choose>
					<xsl:when test="$authors/authors/author[aliases/alias[familyname = $creatornfamily and givenname = $creatorngiven]]">
						
						<xsl:variable name="author" select="$authors/authors/author[aliases/alias[familyname = $creatornfamily and givenname = $creatorngiven]]"/>
						
						<e:person>
							<xsl:choose>
								<xsl:when test="$useAuthorList">
									<e:complete-name>
										<xsl:value-of select="$author/givenname"/>
										<xsl:text> </xsl:text>
										<xsl:value-of select="$author/familyname"/>
									</e:complete-name>
									<e:family-name>
										<xsl:value-of select="$author/familyname"/>
									</e:family-name>
									<e:given-name>
										<xsl:value-of select="$author/givenname"/>
									</e:given-name>
								</xsl:when>
								<xsl:otherwise>
									<e:complete-name><xsl:value-of select="$creatorngivenNew"/><xsl:text> </xsl:text><xsl:value-of select="$creatornfamily"/></e:complete-name>
									<e:family-name><xsl:value-of select="$creatornfamily"/></e:family-name>
									<e:given-name><xsl:value-of select="$creatorngivenNew"/></e:given-name>
								</xsl:otherwise>
							</xsl:choose>

							<e:identifier>
								<xsl:value-of select="$author/@id"/>
							</e:identifier>

							<xsl:variable name="author-organizational-units">
								<ous>
									<xsl:copy-of select="$organizational-units//ou[@name = $author/departments/department]"/>
								</ous>
							</xsl:variable>

							<xsl:if test="not($author-organizational-units/ous/ou)">
								<e:organization>
									<e:organization-name>Organizational unit (<xsl:value-of select="$author/departments/department"/>) not found in tree.</e:organization-name>
								</e:organization>
							</xsl:if>
							
							<xsl:for-each select="$author-organizational-units/ous/ou">
								<e:organization>
									<e:organization-name>
										<xsl:value-of select="escidoc:ou-name(@name)"/>
									</e:organization-name>
									<e:identifier>
										<xsl:value-of select="escidoc:ou-id(@name)"/>
									</e:identifier>
								</e:organization>
							</xsl:for-each>
							
							<xsl:variable name="collection" select="../../../docaff/collection"/>

							<xsl:if test="$collection-mapping/mapping[edoc-collection = $collection] and count($author-organizational-units/ous/ou[@name = $collection-mapping/mapping[edoc-collection = $collection]/escidoc-ou]) = 0">
								<e:organization>
									<e:organization-name>
										<xsl:value-of select="escidoc:ou-name($collection-mapping/mapping[edoc-collection = $collection]/escidoc-ou)"/>
									</e:organization-name>
									<e:identifier>
										<xsl:value-of select="escidoc:ou-id($collection-mapping/mapping[edoc-collection = $collection]/escidoc-ou)"/>
									</e:identifier>
								</e:organization>
							</xsl:if>
							
						</e:person>
												
					</xsl:when>
					<xsl:otherwise>
						<xsl:element name="e:person">
							<xsl:element name="e:complete-name">
								<xsl:value-of select="concat($creatorngivenNew, ' ', creatornfamily)"/>
							</xsl:element>
							<xsl:element name="e:family-name"><xsl:value-of select="creatornfamily"/></xsl:element>				
							<xsl:choose>
								<xsl:when test="exists(creatorngiven) and not(creatorngiven='')">
									<xsl:element name="e:given-name"><xsl:value-of select="$creatorngivenNew"/></xsl:element>
								</xsl:when>
								<xsl:otherwise>
									<xsl:element name="e:given-name"><xsl:value-of select="$creatoriniNew"/></xsl:element>
								</xsl:otherwise>
							</xsl:choose>	
							<xsl:choose>
								<xsl:when test="@internextern='mpg'">
									<xsl:for-each select="../../../docaff/affiliation">
										<xsl:element name="e:organization">
											<xsl:element name="e:organization-name">
												<xsl:choose>
													<xsl:when test="mpgsunit">
														<xsl:value-of select="escidoc:ou-name(mpgsunit)"/>
													</xsl:when>
													<xsl:otherwise>
														<xsl:value-of select="escidoc:ou-name(mpgunit)"/>
													</xsl:otherwise>
												</xsl:choose>							
											</xsl:element>
											<e:identifier>
												<xsl:choose>
													<xsl:when test="mpgsunit">
														<xsl:value-of select="escidoc:ou-id(mpgsunit)"/>
													</xsl:when>
													<xsl:otherwise>
														<xsl:value-of select="escidoc:ou-id(mpgunit)"/>
													</xsl:otherwise>
												</xsl:choose>		
											</e:identifier>
										</xsl:element>
									</xsl:for-each>
									
									<xsl:variable name="collection" select="../../../docaff/collection"/>

									<xsl:if test="$collection-mapping/mapping[edoc-collection = $collection] and not(../../../docaff/affiliation/*[. = $collection])">
										<e:organization>
											<e:organization-name>
												<xsl:value-of select="escidoc:ou-name($collection-mapping/mapping[edoc-collection = $collection]/escidoc-ou)"/>
											</e:organization-name>
											<e:identifier>
												<xsl:value-of select="escidoc:ou-id($collection-mapping/mapping[edoc-collection = $collection]/escidoc-ou)"/>
											</e:identifier>
										</e:organization>
									</xsl:if>
												
								</xsl:when>
								<xsl:when test="@internextern='unknown' and not(../creator[@internextern = 'mpg']) and ../../../docaff/affiliation and not(../../../docaff_external)">						
									<xsl:for-each select="../../../docaff/affiliation">
										<xsl:element name="e:organization">
											<xsl:element name="e:organization-name">
												<xsl:choose>
													<xsl:when test="mpgsunit">
														<xsl:value-of select="escidoc:ou-name(mpgsunit)"/>
													</xsl:when>
													<xsl:otherwise>
														<xsl:value-of select="escidoc:ou-name(mpgunit)"/>
													</xsl:otherwise>
												</xsl:choose>							
											</xsl:element>
											<e:identifier>
												<xsl:choose>
													<xsl:when test="mpgsunit">
														<xsl:value-of select="escidoc:ou-id(mpgsunit)"/>
													</xsl:when>
													<xsl:otherwise>
														<xsl:value-of select="escidoc:ou-id(mpgunit)"/>
													</xsl:otherwise>
												</xsl:choose>		
											</e:identifier>
										</xsl:element>
									</xsl:for-each>
									
									<xsl:variable name="collection" select="../../../docaff/collection"/>

									<xsl:if test="$collection-mapping/mapping[edoc-collection = $collection] and not(../../../docaff/affiliation/*[. = $collection])">
										<e:organization>
											<e:organization-name>
												<xsl:value-of select="escidoc:ou-name($collection-mapping/mapping[edoc-collection = $collection]/escidoc-ou)"/>
											</e:organization-name>
											<e:identifier>
												<xsl:value-of select="escidoc:ou-id($collection-mapping/mapping[edoc-collection = $collection]/escidoc-ou)"/>
											</e:identifier>
										</e:organization>
									</xsl:if>
														
								</xsl:when>
								<xsl:otherwise>
									<xsl:if test="../../../docaff/docaff_external">
										<e:organization>
											<e:organization-name>
												<xsl:value-of select="escidoc:ou-name(../../../docaff/docaff_external)"/>
											</e:organization-name>
											<e:identifier>
												<xsl:value-of select="escidoc:ou-id(../../../docaff/docaff_external)"/>
											</e:identifier>
										</e:organization>
									</xsl:if>
								</xsl:otherwise>
							</xsl:choose>		
						</xsl:element>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>				
				<xsl:element name="e:organization">
					<xsl:element name="e:organization-name">
						<xsl:value-of select="creatornfamily"/>
					</xsl:element>
				</xsl:element>
			</xsl:otherwise>
		</xsl:choose>				
	</xsl:template>
	
	<xsl:template match="title">
		<xsl:element name="dc:title">
			<xsl:value-of select="title"/>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="journaltitle">
		<xsl:attribute name="type" select="'journal'"/>
	</xsl:template>
	<xsl:template match="booktitle">
		<xsl:attribute name="type" select="'book'"/>
	</xsl:template>
	<xsl:template match="issuetitle">
		<xsl:attribute name="type" select="'issue'"/>
	</xsl:template>
	<xsl:template match="titleofseries">
		<xsl:attribute name="type" select="'series'"/>
	</xsl:template>
	<xsl:template match="titleofproceedings">
		<xsl:attribute name="type" select="'proceedings'"/>
	</xsl:template>
	
		
	
	
	<!-- REVIEW-METHOD TEMPLATE -->
	<xsl:template match="reviewType">
		<xsl:choose>
			<xsl:when test="reviewType='joureview'">
				<xsl:element name="mdp:review-method">
					<xsl:value-of select="'peer'"/>
				</xsl:element>
			</xsl:when>
			<xsl:when test="reviewType='notrev'">
				<xsl:element name="mdp:review-method">
					<xsl:value-of select="'no review'"/>
				</xsl:element>
			</xsl:when>
			<xsl:when test="reviewType='intrev'">
				<xsl:element name="mdp:review-method">
					<xsl:value-of select="'internal'"/>
				</xsl:element>
			</xsl:when>
			<xsl:otherwise></xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	
	
	<xsl:template match="issuecontributorfn">		
		<xsl:call-template name="parseContributor">
			<xsl:with-param name="role" select="'editor'"/>
		</xsl:call-template>	
	</xsl:template>
	<xsl:template match="proceedingscontributorfn">		
		<xsl:call-template name="parseContributor">
			<xsl:with-param name="role" select="'editor'"/>
		</xsl:call-template>	
	</xsl:template>
	<xsl:template match="seriescontributorfn">
		<xsl:call-template name="parseContributor">
			<xsl:with-param name="role" select="'editor'"/>
		</xsl:call-template>	
	</xsl:template>
	<xsl:template match="bookcontributorfn">
		<xsl:call-template name="parseContributor">
			<xsl:with-param name="role" select="'editor'"/>
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="bookcreatorfn">
		<xsl:call-template name="parseContributor">
			<xsl:with-param name="role" select="'author'"/>
		</xsl:call-template>
	</xsl:template>
	<xsl:template name="parseContributor">
		<xsl:param name="role"/>

		<xsl:element name="creatorstring">
			<xsl:attribute name="role" select="$role"/>				
				<xsl:value-of select="."/>									
		</xsl:element>
	</xsl:template>
	<xsl:template match="journalabbreviation">
		<xsl:element name="dcterms:alternative">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<!-- EVENT TEMPLATE -->
	<xsl:template name="createEvent">
		<xsl:element name="publ:event">		
			<xsl:element name="dc:title">
				<xsl:value-of select="nameofevent"/>
			</xsl:element>
			<xsl:element name="e:start-date">
				<xsl:value-of select="dateofevent"/>
			</xsl:element>
			<xsl:element name="e:end-date">
				<xsl:value-of select="enddateofevent"/>
			</xsl:element>
			<xsl:element name="e:place">
				<xsl:value-of select="placeofevent"/>
			</xsl:element>
			<xsl:apply-templates select="invitationStatus[.='invited']"/>
		</xsl:element>		
	</xsl:template>	
	
	<xsl:template match="invitationStatus[.='invited']">
		<xsl:element name="e:invitation-status">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="artnum">
		<e:sequence-number>
			<xsl:value-of select="."/>
		</e:sequence-number>
	</xsl:template>
	<xsl:template match="spage">
		<e:start-page>
			<xsl:value-of select="."/>
		</e:start-page>
	</xsl:template>
	<xsl:template match="epage">
		<e:end-page>
			<xsl:value-of select="."/>
		</e:end-page>
	</xsl:template>
	<xsl:template match="issuenr">
		<e:issue>
			<xsl:value-of select="."/>
		</e:issue>
	</xsl:template>
	<xsl:template match="toc">
		<xsl:element name="dcterms:tableOfContents">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="discipline">
		<xsl:element name="dc:subject">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="keywords">
		<xsl:element name="dc:subject">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="abstract">
		<xsl:element name="dcterms:abstract">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="datepublished">
		<xsl:element name="dcterms:issued">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="datemodified">
		<xsl:element name="dcterms:modified">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="dateaccepted">
		<xsl:element name="dcterms:dateAccepted">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="datesubmitted">
		<xsl:element name="dcterms:dateSubmitted">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="titlealt">
		<xsl:element name="dcterms:alternative">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="publisher">
		<xsl:element name="dc:publisher">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>	
	
	<xsl:template match="language">		
		<xsl:variable name="lang-label" select="."/>
		<xsl:element name="dc:language">
			<xsl:value-of select="$languages/language[$lang-label=@name][1]/@abbrev"/>
		</xsl:element>
	</xsl:template>
	
</xsl:stylesheet>
