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
	Transformations from a GDZ Item to eSciDoc PubItem 
	Author: Andreas Gros (initial creation) 
	$Author: agros $ (last changed)
	$Revision: 0000 $ 
	$LastChangedDate: 2009-09-15 18:09:23 +0200 (Di, 15 Sep 2009) $
-->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions"
	xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:dc="${xsd.metadata.dc}" xmlns:dcterms="${xsd.metadata.dcterms}"
	xmlns:eidt="${xsd.metadata.escidocprofile.idtypes}" xmlns:srel="${xsd.soap.common.srel}"
	xmlns:pub="${xsd.metadata.publication}"
	xmlns:AuthorDecoder="java:de.mpg.escidoc.services.common.util.creators.AuthorDecoder"
	xmlns:escidoc="urn:escidoc:functions" xmlns:ei="${xsd.soap.item.item}"
	xmlns:mdr="${xsd.soap.common.mdrecords}" xmlns:mdp="${xsd.metadata.escidocprofile}"
	xmlns:e="${xsd.metadata.escidocprofile.types}" xmlns:ec="${xsd.soap.item.components}"
	xmlns:prop="${xsd.soap.common.prop}"
	xsi:schemaLocation="http://www.loc.gov/mods/v3 http://www.loc.gov/standards/mods/v3/mods-3-3.xsd http://www.loc.gov/METS/ http://www.loc.gov/standards/mets/version17/mets.v1-7.xsd"
	xmlns:mets="http://www.loc.gov/METS/" xmlns:xlin="http://www.w3.org/1999/xlink"
	xmlns:mods="http://www.loc.gov/mods/v3"
	xmlns:file="http://escidoc.mpg.de/metadataprofile/schema/0.1/file">


	<xsl:output method="xml" encoding="UTF-8" indent="yes"/>

	<xsl:param name="user" select="'dummy-user'"/>
	<!-- Kristina has to create a corresponding context 
	<xsl:param name="context" select="'escidoc:XXXXXX'"/>-->
	<xsl:param name="is-item-list" select="true()"/>

	<xsl:param name="filename" select="tokenize(document-uri(/),'/')[last()]"/>
	<xsl:param name="anchorfilename"
		select="concat(substring-before($filename, '.xml'), '_anchor.xml')"/>
	<xsl:param name="partName" select="document($anchorfilename)//mods:subTitle"/>
	<xsl:param name="ISSN" select="document($anchorfilename)//mods:identifier[@type='ISSN']"/>
	<xsl:param name="ZDBID" select="document($anchorfilename)//mods:identifier[@type='ZDBID']"/>

	<!--
		DC XML  Header
	-->

	<!-- VARIABLEN -->
	<xsl:variable name="volume" select="mets:mets/mets:dmdSec[@ID='DMDLOG_0001']//mods:partNumber"/>
	<xsl:variable name="year" select="mets:mets/mets:dmdSec[@ID='DMDLOG_0001']//mods:dateIssued"/>
	<xsl:variable name="publisher" select="mets:mets/mets:dmdSec[@ID='DMDLOG_0001']//mods:publisher"/>
	<xsl:variable name="publisherplace" select="mets:mets/mets:dmdSec[@ID='DMDLOG_0001']//mods:placeTerm"/>

	<xsl:template match="/">
		<xsl:choose>
			<xsl:when test="$is-item-list">
				<item-list>
					<xsl:apply-templates select="//mets:dmdSec"/>
				</item-list>
			</xsl:when>
			<xsl:when test="count(//mets:dmdSec) = 0">
				<xsl:value-of
					select="error(QName('http://www.escidoc.de', 'err:NoSourceForSingleTarget' ), 'Single item was selected as target, but the source contained no items')"
				/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of
					select="error(QName('http://www.escidoc.de', 'err:MultipleSourceForSingleTarget' ), 'Single item was selected as target, but the source contained multiple items')"
				/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="mets:dmdSec">
		<xsl:choose>
			<!-- SOURCE -->
			<!--VOLUME-->
			<xsl:when test="@ID = 'DMDLOG_0001' and .//mods:partName = 'Band'">
			
			</xsl:when>
			<!--ISSUE-->
			<xsl:when test="matches(@ID, 'DMDLOG_\d+') and .//mods:partName = 'Heft'">

			</xsl:when>
			<!--CONTRIBUTIONS-->
			<xsl:otherwise>
				<xsl:element name="ei:item">
					<xsl:element name="ei:properties">
						<srel:content-model objid="escidoc:persistent4"/>
						<xsl:element name="prop:content-model-specific"/>
					</xsl:element>
					<xsl:element name="mdr:md-records">
						<mdr:md-record name="escidoc">
							<xsl:call-template name="itemMetadata">
								<xsl:with-param name="publisher" select="$publisher"/>
								<xsl:with-param name="publisherplace" select="$publisherplace"/>
								<xsl:with-param name="volume" select="$volume"/>
								<xsl:with-param name="year" select="$year"/>
							</xsl:call-template>
						</mdr:md-record>
					</xsl:element>
					<xsl:element name="ec:components">
						<xsl:element name="ec:component">	
							<xsl:element name="ec:properties">	
								<xsl:element name="prop:visibility">
									<xsl:value-of select="'public'"/>
								</xsl:element>
								<xsl:element name="prop:content-category">
									<xsl:value-of select="'publisher-version'"/>
								</xsl:element>
								<xsl:call-template name="createFile">
									<xsl:with-param name="id" select=".//mods:identifier[@type='ArticleID']"/>
								</xsl:call-template>
								<xsl:element name="prop:mime-type">
									<xsl:value-of select="'application/pdf'"/>
								</xsl:element>
							</xsl:element>
						</xsl:element>
					</xsl:element>
				</xsl:element>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<!-- IDENTIFIER -->
	<xsl:template match="mods:identifier">
		<xsl:call-template name="createID">
			<xsl:with-param name="idtype">eidt:OTHER</xsl:with-param>
		</xsl:call-template>
	</xsl:template>


	<!-- GENRE -->
	<xsl:template name="itemMetadata">
		 <xsl:param name="volume"/>
		<xsl:param name="publisher"/>
		<xsl:param name="publisherplace"/>
		<xsl:param name="year"/> 
		<xsl:choose>
			<xsl:when test=".//mods:genre='Review'">
				<xsl:call-template name="createEntry">
					<xsl:with-param name="gen" select="'Article'"/>
					<xsl:with-param name="volume" select="$volume"/>
					<xsl:with-param name="year" select="$year"/>
					<xsl:with-param name="publisher" select="$publisher"/>
					<xsl:with-param name="publisherplace" select="$publisherplace"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test=".//mods:genre='Original Communication'">
				<xsl:call-template name="createEntry">
					<xsl:with-param name="gen" select="'Article'"/>
					<xsl:with-param name="volume" select="$volume"/>
					<xsl:with-param name="year" select="$year"/>
					<xsl:with-param name="publisher" select="$publisher"/>
					<xsl:with-param name="publisherplace" select="$publisherplace"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test=".//mods:genre='Report'">
				<xsl:call-template name="createEntry">
					<xsl:with-param name="gen" select="'Report'"/>
					<xsl:with-param name="volume" select="$volume"/>
					<xsl:with-param name="year" select="$year"/>
					<xsl:with-param name="publisher" select="$publisher"/>
					<xsl:with-param name="publisherplace" select="$publisherplace"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="createEntry">
					<xsl:with-param name="gen" select="'Other'"/>
					<xsl:with-param name="volume" select="$volume"/>
					<xsl:with-param name="year" select="$year"/>
					<xsl:with-param name="publisher" select="$publisher"/>
					<xsl:with-param name="publisherplace" select="$publisherplace"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Create eSciDoc Entry -->
	<xsl:template name="createEntry">
		<xsl:param name="gen"/>
		 <xsl:param name="year"/>
		<xsl:param name="volume"/>
		<xsl:param name="publisher"/>
		<xsl:param name="publisherplace"/>
		<xsl:variable name="identifier" select=".//mods:identifier"/>

		<xsl:element name="mdp:publication">
			<xsl:attribute name="type">
				<xsl:value-of select="$gen"/>
			</xsl:attribute>

			<!-- CREATOR -->
			<xsl:call-template name="createAuthors"/>


			<!-- TITLE -->
			<xsl:call-template name="createTitle">
				<xsl:with-param name="title" select=".//mods:title"/>
			</xsl:call-template>

			<!-- ALT -->


			<!-- LANGUAGE -->
			<xsl:if test=".//mods:language">
				<xsl:element name="dc:language">
					<xsl:attribute name="xsi:type" select="'dcterms:RFC3066'"/>
					<xsl:value-of select=".//mods:languageTerm"/>
				</xsl:element>
			</xsl:if>

			<!-- Source Issue -->
			<xsl:variable name="pos" select="position()"/>
			<xsl:apply-templates mode="createIssue" select="/mets:mets/mets:dmdSec[matches(@ID, 'DMDLOG_\d+') and (.//mods:partName = 'Heft') and (position() &lt; $pos)][last()]"/>
			
			<xsl:choose>
				<xsl:when test="@ID = 'DMDLOG_0001' and .//mods:partName = 'Band'">
					
				</xsl:when>
				<!--ISSUE-->
				<xsl:when test="matches(@ID, 'DMDLOG_\d+') and .//mods:partName = 'Heft'">
					
				</xsl:when>
			</xsl:choose>
			
			<!-- EVENT -->

			<!-- PAGES -->
			<xsl:if test=".//mods:start">
				<xsl:element name="pub:start-page">
					<xsl:value-of select=".//mods:start"/>
				</xsl:element>
				<xsl:if test=".//mods:end">
					<xsl:element name="pub:end-page">
						<xsl:value-of select=".//mods:end"/>
					</xsl:element>
				</xsl:if>
			</xsl:if>
			<xsl:choose>
				<xsl:when test=".//mods:total">
					<xsl:element name="pub:total-number-of-pages">
						<xsl:value-of select=".//mods:total"/>
					</xsl:element>
				</xsl:when>
				<xsl:otherwise>
					<xsl:if test=".//mods:start and .//mods:end">
						<xsl:element name="pub:total-number-of-pages">
							<xsl:value-of select=".//mods:end - .//mods:start"/>
						</xsl:element>
					</xsl:if>
				</xsl:otherwise>
			</xsl:choose>

			<!-- SUBJECT -->
			<xsl:call-template name="createSubject">
				<xsl:with-param name="gen" select=".//mods:genre"/>
			</xsl:call-template>


			<!--end publication-->
		</xsl:element>
	</xsl:template>



  <!-- SOURCE -->
	<xsl:template mode="createIssue" match="mets:dmdSec">
			<xsl:call-template name="createSource">
			<xsl:with-param name="title" select=".//mods:title"/>
			<xsl:with-param name="identifier" select=".//mods:identifier"/>
			<xsl:with-param name="publisher" select="$publisher"/>
			<xsl:with-param name="publisherplace" select="$publisherplace"/>
			<xsl:with-param name="volume" select="$volume"/>
			<xsl:with-param name="j_genre" select="'Journal'"/>
			<xsl:with-param name="issue" select=".//mods:partNumber"/>
			<xsl:with-param name="partName" select="$partName"/>
			<xsl:with-param name="ISSN" select="$ISSN"/>
			<xsl:with-param name="ZDBID" select="$ZDBID"/>
		</xsl:call-template>
	</xsl:template>	
	
	<!-- CREATOR -->
	<xsl:template name="createAuthors">
		<xsl:for-each select=".//mods:name[@type='personal']">
			<xsl:element name="pub:creator">
				<xsl:element name="e:person">
					<xsl:attribute name="role">author</xsl:attribute>
					<xsl:element name="e:complete-name">
						<xsl:value-of select="mods:displayForm"/>
					</xsl:element>
					<xsl:if test=".//mods:namePart/@type='family'">
						<xsl:element name="e:family-name">
							<xsl:value-of select="mods:namePart[@type='family']"/>
						</xsl:element>
					</xsl:if>
					<xsl:if test=".//mods:namePart/@type='given'">
						<xsl:element name="e:given-name">
							<xsl:value-of select="mods:namePart[@type='given']"/>
						</xsl:element>
					</xsl:if>
					<xsl:element name="e:organization">
						<xsl:element name="e:organization-name">
							<xsl:value-of select="'External Organizations'"/>
						</xsl:element>
					</xsl:element>
				</xsl:element>
			</xsl:element>
		</xsl:for-each>
	</xsl:template>




	<!-- TITLE -->
	<xsl:template name="createTitle">
		<xsl:param name="title"/>
		<xsl:element name="dc:title">
			<xsl:value-of select="$title"/>
		</xsl:element>
	</xsl:template>
	<!-- ALTERNATIVE -->
	<xsl:template name="createAlternative">
		<xsl:param name="title"/>
		<xsl:element name="dcterms:alternative">
			<xsl:value-of select="$title"/>
		</xsl:element>
	</xsl:template>
	
	
	<xsl:template name="createIDVal">
		<xsl:param name="idtype"/>
		<xsl:param name="id"/>
		<xsl:element name="dc:identifier">
			<xsl:attribute name="xsi:type" select="$idtype"/>
			<xsl:value-of select="$id"/>
		</xsl:element>
	</xsl:template>

	<xsl:template name="createID">
		<xsl:param name="idtype"/>
		<xsl:element name="dc:identifier">
			<xsl:attribute name="xsi:type" select="$idtype"/>
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>


	<!-- SUBJECT -->
	<xsl:template name="createSubject">
		<xsl:param name="gen"/>
		<xsl:element name="dcterms:subject">
			<xsl:value-of select="$gen"/>
		</xsl:element>
	</xsl:template>
	<!-- SOURCE -->
	<xsl:template name="createSource">
		<xsl:param name="title"/>
		<xsl:param name="volume"/>
		<xsl:param name="issue"/>
		<xsl:param name="identifier"/>
		<xsl:param name="publisher"/>
		<xsl:param name="publisherplace"/>
		<xsl:param name="partName"/>
		<xsl:param name="ISSN"/>
		<xsl:param name="ZDBID"/>
		<xsl:param name="j_genre"/>
		<xsl:element name="pub:source">
			<xsl:attribute name="type">
				<xsl:value-of select="$j_genre"/>
			</xsl:attribute>
				<xsl:choose>
					<xsl:when test="$partName = 'Teil A'">
						<xsl:element name="dc:title">
							<xsl:value-of select="'Zeitschrift für Naturforschung A - A Journal of Physical Sciences'"/>
						</xsl:element>
					</xsl:when>
					<xsl:when test="$partName = 'Teil B'">
						<xsl:element name="dc:title">
							<xsl:value-of select="'Zeitschrift für Naturforschung B - A Journal of Chemical Sciences'"/>
						</xsl:element>
					</xsl:when>
					<xsl:otherwise>
						<xsl:element name="dc:title">
							<xsl:value-of select="'Zeitschrift für Naturforschung C - A Journal of Biosciences'"/>
						</xsl:element>
					</xsl:otherwise>
				</xsl:choose>		
			<xsl:element name="e:volume">
				<xsl:value-of select="$volume"/>
			</xsl:element>
			<xsl:if test="string-length($issue) &gt; 0">
				<xsl:element name="e:issue">
					<xsl:value-of select="$issue"/>
				</xsl:element>
			</xsl:if>
			<xsl:element name="e:publishing-info">
				<xsl:element name="dc:publisher">
					<xsl:value-of select="$publisher"/>
				</xsl:element>
				<xsl:element name="e:place">
					<xsl:value-of select="$publisherplace"/>
				</xsl:element>
			</xsl:element>
			<xsl:if test="string-length($identifier) &gt; 0">
				<xsl:call-template name="createIDVal">
					<xsl:with-param name="idtype">eidt:OTHER</xsl:with-param>
					<xsl:with-param name="id" select="$identifier"/>
				</xsl:call-template>
			</xsl:if>
			<xsl:call-template name="createIDVal">
				<xsl:with-param name="idtype">eidt:ISSN</xsl:with-param>
				<xsl:with-param name="id" select="$ISSN"/>
			</xsl:call-template>
			<xsl:call-template name="createIDVal">
				<xsl:with-param name="idtype">eidt:ZDB</xsl:with-param>
				<xsl:with-param name="id" select="$ZDBID"/>
			</xsl:call-template>
		</xsl:element>
	</xsl:template>

	<!-- FILE -->
	<xsl:template name="createFile">
		<xsl:param name="id"/>
		<xsl:element name="prop:file-name">
			<xsl:value-of select="/mets:mets/mets:fileSec/mets:fileGrp[@USE='DOWNLOAD']/mets:file[@ID = $id]/mets:FLocat/@xlin:href"/> 
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
