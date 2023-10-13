<?xml version="1.0" encoding="UTF-8"?>
<!--  CDDL HEADER START  The contents of this file are subject to the terms of the  Common Development and Distribution License, Version 1.0 only  (the "License"). You may not use this file except in compliance  with the License.  You can obtain a copy of the license at license/ESCIDOC.LICENSE  or http://www.escidoc.org/license.  See the License for the specific language governing permissions  and limitations under the License.  When distributing Covered Code, include this CDDL HEADER in each  file and include the License file at license/ESCIDOC.LICENSE.  If applicable, add the following below this CDDL HEADER, with the  fields enclosed by brackets "[]" replaced with your own identifying  information: Portions Copyright [yyyy] [name of copyright owner]  CDDL HEADER END  Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft  für wissenschaftlich-technische Information mbH and Max-Planck-  Gesellschaft zur Förderung der Wissenschaft e.V.  All rights reserved. Use is subject to license terms. -->
<!--   Transformations from eSciDoc PubItem Schema to Highwire citation tags (for Google Scholar indexing)  Author: Markus Haarländer (initial creation)   $Author: MWalter $ (last changed)  $Revision: $   $LastChangedDate: $ -->
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:fn="http://www.w3.org/2005/xpath-functions"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:xlink="http://www.w3.org/1999/xlink"
	xmlns:escidoc="${xsd.metadata.escidocprofile.types}"
	xmlns:func="urn:my-functions"
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
	xmlns:eterms="${xsd.metadata.terms}"
	xmlns:escidocComponents="${xsd.soap.item.components}"
	xmlns:escidocItem="${xsd.soap.item.item}"
	xmlns:Util="java:de.mpg.mpdl.inge.transformation.Util">
	<xsl:import href="../../vocabulary-mappings.xsl"/>
	<xsl:output method="xhtml" version="1.0" encoding="UTF-8" indent="yes" omit-xml-declaration="yes"/>
	<xsl:param name="pubmanInstanceUrl"/>
	<xsl:param name="pubmanContextPath"/>
	<xsl:param name="pubmanComponentPattern"/>
	<xsl:variable name="escidocId" select="if (/escidocItem:item/@objid) then /escidocItem:item/@objid else tokenize(/escidocItem:item/@xlink:href, '/')[last()]" />
	<xsl:variable name="escidocIdWithVersion" select="if (/escidocItem:item/escidocItem:properties/prop:version/@objid) then /escidocItem:item/escidocItem:properties/prop:version/@objid else tokenize(/escidocItem:item/escidocItem:properties/prop:version/@xlink:href, '/')[last()]" />
	<xsl:variable name="gen" select="//pub:publication[1]/@type"/>
	<xsl:variable name="genre" select="$genre-ves/enum[@uri=$gen]"/>
	<!--Start with a line break -->
	<xsl:template match="/">
		<xsl:text>&#xa;</xsl:text>
		<xsl:apply-templates />
	</xsl:template>
	<!-- fulltext links -->
	<xsl:template match="//escidocComponents:component">
		<xsl:variable name="contentCategory" select="tokenize(escidocComponents:properties/prop:content-category, '/')[last()]"/>
		<xsl:variable name="visibility" select="escidocComponents:properties/prop:visibility"/>
		<xsl:if test="$visibility='public' and ($contentCategory='any-fulltext' or $contentCategory='pre-print' or  $contentCategory='post-print' or $contentCategory='publisher-version')">
			<!-- Files -->
			<xsl:if test="escidocComponents:content[@storage='internal-managed']">
				<xsl:variable name="mimeType" select="escidocComponents:properties/prop:mime-type"/>
				<xsl:variable name="citationKey" select="if ($mimeType='application/pdf') then $key-pdf-url else if ($mimeType='text/html' or $mimeType='application/xhtml+xml') then $key-fulltext-html-url else ''" />
				<!-- Do not use handles as fulltext links for now, because Google Scholar only accepts links from same host -->
				<xsl:if test="$citationKey!=''">
					<!-- <xsl:otherwise>-->
					<xsl:variable name="componentId" select="if (@objid) then @objid else tokenize(@xlink:href, '/')[last()]" />
					<xsl:variable name="filename" select="escidocComponents:properties/prop:file-name" />
					<xsl:variable name="path" select="replace(replace(replace($pubmanComponentPattern, '\$1', $escidocIdWithVersion), '\$2', $componentId), '\$3', encode-for-uri($filename))" />
					<xsl:call-template name="createMetatag">
						<xsl:with-param name="name" select="$citationKey"/>
						<xsl:with-param name="content" select="concat($pubmanInstanceUrl, $pubmanContextPath, $path)"/>
					</xsl:call-template>
					<!-- </xsl:otherwise></xsl:choose>-->
				</xsl:if>
			</xsl:if>
			<!-- Locators -->
			<xsl:if test="escidocComponents:content[@storage='external-url']">
				<xsl:call-template name="createMetatag">
					<xsl:with-param name="name" select="$key-fulltext-html-url"/>
					<xsl:with-param name="content" select="escidocComponents:content/@xlink:href"/>
				</xsl:call-template>
			</xsl:if>
		</xsl:if>
	</xsl:template>
	<!-- start md-record/publication -->
	<xsl:template match="//pub:publication">
		<xsl:variable name="pubdate" select="if(dcterms:issued!='') then dcterms:issued else if (eterms:published-online!='') then eterms:published-online else if (dcterms:dateAccepted!='') then dcterms:dateAccepted else if (dcterms:dateSubmitted!='') then dcterms:dateSubmitted else if (dcterms:modified!='') then dcterms:modified else if (dcterms:created!='') then dcterms:created else ''"/>
		<xsl:call-template name="createMetatag">
			<xsl:with-param name="name" select="$key-publication-date"/>
			<xsl:with-param name="content" select="replace($pubdate, '-', '/')"/>
		</xsl:call-template>
		<xsl:apply-templates/>
	</xsl:template>
	<xsl:template match="pub:publication/dc:title">
		<xsl:call-template name="createMetatag">
			<xsl:with-param name="name" select="$key-title"/>
			<xsl:with-param name="content" select="Util:stripHtml(.)"/>
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="pub:publication/eterms:creator[@role!=($creator-ves/enum[.='referee']/@uri) and @role!=($creator-ves/enum[.='advisor']/@uri) and @role!=($creator-ves/enum[.='honoree']/@uri) and @role!=($creator-ves/enum[.='translator']/@uri) and @role!=($creator-ves/enum[.='transcriber']/@uri)  and @role!=($creator-ves/enum[.='contributor']/@uri)]/person:person">
		<xsl:call-template name="createMetatag">
			<xsl:with-param name="name" select="$key-author"/>
			<xsl:with-param name="content" select="concat(eterms:family-name, ', ', eterms:given-name)"/>
		</xsl:call-template>
		<xsl:for-each select="organization:organization">
			<xsl:choose>
				<xsl:when test="$genre='thesis'">
					<xsl:if test="not(//pub:publication/eterms:publishing-info/dc:publisher)">
						<xsl:call-template name="createMetatag">
							<xsl:with-param name="name" select="$key-dissertation-institution"/>
							<xsl:with-param name="content" select="dc:title"/>
						</xsl:call-template>
					</xsl:if>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="createMetatag">
						<xsl:with-param name="name" select="$key-author-affiliation"/>
						<xsl:with-param name="content" select="dc:title"/>
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
	</xsl:template>
	<xsl:template match="pub:publication/eterms:creator/organization:organization">
		<xsl:choose>
			<xsl:when test="$genre='thesis'">
				<xsl:if test="not(//pub:publication/eterms:publishing-info/dc:publisher)">
					<xsl:call-template name="createMetatag">
						<xsl:with-param name="name" select="$key-dissertation-institution"/>
						<xsl:with-param name="content" select="dc:title"/>
					</xsl:call-template>
				</xsl:if>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="createMetatag">
					<xsl:with-param name="name" select="$key-author-institution"/>
					<xsl:with-param name="content" select="dc:title"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="pub:publication/eterms:degree">
		<xsl:if test="$genre='thesis'">
			<xsl:variable name="degree" select="."/>
			<xsl:call-template name="createMetatag">
				<xsl:with-param name="name" select="$key-degree"/>
				<xsl:with-param name="content" select="$degree-ves/enum[@uri=$degree]"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	<xsl:template match="pub:publication/dc:language">
		<xsl:call-template name="createMetatag">
			<xsl:with-param name="name" select="$key-language"/>
			<xsl:with-param name="content" select="."/>
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="pub:publication/dc:identifier[@xsi:type='eterms:DOI']">
		<xsl:call-template name="createMetatag">
			<xsl:with-param name="name" select="$key-doi"/>
			<xsl:with-param name="content" select="concat($prefix-doi, .)"/>
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="pub:publication/dc:identifier[@xsi:type='eterms:ARXIV']">
		<xsl:call-template name="createMetatag">
			<xsl:with-param name="name" select="$key-arxiv-id"/>
			<xsl:with-param name="content" select="concat($prefix-arxiv-id, .)"/>
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="pub:publication/dc:identifier[@xsi:type='eterms:PMID']">
		<xsl:call-template name="createMetatag">
			<xsl:with-param name="name" select="$key-pmid"/>
			<xsl:with-param name="content" select="concat($prefix-pmid, .)"/>
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="pub:publication/dc:identifier[@xsi:type='eterms:ISBN']">
		<xsl:if test="$genre='book' or $genre='collected-edition' or $genre='festschrift' or $genre='handbook' or $genre='monograph'">
			<xsl:call-template name="createMetatag">
				<xsl:with-param name="name" select="$key-isbn"/>
				<xsl:with-param name="content" select="concat($prefix-isbn, .)"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	<xsl:template match="pub:publication/dcterms:subject">
		<xsl:call-template name="createMetatag">
			<xsl:with-param name="name" select="$key-keywords"/>
			<xsl:with-param name="content" select="Util:stripHtml(.)"/>
		</xsl:call-template>
	</xsl:template>
	<!-- start EVENT tags -->
	<xsl:template match="pub:publication/event:event/dc:title">
		<xsl:if test="$genre='conference-paper' or $genre='proceedings' or $genre='conference-report' or $genre='talk-at-event' or $genre='courseware-lecture' or $genre='poster'">
			<xsl:call-template name="createMetatag">
				<xsl:with-param name="name" select="$key-conference"/>
				<xsl:with-param name="content" select="."/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	<!-- end EVENT tags -->
	<!-- Use publisher as dissertation institution for thesis -->
	<xsl:template match="pub:publication/eterms:publishing-info/dc:publisher">
		<xsl:choose>
			<xsl:when test="$genre='thesis'">
				<xsl:call-template name="createMetatag">
					<xsl:with-param name="name" select="$key-dissertation-institution"/>
					<xsl:with-param name="content" select="normalize-space(concat(., ' ', ../eterms:place))"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="createMetatag">
					<xsl:with-param name="name" select="$key-publisher"/>
					<xsl:with-param name="content" select="."/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="pub:publication/source:source">
		<xsl:variable name="sourceGen" select="@type"/>
		<xsl:variable name="sourceGenre" select="$genre-ves/enum[@uri=$sourceGen]"/>
		<!--<xsl:if test="$sourceGenre='journal'">-->
		<xsl:apply-templates mode="journal"/>
		<!--</xsl:if>-->
	</xsl:template>
	<!-- start JOURNAL tags -->
	<xsl:template match="source:source/dc:title" mode="journal">
		<xsl:choose>
			<xsl:when test="$genre='book-item' or $genre='contribution-to-collected-edition' or $genre='contribution-to-festschrift' or $genre='contribution-to-handbook'">
				<xsl:call-template name="createMetatag">
					<xsl:with-param name="name" select="$key-inbook-title"/>
					<xsl:with-param name="content" select="."/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$genre='article' or $genre='newspaper-article' or $genre='book-review' or $genre='magazine-article' or $genre='review-article'">
				<xsl:call-template name="createMetatag">
					<xsl:with-param name="name" select="$key-journal-title"/>
					<xsl:with-param name="content" select="."/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise></xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="source:source/dcterms:alternative[@xsi:type='eterms:ABBREVIATION']" mode="journal">
		<xsl:if test="$genre='article' or $genre='newspaper-article' or $genre='magazine-article' or $genre='review-article'">
			<xsl:call-template name="createMetatag">
				<xsl:with-param name="name" select="$key-journal-abbrev"/>
				<xsl:with-param name="content" select="."/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	<xsl:template match="source:source/eterms:volume" mode="journal">
		<xsl:if test="$genre='article' or $genre='newspaper-article' or $genre='magazine-article' or $genre='review-article'">
			<xsl:call-template name="createMetatag">
				<xsl:with-param name="name" select="$key-volume"/>
				<xsl:with-param name="content" select="."/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	<xsl:template match="source:source/eterms:issue" mode="journal">
		<xsl:if test="$genre='article' or $genre='newspaper-article' or $genre='magazine-article' or $genre='review-article'">
			<xsl:call-template name="createMetatag">
				<xsl:with-param name="name" select="$key-issue"/>
				<xsl:with-param name="content" select="."/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	<xsl:template match="source:source/eterms:start-page" mode="journal">
		<xsl:call-template name="createMetatag">
			<xsl:with-param name="name" select="$key-firstpage"/>
			<xsl:with-param name="content" select="."/>
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="source:source/eterms:end-page" mode="journal">
		<xsl:call-template name="createMetatag">
			<xsl:with-param name="name" select="$key-lastpage"/>
			<xsl:with-param name="content" select="."/>
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="source:source/eterms:publishing-info/dc:publisher" mode="journal">
		<xsl:call-template name="createMetatag">
			<xsl:with-param name="name" select="$key-publisher"/>
			<xsl:with-param name="content" select="."/>
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="source:source/dc:identifier[@xsi:type='eterms:ISSN']" mode="journal">
		<xsl:if test="$genre='article' or $genre='newspaper-article' or $genre='magazine-article' or $genre='review-article'">
			<xsl:call-template name="createMetatag">
				<xsl:with-param name="name" select="$key-issn"/>
				<xsl:with-param name="content" select="concat($prefix-issn, .)"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	<xsl:template match="source:source/dc:identifier[@xsi:type='eterms:ISBN']" mode="journal">
		<xsl:if test="$genre='book-item' or $genre='contribution-to-collected-edition' or $genre='contribution-to-festschrift' or $genre='contribution-to-handbook'">
			<xsl:call-template name="createMetatag">
				<xsl:with-param name="name" select="$key-isbn"/>
				<xsl:with-param name="content" select="concat($prefix-isbn, .)"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	<!-- end JOURNAL tags -->
	<!-- end md-record/publication -->
	<!-- create meta tag -->
	<xsl:template name="createMetatag">
		<xsl:param name="name"/>
		<xsl:param name="content"/>
		<xsl:if test="normalize-space($name) and normalize-space($content)">
			<xsl:element name="meta" namespace="http://www.w3.org/1999/xhtml" >
				<xsl:attribute name="name" select="$name"/>
				<xsl:attribute name="content" select="$content"/>
			</xsl:element>
		</xsl:if>
	</xsl:template>
	<xsl:template match="text()"></xsl:template>
	<xsl:template match="text()" mode="journal"></xsl:template>
</xsl:stylesheet>