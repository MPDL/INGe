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
	Transformations from TEI 2 to eSciDoc PubItem 
	See mapping: http://colab.mpdl.mpg.de/mediawiki/PubMan_Func_Spec_Submission/TEI_2_PubItem_Mapping 
	Author: Vlad Makarenko (initial creation) 
	$Author: vmakarenko $ (last changed)
	$Revision: $ 
	$LastChangedDate:  $
-->
<xsl:stylesheet version="2.0"
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
   xmlns:xs="http://www.w3.org/2001/XMLSchema"
   xmlns:fn="http://www.w3.org/2005/xpath-functions"
   xmlns:xlink="http://www.w3.org/1999/xlink"
   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
   xmlns:dc="http://purl.org/dc/elements/1.1/"
   xmlns:dcterms="http://purl.org/dc/terms/"
   xmlns:mdr="${xsd.soap.common.mdrecords}"
   xmlns:mdp="${xsd.metadata.escidocprofile}"
   xmlns:e="${xsd.metadata.escidocprofile.types}"
   xmlns:ei="${xsd.soap.item.item}"
   xmlns:eidt="${xsd.metadata.escidocprofile}idtypes"
   xmlns:srel="${xsd.soap.common.srel}"
   xmlns:prop="${xsd.core.properties}"
   xmlns:oaipmh="http://www.openarchives.org/OAI/2.0/"
   xmlns:ec="${xsd.soap.item.components}"
   xmlns:file="${xsd.metadata.file}"
   xmlns:pub="${xsd.metadata.publication}"
   xmlns:escidoc="urn:escidoc:functions"
   xmlns:t="http://www.tei-c.org/ns/1.0" 
   xmlns:ce="http://www.elsevier.com"
   xmlns:mml="http://www.w3.org/1998/Math/MathML"   
   xmlns:AuthorDecoder="java:de.mpg.escidoc.services.common.util.creators.AuthorDecoder"
   >

	<xsl:output method="xml" encoding="UTF-8" indent="yes"/>
	
	<xsl:param name="user" select="'dummy:user'"/>
	<xsl:param name="context" select="'dummy:context'"/>
	<xsl:param name="content-model" select="'dummy:content-model'"/>

	<xsl:param name="is-item-list" select="true()"/>
	
	<xsl:param name="refType" />
	
	<xsl:variable name="genreMap">
			<m key="article">article</m>
			<m key="inproceeding">proceedings</m>
			<m key="inbook">book-item</m>
			<m key="book">book</m>
			<m key="thesis">thesis</m>
			<m key="report">report</m>
	</xsl:variable>
	
	<xsl:variable name="identMap">
			<m key="DOI">eidt:DOI</m>
			<m key="ISSN">eidt:ISSN</m>
			<m key="pISSN">eidt:ISSN</m>
			<m key="eISSN">eidt:ISSN</m>
			<m key="pmid">eidt:PMID</m>
			<m key="pii">eidt:PII</m>
	</xsl:variable>
	
      	
	<xsl:variable name="dateMap">
			<m key="Received">dcterms:created</m>
			<m key="Revised">dcterms:modified</m>
			<m key="Accepted">dcterms:dateAccepted</m>
			<m key="Online">pub:published-online</m>
			<m key="Submitted">dcterms:dateSubmitted</m>
			<m key="publication">dcterms:issued</m>
	</xsl:variable>
	
	<xsl:template match="/">
		<xsl:choose>
			<xsl:when test="$is-item-list">
				<item-list>
					<xsl:call-template name="createItem"/>
				</item-list>
			</xsl:when>
			<xsl:when test="count(.) = 1">
				<xsl:call-template name="createItem"/>
			</xsl:when>
			<xsl:when test="count(.) = 0">
				<xsl:value-of select="error(QName('http://www.escidoc.de', 'err:NoSourceForSingleTarget' ), 'Single item was selected as target, but the source contained no items')"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="error(QName('http://www.escidoc.de', 'err:MultipleSourceForSingleTarget' ), 'Single item was selected as target, but the source contained multiple items')"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="createItem">
		<xsl:element name="ei:item">
			<xsl:element name="ei:properties">
				<srel:context objid="{$context}" />
				<srel:content-model objid="{$content-model}" />
				<xsl:element name="prop:content-model-specific"/>
			</xsl:element>
			<xsl:element name="mdr:md-records">
				<mdr:md-record name="escidoc">
					<xsl:call-template name="createMetadata"/>
				</mdr:md-record>
			</xsl:element>
			
			<xsl:call-template name="createComponents"/>
			
		</xsl:element>
	</xsl:template>
		
	
	<!-- METADATA -->
	<xsl:template name="createMetadata">
	
		<xsl:variable name="refType" select="/t:TEI/t:teiHeader/t:fileDesc/t:sourceDesc/t:biblStruct/@type"/>
		
		<xsl:variable name="curGenre" select="$genreMap/m[@key=$refType]" />
		<xsl:call-template name="createEntry">
			<xsl:with-param name="gen" select="if (exists($curGenre)) then $curGenre else 'article'"/>
		</xsl:call-template>
	</xsl:template>
	
	
	<!-- Create eSciDoc Entry -->		
	
	<xsl:template name="createEntry">
		<xsl:param name="gen"/>
		
		<xsl:element name="mdp:publication">
		
			<xsl:attribute name="type">
				<xsl:value-of select="$gen"/>
			</xsl:attribute>
			
			<xsl:variable name="sDesc" select="/t:TEI/t:teiHeader/t:fileDesc/t:sourceDesc"/>
			<xsl:variable name="pDesc" select="/t:TEI/t:teiHeader/t:profileDesc"/>
			<xsl:variable name="rDesc" select="/t:TEI/t:teiHeader/t:revisionDesc"/>
			
			<!-- CREATORS -->
			<xsl:call-template name="createCreators"/>
						
			<!-- TITLE -->
			<xsl:element name="dc:title">
				<xsl:value-of select="normalize-space(
					if (exists($sDesc/t:titleStmt/t:title[@type='main']))
					then $sDesc/t:titleStmt/t:title[@type='main']
					else if (exists($sDesc/t:biblStruct/t:analytic/t:title[@type='main']))
					then $sDesc/t:biblStruct/t:analytic/t:title[@type='main']
					else error(QName('http://www.escidoc.de', 'err:NoTitleDefined' ), 'No title is defined for the item')
				)"/>
			</xsl:element>
			
			<!-- LANGUAGE -->
			<xsl:if test="exists($pDesc/t:langUsage/t:lang)">
				<xsl:element name="dc:language">
					<xsl:attribute name="xsi:type">dcterms:RFC3066</xsl:attribute>
					<xsl:value-of select="$pDesc/t:langUsage/t:lang"/>
				</xsl:element>
			</xsl:if> 
			
			<!--ALTTITLE -->
			<xsl:for-each select="
				$sDesc/t:titleStmt/t:title[empty(@type) or @type!='main']
				|
				$sDesc/t:biblStruct/t:analytic/t:title[empty(@type) or @type!='main']
			">
				<xsl:element name="dcterms:alternative">
					<xsl:value-of select="normalize-space(.)"/>
				</xsl:element>
			</xsl:for-each>
			
			<!-- IDENTIFIERS -->
			<xsl:call-template name="createIdentifiers">
				<xsl:with-param name="idents" select="$sDesc/t:biblStruct/t:idno"/>
			</xsl:call-template>

			
			<xsl:variable name="monogr" select="$sDesc/t:biblStruct/t:monogr"/>
			<xsl:variable name="imprint" select="$monogr/t:imprint"/>
			
			<!-- PUBLISHING INFO -->
			<!-- not presented!-->

			
			
			<!-- DATES -->
			
			<xsl:variable name="iDates" select="$imprint/t:date"/>
			<xsl:variable name="rDates" select="$rDesc/t:change"/>
			
			<xsl:for-each select="$dateMap/m/@key">
				<xsl:variable name="dateType" select="."/>
				<xsl:variable name="date" select="$iDates[@type=$dateType]"/>
				<xsl:variable name="change" select="$rDates[.=$dateType]"/>
				<xsl:if test="exists($date) or exists($change)">
					<xsl:element name="{$dateMap/m[@key=$dateType]}">
						<xsl:attribute name="xsi:type">dcterms:W3CDTF</xsl:attribute>
						<xsl:value-of select="
							if (
								translate($date/@when, '-', '') > 
								translate($change/@when, '-', '')
							)
							then $date/@when
							else $change/@when
						"/>
					</xsl:element>
				</xsl:if>
			
			</xsl:for-each>
			
			<!-- SOURCE -->
			<xsl:if test="exists($monogr/t:title/@level)">
				<xsl:call-template name="createSource">
					<xsl:with-param name="monogr" select="$monogr"/>
				</xsl:call-template>
			</xsl:if>
			
			
			<!-- TOTAL NUMBER OF PAGES -->
			<xsl:if test="exists($imprint/t:biblScope[@type='pp'])">
				<xsl:element name="pub:total-number-of-pages">
					<xsl:value-of select="$imprint/t:biblScope[@type='pp']"/>
				</xsl:element>
			</xsl:if>
			
			
			<!-- EVENT -->
			<!-- not presented!-->
			
			
			<!-- DEGREE -->
			<!-- not presented!-->
			
			
			<xsl:variable name="text" select="/t:TEI/t:text"/>
						
			<!-- ABSTRACT -->
			<xsl:if test="exists($text/t:front/t:div[@type='abstract'])">
				<xsl:element name="dcterms:abstract">
					<xsl:value-of select="$text/t:front/t:div[@type='abstract']/t:p"/>
				</xsl:element>
			</xsl:if>
			
			<!-- SUBJECT -->
			<!-- TODO: mit comma-->
			<xsl:variable name="subjCount" select="count($pDesc/t:textClass/t:keywords/t:list/t:item/t:term)"/>
			
			<xsl:if test="$subjCount>0">
				<xsl:element name="dcterms:subject">
					<xsl:for-each select="$pDesc/t:textClass/t:keywords/t:list/t:item/t:term">
							<xsl:value-of select="
								concat(
									normalize-space(.),
									if ($subjCount>1 and (position()!=last())) then ', ' else ''
								)	
							"/>
					</xsl:for-each>
				</xsl:element>
			</xsl:if>
			
			
			<!-- LOCATION -->
			<xsl:if test="I and $refType = 'Manuscript'">
				<xsl:element name="pub:location">
					<xsl:value-of select="I"/>
				</xsl:element>
			</xsl:if>
			
			
		</xsl:element>
		
	</xsl:template>
	
	
	
	<!-- SOURCE -->
	<xsl:template name="createSource">
		<xsl:param name="monogr"/>
		
		<xsl:element name="pub:source">

			<!-- SOURCE GENRE -->
			<xsl:attribute name="type">
				<xsl:value-of select="
					if ($monogr/t:title/@level='j') 
					then 'journal' 
					else if ($monogr/t:title/@level='s') 
					then 'series'
					else error(QName('http://www.escidoc.de', 'err:UnknownSourceGenre' ), 'Unknown or empty Source genre: ', $monogr/t:title/@level)
					"/>
			</xsl:attribute>

			<!-- SOURCE TITLE -->
			<xsl:variable name="title" select="
				if (count($monogr/t:title[@type='main'])=0) 
					then $monogr/t:title[1]
					else $monogr/t:title[@type='main']
			"/>							
			<xsl:element name="dc:title">
				<xsl:value-of select="$title"/>				
			</xsl:element>

			<!-- SOURCE ALTTITLE -->
			<xsl:for-each select="$monogr/t:title[@type!='main' and .!=$title]">
				<xsl:element name="dcterms:alternative">
					<xsl:value-of select="."/>
				</xsl:element>
			</xsl:for-each>
			
			
			<!-- SOURCE CREATORS -->


			
			<!-- SOURCE VOLUME -->
			<xsl:if test="exists($monogr/t:biblScope[@type='vol'])">
				<xsl:element name="e:volume">
					<xsl:value-of select="$monogr/t:biblScope[@type='vol']"/>
				</xsl:element>
			</xsl:if>	

			<xsl:variable name="imprint" select="$monogr/t:imprint"/>
			
			<!-- SOURCE ISSUE -->
			<xsl:if test="exists($imprint/t:biblScope[@type='issue'])">
				<xsl:element name="e:issue">
					<xsl:value-of select="$imprint/t:biblScope[@type='issue']"/>
				</xsl:element>
			</xsl:if>
			
			<!-- SOURCE PAGES -->
			<xsl:if test="exists($imprint/t:biblScope[@type='fpage'])">
				<xsl:element name="e:start-page">
					<xsl:value-of select="$imprint/t:biblScope[@type='fpage']"/>
				</xsl:element>
			</xsl:if>
			<xsl:if test="exists($imprint/t:biblScope[@type='lpage'])">
				<xsl:element name="e:end-page">
					<xsl:value-of select="$imprint/t:biblScope[@type='lpage']"/>								
				</xsl:element>						
			</xsl:if>
			
			<!-- SOURCE SEQUENCE NUMBER -->
			<xsl:if test="exists($imprint/t:biblScope[@type='elocation-id'])">
				<xsl:element name="e:sequence-number">
					<xsl:value-of select="$imprint/t:biblScope[@type='elocation-id']"/>
				</xsl:element>
			</xsl:if>
				

			<!-- SOURCE PUBLISHINGINFO -->
			<xsl:variable name="publisher" select="$imprint/t:publisher"/>
			<xsl:variable name="place" select="$imprint/t:pubPlace"/>
			
			<xsl:if test="exists($publisher)">
				<xsl:element name="e:publishing-info">
					<xsl:element name="dc:publisher">
						<xsl:value-of select="$publisher"/>
					</xsl:element>
					<xsl:if test="exists($place)">
						<xsl:element name="e:place">
							<xsl:value-of select="$place"/>
						</xsl:element>
					</xsl:if>
				</xsl:element>
			</xsl:if>	
			
			<!-- SOURCE IDENTIFIERS -->
			<xsl:call-template name="createIdentifiers">
				<xsl:with-param name="idents" select="$monogr/t:idno"/>
			</xsl:call-template>

		</xsl:element>
		
	</xsl:template>
	<!-- END OF SOURCE -->
	
	
	<!-- CREATORS -->
	<xsl:template name="createCreators">
		<!-- take corresp Author as first author-->
		<xsl:variable name="authors" select="//t:teiHeader/t:fileDesc/t:sourceDesc/t:biblStruct/t:analytic/t:author"/>
			<xsl:for-each select="($authors[@type='corresp'], $authors[empty(@type) or @type!='corresp'])">
			<xsl:call-template name="createCreator">
				<xsl:with-param name="role" select="'author'"/>
			</xsl:call-template>
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="createCreator">
		<xsl:param name="role"/>
		<xsl:element name="pub:creator">
			<xsl:attribute name="role"><xsl:value-of select="$role"/></xsl:attribute>
			<xsl:call-template name="createPerson"/>
		</xsl:element>
	</xsl:template>	
	
	<xsl:template name="createPerson">
		<xsl:element name="e:person">
			
			<xsl:variable name="familyName">
				<xsl:if test="empty(t:persName/t:surname)">
					<xsl:element name="e:family-name">
						<xsl:value-of select="t:persName"/>
					</xsl:element>				
				</xsl:if>
				<xsl:if test="exists(t:persName/t:surname)">
					<xsl:element name="e:family-name">
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
					<xsl:element name="e:given-name">
						<xsl:value-of select="t:persName/t:forename"/>
						<xsl:if test="exists(t:persName/t:genName)">
							<xsl:value-of select="concat(', ', t:persName/t:genName)"/>
						</xsl:if>
					</xsl:element>				
				</xsl:if>
			</xsl:variable>
			<xsl:copy-of select="$givenName"/>
			
			<xsl:variable name="a" select="t:affiliation"/>
			<xsl:variable name="orgName" select="
				if(exists($a/t:orgName[@type='department']) or exists($a/t:orgName[@type='institution']))
				then string-join( ($a/t:orgName[@type='department'], $a/t:orgName[@type='institution']), ', ')
				else ''
			"/>
			
			<e:organization>
				<e:organization-name>
					<xsl:value-of select="
						if (empty($orgName))
						then string-join( ($familyName, $givenName), ' ')
						else $orgName
					"/>
				</e:organization-name>
				<xsl:variable name="addr" select="$a/t:address"/>
				<xsl:if test="exists($addr)">
					<e:address>
					<!-- TODO: Not clear the order-->
						<xsl:value-of select="
							string-join(
								(
									 $addr/t:addrLine
									,$addr/t:settlement
									,$addr/t:postCode
									,$addr/t:country
									,t:email
								)
								, ' '
							)"/>
					</e:address>
				</xsl:if>
				<e:identifier>${escidoc.pubman.external.organisation.id}</e:identifier>
			</e:organization>
			
			
		</xsl:element>
	</xsl:template>
<!--	END OF CREATORS-->	


<!--	IDENTIFIER-->
	<xsl:template name="createIdentifiers">
		<xsl:param name="idents"/>
		<xsl:for-each select="$idents">
			<xsl:variable name="ident" select="."/>
			<xsl:element name="dc:identifier">
				<xsl:variable name="idType" select="$identMap/m[@key=$ident/@type]"/>
				<xsl:attribute name="xsi:type">
					<!-- TODO: not clear from specs -->
					<xsl:value-of select="if (exists($idType)) then $idType else 'eidt:OTHER'"/>
				</xsl:attribute>
				<xsl:value-of select="$ident"/>
			</xsl:element>		
		</xsl:for-each>
	</xsl:template>

	
<!--	COMOPONENTS-->
	<xsl:template name="createComponents">
	
		<xsl:variable name="pubStmt" select="/t:TEI/t:teiHeader/t:fileDesc/t:publicationStmt"/>
	
		<ec:components>
		      <ec:component objid="escidoc:dummy">
		        <ec:properties/>
		        <ec:content />
		        <mdr:md-records>
		          <mdr:md-record name="escidoc">
		            <file:file>
		              <dc:title/>
		              <dc:description/>
		              <dc:format/>
		              <dcterms:available/>
		              <dcterms:dateCopyrighted>
		              		<xsl:value-of select="
		              			if (exists($pubStmt/t:date/@when)) 
		              			then $pubStmt/t:date/@when
		              			else if (exists($pubStmt/t:date)) 
		              			then $pubStmt/t:date
		              			else ''
		              		"/>
		              </dcterms:dateCopyrighted>
		              <dc:rights>
		              	<xsl:if test="exists($pubStmt/t:availability)">
		              		<xsl:value-of select="$pubStmt/t:availability"/>
		              	</xsl:if>
		              	<xsl:if test="exists($pubStmt/t:authority)">
		              		<xsl:value-of select="
		              			if (exists($pubStmt/t:availability))
		              			then concat(' (', $pubStmt/t:authority, ')')
		              			else $pubStmt/t:authority
		              		"/>
		              	</xsl:if>
		              </dc:rights>
		              <dcterms:license/>
		            </file:file>
		          </mdr:md-record>
		        </mdr:md-records>
		      </ec:component>
		    </ec:components>
	</xsl:template>				
	

</xsl:stylesheet>