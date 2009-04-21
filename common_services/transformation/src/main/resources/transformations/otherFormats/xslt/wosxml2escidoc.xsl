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
	$Author: kurt $ (last changed)
	$Revision: 747 $ 
	$LastChangedDate: 2008-07-21 19:15:26 +0200 (Tue, 31 Mar 2009) $
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
   xmlns:pub="http://escidoc.mpg.de/metadataprofile/schema/0.1/publication"
   xmlns:escidoc="urn:escidoc:functions">
 <!--  xmlns:ei="${xsd.soap.item.item}"
   xmlns:mdr="${xsd.soap.common.mdrecords}"
   xmlns:mdp="${xsd.metadata.escidocprofile}"
   xmlns:e="http://escidoc.mpg.de/metadataprofile/schema/0.1/types"
   xmlns:ec="${xsd.soap.item.components}"
   xmlns:prop="${xsd.soap.common.prop}"
> -->

	<xsl:output method="xml" encoding="UTF-8" indent="yes"/>
	
	<xsl:param name="user" select="'dummy-user'"/>
	<xsl:param name="context" select="'escidoc:31013'"/>
	
	<!--
		DC XML  Header
	-->
	
		
	<!-- VARIABLEN -->
	
	
	<xsl:variable name="genre"/>		
	
	<xsl:template match="/">
		<item-list>
			<xsl:apply-templates select="item-list/item"/>
		</item-list>
	</xsl:template>

	<xsl:template match="item-list/item">
		<xsl:element name="ei:item">
			<xsl:element name="ei:properties">
				<xsl:element name="srel:context">
					<xsl:attribute name="xlink:href" select="concat('/ir/context/', $context)"/>
				</xsl:element>
				<srel:content-model xlink:href="/cmm/content-model/escidoc:persistent4"/>
				<xsl:element name="prop:content-model-specific"></xsl:element>
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
				<xsl:when test="PT='C'">
					<xsl:call-template name="createEntry">
						<xsl:with-param name="gen" select="'conference-paper'"/>
					</xsl:call-template>
				</xsl:when>				
				<xsl:when test="PT='J'">
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
			<xsl:choose>
				<xsl:when test="AF">
					<xsl:apply-templates select="AF"/>
				</xsl:when>
				<xsl:when test="AU">
					<xsl:apply-templates select="AU"/>
				</xsl:when>
			</xsl:choose>
			
			<!-- TITLE -->
			<xsl:element name="dc:title">				
						<xsl:value-of select="TI"/>					
			</xsl:element>
			<!-- LANGUAGE -->
			<xsl:apply-templates select="LA"/>
			
			<!-- IDENTIFIER -->
			<xsl:apply-templates select="UT"/>
			<xsl:apply-templates select="DI"/>				
			
			<!-- DATES -->
			<xsl:call-template name="createDate"/>
			<!-- SOURCE -->
			<xsl:if test="SO">
				<xsl:call-template name="createSource"/>
			</xsl:if>
			<!-- EVENT -->
			<xsl:if test="CT">
				<xsl:call-template name="createEvent"/>
			</xsl:if>
			<!-- PAGES -->
			<xsl:apply-templates select="PG"/>
			<!-- ABSTRACT -->
			<xsl:apply-templates select="AB"/>
			<!-- SUBJECT -->
			<xsl:call-template name="createSubject"/>
			<!--end publication-->
		</xsl:element>
	</xsl:template>
	
	<!-- EVENT -->
	<xsl:template name="createEvent">
		<xsl:element name="pub:event">
			<xsl:element name="dc:title">
				<xsl:value-of select="CT"/>
			</xsl:element>
			<xsl:variable name="monthStr" select="substring-before(CY,' ')"/>
			<xsl:variable name="month">
				<xsl:choose>
					<xsl:when test="$monthStr='JAN'">01</xsl:when>
					<xsl:when test="$monthStr='FEB'">02</xsl:when>
					<xsl:when test="$monthStr='MAR'">03</xsl:when>
					<xsl:when test="$monthStr='APR'">04</xsl:when>
					<xsl:when test="$monthStr='MAI'">05</xsl:when>
					<xsl:when test="$monthStr='JUN'">06</xsl:when>
					<xsl:when test="$monthStr='JUL'">07</xsl:when>
					<xsl:when test="$monthStr='AUG'">08</xsl:when>
					<xsl:when test="$monthStr='SEP'">09</xsl:when>
					<xsl:when test="$monthStr='OCT'">10</xsl:when>
					<xsl:when test="$monthStr='NOV'">11</xsl:when>
					<xsl:when test="$monthStr='DEC'">12</xsl:when>
				</xsl:choose>
			</xsl:variable>
			<xsl:variable name="day1" select="substring-before(substring-after(CY,' '),'-')"/>
			<xsl:variable name="day2" select="substring-after(substring-before(CY,', '),'-')"/>
			<xsl:variable name="year" select="substring-after(CY,', ')"/>
			<xsl:element name="e:start-date">
				<xsl:value-of select="concat($year,'-',$month,'-',$day1)"/>
			</xsl:element>
			<xsl:element name="e:end-date">
				<xsl:value-of select="concat($year,'-',$month,'-',$day2)"/>
			</xsl:element>
			<xsl:element name="e:place">
				<xsl:value-of select="CL"/>
			</xsl:element>
		</xsl:element>
	</xsl:template>
	<!-- CREATOR -->
	<xsl:template name="createPerson">
		<xsl:element name="e:person">
			
			<xsl:element name="e:complete-name">
				<xsl:value-of select="."/>
			</xsl:element>
			<xsl:if test="../CA">
				<xsl:element name="e:organization">
					<xsl:element name="e:organization-name">
						<xsl:value-of select="../CA"/>
					</xsl:element>
				</xsl:element>
			</xsl:if>
		</xsl:element>
	</xsl:template>
	<xsl:template match="AF">
		<xsl:element name="pub:creator">
			<xsl:attribute name="role">author</xsl:attribute>
			<xsl:call-template name="createPerson"/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="AU">
		<xsl:element name="pub:creator">
			<xsl:attribute name="role">author</xsl:attribute>
			<xsl:call-template name="createPerson"/>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="ED">
		<xsl:element name="pub:creator">
			<xsl:attribute name="role">contributor</xsl:attribute>
			<xsl:call-template name="createPerson"/>
		</xsl:element>
	</xsl:template>
	<xsl:template name="parseCreators">
		<xsl:param name="string"/>
		<xsl:choose>
			<xsl:when test="substring-before($string,';')=''">
				<xsl:element name="e:complete-name">
					<xsl:value-of select="$string"/>
				</xsl:element>
			</xsl:when>
			<xsl:otherwise>
				<xsl:element name="e:complete-name">
					<xsl:value-of select="substring-before($string,';')"/>
				</xsl:element>
				<xsl:call-template name="parseCreators">
					<xsl:with-param name="string" select="substring-after($string,';')"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<!-- LANGUAGE -->
	<xsl:template match="LA">
		<xsl:element name="dc:language">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<!-- SOURCE -->
	<xsl:template name="createSource">
		<xsl:param name="genre"/>
				
		<xsl:element name="pub:source">
			<!-- SOURCE GENRE -->
			<xsl:attribute name="type">
				<xsl:choose>
					<xsl:when test="SO and not(SN) and not(BN) and PT='C'">proceedings</xsl:when>
					<xsl:when test="BN">proceedings</xsl:when>
					<xsl:when test="SN">
						<xsl:choose>
							<xsl:when test="BN">proceedings</xsl:when>
							<xsl:otherwise>journal</xsl:otherwise>
						</xsl:choose>
					</xsl:when>					
				</xsl:choose>
			</xsl:attribute>
			<!-- SOURCE TITLE -->
			<xsl:element name="dc:title">
				<xsl:value-of select="SO"/>
			</xsl:element>					
			
			<!-- SOURCE ALTTITLE -->
			<xsl:if test="JI and not(SE)">
				<xsl:element name="dcterms:alternative">
					<xsl:value-of select="JI"/>
				</xsl:element>
			</xsl:if>
			<xsl:if test="J9 and not(SE)">
				<xsl:element name="dcterms:alternative">
					<xsl:value-of select="J9"/>
				</xsl:element>
			</xsl:if>			
				
			<!-- SOURCE CREATOR -->
			<xsl:if test="ED">
				<xsl:element name="pub:creator">
					<xsl:attribute name="role">editor</xsl:attribute>
					<xsl:element name="e:person">
						<xsl:element name="e:complete-name">
							<xsl:value-of select="ED"/>
						</xsl:element>
					</xsl:element>
				</xsl:element>
			</xsl:if>
			<!-- SOURCE VOLUME -->
			<xsl:if test="VL and not(SE)">
				<xsl:element name="e:volume">
					<xsl:value-of select="VL"/>
				</xsl:element>
			</xsl:if>
			<!-- SOURCE ISSUE -->
			<xsl:if test="IS and not(SE)">
				<xsl:element name="e:issue">
					<xsl:value-of select="IS"/>
				</xsl:element>				
			</xsl:if>
			<!-- SOURCE PAGES -->
			<xsl:if test="EP">
				<xsl:element name="e:start-page">
					<xsl:value-of select="BP"/>
				</xsl:element>
				<xsl:element name="e:end-page">
					<xsl:value-of select="EP"/>
				</xsl:element>
			</xsl:if>
			<!-- SOURCE TOTAL NUMBER OF PAGES -->
			<xsl:if test="not(EP) and SP">
				<xsl:element name="e:total-number-of-pages">
					<xsl:value-of select="SP"/>
				</xsl:element>
			</xsl:if>
			<!-- SOURCE PUBLISHINGINFO -->
			<xsl:if test="PU">
				<xsl:element name="e:publishing-info">
					<xsl:element name="e:publisher">
						<xsl:value-of select="PU"/>
					</xsl:element>
					<xsl:element name="e:place">
						<xsl:value-of select="PA"/>
					</xsl:element>
					
				</xsl:element>
			</xsl:if>
			<!-- SOURCE IDENTIFIER -->
			<xsl:if test="SN">
				<xsl:element name="dc:identifier">
					<xsl:attribute name="xsi:type">eidt:ISSN</xsl:attribute>
					<xsl:value-of select="SN"/>
				</xsl:element>
			</xsl:if>
			<xsl:if test="BN">
				<xsl:element name="dc:identifier">
					<xsl:attribute name="xsi:type">eidt:ISBN</xsl:attribute>
					<xsl:value-of select="BN"/>
				</xsl:element>
			</xsl:if>
		</xsl:element>
		<!-- SECOND SOURCE -->
		<xsl:if test="BS">
			<xsl:element name="pub:source">
				<xsl:attribute name="type">series</xsl:attribute>
				<xsl:element name="dc:title">
					<xsl:value-of select="SE"/>
				</xsl:element>
				<xsl:apply-templates select="BS"/>
				<xsl:apply-templates select="J9"/>
				<xsl:apply-templates select="JI"/>
				<xsl:if test="VL">
					<xsl:element name="e:volume">
						<xsl:value-of select="VL"/>
					</xsl:element>
				</xsl:if>
				<xsl:if test="IS">
					<xsl:element name="e:issue">
						<xsl:value-of select="IS"/>
					</xsl:element>
				</xsl:if>
				<xsl:if test="SN and BN">
					<xsl:element name="dc:identifier">
						<xsl:attribute name="xsi:type">eidt:ISSN</xsl:attribute>
						<xsl:value-of select="SN"/>
					</xsl:element>
				</xsl:if>
			</xsl:element>
		</xsl:if>
	</xsl:template>
	<!-- SOURCE ALTTITLE -->
	<xsl:template match="J9">
		<xsl:element name="dcterms:alternative">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="JI">
		<xsl:element name="dcterms:alternative">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="BS">
		<xsl:element name="dcterms:alternative">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>	
	<!-- PAGES -->
	<xsl:template match="PG">
		<xsl:element name="pub:total-number-of-pages">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<!-- DATES -->
	<xsl:template name="createDate">
		<xsl:if test="PD">
			<xsl:variable name="monthStr" select="PD"/>
			<xsl:variable name="month">
				<xsl:choose>
					<xsl:when test="$monthStr='JAN'">01</xsl:when>
					<xsl:when test="$monthStr='FEB'">02</xsl:when>
					<xsl:when test="$monthStr='MAR'">03</xsl:when>
					<xsl:when test="$monthStr='APR'">04</xsl:when>
					<xsl:when test="$monthStr='MAI'">05</xsl:when>
					<xsl:when test="$monthStr='JUN'">06</xsl:when>
					<xsl:when test="$monthStr='JUL'">07</xsl:when>
					<xsl:when test="$monthStr='AUG'">08</xsl:when>
					<xsl:when test="$monthStr='SEP'">09</xsl:when>
					<xsl:when test="$monthStr='OCT'">10</xsl:when>
					<xsl:when test="$monthStr='NOV'">11</xsl:when>
					<xsl:when test="$monthStr='DEC'">12</xsl:when>
				</xsl:choose>
			</xsl:variable>
			<xsl:element name="dcterms:created">	
				<xsl:value-of select="concat(PY,'-',$month)"/>
			</xsl:element>
		</xsl:if>
	</xsl:template>
	
	
	
	<!-- ABSTRACT -->
	<xsl:template match="AB">
		<xsl:element name="dcterms:abstract">
			<xsl:value-of select="."/>
		</xsl:element>	
	</xsl:template>
	
	<!-- SUBJECT -->
	<xsl:template name="createSubject">
		<xsl:if test="ID or SC or DE">
			<xsl:element name="dcterms:subject">
				<xsl:if test="ID">
					<xsl:value-of select="concat(ID,'; ')"/>			
				</xsl:if>
				<xsl:if test="SC">
					<xsl:value-of select="concat(SC,'; ')"/>
				</xsl:if>
				<xsl:if test="DE">
					<xsl:value-of select="concat(DE,'; ')"/>
				</xsl:if>
			</xsl:element>
		</xsl:if>
	</xsl:template>
	
	<!-- IDENTIFIER -->
	<xsl:template match="UT">		
		<xsl:element name="dc:identifier">
			<xsl:attribute name="xsi:type">eidt:ISI</xsl:attribute>
			<xsl:value-of select="."/>
		</xsl:element>		
	</xsl:template>
	<xsl:template match="DI">
		<xsl:element name="dc:identifier">
			<xsl:attribute name="xsi:type">eidt:DOI</xsl:attribute>
			<xsl:value-of select="."/>
		</xsl:element>		
	</xsl:template>
	
</xsl:stylesheet>