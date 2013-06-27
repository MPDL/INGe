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


 Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft
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
		xmlns:AuthorDecoder="java:de.mpg.escidoc.services.transformation.transformations.commonPublicationFormats.creators.AuthorDecoder"
		xmlns:Util="java:de.mpg.escidoc.services.transformation.Util"
		xmlns:escidoc="urn:escidoc:functions"
		xmlns:ei="http://www.escidoc.de/schemas/item/0.8"
		xmlns:mdr="http://www.escidoc.de/schemas/metadatarecords/0.4"		
		xmlns:file="${xsd.metadata.file}"
		xmlns:pub="${xsd.metadata.publication}"
		xmlns:person="${xsd.metadata.person}"
		xmlns:source="${xsd.metadata.source}"
		xmlns:event="${xsd.metadata.event}"
		xmlns:organization="${xsd.metadata.organization}"		
		xmlns:eterms="${xsd.metadata.terms}"
		xmlns:ec="http://www.escidoc.de/schemas/components/0.8"
		xmlns:prop="${xsd.soap.common.prop}"
		xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
		xmlns:itemlist="http://www.escidoc.de/schemas/itemlist/0.8">
	
	<xsl:import href="../../vocabulary-mappings.xsl"/>
	
	<xsl:output method="xml" encoding="UTF-8" indent="yes"/>
	
	
	
	<xsl:param name="user" select="'dummy-user'"/>
	<xsl:param name="context" select="'dummy-context'"/>
	<xsl:param name="is-item-list" select="true()"/>
	<xsl:param name="localIdentifier" select="'xserveg5.eva.mpg.de'"/>
	<xsl:param name="localPrefix"/>
	<xsl:param name="localSuffix" select="'.pdf'"/>
	<xsl:param name="locator-filename-substitute" select="'external resource'"/>
	<xsl:param name="external-organization" select="'dummy-external-ou'"/>
	
	<xsl:param name="content-model"/>
	<!--
		DC XML  Header
	-->
	
		
	<!-- VARIABLEN -->
	

	
	<xsl:template match="/">
		<xsl:choose>
			<xsl:when test="$is-item-list">
				<itemlist:item-list>
					<xsl:apply-templates select="//item"/>
				</itemlist:item-list>
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
				<srel:content-model objid="{$content-model}"/>
				<xsl:element name="prop:content-model-specific"/>
			</xsl:element>
			<xsl:element name="mdr:md-records">
				<mdr:md-record name="escidoc">
					<xsl:call-template name="itemMetadata"/>
				</mdr:md-record>
			</xsl:element>
			<xsl:element name="ec:components">
				<xsl:for-each select="mab655_e">
					<xsl:call-template name="createFile"/>
				</xsl:for-each>
			</xsl:element>
		</xsl:element>
	</xsl:template>
	
	<!-- GENRE -->
	<xsl:template name="itemMetadata">
		<xsl:variable name="gen">
			<xsl:choose>
				<xsl:when test="normalize-space(mab519) != ''">
					<xsl:value-of select="$genre-ves/enum[.='thesis']/@uri"/>
				</xsl:when>
				<xsl:when test="normalize-space(mab590) != '' and normalize-space(mab591) != ''">
					<xsl:value-of select="$genre-ves/enum[.='book-item']/@uri"/>
				</xsl:when>
				<xsl:when test="normalize-space(mab590) != ''">
					<xsl:value-of select="$genre-ves/enum[.='article']/@uri"/>
				</xsl:when>
				<xsl:when test="mab029_m = 'P'">
					<xsl:value-of select="$genre-ves/enum[.='article']/@uri"/>
				</xsl:when>
				<xsl:when test="mab029_m = 'CH'">
					<xsl:value-of select="$genre-ves/enum[.='book-item']/@uri"/>
				</xsl:when>
				<xsl:when test="mab029_m = 'J'">
					<xsl:value-of select="$genre-ves/enum[.='journal']/@uri"/>
				</xsl:when>
				<!-- Other cases -->
				<xsl:otherwise>
					<xsl:value-of select="$genre-ves/enum[.='book']/@uri"/>			
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		
		<xsl:call-template name="createEntry">
			<xsl:with-param name="gen" select="$gen"/>
		</xsl:call-template>
	</xsl:template>
	
	<!-- Create eSciDoc Entry -->
	<xsl:template name="createEntry">
		<xsl:param name="gen"/>
		
		<xsl:element name="pub:publication">
			<xsl:attribute name="type">
				<xsl:value-of select="$gen"/>
			</xsl:attribute>
			<!-- CREATOR : AUTHOR-->
			<xsl:apply-templates select="mab100"/>
			<xsl:apply-templates select="mab104_a"/>
			<xsl:apply-templates select="mab108_a"/>
			<xsl:apply-templates select="mab112_a"/>
			<!-- CREATOR : EDITOR -->
			<xsl:apply-templates select="mab200"/>
			<xsl:apply-templates select="mab200_b"/>
			<xsl:apply-templates select="mab100_b"/>
			<xsl:apply-templates select="mab104_b"/>
			<xsl:apply-templates select="mab100_c"/>
			<xsl:apply-templates select="mab108_b"/>
			<xsl:apply-templates select="mab112_b"/>
			<xsl:apply-templates select="mab112_f"/>
			<!-- TITLE -->
			<xsl:choose>
				<xsl:when test="mab331 and mab335">
					
					<xsl:variable name="add335" select="concat(concat(mab331,' : '),mab335)"/>
					<xsl:call-template name="createTitle">
						<xsl:with-param name="title" select="$add335"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="createTitle">
						<xsl:with-param name="title" select="mab331"/>
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
			<!-- ALT -->
			<xsl:if test="mab304">
				<xsl:call-template name="createAlternative">
					<xsl:with-param name="title" select="mab304"/>
				</xsl:call-template>
			</xsl:if>
			<xsl:if test="mab310">
				<xsl:call-template name="createAlternative">
					<xsl:with-param name="title" select="mab304"/>
				</xsl:call-template>
			</xsl:if>
			<xsl:if test="mab341">
				<xsl:call-template name="createAlternative">
					<xsl:with-param name="title" select="mab304"/>
				</xsl:call-template>
			</xsl:if>
			<xsl:if test="mab343">
				<xsl:call-template name="createAlternative">
					<xsl:with-param name="title" select="mab304"/>
				</xsl:call-template>
			</xsl:if>
			<xsl:if test="mab370">
				<xsl:call-template name="createAlternative">
					<xsl:with-param name="title" select="mab304"/>
				</xsl:call-template>
			</xsl:if>
			<!-- LANGUAGE -->
			<xsl:apply-templates select="mab037_c"/>
			<!-- IDENTIFIER -->
			<xsl:apply-templates select="mab001"/>
			<xsl:apply-templates select="mab025_z"/>
			<xsl:apply-templates select="mab088"/>
			<xsl:apply-templates select="mab655"/>
			<xsl:apply-templates select="mab655_e"/>
			<xsl:apply-templates select="mab540"/>
			<xsl:apply-templates select="mab540_a"/>
			<xsl:apply-templates select="mab540_b"/>
			<xsl:apply-templates select="mab542"/>
			<xsl:apply-templates select="mab542_a"/>
			<!-- PUBLISHING-INFO -->
			
			<xsl:call-template name="createPublInfo"/>
			<!-- DATES -->
			<xsl:apply-templates select="mab425"/>
			<xsl:apply-templates select="mab519"/>
			
			<!-- SOURCE -->
			<xsl:if test="mab451">
				<xsl:call-template name="createSource">
					<xsl:with-param name="title" select="mab451"/>
					<xsl:with-param name="sourceNo">1</xsl:with-param>
				</xsl:call-template>
			</xsl:if>
			<xsl:if test="mab451_b">
				<xsl:call-template name="createSource">
					<xsl:with-param name="title" select="mab451_b"/>
					<xsl:with-param name="sourceNo">1</xsl:with-param>
				</xsl:call-template>
			</xsl:if>
			<xsl:if test="mab461">
				<xsl:call-template name="createSource">
					<xsl:with-param name="title" select="mab461"/>
					<xsl:with-param name="sourceNo">2</xsl:with-param>
				</xsl:call-template>
			</xsl:if>
			<xsl:if test="mab471">
				<xsl:call-template name="createSource">
					<xsl:with-param name="title" select="mab471"/>
					<xsl:with-param name="sourceNo">3</xsl:with-param>
				</xsl:call-template>
			</xsl:if>
			<xsl:if test="mab590">
				<xsl:call-template name="createSource">
					<xsl:with-param name="title" select="mab590"/>
					<xsl:with-param name="sourceNo">4</xsl:with-param>
				</xsl:call-template>
			</xsl:if>
			<!-- EVENT -->
			
			<!-- PAGES -->
			<xsl:if test="mab433">
				<xsl:element name="eterms:total-number-of-pages">
					<xsl:if test="mab433">
						<xsl:value-of select="mab433"/>
					</xsl:if>
				</xsl:element>
			</xsl:if>
			<!-- DEGREE -->
			<xsl:variable name="degree" select="substring-after(mab519,',')"/>
			<!-- <xsl:variable name="degree" select="substring-after(substring-after(mab519,','),',')"/> -->
			<xsl:if test="not($degree='')">
				<xsl:element name="eterms:degree">
					<xsl:choose>
						<xsl:when test="contains($degree,'Dipl') or contains($degree,'Diplom')">
							<xsl:value-of select="$degree-ves/enum[.='diploma']/@uri"/>
						</xsl:when>
						<xsl:when test="contains($degree,'Master')">
							<xsl:value-of select="$degree-ves/enum[.='master']/@uri"/>
						</xsl:when>
						<xsl:when test="contains($degree,'MA') or contains($degree,'M.A.') or contains($degree,'Magister')">
							<xsl:value-of select="$degree-ves/enum[.='magister']/@uri"/>
						</xsl:when>
						<xsl:when test="contains($degree,'Diss') or contains($degree,'PhD')">
							<xsl:value-of select="$degree-ves/enum[.='phd']/@uri"/>
						</xsl:when>
						<xsl:when test="contains($degree,'Habil.-Schr.')">
							<xsl:value-of select="$degree-ves/enum[.='habilitation']/@uri"/>
						</xsl:when>
						<xsl:when test="contains($degree,'BA') or contains($degree,'B.A.') or contains($degree,'Bachelor')">
							<xsl:value-of select="$degree-ves/enum[.='bachelor']/@uri"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="error(QName('http://www.escidoc.de', 'err:DegreeTypeNotRecognized' ), concat('The following value was read as the degree, but was not recognized as a valid eSciDoc degree: [', $degree, ']'))"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:element>
			</xsl:if>
			<!-- ABSTRACT -->
			<xsl:apply-templates select="mab750"/>
			<xsl:apply-templates select="mab526"/>
			<!-- SUBJECT -->
			<xsl:variable name="subject">
				<xsl:apply-templates select="mab700_c" mode="subject"/>
				<xsl:apply-templates select="mab711_b" mode="subject_iso"/>
				<xsl:apply-templates select="mab711_t" mode="subject_iso"/>
				<xsl:apply-templates select="mab740_s" mode="subject"/>
			</xsl:variable>
			<xsl:element name="dcterms:subject">
				<xsl:value-of select="normalize-space(substring($subject,3))"></xsl:value-of>
			</xsl:element>
			<!--end publication-->
		</xsl:element>
	</xsl:template>
	<!-- CREATOR -->
	<xsl:template match="mab100">
		<xsl:element name="eterms:creator">
			<xsl:attribute name="role" select="$creator-ves/enum[.='author']/@uri"/>
			<xsl:element name="person:person">
				<xsl:call-template name="createPersonName"/>
				<xsl:if test="../mab101">
					<xsl:element name="eterms:alternative-name">
						<xsl:value-of select="../mab101"/>
					</xsl:element>
				</xsl:if>
				<xsl:element name="organization:organization">
					<xsl:choose>
						<xsl:when test="../mab103">
							<xsl:element name="dc:title">
								<xsl:value-of select="../mab103"/>
							</xsl:element>
						</xsl:when>
						<xsl:otherwise>
							<dc:title>External Organizations</dc:title>
							<dc:identifier><xsl:value-of select="$external-organization"/></dc:identifier>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:element>
			</xsl:element>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="mab104_a">
		<xsl:element name="eterms:creator">
			<xsl:attribute name="role" select="$creator-ves/enum[.='author']/@uri"/>
			<xsl:element name="person:person">
				<xsl:call-template name="createPersonName"/>
				<xsl:if test="../mab105">
					<xsl:element name="eterms:alternative-name">
						<xsl:value-of select="../mab105"/>
					</xsl:element>
				</xsl:if>
			</xsl:element>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="mab100_b">
		<xsl:call-template name="createPersonCreator">
			<xsl:with-param name="role" select="$creator-ves/enum[.='editor']/@uri"/>
			<xsl:with-param name="org" select="true()"/>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="mab104_b">
		<xsl:call-template name="createPersonCreator">
			<xsl:with-param name="role" select="$creator-ves/enum[.='editor']/@uri"/>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="mab100_c">
		<xsl:call-template name="createPersonCreator">
			<xsl:with-param name="role" select="$creator-ves/enum[.='editor']/@uri"/>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="mab108_b">
		<xsl:call-template name="createPersonCreator">
			<xsl:with-param name="role" select="$creator-ves/enum[.='editor']/@uri"/>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="mab112_b">
		<xsl:call-template name="createPersonCreator">
			<xsl:with-param name="role" select="$creator-ves/enum[.='editor']/@uri"/>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="mab112_f">
		<xsl:call-template name="createPersonCreator">
			<xsl:with-param name="role" select="$creator-ves/enum[.='honoree']/@uri"/>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template name="createPersonCreator">
		<xsl:param name="role"/>
		<xsl:param name="org" select="false()"/>
		<xsl:element name="eterms:creator">
			<xsl:attribute name="role" select="$role"/>
			<xsl:element name="person:person">
				<xsl:call-template name="createPersonName"/>
				<xsl:if test="$org">
					<xsl:element name="organization:organization">
						<xsl:choose>
							<xsl:when test="../mab103">
								<xsl:element name="dc:title">
									<xsl:value-of select="../mab103"/>
								</xsl:element>
							</xsl:when>
							<xsl:otherwise>
								<dc:title>External Organizations</dc:title>
								<dc:identifier><xsl:value-of select="$external-organization"/></dc:identifier>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:element>
				</xsl:if>
			</xsl:element>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="mab200">
		<xsl:call-template name="createOrganizationCreator">
			<xsl:with-param name="role" select="$creator-ves/enum[.='editor']/@uri"/>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="mab200_b">
		<xsl:variable name="role">
			<xsl:choose>
				<xsl:when test="../mab100"><xsl:value-of select="$creator-ves/enum[.='editor']/@uri"/></xsl:when>
				<xsl:otherwise><xsl:value-of select="$creator-ves/enum[.='editor']/@uri"/></xsl:otherwise>
			</xsl:choose>
		
		</xsl:variable>
		<xsl:call-template name="createOrganizationCreator">
			<xsl:with-param name="role" select="$role"/>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="mab204_a">
		<xsl:call-template name="createOrganizationCreator">
			<xsl:with-param name="role" select="$creator-ves/enum[.='editor']/@uri"/>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="mab204_b">
		<xsl:call-template name="createOrganizationCreator">
			<xsl:with-param name="role" select="$creator-ves/enum[.='editor']/@uri"/>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="mab208_a">
		<xsl:call-template name="createOrganizationCreator">
			<xsl:with-param name="role" select="$creator-ves/enum[.='editor']/@uri"/>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template name="createOrganizationCreator">
		<xsl:param name="role"/>
		<xsl:element name="eterms:creator">
			<xsl:attribute name="role" select="$role"/>
			<xsl:element name="organization:organization">
				<!-- <xsl:attribute name="role" select="$role"/>-->
				<xsl:element name="dc:title">
					<xsl:value-of select="."/>
				</xsl:element>
			</xsl:element>
		</xsl:element>
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
	
	<!-- IDENTIFIER -->
	<xsl:template match="mab001">
		<xsl:call-template name="createID">
			<xsl:with-param name="idtype">eterms:OTHER</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="mab025_z">
		<xsl:call-template name="createID">
			<xsl:with-param name="idtype">eterms:ZDB</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="mab088">
		<xsl:call-template name="createID">
			<xsl:with-param name="idtype">eterms:URI</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="mab655">
		<xsl:call-template name="createID">
			<xsl:with-param name="idtype">eterms:URI</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="mab655_e">
		<xsl:call-template name="createID">
			<xsl:with-param name="idtype">eterms:URI</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="mab540">
		<xsl:call-template name="createID">
			<xsl:with-param name="idtype">eterms:ISBN</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="mab540_a">
		<xsl:call-template name="createID">
			<xsl:with-param name="idtype">eterms:ISBN</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="mab540_b">
		<xsl:call-template name="createID">
			<xsl:with-param name="idtype">eterms:ISBN</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="mab542">
		<xsl:call-template name="createID">
			<xsl:with-param name="idtype">eterms:ISSN</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="mab542_a">
		<xsl:call-template name="createID">
			<xsl:with-param name="idtype">eterms:ISSN</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template name="createID">
		<xsl:param name="idtype"/>
		<xsl:element name="dc:identifier">
			<xsl:choose>
				<xsl:when test="contains(replace(normalize-space(.), ' ', ''), normalize-space($localIdentifier))">
					<xsl:attribute name="xsi:type" select="'eterms:OTHER'"/>
					<xsl:value-of select="escidoc:substring-after-last(., '/')"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:attribute name="xsi:type" select="$idtype"/>
					<xsl:value-of select="."/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:element>
	</xsl:template>
	
	<!-- ABSTRACT -->
	<xsl:template match="mab750">
		<xsl:element name="dcterms:abstract">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="mab526">
		<xsl:element name="dcterms:abstract">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	
	<!-- LANGUAGE -->
	<xsl:template match="mab037_c">
		<xsl:for-each select="tokenize(normalize-space(replace(.,'/', ' ')), '\s+')">
			<xsl:element name="dc:language">
			<xsl:choose>
				<xsl:when test=". = 'afr'">af</xsl:when>
				<xsl:when test=". = 'ara'">ar</xsl:when>
				<xsl:when test=". = 'aze'">az</xsl:when>
				<xsl:when test=". = 'bul'">bg</xsl:when>
				<xsl:when test=". = 'bis'">bi</xsl:when>
				<xsl:when test=". = 'ben'">bn</xsl:when>
				<xsl:when test=". = 'tib'">bo</xsl:when>
				<xsl:when test=". = 'ger'">de</xsl:when>
				<xsl:when test=". = 'eng'">en</xsl:when>
				<xsl:when test=". = 'spa'">es</xsl:when>
				<xsl:when test=". = 'fre'">fr</xsl:when>
				<xsl:when test=". = 'gua'">gn</xsl:when>
				<xsl:when test=". = 'heb'">he</xsl:when>
				<xsl:when test=". = 'hin'">hi</xsl:when>
				<xsl:when test=". = 'ind'">id</xsl:when>
				<xsl:when test=". = 'ita'">it</xsl:when>
				<xsl:when test=". = 'jpn'">ja</xsl:when>
				<xsl:when test=". = 'geo'">ka</xsl:when>
				<xsl:when test=". = 'kaz'">kk</xsl:when>
				<xsl:when test=". = 'kan'">kn</xsl:when>
				<xsl:when test=". = 'kor'">ko</xsl:when>
				<xsl:when test=". = 'lat'">la</xsl:when>
				<xsl:when test=". = 'mon'">mn</xsl:when>
				<xsl:when test=". = 'bur'">my</xsl:when>
				<xsl:when test=". = 'dut'">nl</xsl:when>
				<xsl:when test=". = 'por'">pt</xsl:when>
				<xsl:when test=". = 'que'">qu</xsl:when>
				<xsl:when test=". = 'rum'">ro</xsl:when>
				<xsl:when test=". = 'rus'">ru</xsl:when>
				<xsl:when test=". = 'alb'">sq</xsl:when>
				<xsl:when test=". = 'tam'">ta</xsl:when>
				<xsl:when test=". = 'tur'">tr</xsl:when>
				<xsl:when test=". = 'wol'">wo</xsl:when>
				<xsl:when test=". = 'chi'">zh</xsl:when>
				<xsl:otherwise></xsl:otherwise>
			</xsl:choose>
			</xsl:element>
		</xsl:for-each>
	</xsl:template>
	
	<!-- SUBJECT -->
	<xsl:template match="*" mode="subject">
			<xsl:value-of select="', '"></xsl:value-of>
			<xsl:value-of select="."/>
	</xsl:template>
	
	<xsl:template match="*" mode="subject_iso">
			<xsl:value-of select="', ISO 639-3 : '"></xsl:value-of>
			<xsl:value-of select="."/>
	</xsl:template>
	
	<!-- DATES -->
	<xsl:template match="mab425">
		<xsl:analyze-string select="." regex="\d\d\d\d">
			<xsl:matching-substring>
				<xsl:element name="dcterms:issued">
					<xsl:value-of select="."/>
				</xsl:element>
			</xsl:matching-substring>
		</xsl:analyze-string>
	</xsl:template>
	
	<xsl:template match="mab519">		
	 	<xsl:analyze-string select="." regex="\d\d\d\d">
			<xsl:matching-substring>
				<xsl:element name="dcterms:dateAccepted">
					<xsl:value-of select="."/>
				</xsl:element>
			</xsl:matching-substring>
		</xsl:analyze-string>-->
	</xsl:template>
	<xsl:template name="createPublInfo">
		<xsl:element name="eterms:publishing-info">
			<xsl:variable name="publisher" select="substring-before(substring-after(mab519,','),',')"/>
			<xsl:variable name="place" select="substring-before(mab519,',')"/>
			<!-- PUBLISHER INFO -->
			<xsl:choose>
				<xsl:when test="not(mab412) and $publisher != ''">
					<xsl:element name="dc:publisher">
						<xsl:value-of select="$publisher"/>
					</xsl:element>
				</xsl:when>
				<xsl:when test="mab412 != ''">
					<xsl:element name="dc:publisher">
						<xsl:value-of select="mab412 "/>
					</xsl:element>
				</xsl:when>
				<xsl:otherwise>
					<xsl:element name="dc:publisher">N.A.</xsl:element>
				</xsl:otherwise>
			</xsl:choose>	
			<!-- PUBLISHER PLACE -->
			<xsl:choose>
				<xsl:when test="not(mab412) and $place != ''">
					<xsl:element name="eterms:place">
						<xsl:value-of select="$place"/>
					</xsl:element>		
				</xsl:when>
				<xsl:when test="mab410 != ''">
					<xsl:element name="eterms:place">
						<xsl:value-of select="mab410"/>
					</xsl:element>		
				</xsl:when>	
				<xsl:otherwise>
					<xsl:element name="eterms:place">N.A.</xsl:element>
				</xsl:otherwise>
			</xsl:choose>
			<!-- EDITION -->
			<xsl:choose>
				<xsl:when test="mab403 != ''">
					<xsl:element name="eterms:edition">
						<xsl:value-of select="mab403"/>
					</xsl:element>
				</xsl:when>
				<xsl:otherwise>
					<xsl:element name="eterms:edition">N.A.</xsl:element>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:element>
	</xsl:template>
	
	<!-- SOURCE -->
	<xsl:template name="createSource">
		<xsl:param name="title"/>
		<xsl:param name="sourceNo"/>
		<xsl:element name="source:source">
			<xsl:attribute name="type" select="$genre-ves/enum[.='series']/@uri"/>
			<xsl:element name="dc:title">
				<xsl:value-of select="$title"/>
			</xsl:element>
			<xsl:choose>
				<xsl:when test="$sourceNo=1">
					<!-- ALT -->
					<xsl:apply-templates select="mab454"/>
					<!-- IDENTIFIER -->
					<xsl:apply-templates select="mab542"/>
					<xsl:apply-templates select="mab542_a"/>	
					<!-- VOLUME -->
					<xsl:apply-templates select="mab455"/>
				</xsl:when>
				<xsl:when test="$sourceNo=2">
					<!-- TITLE -->
					
					<!-- ALT -->
					<xsl:apply-templates select="mab464"/>
					<!-- VOLUME -->
					<xsl:apply-templates select="mab465"/>
				</xsl:when>
				<xsl:when test="$sourceNo=3">
					<!-- TITLE -->
					<xsl:call-template name="createTitle">
						<xsl:with-param name="title" select="mab471"/>
					</xsl:call-template>
					<!-- ALT -->
					<xsl:apply-templates select="mab474"/>
					<!-- VOLUME -->
					<xsl:apply-templates select="mab475"/>
				</xsl:when>
				<xsl:when test="$sourceNo=4">
					<!-- TITLE -->
					
					<!-- ALT -->
					<xsl:apply-templates select="mab597"/>
					<!-- CREATOR -->
					<xsl:apply-templates select="mab591"/>
					<!-- SP,EP -->
					<xsl:apply-templates select="mab596"/>
					<!-- PLACE -->
					<xsl:apply-templates select="mab594"/>
				</xsl:when>
			</xsl:choose>
		</xsl:element>
	</xsl:template>
	<!-- SOURCE -->
	<xsl:template match="mab454">
		<xsl:call-template name="createAlternative">
			<xsl:with-param name="title" select="."/>
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="mab464">
		<xsl:call-template name="createAlternative">
			<xsl:with-param name="title" select="."/>
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="mab474">
		<xsl:call-template name="createAlternative">
			<xsl:with-param name="title" select="."/>
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="mab597">
		<xsl:call-template name="createAlternative">
			<xsl:with-param name="title" select="."/>
		</xsl:call-template>
	</xsl:template>
	<!-- SOURCE VOL -->
	<xsl:template match="mab465">
		<xsl:element name="eterms:volume">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="mab475">
		<xsl:element name="eterms:volume">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="mab455">
		<xsl:element name="eterms:volume">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="mab591">
		<xsl:element name="eterms:creator">
			<xsl:attribute name="role" select="$creator-ves/enum[.='author']/@uri"/>
			<xsl:element name="person:person">
			</xsl:element>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="mab594">
		<xsl:element name="eterms:publishing-info">
			<xsl:element name="dc:publisher">N.A.</xsl:element>
			<xsl:element name="eterms:place">
				<xsl:value-of select="."/>
			</xsl:element>
		</xsl:element>
	</xsl:template>
	<xsl:template match="mab596">
		<xsl:variable name="str" select="replace(.,' ','')"/>
		<xsl:variable name="ep" select="substring-after(.,'-')"/>
		<xsl:variable name="sp" select="substring-after(substring-before(.,'-'),',')"/>
		<xsl:element name="eterms:start-page">
			<xsl:value-of select="normalize-space($sp)"/>
		</xsl:element>
		<xsl:element name="eterms:end-page">
			<xsl:value-of select="normalize-space($ep)"/>
		</xsl:element>
	</xsl:template>
	<!-- FILE -->
	<xsl:template name="createFile">
		<xsl:if test="not(normalize-space(.)='-')">
			
			<xsl:variable name="filename" as="xs:string" select="escidoc:computeFilename(.)"/>

			<xsl:choose>
				<xsl:when test="starts-with($filename, $localPrefix)">
				
					<!--  <xsl:variable name="content-category">
						<xsl:choose>
							<xsl:when test="exists(preceding-sibling::*[name() = 'mab655_e' and starts-with(escidoc:computeFilename(.), $localPrefix)])">publisher-version</xsl:when>
							<xsl:otherwise>any-fulltext</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>-->
				
					<xsl:element name="ec:component">
						<ec:properties>
							<prop:visibility>audience</prop:visibility>
							<prop:content-category><xsl:value-of select="$contentCategory-ves/enum[. = 'any-fulltext']/@uri"/></prop:content-category>
							<prop:file-name>
								<xsl:value-of select="escidoc:substring-after-last($filename, '/')"/>
							</prop:file-name>
							<prop:mime-type>application/pdf</prop:mime-type>
						</ec:properties>
						<ec:content xlink:type="simple" xlink:title="{escidoc:substring-after-last($filename, '/')}" xlink:href="{$filename}" storage="internal-managed"/>
						<mdr:md-records xmlns:escidocMetadataRecords="${xsd.soap.common.mdrecords}">
							<mdr:md-record name="escidoc">
								<xsl:element name="file:file">
									<dc:title>
										<xsl:choose>
											<xsl:when test="escidoc:substring-after-last($filename, '/') != '' ">
												<xsl:value-of select="escidoc:substring-after-last($filename, '/')"/>
											</xsl:when>
											<xsl:otherwise>
												<xsl:value-of select="$locator-filename-substitute"/>
											</xsl:otherwise>
										</xsl:choose>
									</dc:title>
									<xsl:element name="dc:identifier">
										<xsl:attribute name="xsi:type">eterms:URI</xsl:attribute>
											<xsl:value-of select="."/>
									</xsl:element>
									<eterms:content-category><xsl:value-of select="$contentCategory-ves/enum[. = 'any-fulltext']/@uri"/></eterms:content-category>
									<dc:format xsi:type="dcterms:IMT">application/pdf</dc:format>
									<xsl:variable name="file-size" select="Util:getSize($filename)"/>
									<xsl:if test="exists($file-size)">
										<dcterms:extent>
											<xsl:value-of select="$file-size"/>
										</dcterms:extent>
									</xsl:if>
								</xsl:element>
							</mdr:md-record>
						</mdr:md-records>
					</xsl:element>
				</xsl:when>
				<xsl:otherwise>
					<xsl:element name="ec:component">
						<ec:properties>
							<prop:visibility>public</prop:visibility>
							<prop:content-category><xsl:value-of select="$contentCategory-ves/enum[. = 'publisher-version']/@uri"/></prop:content-category>
							<prop:file-name><xsl:value-of select="$filename"/></prop:file-name>
						</ec:properties>
						<ec:content xlink:type="simple" xlink:title="{escidoc:substring-after-last($filename, '/')}" xlink:href="{$filename}" storage="external-url"/>
						<mdr:md-records xmlns:escidocMetadataRecords="${xsd.soap.common.mdrecords}">
							<mdr:md-record name="escidoc">
								<xsl:element name="file:file">
									<dc:title>
										<xsl:choose>
											<xsl:when test="escidoc:substring-after-last($filename, '/') != '' and  escidoc:substring-after-last($filename, '/') != 'pdf'">
												<xsl:value-of select="escidoc:substring-after-last($filename, '/')"/>
											</xsl:when>
											<xsl:otherwise>
												<xsl:value-of select="$locator-filename-substitute"/>
											</xsl:otherwise>
										</xsl:choose>
									</dc:title>
									<xsl:element name="dc:identifier">
										<xsl:attribute name="xsi:type">eterms:URI</xsl:attribute>
										<!--  <xsl:value-of select="$filename"/>-->
										<xsl:value-of select="escidoc:substring-after-last($filename, '/')"/>
									</xsl:element>
									<xsl:element name="eterms:content-category"><xsl:value-of select="$contentCategory-ves/enum[. = 'publisher-version']/@uri"/></xsl:element>
								</xsl:element>
							</mdr:md-record>
						</mdr:md-records>
					</xsl:element>
				</xsl:otherwise>
			</xsl:choose>
			
		
		</xsl:if>
	</xsl:template>
	
	<xsl:template name="createPersonName">
		<xsl:variable name="person" select="AuthorDecoder:parseAsNode(.)/authors/author[1]"/>
		<xsl:element name="eterms:family-name">
			<xsl:value-of select="$person/familyname"/>
		</xsl:element>
		<xsl:element name="eterms:given-name">
			<xsl:value-of select="$person/givenname"/>
		</xsl:element>
	</xsl:template>
	
	<xsl:function name="escidoc:computeFilename" as="xs:string">
		<xsl:param name="filename" as="xs:string"/>
		
		<xsl:variable name="srcWithoutSpaces" select="translate($filename, ' &#xA;&#xD; ', '')" as="xs:string"/>
		
		<xsl:variable name="result">
			<xsl:choose>
				<xsl:when test="contains($srcWithoutSpaces, $localIdentifier)">
					<xsl:value-of select="$localPrefix"/>
					<xsl:value-of select="escidoc:substring-before-last(escidoc:substring-after-last($srcWithoutSpaces, '/'), '.')"/>
					<xsl:value-of select="$localSuffix"/>
				</xsl:when>
				<xsl:when test="$srcWithoutSpaces = ''">
					<xsl:value-of select="$locator-filename-substitute"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$srcWithoutSpaces"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:value-of select="$result"/>
	</xsl:function>
	
	<xsl:function name="escidoc:substring-after-last" as="xs:string">
		<xsl:param name="str" as="xs:string"/>
		<xsl:param name="delim" as="xs:string"/>
		
		<xsl:variable name="result">
			<xsl:choose>
				<xsl:when test="contains($str, $delim)">
					<xsl:value-of select="escidoc:substring-after-last(substring-after($str, $delim), $delim)"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$str"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:value-of select="$result"/>
	</xsl:function>
	
	<!-- katu_costello1991_o.html -->
	<xsl:function name="escidoc:substring-before-last" as="xs:string">
		<xsl:param name="str" as="xs:string"/>
		<xsl:param name="delim" as="xs:string"/>
		
		<xsl:variable name="result">
			<xsl:choose>
				<xsl:when test="contains($str, $delim)">
					<xsl:value-of select="substring-before($str, $delim)"/>
					<xsl:if test="contains(substring-after($str, $delim), $delim)">
						<xsl:value-of select="$delim"/>
						<xsl:value-of select="escidoc:substring-before-last(substring-after($str, $delim), $delim)"/>
					</xsl:if>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$str"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:value-of select="$result"/>
	</xsl:function>

</xsl:stylesheet>