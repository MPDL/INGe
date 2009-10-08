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
			<xsl:element name="ec:components">
				<xsl:for-each select="mab655_e">
					<xsl:call-template name="createFile"/>
				</xsl:for-each>
			</xsl:element>
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
			<xsl:apply-templates select="mab100_b"/>
			<xsl:apply-templates select="mab100_c"/>
			<xsl:apply-templates select="mab108_b"/>
			<xsl:apply-templates select="mab112_b"/>
			<xsl:apply-templates select="mab112_f"/>
			<!-- TITLE -->			
			<xsl:choose>
				<xsl:when test="mab331 and mab335">
					<xsl:call-template name="createTitle">
						<xsl:with-param name="title" select="mab331"/>
					</xsl:call-template>
					<xsl:variable name="add335" select="concat(' : ',mab335)"/>
					<xsl:call-template name="createAlternative">
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
				<xsl:element name="pub:total-number-of-pages">				
					<xsl:if test="mab433">
						<xsl:value-of select="mab433"/>
					</xsl:if>					
				</xsl:element>
			</xsl:if>
			<!-- DEGREE -->
			<xsl:variable name="degree" select="substring-after(substring-after(mab519,','),',')"/>
			<xsl:if test="not($degree='')">
				<xsl:element name="pub:degree">
					<xsl:value-of>
					<xsl:choose>
						<xsl:when test="contains($degree,'Dipl') or contains($degree,'Diplom')">diploma</xsl:when>
						<xsl:when test="contains($degree,'Master')">master</xsl:when>
						<xsl:when test="contains($degree,'MA') or contains($degree,'M.A.') or contains($degree,'Magister')">magister</xsl:when>
						<xsl:when test="contains($degree,'Diss') or contains($degree,'PhD')">phd</xsl:when>
						<xsl:when test="contains($degree,'Habil.-Schr.')">habilitation</xsl:when>
						<xsl:when test="contains($degree,'BA') or contains($degree,'B.A.') or contains($degree,'Bachelor')">bachelor</xsl:when>
					</xsl:choose>
					</xsl:value-of>
				</xsl:element>
			</xsl:if>
			<!-- ABSTRACT -->
			<xsl:apply-templates select="mab750"/>
			<xsl:apply-templates select="mab526"/>
			<!-- SUBJECT -->
			
			<!--end publication-->
		</xsl:element>
	</xsl:template>
	<!-- CREATOR -->
	<xsl:template match="mab100">
		<xsl:element name="pub:creator">
			<xsl:element name="e:person">
				<xsl:attribute name="role">author</xsl:attribute>
				<xsl:element name="e:complete-name">
					<xsl:value-of select="."/>
				</xsl:element>
				<xsl:if test="../mab101">
					<xsl:element name="e:alternative-name">
						<xsl:value-of select="../mab101"/>
					</xsl:element>
				</xsl:if>
				<!-- <xsl:if test="../mab359">
					<xsl:element name="e:alternative-name">
						<xsl:value-of select="../mab101"/>
					</xsl:element>
				</xsl:if>-->	
				<xsl:element name="e:organization">
					<xsl:choose>
						<xsl:when test="../mab103">
							<xsl:element name="e:organization-name">
								<xsl:value-of select="../mab103"/>
							</xsl:element>
						</xsl:when>
						<xsl:otherwise>
							<e:organization-name>External Organizations</e:organization-name>
							<e:identifier>${escidoc.pubman.external.organisation.id}</e:identifier>
						</xsl:otherwise>
					</xsl:choose>					
				</xsl:element>
			</xsl:element>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="mab104_a">
		<xsl:element name="pub:creator">
			<xsl:element name="e:person">
				<xsl:attribute name="role">author</xsl:attribute>
				<xsl:element name="e:complete-name">
					<xsl:value-of select="."/>
				</xsl:element>
				<xsl:if test="../mab105">
					<xsl:element name="e:alternative-name">
						<xsl:value-of select="../mab105"/>
					</xsl:element>			
				</xsl:if>	
				<!-- <xsl:if test="../mab359">
					<xsl:element name="e:alternative-name">
						<xsl:value-of select="../mab101"/>
					</xsl:element>
				</xsl:if>-->	
			</xsl:element>
		</xsl:element>
	</xsl:template>
	<xsl:template match="mab100_b">
		<xsl:call-template name="createPersonCreator">
			<xsl:with-param name="role">editor</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="mab104_b">
		<xsl:call-template name="createPersonCreator">
			<xsl:with-param name="role">editor</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="mab100_c">
		<xsl:call-template name="createPersonCreator">
			<xsl:with-param name="role">editor</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="mab108_b">
		<xsl:call-template name="createPersonCreator">
			<xsl:with-param name="role">editor</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="mab112_b">
		<xsl:call-template name="createPersonCreator">
			<xsl:with-param name="role">editor</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="mab112_f">
		<xsl:call-template name="createPersonCreator">
			<xsl:with-param name="role">honoree</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	<xsl:template name="createPersonCreator">
		<xsl:param name="role"/>
		<xsl:element name="pub:creator">
			<xsl:element name="e:person">
				<xsl:attribute name="role" select="$role"/>
				<xsl:element name="e:complete-name">
					<xsl:value-of select="."/>
				</xsl:element>
				<!-- <xsl:if test="../mab359">
					<xsl:element name="e:alternative-name">
						<xsl:value-of select="../mab101"/>
					</xsl:element>
				</xsl:if>-->	
			</xsl:element>
		</xsl:element>
	</xsl:template>
	<xsl:template match="mab200">
		<xsl:call-template name="createPersonCreator">
			<xsl:with-param name="role">editor</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="mab200_b">
		<xsl:call-template name="createPersonCreator">
			<xsl:with-param name="role">editor</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="mab204_a">
		<xsl:call-template name="createPersonCreator">
			<xsl:with-param name="role">editor</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="mab204_b">
		<xsl:call-template name="createPersonCreator">
			<xsl:with-param name="role">editor</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="mab208_a">
		<xsl:call-template name="createPersonCreator">
			<xsl:with-param name="role">editor</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	<xsl:template name="createOrganizationCreator">
		<xsl:param name="role"/>
		<xsl:element name="pub:creator">
			<xsl:element name="e:organization">
				<xsl:attribute name="role" select="$role"/>
				<xsl:element name="e:organization-name">
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
			<xsl:with-param name="idtype">eidt:OTHER</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="mab025_z">
		<xsl:call-template name="createID">
			<xsl:with-param name="idtype">eidt:ZDB</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="mab088">
		<xsl:call-template name="createID">
			<xsl:with-param name="idtype">eidt:URI</xsl:with-param>
		</xsl:call-template>
	</xsl:template>	
	<xsl:template match="mab655">
		<xsl:call-template name="createID">
			<xsl:with-param name="idtype">eidt:URI</xsl:with-param>
		</xsl:call-template>
	</xsl:template>	
	<xsl:template match="mab655_e">
		<xsl:call-template name="createID">
			<xsl:with-param name="idtype">eidt:URI</xsl:with-param>
		</xsl:call-template>
	</xsl:template>	
	<xsl:template match="mab540">
		<xsl:call-template name="createID">
			<xsl:with-param name="idtype">eidt:ISBN</xsl:with-param>
		</xsl:call-template>
	</xsl:template>	
	<xsl:template match="mab540_a">
		<xsl:call-template name="createID">
			<xsl:with-param name="idtype">eidt:ISBN</xsl:with-param>
		</xsl:call-template>
	</xsl:template>	
	<xsl:template match="mab540_b">
		<xsl:call-template name="createID">
			<xsl:with-param name="idtype">eidt:ISBN</xsl:with-param>
		</xsl:call-template>
	</xsl:template>	
	<xsl:template match="mab542">
		<xsl:call-template name="createID">
			<xsl:with-param name="idtype">eidt:ISSN</xsl:with-param>
		</xsl:call-template>
	</xsl:template>	
	<xsl:template match="mab542_a">
		<xsl:call-template name="createID">
			<xsl:with-param name="idtype">eidt:ISSN</xsl:with-param>
		</xsl:call-template>
	</xsl:template>	
	<xsl:template name="createID">
		<xsl:param name="idtype"/>
		<xsl:element name="dc:identifier">
			<xsl:attribute name="xsi:type" select="$idtype"/>
			<xsl:value-of select="."/>
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
		<!-- <xsl:element name="dc:language">
			
		</xsl:element>-->
	</xsl:template>
	<!-- SUBJECT -->
	<xsl:template name="createSubject">
		<xsl:element name="dcterms:subject">
			<xsl:if test="mab711_b">
				<xsl:value-of select="concat(mab711_b,'; ')"/>
			</xsl:if>
			<xsl:if test="mab711_t">
				<xsl:value-of select="concat(mab711_t,'; ')"/>
			</xsl:if>
			<xsl:if test="mab740_s">
				<xsl:value-of select="concat(mab740_s,'; ')"/>
			</xsl:if>
		</xsl:element>
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
	<!-- PUBLISHINGINFO -->
	<xsl:template match="mab519">		
		<xsl:analyze-string select="." regex="\d\d\d\d">
				<xsl:matching-substring>
					<xsl:element name="dcterms:dateAccepted">
						<xsl:value-of select="."/>
					</xsl:element>
				</xsl:matching-substring>
			</xsl:analyze-string>
	</xsl:template>
	<xsl:template name="createPublInfo">
		<xsl:element name="pub:publishing-info">
		<xsl:variable name="publisher" select="substring-before(substring-after(mab519,','),',')"/>
		<xsl:variable name="place" select="substring-before(mab519,',')"/>
		<xsl:choose>
			<xsl:when test="not(mab412)">
				<xsl:element name="dc:publisher">
					<xsl:value-of select="$publisher"/>
				</xsl:element>
			</xsl:when>
			<xsl:otherwise>
				<xsl:element name="dc:publisher">N.A.</xsl:element>
			</xsl:otherwise>
		</xsl:choose>	
			<xsl:if test="not($place='')">
				<xsl:element name="e:place">
					<xsl:value-of select="$place"/>
				</xsl:element>		
			</xsl:if>			
		</xsl:element>
	</xsl:template>
	<!-- SOURCE -->
	<xsl:template name="createSource">
		<xsl:param name="title"/>
		<xsl:param name="sourceNo"/>
		<xsl:element name="pub:source">
			<xsl:attribute name="type">series</xsl:attribute>
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
		<xsl:element name="e:volume">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="mab475">
		<xsl:element name="e:volume">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="mab455">
		<xsl:element name="e:volume">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="mab591">
		<xsl:element name="e:creator">
			<xsl:attribute name="role">author</xsl:attribute>
			<xsl:element name="e:person">
				<xsl:element name="e:complete-name">
					<xsl:value-of select="."/>
				</xsl:element>
			</xsl:element>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="mab594">
		<xsl:element name="e:publishing-info">
			<xsl:element name="dc:publisher">N.A.</xsl:element>
			<xsl:element name="e:place">
				<xsl:value-of select="."/>
			</xsl:element>
		</xsl:element>
	</xsl:template>
	<xsl:template match="mab596">
	<xsl:variable name="str" select="replace(.,' ','')"/>
	<xsl:variable name="ep" select="substring-after(.,'-')"/>
	<xsl:variable name="sp" select="substring-after(substring-before(.,'-'),',')"/>
		<xsl:element name="e:start-page">
			<xsl:value-of select="normalize-space($sp)"/>			
		</xsl:element>
		<xsl:element name="e:end-page">
			<xsl:value-of select="normalize-space($ep)"/>
		</xsl:element>
	</xsl:template>
	<!-- FILE -->
	<xsl:template name="createFile">
		<xsl:if test="not(normalize-space(.)='-')">
		<xsl:element name="ec:component">
		<xsl:element name="file:file">
			<xsl:variable name="fileurl" select="."/>
			<xsl:choose>
				<xsl:when test="contains($fileurl,'serveg5.eva.mpg.de')">
					<xsl:call-template name="createTitle">
						<xsl:with-param name="title" select="."/>
					</xsl:call-template>			
				</xsl:when>
				<xsl:otherwise>
					<xsl:element name="dc:title"/>
					
				</xsl:otherwise>
			</xsl:choose>
			<xsl:element name="dc:identifier">	
				<xsl:attribute name="xsi:type">eidt:URI</xsl:attribute>
				<xsl:value-of select="."/>
			</xsl:element>	
			<xsl:element name="file:content-category">any-fulltext</xsl:element>	
			<xsl:element name="dc:format">application/pdf</xsl:element>	
		</xsl:element>
		</xsl:element>
		</xsl:if>
	</xsl:template>
	
</xsl:stylesheet>