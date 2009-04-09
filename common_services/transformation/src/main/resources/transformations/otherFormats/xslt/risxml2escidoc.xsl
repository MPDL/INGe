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
	Transformations from RIS Item to eSciDoc PubItem 
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
	
	<xsl:variable name="sourceGenre">
		<genre item="CHAP" source="book"/>
		<genre item="JOUR" source="journal"/>
		<genre item="MGZN" source="series"/>
		<genre item="NEWS" source="article"/>
	</xsl:variable>
	
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
				<xsl:when test="TY='BOOK'">
					<xsl:call-template name="createEntry">
						<xsl:with-param name="gen" select="'book'"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:when test="TY='CHAP'">
					<xsl:call-template name="createEntry">
						<xsl:with-param name="gen" select="'book-item'"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:when test="TY='CONF'">
					<xsl:call-template name="createEntry">
						<xsl:with-param name="gen" select="'proceedings'"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:when test="TY='JFULL'">
					<xsl:call-template name="createEntry">
						<xsl:with-param name="gen" select="'journal'"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:when test="TY='MGZN'">
					<xsl:call-template name="createEntry">
						<xsl:with-param name="gen" select="'article'"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:when test="TY='NEWS'">
					<xsl:call-template name="createEntry">
						<xsl:with-param name="gen" select="'article'"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:when test="TY='RPRT'">
					<xsl:call-template name="createEntry">
						<xsl:with-param name="gen" select="'report'"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:when test="TY='SER'">
					<xsl:call-template name="createEntry">
						<xsl:with-param name="gen" select="'series'"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:when test="TY='THES'">
					<xsl:call-template name="createEntry">
						<xsl:with-param name="gen" select="'thesis'"/>
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
				<xsl:when test="A1">
					<xsl:apply-templates select="A1"/>
				</xsl:when>
				<xsl:when test="AU">
					<xsl:apply-templates select="AU"/>
				</xsl:when>
			</xsl:choose>
			<!-- EDITOR -->
			<xsl:choose>
				<xsl:when test="A2">
					<xsl:apply-templates select="A2"/>
				</xsl:when>
				<xsl:when test="ED">
					<xsl:apply-templates select="ED"/>
				</xsl:when>
			</xsl:choose>
			<!-- TITLE -->
			<xsl:element name="dc:title">
				<xsl:choose>
					<xsl:when test="T1">
						<xsl:value-of select="T1"/>
					</xsl:when>
					<xsl:when test="TI">
						<xsl:value-of select="TI"/>
					</xsl:when>
					<xsl:when test="CT">
						<xsl:value-of select="CT"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:if test="BT">
							<xsl:value-of select="BT"/>
						</xsl:if>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:element>
			<!--ALTTITLE --> 
			<xsl:apply-templates select="T2"/>
			<!-- IDENTIFIER -->
			<xsl:apply-templates select="UR"/>
			<xsl:apply-templates select="L1"/>
			<xsl:apply-templates select="L2"/>
			<xsl:apply-templates select="ID"/>			
			<xsl:if test="SN and ($genre='journal' or $genre='series' or $genre='book' or $genre='thesis' or $genre='proceedings' or $genre='report')">
				<xsl:element name="dc:identifier">
					<xsl:attribute name="xsi:type">
						<xsl:choose>
							<xsl:when test="$genre='series' or $genre='journal'">ISSN</xsl:when>
							<xsl:otherwise>ISBN</xsl:otherwise>
						</xsl:choose>						
					</xsl:attribute>
					<xsl:value-of select="SN"/>
				</xsl:element>
			</xsl:if>	
			<!-- PUBLISHING-INFO -->
			<xsl:apply-templates select="VL"/>
			<!-- DATES -->
			<xsl:call-template name="createDate"/>
			<!-- SOURCE -->
			<xsl:choose>
				<xsl:when test="BT">
					<xsl:if test="not($gen='book' or $gen='proceedings' or $gen='thesis' or $gen='journal' or $gen='series' or $gen='other')">
						<xsl:call-template name="createSource">
							<xsl:with-param name="genre" select="$gen"/>
						</xsl:call-template>
					</xsl:if>
				</xsl:when>
				<xsl:when test="T3">
					<xsl:if test="(count(T1) + count(TI) + count(CT) + count(BT))>2">
						<xsl:call-template name="createSource">
							<xsl:with-param name="genre" select="$gen"/>
						</xsl:call-template>
					</xsl:if>
				</xsl:when>
			</xsl:choose>
			<!-- ABSTRACT -->
			<xsl:call-template name="createAbstract"/>
			<!-- SUBJECT -->
			<xsl:call-template name="createSubject"/>
			<!--end publication-->
		</xsl:element>
	</xsl:template>
	<!-- CREATOR -->
	<xsl:template name="createPerson">
		<xsl:element name="e:person">
			<!--<xsl:call-template name="parseCreators">
				<xsl:with-param name="string" select="."/>
			</xsl:call-template>-->
			<xsl:element name="e:complete-name">
				<xsl:value-of select="."/>
			</xsl:element>
			<xsl:if test="../AD">
				<xsl:element name="e:organization">
					<xsl:element name="e:organization-name">
						<xsl:value-of select="../AD"/>
					</xsl:element>
				</xsl:element>
			</xsl:if>
		</xsl:element>
	</xsl:template>
	<xsl:template match="A1">
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
	<xsl:template match="A2">
		<xsl:element name="pub:creator">
			<xsl:attribute name="role">contributor</xsl:attribute>
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
	<!-- SOURCE -->
	<xsl:template name="createSource">
		<xsl:param name="genre"/>
				
		<xsl:element name="pub:source">
			<!-- SOURCE GENRE -->
			<xsl:attribute name="type">
				<xsl:choose>
					<xsl:when test="not($genre='book' or $genre='proceedings' or $genre='thesis' or $genre='journal' or $genre='series' or $genre='other') and BT">journal</xsl:when>
					<xsl:when test="TY='CHAP'">book</xsl:when>
					<xsl:when test="TY='JOUR'">journal</xsl:when>
					<xsl:when test="TY='MGZN'">article</xsl:when>
					<xsl:when test="TY='NEWS'">article</xsl:when>
				</xsl:choose>
			</xsl:attribute>
			<!-- SOURCE TITLE -->
			<xsl:choose>
				<xsl:when test="not($genre='book' or $genre='proceedings' or $genre='thesis' or $genre='journal' or $genre='series' or $genre='other')">
					<xsl:element name="dc:title">
						<xsl:value-of select="BT"/>
					</xsl:element>	
					<xsl:apply-templates select="JF"/>
					<xsl:apply-templates select="JO"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:choose>
						<xsl:when test="JF">
							<xsl:element name="dc:title">
								<xsl:value-of select="JF"/>
							</xsl:element>
						</xsl:when>
						<xsl:when test="JO">
							<xsl:element name="dc:title">
								<xsl:value-of select="JO"/>
							</xsl:element>
						</xsl:when>
					</xsl:choose>
				</xsl:otherwise>
			</xsl:choose>
			
			<!-- SOURCE ALTTITLE -->
			<xsl:choose>
				<xsl:when test="JA">
					<xsl:element name="dcterms:alternative">
						<xsl:value-of select="JA"/>
					</xsl:element>
				</xsl:when>
				<xsl:when test="J1">
					<xsl:element name="dcterms:alternative">
						<xsl:value-of select="J1"/>
					</xsl:element>
				</xsl:when>
				<xsl:otherwise>
					<xsl:if test="J2">
						<xsl:element name="dcterms:alternative">
							<xsl:value-of select="J2"/>
						</xsl:element>
					</xsl:if>
				</xsl:otherwise>
			</xsl:choose>
			<!-- SOURCE CREATOR -->
			<xsl:if test="A3">
				<xsl:element name="pub:creator">
					<xsl:attribute name="role">author</xsl:attribute>
					<xsl:element name="e:person">
						<xsl:element name="e:complete-name">
							<xsl:value-of select="A3"/>
						</xsl:element>
					</xsl:element>
				</xsl:element>
			</xsl:if>
			<!-- SOURCE VOLUME -->
			<xsl:if test="ET and VL">
				<xsl:element name="e:volume">
					<xsl:value-of select="VL"/>
				</xsl:element>
			</xsl:if>
			<!-- SOURCE ISSUE -->
			<xsl:choose>
				<xsl:when test="IS">
					<xsl:element name="e:issue">
						<xsl:value-of select="IS"/>
					</xsl:element>
				</xsl:when>
				<xsl:otherwise>
					<xsl:element name="e:issue">
						<xsl:value-of select="CP"/>
					</xsl:element>
				</xsl:otherwise>
			</xsl:choose>
			<!-- SOURCE PAGES -->
			<xsl:if test="EP">
				<xsl:element name="e:start-page">
					<xsl:value-of select="SP"/>
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
			<xsl:if test="($genre='article' or $genre='paper' or $genre='issue' or $genre='other' or $genre='conference-paper' or $genre='book-item') and (PB or CY)">
				<xsl:element name="e:publishing-info">
					<xsl:element name="e:publisher">
						<xsl:value-of select="PB"/>
					</xsl:element>
					<xsl:element name="e:place">
						<xsl:value-of select="CY"/>
					</xsl:element>
					<xsl:if test="ET and ($genre='book' or $genre='thesis' or $genre='proceedings' or $genre='report')">
						<xsl:element name="e:edition">
							<xsl:value-of select="ET"/>
						</xsl:element>
					</xsl:if>
				</xsl:element>
			</xsl:if>
			<!-- SOURCE IDENTIFIER -->
			<xsl:if test="SN and not($genre='journal' or $genre='series' or $genre='book' or $genre='thesis' or $genre='proceedings' or $genre='report')">
				<xsl:element name="dc:identifier">
					<xsl:attribute name="xsi:type">
						<xsl:choose>
							<xsl:when test="$genre='series' or $genre='journal'">ISSN</xsl:when>
							<xsl:otherwise>ISBN</xsl:otherwise>
						</xsl:choose>						
					</xsl:attribute>
					<xsl:value-of select="SN"/>
				</xsl:element>
			</xsl:if>			
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="JF">
		<xsl:element name="dcterms:alternative">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="JO">
		<xsl:element name="dcterms:alternative">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>	
	<!-- DATES -->
	<xsl:template name="createDate">
		<xsl:choose>
			<xsl:when test="Y1">
				<xsl:apply-templates select="Y1"/>
			</xsl:when>
			<xsl:when test="PY">
				<xsl:apply-templates select="PY"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select="Y2"/>
			</xsl:otherwise>
		</xsl:choose>		
	</xsl:template>
	<xsl:template match="Y1">
		<xsl:element name="dcterms:issued">
			<xsl:call-template name="parseDate"/>
		</xsl:element>
		
	</xsl:template>
	<xsl:template match="PY">
		<xsl:element name="dcterms:issued">
			<xsl:call-template name="parseDate"/>
		</xsl:element>
		
	</xsl:template>
	<xsl:template match="Y2">
		<xsl:element name="dcterms:issued">
			<xsl:call-template name="parseDate"/>
		</xsl:element>
		
	</xsl:template>
	<xsl:template name="parseDate">
		<xsl:variable name="year" select="substring-before(.,'/')"/>
		<xsl:variable name="string-md" select="substring-after(.,'/')"/>
		<xsl:variable name="month" select="substring-before($string-md, '/')"/>
		<xsl:variable name="day" select="substring-after($string-md, '/')"/>
		<xsl:variable name="date">
			<xsl:if test="not($year='')">
				<xsl:value-of select="$year"/>
				<xsl:if test="not($month='')">
					<xsl:value-of select="concat('-',$month)"/>
				</xsl:if>
				<xsl:if test="not($day='')">
					<xsl:value-of select="concat('-',$day)"/>
				</xsl:if>
			</xsl:if>				
		</xsl:variable>
		<xsl:value-of select="$date"/>
	</xsl:template>
	<!-- ALTTITLE -->
	<xsl:template match="T2">
		<xsl:element name="dcterms:alternative">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>	
	<!-- ABSTRACT -->
	<xsl:template name="createAbstract">
		<xsl:choose>
			<xsl:when test="N2">				
				<xsl:element name="dcterms:abstract">
					<xsl:value-of select="N2"/>
				</xsl:element>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select="AB"/>
			</xsl:otherwise>			
		</xsl:choose>		
	</xsl:template>
	<xsl:template match="AB">
		<xsl:element name="dcterms:abstract">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<!-- SUBJECT -->
	<xsl:template name="createSubject">
		<xsl:if test="KW">
			<xsl:element name="dcterms:subject">
				<xsl:value-of select="KW[position()=1]"/>
				<xsl:for-each select="KW[position()>1]">
					<xsl:text>, </xsl:text>
					<xsl:value-of select="."/>
				</xsl:for-each>
			</xsl:element>
		</xsl:if>
	</xsl:template>
	<!-- PUBLISHINGINFO -->
	<xsl:template name="createEdition">
		<xsl:element name="pub:publishing-info">
			<xsl:element name="e:edition">
				<xsl:value-of select="ET"/>
			</xsl:element>
		</xsl:element>
	</xsl:template>
	<!-- IDENTIFIER -->
	<xsl:template match="UR">		
		<xsl:element name="dc:identifier">
			<xsl:attribute name="xsi:type">eidt:URI</xsl:attribute>
			<xsl:value-of select="."/>
		</xsl:element>		
	</xsl:template>
	<xsl:template match="L1">
		<xsl:element name="dc:identifier">
			<xsl:attribute name="xsi:type">eidt:URI</xsl:attribute>
			<xsl:value-of select="."/>
		</xsl:element>		
	</xsl:template>
	<xsl:template match="L2">
		<xsl:element name="dc:identifier">
			<xsl:attribute name="xsi:type">eidt:URI</xsl:attribute>
			<xsl:value-of select="."/>
		</xsl:element>		
	</xsl:template>
	<xsl:template match="ID">
		<xsl:element name="dc:identifier">
			<xsl:attribute name="xsi:type">eidt:OTHER</xsl:attribute>
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>