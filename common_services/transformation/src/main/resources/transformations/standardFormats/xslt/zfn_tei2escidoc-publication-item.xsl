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


 Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft
 für wissenschaftlich-technische Information mbH and Max-Planck-
 Gesellschaft zur Förderung der Wissenschaft e.V.
 All rights reserved. Use is subject to license terms.
-->
<!-- 
	Transformations from TEI 2 to eSciDoc PubItem 
	See mapping: http://colab.mpdl.mpg.de/mediawiki/Zeitschrift_Naturforschung#Mapping_ZfNTei2PubItem
	Author: Friederike Kleinfercher (initial creation) 
-->
<xsl:stylesheet version="2.0"
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
   xmlns:xs="http://www.w3.org/2001/XMLSchema"
   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
   xmlns:dc="http://purl.org/dc/elements/1.1/"
   xmlns:dcterms="http://purl.org/dc/terms/"
   xmlns:file="${xsd.metadata.file}"
   xmlns:pub="${xsd.metadata.publication}"
   xmlns:person="${xsd.metadata.person}"
   xmlns:source="${xsd.metadata.source}"
   xmlns:organization="${xsd.metadata.organization}"		
   xmlns:eterms="${xsd.metadata.terms}"   
   xmlns:ei="${xsd.soap.item.item}"   
   xmlns:prop="${xsd.core.properties}"
   xmlns:t="http://www.tei-c.org/ns/1.0" 
   xmlns:srel="${xsd.soap.common.srel}"
   xmlns:mdr="${xsd.soap.common.mdrecords}"
   xmlns:escidoc="urn:escidoc:functions"
   xmlns:Util="java:de.mpg.escidoc.services.transformation.Util"
   xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#">

	<xsl:import href="../../vocabulary-mappings.xsl"/>

	<xsl:output method="xml" encoding="UTF-8" indent="yes"/>

	<xsl:param name="JournalConeID" select="'954927655916'"/>
	<xsl:param name="License" select="'http://creativecommons.org/licenses/by/3.0/'"/>
	<xsl:param name="content-model" select="'escidoc:persistent4'"/>
	<xsl:param name="external_organization_id" select="'escidoc:persistent22'"/>
	<xsl:param name="zfnId" select="''"/>
	
	<xsl:param name="refType" />
	
	<xsl:variable name="vm" select="document('../../ves-mapping.xml')/mappings"/>
	
	<xsl:variable name="genreMap">
			<m key="article">article</m>
			<m key="inproceeding">proceedings</m>
			<m key="inbook">book-item</m>
			<m key="book">book</m>
			<m key="thesis">thesis</m>
			<m key="report">report</m>
	</xsl:variable>
	
	<xsl:variable name="identMap">
			<m key="DOI">eterms:DOI</m>
			<m key="ISSN">eterms:ISSN</m>
			<m key="pISSN">eterms:ISSN</m>
			<m key="eISSN">eterms:ISSN</m>
			<m key="PMID">eterms:PMID</m>
			<m key="PII">eterms:PII</m>
	</xsl:variable>
	
      	
	<xsl:variable name="dateMap">
			<m key="submission">dcterms:dateSubmitted</m>
			<m key="publication">dcterms:issued</m>
			<m key="Published">dcterms:issued</m>
	</xsl:variable>

	<!-- ITEM -->
	<xsl:template match="/">
		<xsl:element name="ei:item">
			<xsl:element name="ei:properties">
				<srel:content-model objid="{$content-model}" />
				<xsl:element name="prop:content-model-specific"/>
			</xsl:element>
			<xsl:element name="mdr:md-records">
				<mdr:md-record name="escidoc">
					<xsl:call-template name="createMetadata"/>
				</mdr:md-record>
			</xsl:element>
		</xsl:element>
	</xsl:template>
		
	<!-- METADATA -->
	<xsl:template name="createMetadata">	
		<xsl:variable name="refType" select="/t:TEI/t:teiHeader/t:fileDesc/t:sourceDesc/t:biblStruct/@type"/>		
		<xsl:variable name="curGenre" select="$genreMap/m[@key=$refType]" />
		<xsl:variable name="curGenreURI" select="$genre-ves/enum[.=$curGenre]/@uri" />
		<!-- Takes genre article as default when no info is given -->
		<xsl:call-template name="createEntry">
			<xsl:with-param name="gen" select="if (exists($curGenre)) then $curGenreURI else $genre-ves/enum[.='article']/@uri"/>
		</xsl:call-template>
	</xsl:template>
	
	<!-- Create eSciDoc Entry -->		
	
	<xsl:template name="createEntry">
		<xsl:param name="gen"/>		
		<xsl:element name="pub:publication">
		
			<xsl:attribute name="type">
				<xsl:value-of select="$gen"/>
			</xsl:attribute>
			
			<xsl:variable name="fDesc" select="/t:TEI/t:teiHeader/t:fileDesc"/>
			<xsl:variable name="sDesc" select="$fDesc/t:sourceDesc"/>
			
			<!-- CREATORS -->
			<xsl:call-template name="createCreators"/>
						
			<!-- TITLE -->
			<xsl:element name="dc:title">
				<xsl:value-of select="
					if (exists($fDesc/t:titleStmt/t:title[@type='main'][1]))
					then normalize-space($fDesc/t:titleStmt/t:title[@type='main'][1])
					else if (exists($sDesc/t:biblStruct/t:analytic/t:title[@type='main'][1]))
					then normalize-space($sDesc/t:biblStruct/t:analytic/t:title[@type='main'][1])
					else error(QName('http://www.escidoc.de', 'err:NoTitleDefined' ), 'No title is defined for the item')
				"/>
			</xsl:element>
			
			<xsl:variable name="monogr" select="$sDesc/t:biblStruct/t:monogr"/>
			<xsl:variable name="imprint" select="$monogr/t:imprint"/>	
			<xsl:variable name="pDesc" select="/t:TEI/t:teiHeader/t:profileDesc"/>	
			
			<!-- DATES -->			
			<xsl:variable name="iDates" select="$imprint/t:date"/>			
			<xsl:for-each select="$dateMap/m/@key">
				<xsl:variable name="dateType" select="."/>
				<xsl:variable name="date" select="$iDates[upper-case(@type)=upper-case($dateType)]"/>
				<xsl:if test="exists($date)">				
					<xsl:element name="{$dateMap/m[@key=$dateType]}">
						<xsl:attribute name="xsi:type">dcterms:W3CDTF</xsl:attribute>
						<xsl:value-of select="$date/@when"/>
					</xsl:element>
				</xsl:if>
			</xsl:for-each>			
			<!-- END OF DATE -->
			
			<!-- SOURCE -->
			<xsl:call-template name="createSource">
				<xsl:with-param name="monogr" select="$monogr"/>
				<xsl:with-param name="imprint" select="$monogr/t:imprint"/>
			</xsl:call-template>
						
			<!-- ABSTRACT -->
			<xsl:if test="exists(/t:TEI/t:text/t:front/t:div[@type='abstract'])">
				<xsl:element name="dcterms:abstract">
					<xsl:value-of select="/t:TEI/t:text/t:front/t:div/t:p"/>
				</xsl:element>
			</xsl:if>
			
			<!-- SUBJECT -->
			<xsl:variable name="subjCount" select="count($pDesc/t:textClass/t:keywords/t:list/t:item/t:term)"/>						
				<xsl:element name="dcterms:subject">
					<xsl:for-each select="$pDesc/t:textClass/t:keywords/t:list/t:item/t:term">
						<xsl:value-of select="
							concat(.,
								if ($subjCount>1 and (position()!=last())) then ', ' else ''
							)	
						"/>
				</xsl:for-each>
			</xsl:element>
	
		</xsl:element>		
	</xsl:template>
	
	<!-- SOURCE -->
	<xsl:template name="createSource">
		<xsl:param name="monogr"/>
		<xsl:param name="imprint"/>
		<xsl:variable name="cone-journal">
			<xsl:copy-of select="Util:queryCone('journals', $JournalConeID)"/>
		</xsl:variable>
		
		<xsl:element name="source:source">

			<!-- SOURCE GENRE -->
			<xsl:attribute name="type">
				<xsl:value-of select="$genre-ves/enum[.='journal']/@uri"/>
			</xsl:attribute>
			
			<!-- SOURCE TITLE -->
			<xsl:element name="dc:title">
				<xsl:value-of select="$cone-journal/cone/rdf:RDF/rdf:Description/dc:title"/>				
			</xsl:element>

			<!-- SOURCE ALTTITLE -->
			<xsl:element name="dcterms:alternative">
				<xsl:attribute name="xsi:type">eterms:ABBREVIATION</xsl:attribute>
				<xsl:value-of select="$cone-journal/cone/rdf:RDF/rdf:Description/dcterms:alternative"/>
			</xsl:element>
						
			<!-- SOURCE VOLUME -->
			<xsl:if test="exists($imprint/t:biblScope[@type='vol'])">
				<xsl:element name="eterms:volume">
					<xsl:value-of select="$imprint/t:biblScope[@type='vol']"/>
				</xsl:element>
			</xsl:if>	
			
			<!-- SOURCE PAGES -->
			<xsl:if test="exists($imprint/t:biblScope[@type='fpage'])">
				<xsl:element name="eterms:start-page">
					<xsl:value-of select="$imprint/t:biblScope[@type='fpage']"/>
				</xsl:element>
			</xsl:if>
			<xsl:if test="exists($imprint/t:biblScope[@type='lpage'])">
				<xsl:element name="eterms:end-page">
					<xsl:value-of select="$imprint/t:biblScope[@type='lpage']"/>								
				</xsl:element>						
			</xsl:if>

			<!-- SOURCE PUBLISHINGINFO -->
			<xsl:element name="eterms:publishing-info">
				<xsl:element name="dc:publisher">
					<xsl:value-of select="$cone-journal/cone/rdf:RDF/rdf:Description/dc:publisher"/>
				</xsl:element>					 
				<xsl:element name="eterms:place">
					<xsl:value-of select="$cone-journal/cone/rdf:RDF/rdf:Description/dcterms:publisher"/>
				</xsl:element>					
			</xsl:element>				

			<!-- CONE IDENTIFIERS -->
			<xsl:element name="dc:identifier">
				<xsl:attribute name="xsi:type">eterms:CONE</xsl:attribute>
				<xsl:value-of select="$cone-journal/cone/rdf:RDF/rdf:Description/@rdf:about"/>
			</xsl:element>

			<!-- ISSN Identifier -->
			<xsl:element name="dc:identifier">
				<xsl:attribute name="xsi:type">eterms:ISSN</xsl:attribute>
				<xsl:value-of select="$cone-journal/cone/rdf:RDF/rdf:Description/dc:identifier/rdf:Description/rdf:value"/>
			</xsl:element>
			
		</xsl:element>
		
	</xsl:template>
	<!-- END OF SOURCE -->
	
	
	<!-- CREATORS -->
	<xsl:template name="createCreators">
		<!-- take corresp Author as first author-->
		<xsl:variable name="authors" select="//t:teiHeader/t:fileDesc/t:sourceDesc/t:biblStruct/t:analytic/t:author"/>
		<xsl:for-each select="($authors[@type='corresp'], $authors[empty(@type) or @type!='corresp'])">
			<xsl:call-template name="createCreator">
				<xsl:with-param name="role" select="$creator-ves/enum[.='author']/@uri"/>
			</xsl:call-template>
		</xsl:for-each>
		<xsl:variable name="orgs" select="//t:teiHeader/t:fileDesc/t:sourceDesc/t:biblStruct/t:analytic/t:affiliation"/>
		<xsl:for-each select="($orgs)">
			<xsl:call-template name="createCreator">
				<xsl:with-param name="role" select="$creator-ves/enum[.='author']/@uri"/>
			</xsl:call-template>
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="createCreator">
		<xsl:param name="role"/>
		<xsl:element name="eterms:creator">
			<xsl:attribute name="role" select="$role"/>
			<xsl:if test="exists(t:persName)">
				<xsl:call-template name="createPerson"/>
			</xsl:if>
			<xsl:if test="not (exists(t:persName))">
				<xsl:call-template name="createOrganization"/>
			</xsl:if>
		</xsl:element>
	</xsl:template>	
	
	<xsl:template name="createPerson">
		<xsl:element name="person:person">
			
			<xsl:variable name="familyName">
				<xsl:if test="empty(t:persName/t:surname)">
					<xsl:element name="eterms:family-name">
						<xsl:value-of select="t:persName"/>
					</xsl:element>				
				</xsl:if>
				<xsl:if test="exists(t:persName/t:surname)">
					<xsl:element name="eterms:family-name">
						<xsl:value-of select="t:persName/t:surname"/>
						<xsl:if test="exists(t:persName/t:nameLink)">
							<xsl:value-of select="concat(' ', t:persName/t:nameLink)"/>
						</xsl:if>
					</xsl:element>				
				</xsl:if>
			</xsl:variable>
			<xsl:copy-of select="$familyName"/>
			
			<xsl:variable name="givenName">
				<xsl:if test="exists(t:persName/t:forename)">
					<xsl:element name="eterms:given-name">
						<xsl:value-of select="t:persName/t:forename"/>
						<xsl:if test="exists(t:persName/t:genName)">
							<xsl:value-of select="concat(', ', t:persName/t:genName)"/>
						</xsl:if>
					</xsl:element>				
				</xsl:if>
			</xsl:variable>
			<xsl:copy-of select="$givenName"/>
			
			<xsl:call-template name="createOrganization"/>
		</xsl:element>		
	</xsl:template>
	
	<xsl:template name="createOrganization">	
		<xsl:variable name="orgName" select="
			if (exists(t:affiliation/t:orgName) and (exists(t:affiliation/t:orgName[@type='department']) or exists(t:affiliation/t:orgName[@type='institution'])))
			then string-join( (t:affiliation/t:orgName[@type='department'], t:affiliation/t:orgName[@type='institution']), ', ')
			else 'External Organization'
		"/>	
		<organization:organization>
			<dc:title>
				<xsl:value-of select="$orgName"/>
			</dc:title>
			<xsl:variable name="addr" select="t:affiliation/t:address"/>
			<xsl:if test="exists($addr)">						
				<eterms:address>
					<xsl:value-of select="
								string-join(
									(
										  $addr/t:postBox
										, $addr/t:postCode
										, $addr/t:settlement
										, $addr/t:country 
									)
									, ', '
								)									
						"/>
				</eterms:address>
			</xsl:if>
			<dc:identifier><xsl:value-of select="$external_organization_id"/></dc:identifier>
		</organization:organization>	
	</xsl:template>
<!--	END OF CREATORS-->	

</xsl:stylesheet>