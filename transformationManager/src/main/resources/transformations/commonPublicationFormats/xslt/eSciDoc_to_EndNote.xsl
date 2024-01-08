<?xml version="1.0" encoding="UTF-8"?>
<!--  CDDL HEADER START  The contents of this file are subject to the terms of the  Common Development and Distribution License, Version 1.0 only  (the "License"). You may not use this file except in compliance  with the License.  You can obtain a copy of the license at license/ESCIDOC.LICENSE  or http://www.escidoc.org/license.  See the License for the specific language governing permissions  and limitations under the License.  When distributing Covered Code, include this CDDL HEADER in each  file and include the License file at license/ESCIDOC.LICENSE.  If applicable, add the following below this CDDL HEADER, with the  fields enclosed by brackets "[]" replaced with your own identifying  information: Portions Copyright [yyyy] [name of copyright owner]  CDDL HEADER END  Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft  für wissenschaftlich-technische Information mbH and Max-Planck-  Gesellschaft zur Förderung der Wissenschaft e.V.  All rights reserved. Use is subject to license terms. -->
<!--   Transformations from eSciDoc PubItem Schema to EndNote format 10/11  Author: Julia Kurt (initial creation)   $Author$ (last changed)  $Revision$   $LastChangedDate$ -->
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:fn="http://www.w3.org/2005/xpath-functions"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:dc="${xsd.metadata.dc}"
	xmlns:dcterms="${xsd.metadata.dcterms}"
	xmlns:container="${xsd.soap.container.container}"
	xmlns:container-list="${xsd.soap.container.containerlist}"
	xmlns:escidocItem="${xsd.soap.item.item}"
	xmlns:eterms="http://purl.org/escidoc/metadata/terms/0.1/"
	xmlns:escidocComponents="${xsd.soap.item.components}"
	xmlns:escidocItemList="${xsd.soap.item.itemlist}"
	xmlns:escidocRelations="${xsd.soap.common.relations}"
	xmlns:escidocSearchResult="${xsd.soap.searchresult.searchresult}"
	xmlns:file="${xsd.metadata.file}"
	xmlns:mdr="${xsd.soap.common.mdrecords}"
	xmlns:member-list="${xsd.soap.common.memberlist}"
	xmlns:mods-md="http://www.loc.gov/mods/v3"
	xmlns:prop="${xsd.core.properties}"
	xmlns:pub="${xsd.metadata.publication}"
	xmlns:source="${xsd.metadata.source}"
	xmlns:event="${xsd.metadata.event}"
	xmlns:person="${xsd.metadata.person}"
	xmlns:organization="${xsd.metadata.organization}"
	xmlns:xlink="http://www.w3.org/1999/xlink"
	xmlns:escidocContext="${xsd.soap.context.context}"
	xmlns:escidocContextList="${xsd.soap.context.contextlist}"
	xmlns:release="${xsd.soap.common.release}"
	xmlns:srel="${xsd.soap.common.srel}"
	xmlns:struct-map="${xsd.soap.container.structmap}"
	xmlns:version="${xsd.soap.common.version}"
	xmlns:escidocFunctions="urn:escidoc:functions" >
	<xsl:import href="../../vocabulary-mappings.xsl"/>
	<xsl:output method="text" encoding="UTF-8" indent="yes"/>
	<xsl:param name="handleShort"/>
	<xsl:param name="handleUrl"/>
	<xsl:template match="/*">
		<!-- create entry for each item -->
		<xsl:apply-templates select="//pub:publication"/>
	</xsl:template>
	<!-- create entry -->
	<xsl:template match="pub:publication">
		<xsl:variable name="genre-uri" select="@type"/>
		<xsl:variable name="genre" select="$genre-ves/enum[@uri=$genre-uri]"/>
		<!-- detect entry type -->
		<xsl:choose>
			<xsl:when test="$genre='manual' or $genre='multi-volume' or (($genre='book' or $genre='monograph' or $genre='handbook' or $genre='collected-edition' or $genre='festschrift' or $genre='commentary') and (not(exists(./eterms:creator[@role=$creator-ves/enum[.='editor']/@uri]))))">
				<xsl:call-template name="createEntry">
					<xsl:with-param name="entryType">Book</xsl:with-param>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$genre='book-item' or $genre='contribution-to-handbook' or $genre='contribution-to-encyclopedia' or $genre='contribution-to-festschrift' or $genre='contribution-to-commentary' or $genre='contribution-to-collected-edition'">
				<xsl:call-template name="createEntry">
					<xsl:with-param name="entryType">Book Section</xsl:with-param>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$genre='proceedings' or $genre='conference-paper' or $genre='conference-report'">
				<xsl:call-template name="createEntry">
					<xsl:with-param name="entryType">Conference Proceedings</xsl:with-param>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="($genre='book' or $genre='monograph' or $genre='handbook' or $genre='collected-edition' or $genre='festschrift' or $genre='commentary' or $genre='encyclopedia') and (exists(./eterms:creator[@role=$creator-ves/enum[.='editor']/@uri]))">
				<xsl:call-template name="createEntry">
					<xsl:with-param name="entryType">Edited Book</xsl:with-param>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$genre='encyclopedia'">
				<xsl:call-template name="createEntry">
					<xsl:with-param name="entryType">Encyclopedia</xsl:with-param>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$genre='article' or $genre='editorial' or $genre='book-review' or $genre='review-article'">
				<xsl:call-template name="createEntry">
					<xsl:with-param name="entryType">Journal Article</xsl:with-param>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$genre='magazine-article'">
				<xsl:call-template name="createEntry">
					<xsl:with-param name="entryType">magazine-article</xsl:with-param>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$genre='manuscript'">
				<xsl:call-template name="createEntry">
					<xsl:with-param name="entryType">Manuscript</xsl:with-param>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$genre='newspaper-article'">
				<xsl:call-template name="createEntry">
					<xsl:with-param name="entryType">Newspaper Article</xsl:with-param>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$genre='patent'">
				<xsl:call-template name="createEntry">
					<xsl:with-param name="entryType">Patent</xsl:with-param>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$genre='report' or $genre='paper' or $genre='pre-registration-paper' or $genre='registered-report' or $genre='preprint'">
				<xsl:call-template name="createEntry">
					<xsl:with-param name="entryType">Report</xsl:with-param>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$genre='series' or $genre='journal'">
				<xsl:call-template name="createEntry">
					<xsl:with-param name="entryType">Serial</xsl:with-param>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$genre='thesis'">
				<xsl:call-template name="createEntry">
					<xsl:with-param name="entryType">Thesis</xsl:with-param>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$genre='software'">
				<xsl:call-template name="createEntry">
					<xsl:with-param name="entryType">Computer Program</xsl:with-param>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$genre='data-publication'">
				<xsl:call-template name="createEntry">
					<xsl:with-param name="entryType">Dataset</xsl:with-param>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$genre='blog-post'">
				<xsl:call-template name="createEntry">
					<xsl:with-param name="entryType">Blog</xsl:with-param>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$genre='interview'">
				<xsl:call-template name="createEntry">
					<xsl:with-param name="entryType">Interview</xsl:with-param>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$genre='film'">
				<xsl:call-template name="createEntry">
					<xsl:with-param name="entryType">Film or Broadcast</xsl:with-param>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="createEntry">
					<xsl:with-param name="entryType">Generic</xsl:with-param>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<!-- create entry -->
	<xsl:template name="createEntry">
		<xsl:param name="entryType"/>
		<xsl:variable name="genre-uri" select="@type"/>
		<xsl:variable name="genre" select="$genre-ves/enum[@uri=$genre-uri]"/>
		<!-- GENRE -->
		<xsl:call-template name="print-line">
			<xsl:with-param name="tag" select="'0'"/>
			<xsl:with-param name="value" select="$entryType"/>
		</xsl:call-template>
		<!-- AUTHOR -->
		<xsl:apply-templates select="eterms:creator">
			<xsl:with-param name="genre" select="$genre"/>
		</xsl:apply-templates>
		<!-- AFFILIATIONS -->
		<xsl:variable name="affiliation">
			<xsl:for-each select="eterms:creator/person:person/organization:organization/dc:title">
				<xsl:value-of select="."/>
				<xsl:value-of select="if (position()!=last()) then '&#10;' else ''"/>
			</xsl:for-each>
		</xsl:variable>
		<xsl:if test="$affiliation and $affiliation != ''">
			<xsl:call-template name="print-line">
				<xsl:with-param name="tag" select="'+'"/>
				<xsl:with-param name="value" select="$affiliation"/>
			</xsl:call-template>
		</xsl:if>
		<!-- TITLE -->
		<xsl:variable name="subtitle-as-suffix">
			<xsl:value-of select="concat(' : ', dcterms:alternative[@xsi:type='eterms:SUBTITLE'])"/>
		</xsl:variable>
		<xsl:variable name="title-and-subtitle">
			<xsl:value-of select="concat(dc:title, $subtitle-as-suffix)"/>
		</xsl:variable>
		<xsl:call-template name="print-line">
			<xsl:with-param name="tag">T</xsl:with-param>
			<xsl:with-param name="value" select="$title-and-subtitle"/>
		</xsl:call-template>
		<!-- ALTTITLE -->
		<xsl:if test="exists(dcterms:alternative[@xsi:type='eterms:ABBREVIATION']) and ($genre='report' or $genre='book' or $genre='thesis' or $genre='paper' or $genre='monograph' or $genre='collected-edition' or $genre='handbook' or $genre='festschrift' or $genre='commentary' or $genre='encyclopedia' or $genre='data-publication' or $genre='pre-registration-paper' or $genre='registered-report' or $genre='preprint' or $genre='blog-post' or $genre='interview' or $genre='software')">
			<xsl:call-template name="print-line">
				<xsl:with-param name="tag">O</xsl:with-param>
				<xsl:with-param name="value" select="dcterms:alternative[@xsi:type='eterms:ABBREVIATION']"/>
			</xsl:call-template>
		</xsl:if>
		<xsl:if test="exists(dcterms:alternative[@xsi:type='eterms:ABBREVIATION']) and ($genre='article' or $genre='book-review' or $genre='review-article' or $genre='magazine-article')">
			<xsl:call-template name="print-line">
				<xsl:with-param name="tag">!</xsl:with-param>
				<xsl:with-param name="value" select="dcterms:alternative[@xsi:type='eterms:ABBREVIATION']"/>
			</xsl:call-template>
		</xsl:if>
		<!-- LANGUAGE -->
		<xsl:call-template name="print-line">
			<xsl:with-param name="tag">G</xsl:with-param>
			<xsl:with-param name="value" select="dc:language"/>
		</xsl:call-template>
		<!-- HANDLE -->
		<xsl:if test="../../../escidocItem:properties/prop:pid and ../../../escidocItem:properties/prop:pid != '' ">
			<xsl:call-template name="print-line">
				<xsl:with-param name="tag">U</xsl:with-param>
				<xsl:with-param name="value" select="fn:concat($handleUrl, fn:substring-after(../../../escidocItem:properties/prop:pid, $handleShort))"/>
			</xsl:call-template>
		</xsl:if>
		<!-- IDENTIFIER -->
		<xsl:apply-templates select="dc:identifier">
			<xsl:with-param name="genre" select="$genre"/>
		</xsl:apply-templates>
		<!-- PUBLISHER -->
		<xsl:choose>
			<xsl:when test="not($genre='report')">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag">I</xsl:with-param>
					<xsl:with-param name="value" select="eterms:publishing-info/dc:publisher"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag">Y</xsl:with-param>
					<xsl:with-param name="value" select="eterms:publishing-info/dc:publisher"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
		<!-- PUBLISHING PLACE -->
		<xsl:if test="not($genre='conference-paper' or $genre='proceedings')">
			<xsl:call-template name="print-line">
				<xsl:with-param name="tag">C</xsl:with-param>
				<xsl:with-param name="value" select="eterms:publishing-info/eterms:place"/>
			</xsl:call-template>
		</xsl:if>
		<!-- PUBLISHING EDITION -->
		<xsl:choose>
			<xsl:when test="not($genre='article'  or $genre='book-review' or $genre='magazine-article' or $genre='review-article')">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag">7</xsl:with-param>
					<xsl:with-param name="value" select="eterms:publishing-info/eterms:edition"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag">7</xsl:with-param>
					<xsl:with-param name="value" select="eterms:published-online"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
		<!-- DATE -->
		<xsl:choose>
			<xsl:when test="dcterms:issued">
				<xsl:choose>
					<xsl:when test="fn:contains(dcterms:issued, '-')">
						<xsl:call-template name="print-line">
							<xsl:with-param name="tag">D</xsl:with-param>
							<xsl:with-param name="value" select="fn:substring-before(dcterms:issued, '-')"/>
						</xsl:call-template>
						<xsl:if test="matches(dcterms:issued, '^\d{4}-\d{1,2}-\d{1,2}$')">
							<xsl:call-template name="print-line">
								<xsl:with-param name="tag">8</xsl:with-param>
								<xsl:with-param name="value" select="escidocFunctions:eSciDocDateFormatToGermanDateFormat(dcterms:issued)"/>
							</xsl:call-template>
						</xsl:if>
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="print-line">
							<xsl:with-param name="tag">D</xsl:with-param>
							<xsl:with-param name="value" select="dcterms:issued"/>
						</xsl:call-template>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:when test="eterms:published-online">
				<xsl:choose>
					<xsl:when test="fn:contains(eterms:published-online, '-')">
						<xsl:call-template name="print-line">
							<xsl:with-param name="tag">D</xsl:with-param>
							<xsl:with-param name="value" select="fn:substring-before(eterms:published-online, '-')"/>
						</xsl:call-template>
						<xsl:if test="matches(eterms:published-online, '^\d{4}-\d{1,2}-\d{1,2}$')">
							<xsl:call-template name="print-line">
								<xsl:with-param name="tag">8</xsl:with-param>
								<xsl:with-param name="value" select="escidocFunctions:eSciDocDateFormatToGermanDateFormat(eterms:published-online)"/>
							</xsl:call-template>
						</xsl:if>
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="print-line">
							<xsl:with-param name="tag">D</xsl:with-param>
							<xsl:with-param name="value" select="eterms:published-online"/>
						</xsl:call-template>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:when test="dcterms:dateAccepted">
				<xsl:choose>
					<xsl:when test="fn:contains(dcterms:dateAccepted, '-')">
						<xsl:call-template name="print-line">
							<xsl:with-param name="tag">D</xsl:with-param>
							<xsl:with-param name="value" select="fn:substring-before(dcterms:dateAccepted, '-')"/>
						</xsl:call-template>
						<xsl:if test="matches(dcterms:dateAccepted, '^\d{4}-\d{1,2}-\d{1,2}$')">
							<xsl:call-template name="print-line">
								<xsl:with-param name="tag">8</xsl:with-param>
								<xsl:with-param name="value" select="escidocFunctions:eSciDocDateFormatToGermanDateFormat(dcterms:dateAccepted)"/>
							</xsl:call-template>
						</xsl:if>
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="print-line">
							<xsl:with-param name="tag">D</xsl:with-param>
							<xsl:with-param name="value" select="dcterms:dateAccepted"/>
						</xsl:call-template>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:when test="dcterms:dateSubmitted">
				<xsl:choose>
					<xsl:when test="fn:contains(dcterms:dateSubmitted, '-')">
						<xsl:call-template name="print-line">
							<xsl:with-param name="tag">D</xsl:with-param>
							<xsl:with-param name="value" select="fn:substring-before(dcterms:dateSubmitted, '-')"/>
						</xsl:call-template>
						<xsl:if test="matches(dcterms:dateSubmitted, '^\d{4}-\d{1,2}-\d{1,2}$')">
							<xsl:call-template name="print-line">
								<xsl:with-param name="tag">8</xsl:with-param>
								<xsl:with-param name="value" select="escidocFunctions:eSciDocDateFormatToGermanDateFormat(dcterms:dateSubmitted)"/>
							</xsl:call-template>
						</xsl:if>
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="print-line">
							<xsl:with-param name="tag">D</xsl:with-param>
							<xsl:with-param name="value" select="dcterms:dateSubmitted"/>
						</xsl:call-template>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:when test="dcterms:modified">
				<xsl:choose>
					<xsl:when test="fn:contains(dcterms:modified, '-')">
						<xsl:call-template name="print-line">
							<xsl:with-param name="tag">D</xsl:with-param>
							<xsl:with-param name="value" select="fn:substring-before(dcterms:modified, '-')"/>
						</xsl:call-template>
						<xsl:if test="matches(dcterms:modified, '^\d{4}-\d{1,2}-\d{1,2}$')">
							<xsl:call-template name="print-line">
								<xsl:with-param name="tag">8</xsl:with-param>
								<xsl:with-param name="value" select="escidocFunctions:eSciDocDateFormatToGermanDateFormat(dcterms:modified)"/>
							</xsl:call-template>
						</xsl:if>
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="print-line">
							<xsl:with-param name="tag">D</xsl:with-param>
							<xsl:with-param name="value" select="dcterms:modified"/>
						</xsl:call-template>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:when test="dcterms:created">
				<xsl:choose>
					<xsl:when test="fn:contains(dcterms:created, '-')">
						<xsl:call-template name="print-line">
							<xsl:with-param name="tag">D</xsl:with-param>
							<xsl:with-param name="value" select="fn:substring-before(dcterms:created, '-')"/>
						</xsl:call-template>
						<xsl:if test="matches(dcterms:created, '^\d{4}-\d{1,2}-\d{1,2}$')">
							<xsl:call-template name="print-line">
								<xsl:with-param name="tag">8</xsl:with-param>
								<xsl:with-param name="value" select="escidocFunctions:eSciDocDateFormatToGermanDateFormat(dcterms:created)"/>
							</xsl:call-template>
						</xsl:if>
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="print-line">
							<xsl:with-param name="tag">D</xsl:with-param>
							<xsl:with-param name="value" select="dcterms:created"/>
						</xsl:call-template>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
		</xsl:choose>
		<!-- REVIEW METHOD -->
		<xsl:variable name="review-method-uri" select="eterms:review-method"/>
		<xsl:variable name="review-method" select="concat('Review method: ',$reviewMethod-ves/enum[@uri=$review-method-uri])"/>
		<xsl:if test="$review-method-uri!=''">
			<xsl:choose>
				<xsl:when test="$entryType='Generic' or $entryType='Book Section' or $entryType='Journal Article' or $entryType='Magazine Article' or $entryType='Newspaper Article'">
					<xsl:call-template name="print-line">
						<xsl:with-param name="tag">*</xsl:with-param>
						<xsl:with-param name="value" select="$review-method"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="print-line">
						<xsl:with-param name="tag">Z</xsl:with-param>
						<xsl:with-param name="value" select="$review-method"/>
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
		<!-- EVENT -->
		<xsl:apply-templates select="event:event">
			<xsl:with-param name="genre" select="$genre"/>
		</xsl:apply-templates>
		<!-- TOTAL NUMBER OF PAGES -->
		<xsl:if test="$genre='book' or $genre='monograph' or $genre='handbook' or $genre='collected-edition' or $genre='festschrift' or $genre='manual' or $genre='multi-volume' or $genre='manuscript' or $genre='proceedings' or $genre='report' or $genre='thesis'">
			<xsl:call-template name="print-line">
				<xsl:with-param name="tag">P</xsl:with-param>
				<xsl:with-param name="value" select="eterms:total-number-of-pages"/>
			</xsl:call-template>
		</xsl:if>
		<!-- DEGREE -->
		<xsl:if test="$genre='thesis'">
			<xsl:variable name="degree" select="eterms:degree"/>
			<xsl:call-template name="print-line">
				<xsl:with-param name="tag">V</xsl:with-param>
				<xsl:with-param name="value" select="$degree-ves/enum[@uri=$degree]"/>
			</xsl:call-template>
			<xsl:call-template name="print-line">
				<xsl:with-param name="tag">9</xsl:with-param>
				<xsl:with-param name="value" select="$degree-ves/enum[@uri=$degree]"/>
			</xsl:call-template>
		</xsl:if>
		<!-- ABSTRACT -->
		<xsl:call-template name="print-line">
			<xsl:with-param name="tag">X</xsl:with-param>
			<xsl:with-param name="value" select="dcterms:abstract"/>
		</xsl:call-template>
		<!-- SUBJECT -->
		<xsl:call-template name="print-line">
			<xsl:with-param name="tag">K</xsl:with-param>
			<xsl:with-param name="value" select="dc:subject"/>
		</xsl:call-template>
		<xsl:call-template name="print-line">
			<xsl:with-param name="tag">K</xsl:with-param>
			<xsl:with-param name="value" select="dcterms:subject"/>
		</xsl:call-template>
		<!-- TOC -->
		<xsl:if test="dcterms:tableOfContents and dcterms:tableOfContents!=''">
			<xsl:choose>
				<xsl:when test="$genre='report'">
					<xsl:call-template name="print-line">
						<xsl:with-param name="tag">(</xsl:with-param>
						<xsl:with-param name="value" select="dcterms:tableOfContents"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="print-line">
						<xsl:with-param name="tag">Z</xsl:with-param>
						<xsl:with-param name="value" select="dcterms:tableOfContents"/>
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
		<!-- ESCIDOC ID -->
		<xsl:call-template name="print-line">
			<xsl:with-param name="tag">M</xsl:with-param>
			<xsl:with-param name="value" select="dc:identifier[@xsi:type='eterms:ESCIDOC']"/>
		</xsl:call-template>
		<!-- SOURCE -->
		<xsl:apply-templates select="source:source">
			<xsl:with-param name="genre" select="$genre"/>
		</xsl:apply-templates>
		<!-- COMPONENT -->
		<xsl:apply-templates select="../../../escidocComponents:components" />
		<!-- new lines at the end of the entry -->
		<xsl:value-of select="'&#13;&#10;'"/>
		<xsl:value-of select="'&#13;&#10;'"/>
	</xsl:template>
	<!-- CREATOR -->
	<xsl:template match="eterms:creator">
		<xsl:param name="genre"/>
		<xsl:variable name="role-uri" select="@role"/>
		<xsl:variable name="role" select="$creator-ves/enum[@uri=$role-uri]"/>
		<xsl:apply-templates select="person:person">
			<xsl:with-param name="role" select="$role"/>
			<xsl:with-param name="genre" select="$genre"/>
		</xsl:apply-templates>
		<xsl:apply-templates select="organization:organization">
			<xsl:with-param name="role" select="$role"/>
			<xsl:with-param name="genre" select="$genre"/>
		</xsl:apply-templates>
	</xsl:template>
	<!-- PERSON -->
	<xsl:template match="person:person">
		<xsl:param name="role"/>
		<xsl:param name="genre"/>
		<xsl:variable name="name" select="concat(eterms:family-name, ', ',eterms:given-name)"/>
		<xsl:choose>
			<xsl:when test="$role='author'">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag" select="'A'"/>
					<xsl:with-param name="value" select="$name"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$role='editor' and ($genre='book' or $genre='monograph' or $genre='handbook' or $genre='collected-edition' or $genre='festschrift' or $genre='manual' or $genre='multi-volume')">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag" select="'A'"/>
					<xsl:with-param name="value" select="$name"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$role='editor' and not($genre='book' or $genre='monograph' or $genre='handbook' or $genre='collected-edition' or $genre='festschrift' or $genre='manual' or $genre='multi-volume')">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag" select="'E'"/>
					<xsl:with-param name="value" select="$name"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$role='editor' and ($genre='software')">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag" select="'E'"/>
					<xsl:with-param name="value" select="$name"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$role='artist'">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag" select="'A'"/>
					<xsl:with-param name="value" select="concat($role,': ',$name)"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$role='painter'">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag" select="'A'"/>
					<xsl:with-param name="value" select="concat($role,': ',$name)"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$role='photographer'">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag" select="'A'"/>
					<xsl:with-param name="value" select="concat($role,': ',$name)"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$role='illustrator'">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag" select="'A'"/>
					<xsl:with-param name="value" select="concat($role,': ',$name)"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$role='commentator'">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag" select="'A'"/>
					<xsl:with-param name="value" select="concat($role,': ',$name)"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$role='transcriber'">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag" select="'A'"/>
					<xsl:with-param name="value" select="concat($role,': ',$name)"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$role='contributor'">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag" select="'A'"/>
					<xsl:with-param name="value" select="concat($role,': ',$name)"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$role='advisor'">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag" select="'Y'"/>
					<xsl:with-param name="value" select="$name"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$role='translator' and not($genre='proceedings' or $genre='report')">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag" select="'?'"/>
					<xsl:with-param name="value" select="$name"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$role='translator' and ($genre='proceedings' or $genre='report')">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag" select="'Z'"/>
					<xsl:with-param name="value" select="concat($role,': ',$name)"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$role='honoree'">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag" select="'A'"/>
					<xsl:with-param name="value" select="concat($role,': ',$name)"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$role='referee'">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag" select="'A'"/>
					<xsl:with-param name="value" select="concat($role,': ',$name)"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$role='inventor'">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag" select="'A'"/>
					<xsl:with-param name="value" select="concat($role,': ',$name)"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$role='applicant'">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag" select="'A'"/>
					<xsl:with-param name="value" select="concat($role,': ',$name)"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$role='director'">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag" select="'A'"/>
					<xsl:with-param name="value" select="concat($role,': ',$name)"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$role='producer'">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag" select="'A'"/>
					<xsl:with-param name="value" select="concat($role,': ',$name)"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$role='actor'">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag" select="'A'"/>
					<xsl:with-param name="value" select="concat($role,': ',$name)"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$role='cinematographer'">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag" select="'A'"/>
					<xsl:with-param name="value" select="concat($role,': ',$name)"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$role='sound designer'">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag" select="'A'"/>
					<xsl:with-param name="value" select="concat($role,': ',$name)"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$role='developer' or $role='interviewee'">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag" select="'A'"/>
					<xsl:with-param name="value" select="concat($role,': ',$name)"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$role='interviewer'">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag" select="'E'"/>
					<xsl:with-param name="value" select="concat($role,': ',$name)"/>
				</xsl:call-template>
			</xsl:when>
		</xsl:choose>
	</xsl:template>
	<!-- ORGANIZATION -->
	<xsl:template match="organization:organization">
		<xsl:param name="role"/>
		<xsl:param name="genre"/>
		<xsl:variable name="name" select="concat(dc:title, ', ',eterms:address)"/>
		<xsl:choose>
			<xsl:when test="$role='author'">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag" select="'A'"/>
					<xsl:with-param name="value" select="$name"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$role='editor' and $genre='book'">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag" select="'A'"/>
					<xsl:with-param name="value" select="$name"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$role='editor' and not($genre='book')">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag" select="'E'"/>
					<xsl:with-param name="value" select="$name"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$role='artist'">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag" select="'A'"/>
					<xsl:with-param name="value" select="$name"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$role='painter'">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag" select="'A'"/>
					<xsl:with-param name="value" select="$name"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$role='photographer'">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag" select="'A'"/>
					<xsl:with-param name="value" select="$name"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$role='illustrator'">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag" select="'A'"/>
					<xsl:with-param name="value" select="$name"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$role='commentator'">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag" select="'A'"/>
					<xsl:with-param name="value" select="$name"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$role='transcriber'">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag" select="'A'"/>
					<xsl:with-param name="value" select="$name"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$role='contributor'">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag" select="'A'"/>
					<xsl:with-param name="value" select="$name"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$role='advisor'">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag" select="'A'"/>
					<xsl:with-param name="value" select="concat($role,': ',$name)"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$role='translator'">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag" select="'?'"/>
					<xsl:with-param name="value" select="$name"/>
				</xsl:call-template>
			</xsl:when>
		</xsl:choose>
	</xsl:template>
	<!-- IDENTIFIER -->
	<xsl:template match="dc:identifier">
		<xsl:param name="genre"/>
		<xsl:choose>
			<xsl:when test="@xsi:type='eterms:DOI'">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag">R</xsl:with-param>
					<xsl:with-param name="value" select="."/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="@xsi:type='eterms:ISBN'">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag">@</xsl:with-param>
					<xsl:with-param name="value" select="."/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="@xsi:type='eterms:ISSN'">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag">@</xsl:with-param>
					<xsl:with-param name="value" select="."/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="@xsi:type='eterms:URI'">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag">U</xsl:with-param>
					<xsl:with-param name="value" select="."/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="@xsi:type='eterms:URN'">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag">U</xsl:with-param>
					<xsl:with-param name="value" select="."/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="@xsi:type='eterms:EDOC'">
				<xsl:variable name="idstring" select="concat('EDOC: ',.)"/>
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag">F</xsl:with-param>
					<xsl:with-param name="value" select="$idstring"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="@xsi:type='eterms:ISI'">
				<xsl:variable name="idstring" select="concat('ISI: ',.)"/>
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag">F</xsl:with-param>
					<xsl:with-param name="value" select="$idstring"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="@xsi:type='eterms:PND'">
				<xsl:variable name="idstring" select="concat('PND: ',.)"/>
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag">F</xsl:with-param>
					<xsl:with-param name="value" select="$idstring"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="@xsi:type='eterms:OTHER'">
				<xsl:variable name="idstring" select="concat('OTHER: ',.)"/>
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag">F</xsl:with-param>
					<xsl:with-param name="value" select="$idstring"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="@xsi:type='eterms:PMC' and $genre='article' or $genre='book-review' or $genre='magazine-article' or $genre='review-article'">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag">2</xsl:with-param>
					<xsl:with-param name="value" select="."/>
				</xsl:call-template>
			</xsl:when>
		</xsl:choose>
	</xsl:template>
	<!-- EVENT -->
	<xsl:template match="event:event">
		<xsl:param name="genre"/>
		<!-- TITLE -->
		<xsl:choose>
			<xsl:when test="$genre='proceedings-paper' or $genre='conference-paper' or $genre='conference-report' or $genre='proceedings'">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag">B</xsl:with-param>
					<xsl:with-param name="value" select="dc:title"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag">Z</xsl:with-param>
					<xsl:with-param name="value" select="concat('name of event: ',dc:title)"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
		<!-- DATE -->
		<xsl:variable name="event-date" select="concat(eterms:start-date,' - ',eterms:end-date)"/>
		<xsl:call-template name="print-line">
			<xsl:with-param name="tag">Z</xsl:with-param>
			<xsl:with-param name="value" select="concat('date of event: ',$event-date)"/>
		</xsl:call-template>
		<xsl:if test="($genre='proceedings' or $genre='talk-at-event') and eterms:start-date and eterms:start-date!=''">
			<xsl:variable name="event-start-date-year">
				<xsl:choose>
					<xsl:when test="fn:contains(eterms:start-date, '-')">
						<xsl:value-of select="fn:substring-before(eterms:start-date, '-')"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="eterms:start-date"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<xsl:call-template name="print-line">
				<xsl:with-param name="tag">D</xsl:with-param>
				<xsl:with-param name="value" select="$event-start-date-year"/>
			</xsl:call-template>
		</xsl:if>
		<!-- PLACE -->
		<xsl:choose>
			<xsl:when test="$genre='proceedings' or $genre='conference-paper' or $genre='conference-report' ">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag">C</xsl:with-param>
					<xsl:with-param name="value" select="eterms:place"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag">Z</xsl:with-param>
					<xsl:with-param name="value" select="concat('place of event: ',eterms:place)"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:if test="eterms:invitation-status and eterms:invitation-status!=''">
			<xsl:call-template name="print-line">
				<xsl:with-param name="tag">9</xsl:with-param>
				<xsl:with-param name="value" select="eterms:invitation-status"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	<!-- SOURCE -->
	<xsl:template match="source:source">
		<xsl:param name="genre"/>
		<xsl:variable name="sgenre-uri" select="@type"/>
		<xsl:variable name="sgenre" select="$genre-ves/enum[@uri=$sgenre-uri]"/>
		<!-- TITLE -->
		<xsl:choose>
			<xsl:when test="($sgenre='book' or $sgenre='proceedings' or $sgenre='issue' or $sgenre='collected-edition' or $sgenre='encyclopedia' or $sgenre='festschrift' or $sgenre='handbook' or $sgenre='commentary' or ($sgenre='series' and not($genre='book-item'))) and not($genre='proceedings')">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag">B</xsl:with-param>
					<xsl:with-param name="value" select="dc:title"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="($genre='proceedings' and $sgenre='series') or (($genre='book-item' or $genre='contribution-to-handbook' or $genre='contribution-to-encyclopedia' or $genre='contribution-to-festschrift' or $genre='contribution-to-commentary' or $genre='contribution-to-collected-edition') and $sgenre='series')">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag">S</xsl:with-param>
					<xsl:with-param name="value" select="dc:title"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$sgenre='journal'">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag">J</xsl:with-param>
					<xsl:with-param name="value" select="dc:title"/>
				</xsl:call-template>
			</xsl:when>
		</xsl:choose>
		<!-- ALTTITLE -->
		<xsl:if test="$genre='article'  or $genre='book-review' or $genre='magazine-article' or $genre='review-article'">
			<xsl:call-template name="print-line">
				<xsl:with-param name="tag">O</xsl:with-param>
				<xsl:with-param name="value" select="dcterms:alternative"/>
			</xsl:call-template>
		</xsl:if>
		<!-- CREATOR -->
		<xsl:if test="not($genre='article' or $genre='book-review' or $genre='magazine-article' or $genre='review-article') and (not($sgenre='series') and position()!=2)">
			<xsl:call-template name="print-line">
				<xsl:with-param name="tag">E</xsl:with-param>
				<xsl:with-param name="value">
					<xsl:for-each select="eterms:creator/person:person">
						<xsl:value-of select="  string-join(  (  eterms:family-name[.!=''],   eterms:given-name[.!='']  ),   ', '  )  "/>
						<xsl:value-of select="if (position()!=last()) then '; ' else ''" />
					</xsl:for-each>
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>
		<xsl:if test="$sgenre='series' and position()=2">
			<xsl:call-template name="print-line">
				<xsl:with-param name="tag">Y</xsl:with-param>
				<xsl:with-param name="value">
					<xsl:for-each select="eterms:creator/person:person">
						<xsl:value-of select="  string-join(  (  eterms:family-name[.!=''],   eterms:given-name[.!='']  ),   ', '  )  "/>
						<xsl:value-of select="if (position()!=last()) then '; ' else ''" />
					</xsl:for-each>
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>
		<!-- VOLUME -->
		<xsl:choose>
			<xsl:when test="$sgenre='series' and not($genre='proceedings')">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag">N</xsl:with-param>
					<xsl:with-param name="value" select="eterms:volume"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag">V</xsl:with-param>
					<xsl:with-param name="value" select="eterms:volume"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
		<!-- ISSUE -->
		<xsl:if test="not($sgenre='series')">
			<xsl:call-template name="print-line">
				<xsl:with-param name="tag">N</xsl:with-param>
				<xsl:with-param name="value" select="eterms:issue"/>
			</xsl:call-template>
		</xsl:if>
		<!-- PAGES -->
		<xsl:if test="$genre='article' or $genre='book-review' or $genre='magazine-article' or $genre='review-article' or $genre='manuscript'">
			<xsl:call-template name="print-line">
				<xsl:with-param name="tag" select="'&amp;'"/>
				<xsl:with-param name="value" select="eterms:start-page"/>
			</xsl:call-template>
		</xsl:if>
		<xsl:if test="not($genre='book' or $genre='monograph' or $genre='handbook' or $genre='collected-edition' or $genre='festschrift' or $genre='manual' or $genre='multi-volume')">
			<xsl:variable name="pages">
				<xsl:value-of select="eterms:start-page"/>
				<xsl:if test="eterms:end-page!=''">
					<xsl:value-of select="concat(' - ',eterms:end-page)"/>
				</xsl:if>
			</xsl:variable>
			<xsl:call-template name="print-line">
				<xsl:with-param name="tag">P</xsl:with-param>
				<xsl:with-param name="value" select="$pages"/>
			</xsl:call-template>
		</xsl:if>
		<!-- SEQ NO -->
		<xsl:if test="eterms:sequence-number!=''">
			<xsl:choose>
				<xsl:when test="$genre='report' or $genre='manuscript'">
					<xsl:call-template name="print-line">
						<xsl:with-param name="tag">N</xsl:with-param>
						<xsl:with-param name="value" select="eterms:sequence-number"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:when test="$genre='book-item'">
					<xsl:call-template name="print-line">
						<xsl:with-param name="tag" select="'&amp;'"/>
						<xsl:with-param name="value" select="eterms:sequence-number"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:when test="$genre='article' or $genre='magazine-article' or $genre='newspaper-article'">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag">]</xsl:with-param>
					<xsl:with-param name="value" select="eterms:sequence-number"/>
				</xsl:call-template>
			</xsl:when>
				<xsl:otherwise>
					<xsl:variable name="seq-no" select="concat('sequence number: ',eterms:sequence-number)"/>
					<xsl:call-template name="print-line">
						<xsl:with-param name="tag">Z</xsl:with-param>
						<xsl:with-param name="value" select="$seq-no"/>
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
		<!-- PUBLISHER -->
		<xsl:if test="$genre='book-item' or $genre='contribution-to-handbook' or $genre='contribution-to-encyclopedia' or $genre='contribution-to-festschrift' or $genre='contribution-to-commentary' or $genre='contribution-to-collected-edition' or $genre='conference-paper' or $genre='conference-report' or $genre='proceedings-paper' or $genre='article'  or $genre='book-review' or $genre='magazine-article' or $genre='review-article'">
			<xsl:call-template name="print-line">
				<xsl:with-param name="tag">I</xsl:with-param>
				<xsl:with-param name="value" select="eterms:publishing-info/dc:publisher"/>
			</xsl:call-template>
		</xsl:if>
		<!-- PLACE -->
		<xsl:if test="$genre='book-item' or $genre='contribution-to-handbook' or $genre='contribution-to-encyclopedia' or $genre='contribution-to-festschrift' or $genre='contribution-to-commentary' or $genre='contribution-to-collected-edition' or $genre='article'  or $genre='book-review' or $genre='magazine-article' or $genre='review-article'">
			<xsl:call-template name="print-line">
				<xsl:with-param name="tag">C</xsl:with-param>
				<xsl:with-param name="value" select="eterms:publishing-info/eterms:place"/>
			</xsl:call-template>
		</xsl:if>
		<!-- EDITION -->
		<xsl:if test="$genre='article' or $genre='book-review' or $genre='magazine-article' or $genre='review-article'">
			<xsl:call-template name="print-line">
				<xsl:with-param name="tag">7</xsl:with-param>
				<xsl:with-param name="value" select="eterms:publishing-info/eterms:edition"/>
			</xsl:call-template>
		</xsl:if>
		<!-- IDENTIFIER -->
		<xsl:choose>
			<xsl:when test="dc:identifier/@xsi:type='eterms:ISSN'">
				<xsl:variable name="source-issn">
					<xsl:for-each select="dc:identifier[@xsi:type='eterms:ISSN']">
						<xsl:if test="not(position()=1)">&#10;</xsl:if>
						<xsl:value-of select="."/>
					</xsl:for-each>
				</xsl:variable>
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag">@</xsl:with-param>
					<xsl:with-param name="value" select="$source-issn"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="dc:identifier/@xsi:type='eterms:ISBN'">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag">@</xsl:with-param>
					<xsl:with-param name="value" select="dc:identifier[@xsi:type='eterms:ISBN']"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="dc:identifier/@xsi:type='eterms:URI' and not(../dc:identifier[@xsi:type='eterms:URI'or @xsi:type='eterms:URN'])">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag">U</xsl:with-param>
					<xsl:with-param name="value" select="dc:identifier[@xsi:type='eterms:URI' or @xsi:type='eterms:URN']"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="dc:identifier/@xsi:type='eterms:DOI' and not(../dc:identifier[@xsi:type='eterms:DOI'])">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag">R</xsl:with-param>
					<xsl:with-param name="value" select="dc:identifier[@xsi:type='eterms:DOI']"/>
				</xsl:call-template>
			</xsl:when>
		</xsl:choose>
	</xsl:template>
	<!-- COMPONENTS -->
	<xsl:template match="escidocComponents:components">
		<!-- EXTERNAL LOCATORS -->
		<xsl:variable name="external-locator">
			<xsl:for-each select="escidocComponents:component/escidocComponents:content[@storage = 'external-url' and @xlink:href != '']">
				<xsl:if test="not(position()=1)">&#10;</xsl:if>
				<xsl:value-of select="./@xlink:href"/>
			</xsl:for-each>
		</xsl:variable>
		<xsl:if test="$external-locator and $external-locator != ''">
			<xsl:call-template name="print-line">
				<xsl:with-param name="tag">U</xsl:with-param>
				<xsl:with-param name="value" select="$external-locator"/>
			</xsl:call-template>
		</xsl:if>
		<!-- FILES (TODO) -->
	</xsl:template>
	<!-- Prints result line in EndNote format -->
	<xsl:template name="print-line">
		<xsl:param name="tag"/>
		<xsl:param name="value"/>
		<xsl:variable name="strn" select="$value"/>
		<xsl:if test="$tag!='' and $strn!=''">
			<xsl:value-of select="concat('%', $tag,' ')"/>
			<xsl:value-of select="$strn"/>
			<xsl:value-of select="'&#13;&#10;'"/>
		</xsl:if>
	</xsl:template>
	<!-- FUNCTIONS -->
	<xsl:function name="escidocFunctions:eSciDocDateFormatToGermanDateFormat">
		<xsl:param name="eSciDocDate" />
		<xsl:variable name="tokens" select="tokenize($eSciDocDate, '-')"/>
		<xsl:value-of select="concat(concat(concat(concat($tokens[3], '.'), $tokens[2]), '.'), $tokens[1])"/>
	</xsl:function>
</xsl:stylesheet>