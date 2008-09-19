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
	
	<xsl:variable name="user" select="'dummy-user'"/>
	<xsl:variable name="context" select="'escidoc:36441'"/>
	
	<!--
		DC XML  Header
	-->
	<xsl:include href="src/main/resources/languages.xml"/>
	<xsl:include href="src/main/resources/organizational-units.xml"/>

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
				<prop:valid-status>valid</prop:valid-status>
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
				<prop:valid-status>valid</prop:valid-status>
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
					<xsl:variable name="genre" select="'lecture-courseware'"/>					
					<xsl:call-template name="createEntry">
						<xsl:with-param name="gen" select="'lecture-courseware'"/>
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
				<xsl:when test="$gen='book-item'">
					<xsl:if test="not(exists(booktitle))">
						<xsl:element name="publ:publishing-info">
							<xsl:apply-templates select="publisher"/>
							<xsl:apply-templates select="editiondescription"/>
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
			<!-- TOTAL NUMBER OF PAGES -->
			<xsl:choose>
				<xsl:when test="$gen='book-item' and not(exists(booktitle))">
					<xsl:apply-templates select="phydescPubl"/>
				</xsl:when>
				<xsl:when test="$gen='conference-paper' and not(exists(titleofproceedings))">
					<xsl:apply-templates select="phydescPubl"/>
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
		</xsl:element><!--end publication-->				
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
		<xsl:apply-templates select="identifier"/>
	</xsl:template>
	
	<xsl:template match="identifier">
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
			<!-- SEQUENCE_NR -->
			<xsl:apply-templates select="artnum"/>
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
		<xsl:apply-templates select="issuecontributorfn"/>			
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
		<xsl:apply-templates select="bookcreatorfn"/>
		<xsl:apply-templates select="bookcontributorfn"/>
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
		<xsl:apply-templates select="phydescSource"/>
		
		<xsl:if test="exists(publisher) or exists(editiondescription)">
			<xsl:element name="e:publishing-info">
				<xsl:apply-templates select="publisher"/>
				<xsl:apply-templates select="publisheradd"/>
				<xsl:apply-templates select="editiondescription"/>
			</xsl:element>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="phydescPubl">
		<xsl:element name="publ:total-number-of-pages">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>	
	
	<xsl:template match="phydescSource">
		<xsl:element name="e:total-number-of-pages">
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
		<xsl:apply-templates select="seriescontributorfn"/>
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
			<xsl:apply-templates select="phydescSource"/>
		</xsl:if>
		<!-- CREATOR -->
		<xsl:apply-templates select="proceedingscontributorfn"/>
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
		<xsl:choose>
			<xsl:when test="@creatorType='individual'">
				<xsl:element name="e:person">
					<xsl:element name="e:complete-name">
						<xsl:value-of select="concat(creatorngiven, ' ', creatornfamily)"/>
					</xsl:element>
					<xsl:element name="e:family-name">
						<xsl:value-of select="creatornfamily"/>
					</xsl:element>				
					<xsl:choose>
						<xsl:when test="exists(creatorngiven) and not(creatorngiven='')">
							<xsl:element name="e:given-name">
								<xsl:value-of select="creatorngiven"/>
							</xsl:element>
						</xsl:when>
						<xsl:otherwise>
							<xsl:element name="e:given-name">
								<xsl:value-of select="creatorini"/>
							</xsl:element>
						</xsl:otherwise>
					</xsl:choose>	
					<xsl:choose>
						<xsl:when test="@internextern='mpg'">							
							<xsl:for-each select="../../../docaff/affiliation">
								<xsl:element name="e:organization">						
									<xsl:element name="e:organization-name">
										<xsl:value-of select="escidoc:ou(mpgunit, 'path')"/>
										<xsl:if test="mpgsunit">
											<xsl:value-of select="concat(', ', mpgsunit)"/>
										</xsl:if>							
									</xsl:element>
									<e:identifier><xsl:value-of select="escidoc:ou(mpgunit, 'id')"/></e:identifier>
								</xsl:element>
							</xsl:for-each>						
						</xsl:when>
						<xsl:when test="@internextern='unknown' and not(../creator[@internextern = 'mpg']) and ../../../docaff/affiliation and not(../../../docaff_external)">						
							<xsl:for-each select="../../../docaff/affiliation">
								<xsl:element name="e:organization">						
									<xsl:element name="e:organization-name">
										<xsl:value-of select="mpgunit"/>		
										<xsl:if test="mpgsunit">
											<xsl:value-of select="concat(', ', mpgsunit)"/>
										</xsl:if>							
									</xsl:element>
								</xsl:element>
							</xsl:for-each>						
						</xsl:when>
						<xsl:otherwise>
							<xsl:if test="docaff_external">
								<xsl:element name="e:organization">
									<xsl:element name="e:organization-name">
										<xsl:value-of select="."/>
									</xsl:element>
								</xsl:element>
							</xsl:if>
						</xsl:otherwise>
					</xsl:choose>		
				</xsl:element>
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
	
	<xsl:template match="nameofevent">
		<xsl:element name="publ:event">
			<xsl:call-template name="createEvent"/>
		</xsl:element>
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
