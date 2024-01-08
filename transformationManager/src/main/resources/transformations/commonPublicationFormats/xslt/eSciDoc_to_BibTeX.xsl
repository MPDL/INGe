<?xml version="1.0" encoding="UTF-8"?>
<!--  CDDL HEADER START  The contents of this file are subject to the terms of the  Common Development and Distribution License, Version 1.0 only  (the "License"). You may not use this file except in compliance  with the License.  You can obtain a copy of the license at license/ESCIDOC.LICENSE  or http://www.escidoc.org/license.  See the License for the specific language governing permissions  and limitations under the License.  When distributing Covered Code, include this CDDL HEADER in each  file and include the License file at license/ESCIDOC.LICENSE.  If applicable, add the following below this CDDL HEADER, with the  fields enclosed by brackets "[]" replaced with your own identifying  information: Portions Copyright [yyyy] [name of copyright owner]  CDDL HEADER END  Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft  für wissenschaftlich-technische Information mbH and Max-Planck-  Gesellschaft zur Förderung der Wissenschaft e.V.  All rights reserved. Use is subject to license terms. -->
<!--   Transformations from eSciDoc PubItem Schema to BibTeX  Author: Julia Kurt (initial creation)   $Author$ (last changed)  $Revision$   $LastChangedDate$ -->
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:fn="http://www.w3.org/2005/xpath-functions"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:xlink="http://www.w3.org/1999/xlink"
	xmlns:escidoc="${xsd.metadata.escidocprofile.types}"
	xmlns:jfunc="java:de.mpg.mpdl.inge.transformation.transformers.helpers.bibtex.BibtexExport"
	xmlns:function="urn:pubman:functions"
	xmlns:dc="${xsd.metadata.dc}"
	xmlns:dcterms="${xsd.metadata.dcterms}"
	xmlns:ei="${xsd.soap.item.item}"
	xmlns:mdr="${xsd.soap.common.mdrecords}"
	xmlns:mdp="${xsd.metadata.escidocprofile}"
	xmlns:e="${xsd.metadata.escidocprofile.types}"
	xmlns:ec="${xsd.soap.item.components}"
	xmlns:prop="${xsd.soap.common.prop}"
	xmlns:pub="${xsd.metadata.publication}"
	xmlns:source="${xsd.metadata.source}"
	xmlns:event="${xsd.metadata.event}"
	xmlns:person="${xsd.metadata.person}"
	xmlns:organization="${xsd.metadata.organization}"
	xmlns:eterms="${xsd.metadata.terms}">
	<!-- <xsl:import href="functions.xsl"/>-->
	<xsl:import href="../../vocabulary-mappings.xsl"/>
	<xsl:import href="../../functions.xsl"/>
	<xsl:output method="text" encoding="UTF-8" indent="yes"/>
	<xsl:template match="/*">
		<!-- create entry for each item -->
		<xsl:apply-templates select="//pub:publication"/>
	</xsl:template>
	<!-- create bibTeX entry -->
	<xsl:template match="pub:publication">
		<xsl:variable name="gen" select="@type"/>
		<xsl:variable name="genre" select="$genre-ves/enum[@uri=$gen]"/>
		<!-- detect bibtex entry type -->
		<xsl:choose>
			<xsl:when test="$genre='article' or $genre='book-review' or $genre='magazine-article' or $genre='review-article'">
				<xsl:call-template name="createEntry">
					<xsl:with-param name="entryType">article</xsl:with-param>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$genre='proceedings'">
				<xsl:call-template name="createEntry">
					<xsl:with-param name="entryType">proceedings</xsl:with-param>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$genre='conference-paper' or $genre='poster' or $genre='talk-at-event' or $genre='meeting-abstract'">
				<xsl:call-template name="createEntry">
					<xsl:with-param name="entryType">inproceedings</xsl:with-param>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$genre='book'">
				<xsl:call-template name="createEntry">
					<xsl:with-param name="entryType" select="'book'"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$genre='book-item'">
				<xsl:call-template name="createEntry">
					<xsl:with-param name="entryType" select="'incollection'"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$genre='report'">
				<xsl:call-template name="createEntry">
					<xsl:with-param name="entryType" select="'techreport'"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$genre='paper'">
				<xsl:call-template name="createEntry">
					<xsl:with-param name="entryType" select="'unpublished'"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$genre='thesis'">
				<xsl:choose>
					<xsl:when test="eterms:degree=$degree-ves/enum[.='master']/@uri   or eterms:degree=$degree-ves/enum[.='diploma']/@uri  or eterms:degree=$degree-ves/enum[.='magister']/@uri  or eterms:degree=$degree-ves/enum[.='staatsexamen']/@uri  or eterms:degree=$degree-ves/enum[.='bachelor']/@uri">
						<xsl:call-template name="createEntry">
							<xsl:with-param name="entryType" select="'mastersthesis'"/>
						</xsl:call-template>
					</xsl:when>
					<xsl:when test="eterms:degree=$degree-ves/enum[.='phd']/@uri or eterms:degree=$degree-ves/enum[.='habilitation']/@uri">
						<xsl:call-template name="createEntry">
							<xsl:with-param name="entryType" select="'phdthesis'"/>
						</xsl:call-template>
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="createEntry">
							<xsl:with-param name="entryType" select="'misc'"/>
						</xsl:call-template>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="createEntry">
					<xsl:with-param name="entryType" select="'misc'"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<!-- create bibTeX entry -->
	<xsl:template name="createEntry">
		<xsl:param name="entryType"/>
		<xsl:variable name="uri-type-of-publication" select="./@type"/>
		<xsl:variable name="type-of-publication" select="$genre-ves/enum[@uri=$uri-type-of-publication]"/>
		<xsl:variable name="cite-key">
			<xsl:choose>
				<xsl:when test="fn:exists(.//dc:identifier[@xsi:type='eterms:BIBTEX_CITEKEY'])">
					<xsl:value-of select=".//dc:identifier[@xsi:type='eterms:BIBTEX_CITEKEY'][1]"/>
				</xsl:when>
				<xsl:when test="fn:exists(.//dc:identifier[@xsi:type='eterms:OTHER' and fn:matches(., '^Local-ID:\s[A-Z0-9\-]+?-([a-zA-Z][a-z\W]+.*)')]) ">
					<xsl:analyze-string select=".//dc:identifier[@xsi:type='eterms:OTHER' and fn:matches(., '^Local-ID:\s[A-Z0-9\-]+?-([a-zA-Z][a-z\W]+.*)')]" regex="^Local-ID:\s[A-Z0-9\-]+?-([a-zA-Z][a-z\W]+.*)">
						<xsl:matching-substring>
							<xsl:value-of select="fn:normalize-space(regex-group(1))"/>
						</xsl:matching-substring>
					</xsl:analyze-string>
				</xsl:when>
				<xsl:when test="fn:exists(parent::mdr:md-record/parent::mdr:md-records/parent::ei:item/@xlink:href)">
					<xsl:value-of select="parent::mdr:md-record/parent::mdr:md-records/parent::ei:item/@xlink:href"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="parent::mdr:md-record/parent::mdr:md-records/parent::ei:item/@objid"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:value-of select="concat('% pubman genre = ', $type-of-publication)"/>
		<xsl:text disable-output-escaping="yes">&#xD;&#xA;</xsl:text>
		<!-- line break -->
		<xsl:value-of select="concat('@', $entryType, '{')"/>
		<xsl:value-of select="$cite-key"/>
		<xsl:value-of select="','"/>
		<xsl:text disable-output-escaping="yes">&#xD;&#xA;</xsl:text>
		<!-- line break -->
		<!-- TITLE -->
		<xsl:apply-templates select="dc:title"/>
		<!--  ALTERNATIVE TITLE -->
		<xsl:apply-templates select="dcterms:alternative"/>
		<!-- CREATOR -->
		<xsl:apply-templates select="eterms:creator[@role=$creator-ves/enum[.='author']/@uri]"/>
		<!-- EDITOR -->
		<xsl:apply-templates select="eterms:creator[@role=$creator-ves/enum[.='editor']/@uri]"/>
		<!-- LANGUAGE -->
		<xsl:apply-templates select="dc:language"/>
		<!-- Identifiers -->
		<xsl:if test="exists(.//dc:identifier)">
			<xsl:variable name="identifier-xml">
				<xsl:for-each select=".//dc:identifier">
					<xsl:copy-of select="."/>
				</xsl:for-each>
			</xsl:variable>
			<xsl:call-template name="identifiers">
				<xsl:with-param name="identifier-list" select="$identifier-xml" />
			</xsl:call-template>
		</xsl:if>
		<!-- PUBLISHER, ADDRESS -->
		<xsl:choose>
			<xsl:when test="(not (source:source/eterms:publishing-info/dc:publisher)) or source:source/eterms:publishing-info/dc:publisher=''">
				<xsl:apply-templates select="eterms:publishing-info/dc:publisher">
					<xsl:with-param name="genre" select="$type-of-publication"/>
				</xsl:apply-templates>
				<xsl:if test="not(event:event/eterms:place) or event:event/eterms:place=''">
					<xsl:apply-templates select="eterms:publishing-info/eterms:place">
						<xsl:with-param name="genre" select="$type-of-publication"/>
					</xsl:apply-templates>
				</xsl:if>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select="source:source/eterms:publishing-info/dc:publisher">
					<xsl:with-param name="genre" select="$type-of-publication"/>
				</xsl:apply-templates>
				<xsl:if test="not(event:event/eterms:place) or event:event/eterms:place=''">
					<xsl:apply-templates select="source:source/eterms:publishing-info/eterms:place">
						<xsl:with-param name="genre" select="$type-of-publication"/>
					</xsl:apply-templates>
				</xsl:if>
			</xsl:otherwise>
		</xsl:choose>
		<!-- EDITION / NUMBER -->
		<xsl:choose>
			<xsl:when test="(not (source:source/eterms:publishing-info/eterms:edition)) or source:source/eterms:publishing-info/eterms:edition=''">
				<xsl:apply-templates select="eterms:publishing-info/eterms:edition"/>
			</xsl:when>
			<xsl:when test="source:source/eterms:publishing-info/eterms:edition   and $type-of-publication = 'report'  and (not(exists(dc:identifier[@xsi:type = 'eterms:REPORT_NR'])))">
				<xsl:call-template name="createField">
					<xsl:with-param name="name" select="'number'"/>
					<xsl:with-param name="xpath" select="source:source/eterms:publishing-info/eterms:edition"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select="source:source/eterms:publishing-info/eterms:edition"/>
			</xsl:otherwise>
		</xsl:choose>
		<!-- YEAR -->
		<xsl:variable name="pubdate" select="if(dcterms:issued!='') then dcterms:issued else if (eterms:published-online!='') then eterms:published-online else if (dcterms:dateAccepted!='') then dcterms:dateAccepted else if (dcterms:dateSubmitted!='') then dcterms:dateSubmitted else if (dcterms:modified!='') then dcterms:modified else if (dcterms:created!='') then dcterms:created else ''"/>
		<xsl:if test="$pubdate!=''">
			<xsl:variable name="year" select="substring($pubdate,1,4)"/>
			<xsl:call-template name="createField">
				<xsl:with-param name="name" select="'year'"/>
				<xsl:with-param name="xpath" select="$year"/>
			</xsl:call-template>
		</xsl:if>
		<!-- DATE -->
		<xsl:apply-templates select="dcterms:issued"/>
		<!-- ABSTRACT -->
		<xsl:apply-templates select="dcterms:abstract"/>
		<!-- SUBJECT -->
		<xsl:apply-templates select="dc:subject"/>
		<!-- TABLE OF CONTENTS -->
		<xsl:apply-templates select="dcterms:tableOfContents"/>
		<!-- TYPE -->
		<xsl:variable name="degree" select="eterms:degree"/>
		<xsl:if test="$type-of-publication = 'thesis'   and ($degree=$degree-ves/enum[.='diploma']/@uri   or $degree=$degree-ves/enum[.='bachelor']/@uri  or $degree=$degree-ves/enum[.='magister']/@uri   or $degree=$degree-ves/enum[.='habilitation']/@uri   or $degree=$degree-ves/enum[.='staatsexamen']/@uri)">
			<xsl:call-template name="createField">
				<xsl:with-param name="name" select="'type'"/>
				<xsl:with-param name="xpath" select="$degree-ves/enum[@uri=$degree]"/>
			</xsl:call-template>
		</xsl:if>
		<!-- PAGES -->
		<xsl:if test="exists(eterms:total-number-of-pages)  and eterms:total-number-of-pages != ''  and ($type-of-publication = 'book'  or $type-of-publication = 'proceedings'  or $type-of-publication = 'issue')">
			<xsl:call-template name="createField">
				<xsl:with-param name="name" select="'pages'"/>
				<xsl:with-param name="xpath" select="eterms:total-number-of-pages"/>
			</xsl:call-template>
		</xsl:if>
		<!-- SOURCE -->
		<xsl:apply-templates select="source:source"/>
		<!-- EVENT -->
		<xsl:apply-templates select="event:event">
			<xsl:with-param name="publication-type" select="$type-of-publication"/>
		</xsl:apply-templates>
		<!-- END OF ENTRY -->
		<xsl:value-of select="concat('}','')"/>
		<xsl:text disable-output-escaping="yes">&#xD;&#xA;&#xD;&#xA;</xsl:text>
	</xsl:template>
	<!-- END createEntry -->
	<xsl:template match="dc:title">
	
		<xsl:if test=".!='' and (not(./../dcterms:alternative[@xsi:type='eterms:LATEX']) or ./../dcterms:alternative[@xsi:type='eterms:LATEX'] = '')" >
			<xsl:variable name="titleWithoutTags">
				<xsl:call-template name="removeSubSup">
					<xsl:with-param name="elem" select="."/>
				</xsl:call-template>
			</xsl:variable>
			<xsl:call-template name="createField">
				<xsl:with-param name="name" select="'title'"/>
				<xsl:with-param name="xpath" select="concat(normalize-space($titleWithoutTags), '')"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	<xsl:template match="dcterms:alternative">
		<xsl:if test="./@xsi:type='eterms:LATEX'" >
			<xsl:variable name="titleWithoutTags">
				<xsl:call-template name="removeSubSup">
					<xsl:with-param name="elem" select="."/>
				</xsl:call-template>
			</xsl:variable>
			<xsl:call-template name="createField">
				<xsl:with-param name="name" select="'title'"/>
				<xsl:with-param name="xpath" select="concat(normalize-space($titleWithoutTags), '')"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	<xsl:template match="dc:language">
		<xsl:if test=".!=''">
			<xsl:call-template name="createField">
				<xsl:with-param name="name" select="'language'"/>
				<xsl:with-param name="xpath" select="."/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	<xsl:template match="eterms:publishing-info/dc:publisher">
		<xsl:param name="genre"/>
		<xsl:if test=".!=''">
			<xsl:choose>
				<xsl:when test="$genre='thesis'">
					<xsl:call-template name="createField">
						<xsl:with-param name="name" select="'school'"/>
						<xsl:with-param name="xpath" select="."/>
					</xsl:call-template>
				</xsl:when>
				<xsl:when test="$genre='report'">
					<xsl:call-template name="createField">
						<xsl:with-param name="name" select="'institution'"/>
						<xsl:with-param name="xpath" select="."/>
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="createField">
						<xsl:with-param name="name" select="'publisher'"/>
						<xsl:with-param name="xpath" select="."/>
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>
	<xsl:template match="eterms:publishing-info/eterms:place">
		<xsl:param name="genre"/>
			<xsl:choose>
				<xsl:when test="$genre = 'conference-paper' or $genre = 'proceedings'">
					<!-- DO NOTHING -->
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="createField">
						<xsl:with-param name="name" select="'address'"/>
						<xsl:with-param name="xpath" select="."/>
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
	</xsl:template>
	<xsl:template match="dcterms:abstract">
		<xsl:if test=".!=''">
			<xsl:call-template name="createField">
				<xsl:with-param name="name" select="'abstract'"/>
				<xsl:with-param name="xpath" select="."/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	<xsl:template match="dc:subject">
		<xsl:if test=".!=''">
			<xsl:call-template name="createField">
				<xsl:with-param name="name" select="'keywords'"/>
				<xsl:with-param name="xpath" select="."/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	<xsl:template match="dcterms:tableOfContents">
		<xsl:if test=".!=''">
			<xsl:call-template name="createField">
				<xsl:with-param name="name" select="'contents'"/>
				<xsl:with-param name="xpath" select="."/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	<!-- creates a field for the bibtex entry -->
	<xsl:template name="createField">
		<xsl:param name="name"/>
		<xsl:param name="xpath"/>
		<xsl:value-of select="$name"/>
		<xsl:text disable-output-escaping="yes"> = &#123;</xsl:text>
		<xsl:choose>
			<xsl:when test="$name = 'title' or $name = 'booktitle' or $name = 'series' or $name = 'booktitle' or $name = 'journal' or $name = 'abstract' or $name = 'keywords '">
				<xsl:value-of select="concat('&#123;', jfunc:texString(normalize-space($xpath)), '&#125;')"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="jfunc:texString(normalize-space($xpath))"/>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:text disable-output-escaping="yes">&#125;,&#xD;&#xA;</xsl:text>
	</xsl:template>
	<!-- SOURCE -->
	<xsl:template match="source:source">
		<xsl:variable name="sourceType">
			<xsl:value-of select="@type"/>
		</xsl:variable>
		<xsl:variable name="publication-type-uri" select="parent::pub:publication/@type"/>
		<xsl:variable name="publication-type" select="$genre-ves/enum[@uri=$publication-type-uri]"/>
		<!-- TITLE -->
		<xsl:variable name="sgenre" select="$genre-ves/enum[@uri=$sourceType]"/>
		<xsl:choose>
			<xsl:when test="$publication-type = 'report'">
				<xsl:call-template name="createField">
					<xsl:with-param name="name" select="'type'"/>
					<xsl:with-param name="xpath" select="dc:title"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$sgenre='series'">
				<xsl:call-template name="createField">
					<xsl:with-param name="name" select="'series'"/>
					<xsl:with-param name="xpath" select="dc:title"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$sgenre='journal' or $sgenre='newspaper' or $sgenre='issue' ">
				<xsl:call-template name="createField">
					<xsl:with-param name="name" select="'journal'"/>
					<xsl:with-param name="xpath" select="dc:title"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$sgenre='book' or $sgenre='proceedings' or $sgenre='collected-edition' or $sgenre='handbook' or $sgenre='festschrift' or $sgenre='encyclopedia' ">
				<xsl:call-template name="createField">
					<xsl:with-param name="name" select="'booktitle'"/>
					<xsl:with-param name="xpath" select="dc:title"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise></xsl:otherwise>
		</xsl:choose>
		<!-- SOURCE CREATOR -->
		<xsl:if test="exists(eterms:creator[@role = $creator-ves/enum[.='author']/@uri]/*)">
			<xsl:text disable-output-escaping="yes">note = &#123;</xsl:text>
			<xsl:for-each select="eterms:creator[@role = $creator-ves/enum[.='author']/@uri]">
				<xsl:if test="position() &gt; 1">; </xsl:if>
				<xsl:variable name="role" select="@role"/>
				<xsl:choose>
					<xsl:when test="exists(person:person)">
						<xsl:value-of select="$creator-ves/enum[@uri = $role]"/>: 
						<xsl:value-of select="person:person/eterms:family-name"/>, 
						<xsl:value-of select="person:person/eterms:given-name"/>
					</xsl:when>
					<xsl:when test="exists(organization:organization)">
						<xsl:value-of select="$creator-ves/enum[@uri = $role]"/>: 
						<xsl:value-of select="organization:organization/dc:title"/>
					</xsl:when>
				</xsl:choose>
			</xsl:for-each>
			<xsl:text disable-output-escaping="yes">&#125;,&#xD;&#xA;</xsl:text>
		</xsl:if>
		<!-- SOURCE EDITOR -->
		<xsl:if test="exists(eterms:creator[@role = $creator-ves/enum[.='editor']/@uri]/*)">
			<xsl:choose>
				<xsl:when test="exists(eterms:creator[@role = $creator-ves/enum[.='editor']/@uri]/*) and (not (exists(../eterms:creator[@role = $creator-ves/enum[.='editor']/@uri]/*)))">
					<xsl:for-each select="eterms:creator[@role = $creator-ves/enum[.='editor']/@uri]">
						<xsl:apply-templates select="."/>
					</xsl:for-each>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text disable-output-escaping="yes">note = &#123;</xsl:text>
					<xsl:for-each select="eterms:creator[@role = $creator-ves/enum[.='editor']/@uri]">
						<xsl:if test="position() &gt; 1">; </xsl:if>
						<xsl:variable name="role" select="@role"/>
						<xsl:choose>
							<xsl:when test="exists(person:person)">
								<xsl:value-of select="$creator-ves/enum[@uri = $role]"/>: 
								<xsl:value-of select="person:person/eterms:family-name"/>, 
								<xsl:value-of select="person:person/eterms:given-name"/>
							</xsl:when>
							<xsl:when test="exists(organization:organization)">
								<xsl:value-of select="$creator-ves/enum[@uri = $role]"/>: 
								<xsl:value-of select="organization:organization/dc:title"/>
							</xsl:when>
						</xsl:choose>
					</xsl:for-each>
					<xsl:text disable-output-escaping="yes">&#125;,&#xD;&#xA;</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
		<!-- SOURCE VOLUME -->
		<xsl:if test="eterms:volume!=''">
			<xsl:call-template name="createField">
				<xsl:with-param name="name" select="'volume'"/>
				<xsl:with-param name="xpath" select="eterms:volume"/>
			</xsl:call-template>
		</xsl:if>
		<!-- SOURCE ISSUE -->
		<xsl:if test="eterms:issue!=''">
			<xsl:choose>
				<xsl:when test="$publication-type = 'article' or $publication-type='book-review' or $publication-type='magazine-article' or $publication-type='review-article'">
					<xsl:call-template name="createField">
						<xsl:with-param name="name" select="'number'"/>
						<xsl:with-param name="xpath" select="eterms:issue"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:when test="($publication-type = 'conference-paper' or $publication-type = 'proceedings') and not(eterms:volume)">
					<xsl:call-template name="createField">
						<xsl:with-param name="name" select="'number'"/>
						<xsl:with-param name="xpath" select="eterms:issue"/>
					</xsl:call-template>
				</xsl:when>
			</xsl:choose>
		</xsl:if>
		<!-- SOURCE PAGES -->
		<xsl:choose>
			<xsl:when test="fn:exists(parent::pub:publication/eterms:total-number-of-pages) and ($publication-type = 'book' or $publication-type = 'proceedings' or $publication-type = 'issue')" />
			<xsl:otherwise>
				<xsl:choose>
					<xsl:when test="normalize-space(eterms:start-page)!='' and normalize-space(eterms:end-page) != ''">
						<xsl:call-template name="createField">
							<xsl:with-param name="name" select="'pages'"/>
							<xsl:with-param name="xpath" select="concat(eterms:start-page, '--', eterms:end-page)"/>
						</xsl:call-template>
					</xsl:when>
					<xsl:when test="normalize-space(eterms:start-page)!='' and (not(eterms:end-page) or normalize-space(eterms:end-page) = '') ">
						<xsl:call-template name="createField">
							<xsl:with-param name="name" select="'pages'"/>
							<xsl:with-param name="xpath" select="eterms:start-page"/>
						</xsl:call-template>
					</xsl:when>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
		<!-- SEQUENCE NUMBER -->
		<xsl:if test="fn:exists(eterms:sequence-number) and (fn:normalize-space(eterms:sequence-number) != '')">
			<xsl:call-template name="createField">
				<xsl:with-param name="name" select="'eid'"/>
				<xsl:with-param name="xpath" select="eterms:sequence-number"/>
			</xsl:call-template>
		</xsl:if>
		<!-- TODO SOURCE HOWPUBLISHED -->
	</xsl:template>
	<xsl:template match="event:event">
		<xsl:param name="publication-type"/>
		<xsl:if test="./eterms:place!='' 
			and ($publication-type = 'proceedings'  or $publication-type = 'conference-paper' 
				or $publication-type='poster' or $publication-type='talk-at-event' 
				or $publication-type='meeting-abstract')">
			<xsl:call-template name="createField">
				<xsl:with-param name="name" select="'address'"/>
				<xsl:with-param name="xpath" select="./eterms:place"/>
			</xsl:call-template>
		</xsl:if>
		<xsl:if test="./dc:title!='' 
				and ($publication-type='conference-paper' or $publication-type='poster' or $publication-type='talk-at-event' 
					or $publication-type='meeting-abstract')">
			<xsl:call-template name="createField">
				<xsl:with-param name="name" select="'note'"/>
				<xsl:with-param name="xpath" select="./dc:title"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	<xsl:template match="eterms:publishing-info/eterms:edition">
		<xsl:if test=".!=''">
			<xsl:call-template name="createField">
				<xsl:with-param name="name" select="'edition'"/>
				<xsl:with-param name="xpath" select="."/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	<xsl:template match="dcterms:issued">
		<xsl:if test=".!=''">
			<xsl:call-template name="createField">
				<xsl:with-param name="name" select="'date'"/>
				<xsl:with-param name="xpath" select="substring(., 1, 10)"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	<!-- AUTHOR, EDITOR TEMPLATE -->
	<xsl:template match="eterms:creator">
		<xsl:apply-templates select="person:person"/>
		<xsl:apply-templates select="organization:organization"/>
	</xsl:template>
	<xsl:template name="roleLabel">
		<xsl:variable name="role-string" select="../@role"/>
		<xsl:variable name="role" select="$creator-ves/enum[@uri=$role-string]"/>
		<xsl:value-of select="$role"/>
		<!-- <xsl:text disable-output-escaping="yes"> = "</xsl:text>-->
	</xsl:template>
	<xsl:template match="person:person">
		<xsl:variable name="role-string" select="../@role"/>
		<xsl:variable name="role" select="$creator-ves/enum[@uri=$role-string]"/>
		<xsl:choose>
			<xsl:when test="count(../preceding-sibling::eterms:creator[@role=$role/@uri])=0">
				<!-- <xsl:call-template name="roleLabel"/>-->
				<xsl:value-of select="concat($role, ' = &#123;')"/>
			</xsl:when>
			<xsl:when test="count(../preceding-sibling::eterms:creator[@role=$role/@uri])=0 and count(../parent::source:source)=1">
				<xsl:value-of select="concat($role, ' = &#123;')"/>
			</xsl:when>
			<xsl:otherwise></xsl:otherwise>
		</xsl:choose>
		<xsl:variable name="familyname" select="jfunc:texString(normalize-space(eterms:family-name))"/>
		<xsl:variable name="givenname" select="jfunc:texString(normalize-space(eterms:given-name))"/>
		<xsl:value-of select="concat($familyname, ', ', $givenname, '')"/>
		<!-- AND-connection of persons -->
		<xsl:choose>
			<xsl:when test="exists(../following-sibling::eterms:creator[@role=$role/@uri])">
				<xsl:value-of select="' and '"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text disable-output-escaping="yes">&#125;,&#xD;&#xA;</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="organization:organization">
		<xsl:variable name="role-string" select="../@role"/>
		<xsl:variable name="role" select="$creator-ves/enum[@uri=$role-string]"/>
		<xsl:choose>
			<xsl:when test="count(../preceding-sibling::eterms:creator[@role=$role/@uri])=0 and count(../parent::source:source)=1">
				<xsl:value-of select="concat($role, ' = &#123;')"/>
			</xsl:when>
			<xsl:when test="count(../preceding-sibling::eterms:creator[@role=$role/@uri])=0">
				<!-- <xsl:call-template name="roleLabel"/>-->
				<xsl:value-of select="concat($role, ' = &#123;')"/>
			</xsl:when>
			<xsl:otherwise></xsl:otherwise>
		</xsl:choose>
		<xsl:value-of select="jfunc:texString(dc:title)"/>
		<!-- AND-connection of orgas -->
		<xsl:choose>
			<xsl:when test="exists(../following-sibling::eterms:creator[@role=$role/@uri])">
				<xsl:value-of select="' and '"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text disable-output-escaping="yes">&#125;,&#xD;&#xA;</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<!-- IDENTIFIER TEMPLATE -->
	<xsl:template name="identifiers">
		<xsl:param name="identifier-list"/>
		<xsl:variable name="uri-type-of-publication" select="./@type"/>
		<xsl:variable name="type-of-publication" select="$genre-ves/enum[@uri=$uri-type-of-publication]"/>
		<xsl:if test="exists($identifier-list/dc:identifier[@xsi:type='eterms:ISSN'])">
			<xsl:variable name="issn-concated">
				<xsl:value-of select="$identifier-list/dc:identifier[@xsi:type='eterms:ISSN']" separator="; " />
			</xsl:variable>
			<xsl:call-template name="createField">
				<xsl:with-param name="name" select="'issn'"/>
				<xsl:with-param name="xpath" select="jfunc:texString($issn-concated)"/>
			</xsl:call-template>
		</xsl:if>
		<xsl:if test="exists($identifier-list/dc:identifier[@xsi:type='eterms:ISBN'])">
			<xsl:variable name="isbn-concated">
				<xsl:value-of select="$identifier-list/dc:identifier[@xsi:type='eterms:ISBN']" separator="; " />
			</xsl:variable>
			<xsl:call-template name="createField">
				<xsl:with-param name="name" select="'isbn'"/>
				<xsl:with-param name="xpath" select="jfunc:texString($isbn-concated)"/>
			</xsl:call-template>
		</xsl:if>
		<xsl:if test="exists($identifier-list/dc:identifier[@xsi:type='eterms:URI' or @xsi:type='eterms:URN'])">
			<xsl:variable name="url-concated">
				<xsl:value-of select="$identifier-list/dc:identifier[@xsi:type='eterms:URI' or @xsi:type='eterms:URN']" separator="; " />
			</xsl:variable>
			<xsl:call-template name="createField">
				<xsl:with-param name="name" select="'url'"/>
				<xsl:with-param name="xpath" select="jfunc:texString($url-concated)"/>
			</xsl:call-template>
		</xsl:if>
		<xsl:if test="exists($identifier-list/dc:identifier[@xsi:type='eterms:DOI'])">
			<xsl:variable name="doi-concated">
				<xsl:value-of select="$identifier-list/dc:identifier[@xsi:type='eterms:DOI']" separator="; " />
			</xsl:variable>
			<xsl:call-template name="createField">
				<xsl:with-param name="name" select="'doi'"/>
				<xsl:with-param name="xpath" select="jfunc:texString($doi-concated)"/>
			</xsl:call-template>
		</xsl:if>
		<xsl:if test="exists($identifier-list/dc:identifier[@xsi:type='eterms:ARXIV'])">
			<xsl:variable name="arxiv-concated">
				<xsl:value-of select="$identifier-list/dc:identifier[@xsi:type='eterms:ARXIV']" separator="; " />
			</xsl:variable>
			<xsl:call-template name="createField">
				<xsl:with-param name="name" select="'eprint'"/>
				<xsl:with-param name="xpath" select="jfunc:texString($arxiv-concated)"/>
			</xsl:call-template>
		</xsl:if>
		<xsl:if test="exists($identifier-list/dc:identifier[@xsi:type='eterms:REPORT_NR']) and $type-of-publication = 'report'">
			<xsl:variable name="report_nr-concated">
				<xsl:value-of select="$identifier-list/dc:identifier[@xsi:type='eterms:REPORT_NR']" separator="; " />
			</xsl:variable>
			<xsl:call-template name="createField">
				<xsl:with-param name="name" select="'number'"/>
				<xsl:with-param name="xpath" select="jfunc:texString($report_nr-concated)"/>
			</xsl:call-template>
		</xsl:if>
		<xsl:if test="exists($identifier-list/dc:identifier[@xsi:type='eterms:PATENT_NR'])">
			<xsl:variable name="patent_nr-concated">
				<xsl:value-of select="$identifier-list/dc:identifier[@xsi:type='eterms:PATENT_NR']" separator="; " />
			</xsl:variable>
			<xsl:call-template name="createField">
				<xsl:with-param name="name" select="'note'"/>
				<xsl:with-param name="xpath" select="jfunc:texString(concat('Patent number: ', $patent_nr-concated))"/>
			</xsl:call-template>
		</xsl:if>
		<xsl:if test="exists($identifier-list/dc:identifier[@xsi:type='eterms:OTHER' and fn:starts-with(fn:lower-case(.), 'local-id:')]) ">
			<xsl:variable name="local-id-concated">
				<xsl:value-of select="$identifier-list/dc:identifier[@xsi:type='eterms:OTHER' and fn:starts-with(fn:lower-case(.), 'local-id:')]" separator="; " />
			</xsl:variable>
			<xsl:call-template name="createField">
				<xsl:with-param name="name" select="'localid'"/>
				<xsl:with-param name="xpath" select="jfunc:texString($local-id-concated)"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	<!-- REMOVE SUB AND SUP TAGS -->
	<xsl:template name="removeSubSup">
		<xsl:param name="elem" />
		<xsl:call-template name="removeTag">
			<xsl:with-param name="str">
				<xsl:call-template name="removeTag">
					<xsl:with-param name="str" select="$elem" />
					<xsl:with-param name="tag" select="'sub'" />
				</xsl:call-template>
			</xsl:with-param>
			<xsl:with-param name="tag" select="'sup'" />
		</xsl:call-template>
	</xsl:template>
	<!-- REMOVE TAG -->
	<xsl:template name="removeTag">
		<xsl:param name="str"/>
		<xsl:param name="tag"/>
		<xsl:choose>
			<xsl:when test="contains($str, concat('&lt;', $tag, '&gt;'))">
				<xsl:call-template name="replace-substring">
					<xsl:with-param name="original">
						<xsl:call-template name="replace-substring">
							<xsl:with-param name="original" select="$str"/>
							<xsl:with-param name="substring" select="concat('&lt;', $tag, '&gt;')"/>
						</xsl:call-template>
					</xsl:with-param>
					<xsl:with-param name="substring" select="concat('&lt;/', $tag, '&gt;')"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$str"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<!-- REPLACE STRING -->
	<xsl:template name="replace-substring">
		<xsl:param name="original"/>
		<xsl:param name="substring"/>
		<xsl:param name="replacement" select="''"/>
		<xsl:variable name="first">
			<xsl:choose>
				<xsl:when test="contains($original, $substring)">
					<xsl:value-of select="substring-before($original, $substring)"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$original"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="middle">
			<xsl:choose>
				<xsl:when test="contains($original, $substring)">
					<xsl:value-of select="$replacement"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text></xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="last">
			<xsl:choose>
				<xsl:when test="contains($original, $substring)">
					<xsl:choose>
						<xsl:when test="contains(substring-after($original, $substring), $substring)">
							<xsl:call-template name="replace-substring">
								<xsl:with-param name="original">
									<xsl:value-of select="substring-after($original, $substring)"/>
								</xsl:with-param>
								<xsl:with-param name="substring">
									<xsl:value-of select="$substring"/>
								</xsl:with-param>
								<xsl:with-param name="replacement">
									<xsl:value-of select="$replacement"/>
								</xsl:with-param>
							</xsl:call-template>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="substring-after($original, $substring)"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text></xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:value-of select="concat($first, $middle, $last)"/>
	</xsl:template>
</xsl:stylesheet>