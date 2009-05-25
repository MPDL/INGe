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
	Transformations from eDoc Item to eSciDoc PubItem 
	Author: Julia Kurt (initial creation) 
	$Author: kurt $ (last changed)
	$Revision: 747 $ 
	$LastChangedDate: 2008-07-21 19:15:26 +0200 (Mo, 21 Jul 2008) $
-->
<xsl:stylesheet version="2.0"
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:xs="http://www.w3.org/2001/XMLSchema"
		xmlns:fn="http://www.w3.org/2005/xpath-functions"
		xmlns:xlink="http://www.w3.org/1999/xlink"
		xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		xmlns:dc="http://purl.org/dc/elements/1.1/"
		xmlns:dcterms="http://purl.org/dc/terms/"
		xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
		xmlns:ei="${xsd.soap.item.item}"
		xmlns:mdr="${xsd.soap.common.mdrecords}"
		xmlns:mdp="${xsd.metadata.escidocprofile}"
		xmlns:e="http://escidoc.mpg.de/metadataprofile/schema/0.1/types"
		xmlns:ec="${xsd.soap.item.components}"
		xmlns:prop="${xsd.soap.common.prop}"
		xmlns:srel="${xsd.soap.common.srel}"
		xmlns:version="${xsd.soap.common.version}"
		xmlns:release="${xsd.soap.common.release}"
		xmlns:file="${xsd.metadata.file}"
		xmlns:publ="${xsd.metadata.publication}"
		xmlns:escidocFunctions="urn:escidoc:functions"
		xmlns:escidoc="http://escidoc.mpg.de/"
		xmlns:Util="java:de.mpg.escidoc.services.transformation.Util">
	
	<xsl:output method="xml" encoding="UTF-8" indent="yes"/>
	
	<xsl:param name="is-item-list" select="true()"/>
	
	<xsl:param name="useAuthorList" select="false()"/>
	<xsl:param name="removeSpacesInInitials" select="false()"/>
	<xsl:param name="createLocatorsForPublicComponents" select="false()"/>
	
	<xsl:param name="user" select="'dummy-user'"/>
	<xsl:param name="context" select="'escidoc:57277'"/>
	
	<xsl:param name="content-model" select="'dummy-content-model'"/>
	
	<!--
		DC XML  Header
	-->
	
	<xsl:variable name="dependentGenre">
		<type>article</type>
		<type>conference-paper</type>
		<type>conference-report</type>
		<type>book-item</type>
		<type>issue</type>
		<type>paper</type>
		<type>poster</type>
		<type>talk-at-event</type>
	</xsl:variable>
	
	<xsl:variable name="collection-mapping">
		<mapping>
			<edoc-collection>Language Acquisition</edoc-collection>
			<escidoc-ou>Language Acquisition Group</escidoc-ou>
		</mapping>
		<mapping>
			<edoc-collection>Acquisition Group</edoc-collection>
			<escidoc-ou>Language Acquisition Group</escidoc-ou>
		</mapping>
		<mapping>
			<edoc-collection>Adaptive Listening</edoc-collection>
			<escidoc-ou>Adaptive Listening</escidoc-ou>
		</mapping>
		<mapping>
			<edoc-collection>Language and Cognition Group</edoc-collection>
			<escidoc-ou>Categories across Language and Cognition</escidoc-ou>
		</mapping>
		<mapping>
			<edoc-collection>Categories across Language and Cognition</edoc-collection>
			<escidoc-ou>Categories across Language and Cognition</escidoc-ou>
		</mapping>
		<mapping>
			<edoc-collection>Communication Before Language</edoc-collection>
			<escidoc-ou>Communication before Language</escidoc-ou>
		</mapping>
		<mapping>
			<edoc-collection>Comparative Cognitive Anthropology</edoc-collection>
			<escidoc-ou>Comparative Cognitive Anthropology</escidoc-ou>
		</mapping>
		<mapping>
			<edoc-collection>Decoding Continuous Speech</edoc-collection>
			<escidoc-ou>Decoding Continuous Speech</escidoc-ou>
		</mapping>
		<mapping>
			<edoc-collection>Event Representation</edoc-collection>
			<escidoc-ou>Event Representation</escidoc-ou>
		</mapping>
		<mapping>
			<edoc-collection>Evolutionary Processes in Language and Culture</edoc-collection>
			<escidoc-ou>Evolutionary Processes in Language and Culture</escidoc-ou>
		</mapping>
		<mapping>
			<edoc-collection>Gesture</edoc-collection>
			<escidoc-ou>Gesture</escidoc-ou>
		</mapping>
		<mapping>
			<edoc-collection>Information Structure in Language Acquisition</edoc-collection>
			<escidoc-ou>Information Structure in Language Acquisition</escidoc-ou>
		</mapping>
		<mapping>
			<edoc-collection>Language and Genetics</edoc-collection>
			<escidoc-ou>Language and Genetics</escidoc-ou>
		</mapping>
		<mapping>
			<edoc-collection>Language in Action</edoc-collection>
			<escidoc-ou>Language in Action</escidoc-ou>
		</mapping>
		<mapping>
			<edoc-collection>Island Melanesia</edoc-collection>
			<escidoc-ou>Pioneers of Island Melanesia</escidoc-ou>
		</mapping>
		<mapping>
			<edoc-collection>Multimodal Interaction</edoc-collection>
			<escidoc-ou>Multimodal Interaction</escidoc-ou>
		</mapping>
		<mapping>
			<edoc-collection>Neurocognition of Language Processing</edoc-collection>
			<escidoc-ou>Neurocognition of Language Processing</escidoc-ou>
		</mapping>
		<mapping>
			<edoc-collection>Other Research</edoc-collection>
			<escidoc-ou>Other Research</escidoc-ou>
		</mapping>
		<mapping>
			<edoc-collection>Phonological Learning for Speech Perception</edoc-collection>
			<escidoc-ou>Phonological Learning for Speech Perception</escidoc-ou>
		</mapping>
		<mapping>
			<edoc-collection>Sign Language Typology</edoc-collection>
			<escidoc-ou>Sign Language Typology</escidoc-ou>
		</mapping>
		<mapping>
			<edoc-collection>Space</edoc-collection>
			<escidoc-ou>Space</escidoc-ou>
		</mapping>
		<mapping>
			<edoc-collection>Technical Group</edoc-collection>
			<escidoc-ou>Technical Group</escidoc-ou>
		</mapping>
		<mapping>
			<edoc-collection>The Comparative Study of L2 Acquisition</edoc-collection>
			<escidoc-ou>The Comparative Study of L2 Acquisition</escidoc-ou>
		</mapping>
		<mapping>
			<edoc-collection>The Dynamics of Learner Varieties</edoc-collection>
			<escidoc-ou>The Dynamics of Learner Varieties</escidoc-ou>
		</mapping>
		<mapping>
			<edoc-collection>The Dynamics of Multilingual Processing</edoc-collection>
			<escidoc-ou>The Dynamics of Multilingual Processing</escidoc-ou>
		</mapping>
		<mapping>
			<edoc-collection>The Neurobiology of Language</edoc-collection>
			<escidoc-ou>The Neurobiology of Language</escidoc-ou>
		</mapping>
		<mapping>
			<edoc-collection>Unification</edoc-collection>
			<escidoc-ou>Unification</escidoc-ou>
		</mapping>
		<mapping>
			<edoc-collection>The Role of Finiteness</edoc-collection>
			<escidoc-ou>The Role of Finiteness</escidoc-ou>
		</mapping>
		<mapping>
			<edoc-collection>Utterance Encoding</edoc-collection>
			<escidoc-ou>Utterance Encoding</escidoc-ou>
		</mapping>
	</xsl:variable>
	
	<xsl:variable name="authors">
		<authors/>
	</xsl:variable>
	
	<xsl:variable name="organizational-units">
		<organizational-units/>
	</xsl:variable>
	
	<xsl:function name="escidocFunctions:ou-name">
		<xsl:param name="name"/>

		<xsl:value-of select="$organizational-units//ou[@name = $name or @alias = $name]/@name"/>

		<xsl:if test="$organizational-units//ou[@name = $name or @alias = $name]/../@name != $name">
			<xsl:text>, </xsl:text>
			<xsl:value-of select="escidocFunctions:ou-name($organizational-units//ou[@name = $name or @alias = $name]/../@name)"/>
		</xsl:if>
		
	</xsl:function>
	
	<xsl:function name="escidocFunctions:ou-id">
		<xsl:param name="name"/>
		
		<xsl:choose>
			<xsl:when test="$organizational-units//ou[@name = $name or @alias = $name]">
				<xsl:value-of select="$organizational-units//ou[@name = $name or @alias = $name]/@id"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$organizational-units//ou[@name = 'external']/@id"/>
			</xsl:otherwise>
		</xsl:choose>
		
	</xsl:function>
	
	<xsl:template match="/*">
		
		<!-- <xsl:call-template name="validation"/> -->
		<xsl:choose>
			<xsl:when test="$is-item-list">
				<item-list>
					<xsl:apply-templates select="//record/metadata"/>
				</item-list>
			</xsl:when>
			<xsl:when test="count(//record/metadata) = 1">
				<xsl:apply-templates select="//record/metadata"/>
			</xsl:when>
			<xsl:when test="count(//record/metadata) = 0">
				<xsl:value-of select="error(QName('http://www.escidoc.de', 'err:NoSourceForSingleTarget' ), 'Single item was selected as target, but the source contained no items')"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="error(QName('http://www.escidoc.de', 'err:MultipleSourceForSingleTarget' ), 'Single item was selected as target, but the source contained multiple items')"/>
			</xsl:otherwise>
		</xsl:choose>
			<!-- <xsl:copy-of select="Util:queryCone('languages', 'uighur')"/> -->
	</xsl:template>
	
	<xsl:template match="record/metadata">
		<xsl:element name="ei:item">
			<xsl:element name="ei:properties">
				<xsl:element name="srel:context">
					<xsl:attribute name="objid" select="$context"/>
				</xsl:element>
				<srel:content-model objid="{$content-model}"/>
				<xsl:element name="prop:content-model-specific">
					<xsl:if test="../MPGyearbook = '2009'">
						<local-tags>
							<local-tag>Yearbook 2009</local-tag>
						</local-tags>
					</xsl:if>
				</xsl:element>
			</xsl:element>
			<xsl:element name="mdr:md-records">
				<mdr:md-record name="escidoc">
					<xsl:apply-templates select="basic"/>
				</mdr:md-record>
			</xsl:element>
			<xsl:element name="ec:components">
				<xsl:for-each select="basic/fturl">					
					<!-- duplicate filenames -->
					<xsl:variable name="filename" select="@filename"/>
					
					<xsl:choose>
						<xsl:when test="not(preceding-sibling::fturl/@filename = $filename)">
							<xsl:variable name="access">
								<xsl:choose>
									<xsl:when test="following-sibling::fturl[@filename=$filename]/@viewftext='USER' or @viewftext='USER'">USER</xsl:when>
									<xsl:when test="following-sibling::fturl[@filename=$filename]/@viewftext='INSTITUT' or @viewftext='INSTITUT'">INSTITUT</xsl:when>
									<xsl:when test="following-sibling::fturl[@filename=$filename]/@viewftext='MPG' or @viewftext='MPG'">MPG</xsl:when>
									<xsl:when test="following-sibling::fturl[@filename=$filename]/@viewftext='PUBLIC' or @viewftext='PUBLIC'">PUBLIC</xsl:when>
									<xsl:otherwise>
										<!-- ERROR -->
										<xsl:value-of select="error(QName('http://www.escidoc.de', 'err:UnknownAccessLevel' ), concat('acces level [', @viewftext, '] of fulltext is not supported at eSciDoc, record ', ../../../@id))"/>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:variable>
							
							<xsl:call-template name="createComponent">
								<xsl:with-param name="filename" select="$filename"/>
								<xsl:with-param name="access" select="$access"/>
							</xsl:call-template>
						
						</xsl:when>
					</xsl:choose>
					
					<xsl:if test="$createLocatorsForPublicComponents or @viewftext != 'PUBLIC'">
						<xsl:call-template name="createLocator">
							<xsl:with-param name="filename" select="$filename"/>
							<xsl:with-param name="access" select="@viewftext"/>
						</xsl:call-template>
					</xsl:if>
				
				</xsl:for-each>
			</xsl:element>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="fturl">
		<xsl:call-template name="createComponent">
			<xsl:with-param name="filename" select="@filename"/>
			<xsl:with-param name="access" select="@viewftext"/>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template name="createComponent">
		<xsl:param name="filename"/>
		<xsl:param name="access"/>
		<!-- FILE -->
		<xsl:element name="ec:component">
			<ec:properties>
				<!-- <prop:valid-status>valid</prop:valid-status> -->
				<xsl:choose>
					<xsl:when test="$access='USER' or $access='INSTITUT' or $access='MPG'">
						<prop:visibility>private</prop:visibility>
						<prop:content-category>publisher-version</prop:content-category>
					</xsl:when>
					<xsl:when test="$access='PUBLIC'">
						<prop:visibility>public</prop:visibility>
						<prop:content-category>any-fulltext</prop:content-category>
					</xsl:when>
					<xsl:otherwise>
						<!-- ERROR -->
						<xsl:value-of select="error(QName('http://www.escidoc.de', 'err:UnknownAccessLevel' ), concat('acces level [', $access, '] of fulltext is not supported at eSciDoc, record ', ../../../@id))"/>
					</xsl:otherwise>
				</xsl:choose>
				<prop:mime-type>application/pdf</prop:mime-type>
			</ec:properties>
			<xsl:element name="ec:content">
				<xsl:attribute name="xlink:href" select="."/>
				<xsl:attribute name="storage" select="'internal-managed'"/>
			</xsl:element>
			<xsl:element name="mdr:md-records">
				<mdr:md-record name="escidoc">
					<xsl:element name="file:file">
						<xsl:element name="dc:title">
							<xsl:value-of select="@filename"/>
						</xsl:element>
						<xsl:element name="file:content-category">any-fulltext</xsl:element>
						<dc:format xsi:type="dcterms:IMT">application/pdf</dc:format>
						<dcterms:extent>
							<xsl:value-of select="@size"/>
						</dcterms:extent>
						<xsl:element name="dc:rights">
							<xsl:value-of select="concat('eDoc_access: ', $access)"/>
						</xsl:element>
					</xsl:element>
				</mdr:md-record>
			</xsl:element>
		</xsl:element>
	
	</xsl:template>
	
	<xsl:template name="createLocator">
		<xsl:param name="filename"/>
		<xsl:param name="access"/>
		<!-- LOCATOR -->
		<xsl:element name="ec:component">
			<ec:properties>
				<!-- <prop:valid-status>valid</prop:valid-status> -->
				<prop:visibility>public</prop:visibility>
				<prop:content-category>supplementary-material</prop:content-category>
			</ec:properties>
			<xsl:element name="ec:content">
				<xsl:attribute name="xlink:href" select="."/>
				<xsl:attribute name="storage" select="'external-url'"/>
			</xsl:element>
			<xsl:element name="mdr:md-records">
				<mdr:md-record name="escidoc">
					<xsl:element name="file:file">
						<xsl:choose>
							<xsl:when test="$access='USER'">
								<xsl:element name="dc:title">restricted access to full text (selected user)</xsl:element>
								<xsl:element name="dc:description">
									<xsl:value-of select="@filename"/>
								</xsl:element>
							</xsl:when>
							<xsl:when test="$access='INSTITUT'">
								<xsl:element name="dc:title">restricted access to full text (institute-wide)</xsl:element>
								<xsl:element name="dc:description">
									<xsl:value-of select="@filename"/>
								</xsl:element>
							</xsl:when>
							<xsl:when test="$access='MPG'">
								<xsl:element name="dc:title">restricted access to full text (MPS-wide)</xsl:element>
								<xsl:element name="dc:description">
									<xsl:value-of select="@filename"/>
								</xsl:element>
							</xsl:when>
							<xsl:when test="$access='PUBLIC'">
								<xsl:element name="dc:title">
									<xsl:value-of select="@filename"/>
								</xsl:element>
							</xsl:when>
							<xsl:otherwise>
							<!-- ERROR --></xsl:otherwise>
						</xsl:choose>
					</xsl:element>
				</mdr:md-record>
			</xsl:element>
		</xsl:element>
	</xsl:template>
	
	<!-- BASIC -->
	<xsl:template match="basic">
		<xsl:variable name="genre"/>
		<xsl:choose>
			<xsl:when test="genre='Article'">
				<xsl:variable name="genre" select="'article'"/>
				<xsl:call-template name="createEntry">
					<xsl:with-param name="gen" select="'article'"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="genre='Report'">
				<xsl:variable name="genre" select="'report'"/>
				<xsl:call-template name="createEntry">
					<xsl:with-param name="gen" select="'report'"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="genre='Book'">
				<xsl:variable name="genre" select="'book'"/>
				<xsl:call-template name="createEntry">
					<xsl:with-param name="gen" select="'book'"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="genre='Conference-Paper'">
				<xsl:variable name="genre" select="'conference-paper'"/>
				<xsl:call-template name="createEntry">
					<xsl:with-param name="gen" select="'conference-paper'"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="genre='Conference-Report'">
				<xsl:variable name="genre" select="'conference-report'"/>
				<xsl:call-template name="createEntry">
					<xsl:with-param name="gen" select="'conference-report'"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="genre='Habilitation'">
				<xsl:variable name="genre" select="'thesis'"/>
				<xsl:call-template name="createEntry">
					<xsl:with-param name="gen" select="'thesis'"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="genre='InBook'">
				<xsl:variable name="genre" select="'book-item'"/>
				<xsl:call-template name="createEntry">
					<xsl:with-param name="gen" select="'book-item'"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="genre='Issue'">
				<xsl:variable name="genre" select="'issue'"/>
				<xsl:call-template name="createEntry">
					<xsl:with-param name="gen" select="'issue'"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="genre='Interactive Resource'">
				<xsl:variable name="genre" select="'other'"/>
				<xsl:call-template name="createEntry">
					<xsl:with-param name="gen" select="'other'"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="genre='Journal'">
				<xsl:variable name="genre" select="'journal'"/>
				<xsl:call-template name="createEntry">
					<xsl:with-param name="gen" select="'journal'"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="genre='Lecture / Courseware'">
				<xsl:variable name="genre" select="'courseware-lecture'"/>
				<xsl:call-template name="createEntry">
					<xsl:with-param name="gen" select="'courseware-lecture'"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="genre='Other'">
				<xsl:variable name="genre" select="'other'"/>
				<xsl:call-template name="createEntry">
					<xsl:with-param name="gen" select="'other'"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="genre='Paper'">
				<xsl:variable name="genre" select="'paper'"/>
				<xsl:call-template name="createEntry">
					<xsl:with-param name="gen" select="'paper'"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="genre='PhD-Thesis'">
				<xsl:variable name="genre" select="'thesis'"/>
				<xsl:call-template name="createEntry">
					<xsl:with-param name="gen" select="'thesis'"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="genre='Poster'">
				<xsl:variable name="genre" select="'poster'"/>
				<xsl:call-template name="createEntry">
					<xsl:with-param name="gen" select="'poster'"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="genre='Proceedings'">
				<xsl:variable name="genre" select="'proceedings'"/>
				<xsl:call-template name="createEntry">
					<xsl:with-param name="gen" select="'proceedings'"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="genre='Series'">
				<xsl:variable name="genre" select="'series'"/>
				<xsl:call-template name="createEntry">
					<xsl:with-param name="gen" select="'series'"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="genre='Software'">
				<xsl:variable name="genre" select="'other'"/>
				<xsl:call-template name="createEntry">
					<xsl:with-param name="gen" select="'other'"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="genre='Talk at Event'">
				<xsl:variable name="genre" select="'talk-at-event'"/>
				<xsl:call-template name="createEntry">
					<xsl:with-param name="gen" select="'talk-at-event'"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="genre='Thesis'">
				<xsl:variable name="genre" select="'thesis'"/>
				<xsl:call-template name="createEntry">
					<xsl:with-param name="gen" select="'thesis'"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="error(QName('http://www.escidoc.de', 'err:UnknownGenre' ), concat(genre, ' is not mapped to an eSciDoc publication genre'))"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<!-- Create eSciDoc Entry -->
	<xsl:template name="createEntry">
		<xsl:param name="gen"/>
		
		<xsl:element name="mdp:publication">
			<xsl:attribute name="type" select="$gen"/>	
			<!-- creator -->
			<xsl:for-each select="../creators/creator">
				<xsl:element name="publ:creator">
					<xsl:call-template name="createCreator"/>
				</xsl:element>
			</xsl:for-each>
			<xsl:apply-templates select="corporatebody"/>
			<!-- TITLE -->
			<xsl:element name="dc:title">
				<xsl:value-of select="title"/>
			</xsl:element>
			<!-- LANGUAGE -->
			<xsl:apply-templates select="language"/>
			<!--ALTTITLE -->
			<xsl:apply-templates select="titlealt"/>
			<!-- IDENTIFIER -->
			<xsl:call-template name="createIdentifier"/>			
			<!-- PUBLISHING-INFO -->
			<xsl:if test="exists(publisher) or exists(editiondescription)">
				<xsl:choose>
					<xsl:when test="$gen='book' or $gen='proceedings' or $gen='thesis'">
						<!-- case: book or proceedings -->
						<xsl:element name="publ:publishing-info">
							<xsl:call-template name="createPublishinginfo"/>
						</xsl:element>
					</xsl:when>
					<xsl:when test="$gen='book-item'">
						<!-- case: book-item without source book -->
						<xsl:if test="not(exists(booktitle))">
							<xsl:element name="publ:publishing-info">
								<xsl:call-template name="createPublishinginfo"/>
							</xsl:element>
						</xsl:if>
					</xsl:when>
				
				</xsl:choose>
			</xsl:if>
			
			<!-- DATES -->
			<xsl:apply-templates select="datemodified"/>
			<xsl:apply-templates select="datesubmitted"/>
			<xsl:apply-templates select="dateaccepted"/>
			<xsl:apply-templates select="datepublished"/>		
			<!-- REVIEW METHOD -->
			<xsl:apply-templates select="refereed"/>
			<!-- SOURCE -->
			<xsl:choose>
				<xsl:when test="journaltitle">
					<xsl:element name="publ:source">
						<xsl:call-template name="createJournal"/>
					</xsl:element>
					<xsl:if test="issuetitle">
						<xsl:element name="publ:source">
							<xsl:call-template name="createIssue"/>
						</xsl:element>
					</xsl:if>
				</xsl:when>
				<xsl:when test="issuetitle">
					<xsl:element name="publ:source">
						<xsl:call-template name="createIssue"/>
					</xsl:element>
				</xsl:when>
				<xsl:when test="booktitle">
					<xsl:element name="publ:source">
						<xsl:call-template name="createBook"/>
					</xsl:element>
					<xsl:if test="titleofseries">
						<xsl:element name="publ:source">
							<xsl:call-template name="createSeries"/>
						</xsl:element>
					</xsl:if>
				</xsl:when>
				<xsl:when test="titleofproceedings">
					<xsl:element name="publ:source">
						<xsl:call-template name="createProceedings"/>
					</xsl:element>
					<xsl:if test="exists(titleofseries)">
						<xsl:element name="publ:source">
							<xsl:call-template name="createSeries"/>
						</xsl:element>
					</xsl:if>
				</xsl:when>
				<xsl:when test="titleofseries">
					<xsl:element name="publ:source">
						<xsl:call-template name="createSeries"/>
					</xsl:element>
				</xsl:when>
			</xsl:choose>
			
			<!-- isPartOf RELATION -->
			<xsl:if test="../relations/relation[@reltype='ispartof']">
				<xsl:element name="publ:source">
					<xsl:attribute name="type" select="'series'"/>
					<xsl:element name="dc:title">
						<xsl:value-of select="../relations/relation[@reltype='ispartof']"/>
					</xsl:element>
				</xsl:element>
			</xsl:if>

			<!-- EVENT -->
			<xsl:if test="exists(nameofevent)">
				<xsl:call-template name="createEvent"/>
			</xsl:if>
			<!-- TOTAL NUMBER OF PAGES -->
			<xsl:if test="phydesc">
				<xsl:choose>
					<xsl:when test="$gen='book-item' and not(exists(booktitle))">
						<xsl:call-template name="phydescPubl"/>
					</xsl:when>
					<xsl:when test="$gen='conference-paper' and not(exists(titleofproceedings)) and exists(phydesc)">
						<xsl:call-template name="phydescPubl"/>
					</xsl:when>
					<xsl:when test="$gen=$dependentGenre/type">
						<xsl:if test="not(exists(titleofproceedings)) and not(exists(booktitle)) and not(exists(issuetitle)) and not(exists(journaltitle)) and not(exists(titleodseries))">
							<xsl:call-template name="phydescPubl"/>
						</xsl:if>
					</xsl:when>
					<xsl:when test="not($gen=$dependentGenre/type)">
						<xsl:call-template name="phydescPubl"/>
					</xsl:when>
				</xsl:choose>
			</xsl:if>
			
			<!-- DEGREE -->
			<xsl:if test="genre='PhD-Thesis'">
				<xsl:element name="publ:degree">
					<xsl:value-of select="'phd'"/>
				</xsl:element>
			</xsl:if>
			<xsl:if test="genre='Habilitation'">
				<xsl:element name="mdp:degree">
					<xsl:value-of select="'habilitation'"/>
				</xsl:element>
			</xsl:if>
			<!-- ABSTRACT -->
			<xsl:apply-templates select="abstract"/>
			<!-- SUBJECT -->
			<xsl:apply-templates select="discipline"/>
			<xsl:apply-templates select="keywords"/>
			<!-- TOC -->
			<xsl:apply-templates select="toc"/>

			<!--end publication-->
		</xsl:element>
	</xsl:template>
	
	<xsl:template name="createPublishinginfo">
		<xsl:apply-templates select="publisher"/>
		<xsl:apply-templates select="publisheradd"/>
		<xsl:apply-templates select="editiondescription"/>
	</xsl:template>
	
	<xsl:template match="corporatebody">
		<xsl:call-template name="createPublCreatorOrga"/>
	</xsl:template>
	<xsl:template match="issuecorporatebody">
		<xsl:call-template name="createSourceCreatorOrga"/>
	</xsl:template>
	<xsl:template match="seriescorporatebody">
		<xsl:call-template name="createSourceCreatorOrga"/>
	</xsl:template>
	<xsl:template match="bookcorporatebody">
		<xsl:call-template name="createSourceCreatorOrga"/>
	</xsl:template>
	
	<xsl:template name="createPublCreatorOrga">
		<xsl:element name="publ:creator">
			<xsl:attribute name="role" select="'editor'"/>
			<xsl:element name="e:organization">
				<xsl:element name="e:organization-name">
					<xsl:value-of select="."/>
				</xsl:element>
				<e:identifier>
					<xsl:value-of select="$organizational-units/ou[@name = .]/@id"/>
				</e:identifier>
			</xsl:element>
		</xsl:element>
	</xsl:template>
	
	<xsl:template name="createSourceCreatorOrga">
		<xsl:element name="e:creator">
			<xsl:attribute name="role" select="'editor'"/>
			<xsl:element name="e:organization">
				<xsl:element name="e:organization-name">
					<xsl:value-of select="."/>
				</xsl:element>
				<e:identifier>
					<xsl:value-of select="$organizational-units/ou[@name = .]/@id"/>
				</e:identifier>
			</xsl:element>
		</xsl:element>
	</xsl:template>
	
	<xsl:template name="createIdentifier">
		<!-- eDoc ID -->
		<xsl:element name="dc:identifier">
			<xsl:attribute name="xsi:type" select="'eidt:EDOC'"/>
			<xsl:value-of select="../../@id"/>
		</xsl:element>
		<xsl:for-each select="../identifiers/identifier">
			<xsl:call-template name="createOtherIDs"/>
		</xsl:for-each>
	</xsl:template>
	
	<xsl:template name="createOtherIDs">
		<xsl:element name="dc:identifier">
			<xsl:choose>
				<xsl:when test="@type='doi'">
					<xsl:attribute name="xsi:type" select="'eidt:DOI'"/>
					<xsl:value-of select="."/>
				</xsl:when>
				<xsl:when test="@type='issn'">
					<xsl:attribute name="xsi:type" select="'eidt:ISSN'"/>
					<xsl:value-of select="."/>
				</xsl:when>
				<xsl:when test="@type='isbn'">
					<xsl:attribute name="xsi:type" select="'eidt:ISBN'"/>
					<xsl:value-of select="."/>
				</xsl:when>
				<xsl:when test="@type='uri'">
					<xsl:attribute name="xsi:type" select="'eidt:URI'"/>
					<xsl:value-of select="."/>
				</xsl:when>
				<xsl:when test="@type='isi'">
					<xsl:attribute name="xsi:type" select="'eidt:ISI'"/>
					<xsl:value-of select="."/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:attribute name="xsi:type" select="'eidt:OTHER'"/>
					<xsl:value-of select="."/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:element>
	</xsl:template>
	
<!-- ***********************************************SOURCE TEMPLATES ***************************************************************** -->	


	<!-- JOURNAL TEMPLATE -->
	<xsl:template name="createJournal">
		<!-- TITLE -->
		<xsl:if test="journaltitle">
			<xsl:attribute name="type" select="'journal'"/>
			<xsl:element name="dc:title">
				<xsl:value-of select="journaltitle"/>
			</xsl:element>
		</xsl:if>		
		<!-- ALTERNATIVE TITLE -->
		<xsl:apply-templates select="journalabbreviation"/>
		
		<!-- VOLUME -->
		<xsl:apply-templates select="volume"/>
		
		<!-- ISSUE -->
		<xsl:apply-templates select="issuenr"/>
		<!-- START_PAGE -->
		<xsl:apply-templates select="spage"/>
		<!-- END-PAGE -->
		<xsl:apply-templates select="epage"/>
		
		<xsl:if test="not(exists(issuetitle))">
			<!-- SEQUENCE_NR -->
			<xsl:apply-templates select="artnum"/>
		</xsl:if>

		<!-- PUBLISHININFO -->
		<xsl:if test="not(exists(issuetitle)) and (exists(publisher) or exists(editiondescription))">
			<xsl:element name="e:publishing-info">
				<xsl:call-template name="createPublishinginfo"/>
			</xsl:element>
		</xsl:if>
	</xsl:template>
	
	<!-- ISSUE TEMPLATE -->
	<xsl:template name="createIssue">
		<!-- TITLE -->
		<xsl:if test="issuetitle">
			<xsl:attribute name="type" select="'issue'"/>
			<xsl:element name="dc:title">
				<xsl:value-of select="issuetitle"/>
			</xsl:element>
		</xsl:if>
		<!-- CREATOR -->
		<xsl:for-each select="creators/creator[@type = 'issuecontributorfn']">
			<xsl:element name="e:creator">
				<xsl:call-template name="createCreator"/>
			</xsl:element>
		</xsl:for-each>
		<xsl:apply-templates select="issuecorporatebody"/>

		<!-- SEQUENCE_NR -->
		<xsl:apply-templates select="artnum"/>
	</xsl:template>
	
	<!-- BOOK TEMPLATE -->
	<xsl:template name="createBook">
		<!-- TITLE -->
		<xsl:if test="booktitle">
			<xsl:attribute name="type" select="'book'"/>
			<xsl:element name="dc:title">
				<xsl:value-of select="booktitle"/>
			</xsl:element>
		</xsl:if>
		<!-- CREATOR -->
		<xsl:for-each select="creators/creator[@type='bookcontributorfn' or @type='bookcreatorfn']">
			<xsl:element name="e:creator">
				<xsl:call-template name="createCreator"/>
			</xsl:element>
		</xsl:for-each>
		<xsl:apply-templates select="bookcorporatebody"/>
		<!-- VOLUME -->
		<xsl:if test="not(exists(titleofseries))">
			<xsl:apply-templates select="volume"/>
		</xsl:if>
		<!-- START_PAGE -->
		<xsl:apply-templates select="spage"/>
		<!-- END-PAGE -->
		<xsl:apply-templates select="epage"/>
		<!-- SEQUENCE_NR -->
		<xsl:apply-templates select="artnum"/>
		<!--NUMBER OF PAGES -->
		<xsl:if test="phydesc and exists(booktitle)">
			<xsl:call-template name="phydescSource"/>
		</xsl:if>
		<xsl:if test="exists(publisher) or exists(editiondescription)">
			<xsl:element name="e:publishing-info">
				<xsl:call-template name="createPublishinginfo"/>
			</xsl:element>
		</xsl:if>
	</xsl:template>
	
	<xsl:template name="phydescPubl">
		<xsl:element name="publ:total-number-of-pages">
			<xsl:value-of select="phydesc"/>
		</xsl:element>
	</xsl:template>
	
	<xsl:template name="phydescSource">
		<xsl:element name="e:sequence-number">
			<xsl:value-of select="phydesc"/>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="phydesc">
		<xsl:element name="publ:total-number-of-pages">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="publisheradd">
		<xsl:element name="e:place">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="editiondescription">
		<xsl:element name="e:edition">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	
	<!-- SERIES TEMPLATE -->
	<xsl:template name="createSeries">
		<!-- TITLE -->
		<xsl:if test="exists(titleofseries)">
			<xsl:attribute name="type" select="'series'"/>
			<xsl:element name="dc:title">
				<xsl:value-of select="titleofseries"/>
			</xsl:element>
		</xsl:if>		
		<!-- CREATOR -->
		<xsl:for-each select="creators/creator[@type = 'seriescontributorfn']">
			<xsl:element name="e:creator">
				<xsl:call-template name="createCreator"/>
			</xsl:element>
		</xsl:for-each>
		<xsl:apply-templates select="seriescorporatebody"/>		
		<!-- VOLUME -->
		<xsl:apply-templates select="volume"/>
	</xsl:template>
	
	<!-- PROCEEDINGS TEMPLATE -->
	<xsl:template name="createProceedings">
		<!-- TITLE -->
		<xsl:if test="titleofproceedings">
			<xsl:attribute name="type" select="'proceedings'"/>
			<xsl:element name="dc:title">
				<xsl:value-of select="titleofproceedings"/>
			</xsl:element>
			<xsl:if test="editiondescrition">
				<xsl:element name="e:volume">
					<xsl:value-of select="editiondescription"/>
				</xsl:element>
			</xsl:if>
			<xsl:if test="issuenr">
				<xsl:element name="e:issue">
					<xsl:apply-templates select="issuenr"/>
				</xsl:element>
			</xsl:if>
			<xsl:if test="phydesc">
				<xsl:call-template name="phydescSource"/>
			</xsl:if>
			<xsl:if test="exists(publisher) or exists(editiondescription)">
				<xsl:element name="e:publishing-info">
					<xsl:call-template name="createPublishinginfo"/>
				</xsl:element>
			</xsl:if>
		</xsl:if>
		<!-- CREATOR -->
		<xsl:for-each select="creators/creator[@type = 'proceedingscontributorfn']">
			<xsl:element name="e:creator">
				<xsl:call-template name="createCreator"/>
			</xsl:element>
		</xsl:for-each>
		<!-- START_PAGE -->
		<xsl:apply-templates select="spage"/>
		<!-- END-PAGE -->
		<xsl:apply-templates select="epage"/>
	
	</xsl:template>
	
	
	<xsl:template match="volume">
		<e:volume>
			<xsl:value-of select="."/>
		</e:volume>
	</xsl:template>
	
	
	<xsl:template name="createCreator">
		<!-- CREATOR ROLE -->
		<xsl:choose>
			<xsl:when test="@role='advisor'">
				<xsl:attribute name="role" select="'advisor'"/>
			</xsl:when>
			<xsl:when test="@role='artist'">
				<xsl:attribute name="role" select="'artist'"/>
			</xsl:when>
			<xsl:when test="@role='author'">
				<xsl:attribute name="role" select="'author'"/>
			</xsl:when>
			<xsl:when test="@role='contributor'">
				<xsl:attribute name="role" select="'contributor'"/>
			</xsl:when>
			<xsl:when test="@role='editor'">
				<xsl:attribute name="role" select="'editor'"/>
			</xsl:when>
			<xsl:when test="@role='painter'">
				<xsl:attribute name="role" select="'painter'"/>
			</xsl:when>
			<xsl:when test="@role='referee'">
				<xsl:attribute name="role" select="'referee'"/>
			</xsl:when>
			<xsl:when test="@role='translator'">
				<xsl:attribute name="role" select="'translator'"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="error(QName('http://www.escidoc.de', 'err:CreatorRoleNotMapped' ), concat(@role, ' is not mapped to an eSciDoc creator role'))"/>
			</xsl:otherwise>
		</xsl:choose>
		<!-- CREATOR -->
		<xsl:variable name="creatornfamily" select="creatornfamily"/>
		<xsl:variable name="creatorngiven" select="creatorngiven"/>
		<xsl:variable name="creatorngivenNew">
			<xsl:choose>
				<xsl:when test="$removeSpacesInInitials">
					<xsl:value-of select="replace(creatorngiven, '([A-Z][a-z]*\.) ([A-Z][a-z]*\.) ?([A-Z][a-z]*\.)? ?([A-Z][a-z]*\.)? ?([A-Z][a-z]*\.)? ?([A-Z][a-z]*\.)? ?([A-Z][a-z]*\.)? ?([A-Z][a-z]*\.)?', '$1$2$3$4$5$6$7$8')"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="creatorngiven"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="creatoriniNew">
			<xsl:choose>
				<xsl:when test="$removeSpacesInInitials">
					<xsl:value-of select="replace(creatorini, '([A-Z][a-z]*\.) ([A-Z][a-z]*\.) ?([A-Z][a-z]*\.)? ?([A-Z][a-z]*\.)? ?([A-Z][a-z]*\.)? ?([A-Z][a-z]*\.)? ?([A-Z][a-z]*\.)? ?([A-Z][a-z]*\.)?', '$1$2$3$4$5$6$7$8')"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="creatorini"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="@creatorType='individual'">
			
				<xsl:variable name="coneCreator">
					<xsl:copy-of select="Util:queryCone('persons', concat($creatornfamily, ', ', $creatorngiven))"/>
				</xsl:variable>
				
				<xsl:variable name="multiplePersonsFound" select="exists($coneCreator/cone/rdf:RDF/rdf:Description[@rdf:about != preceding-sibling::attribute/@rdf:about])"/>
			
				<xsl:choose>
					<xsl:when test="$multiplePersonsFound">
						<xsl:value-of select="error(QName('http://www.escidoc.de', 'err:MultipleCreatorsFound' ), concat('There is more than one CoNE entry matching --', concat($creatornfamily, ', ', creatorngiven), '--'))"/>
					</xsl:when>
					<xsl:when test="not(exists($coneCreator/cone/rdf:RDF/rdf:Description))">
						<xsl:element name="e:person">
							<xsl:element name="e:complete-name">
								<xsl:value-of select="concat($creatorngivenNew, ' ', creatornfamily)"/>
							</xsl:element>
							<xsl:element name="e:family-name">
								<xsl:value-of select="creatornfamily"/>
							</xsl:element>
							<xsl:choose>
								<xsl:when test="exists(creatorngiven) and not(creatorngiven='')">
									<xsl:element name="e:given-name">
										<xsl:value-of select="$creatorngivenNew"/>
									</xsl:element>
								</xsl:when>
								<xsl:otherwise>
									<xsl:element name="e:given-name">
										<xsl:value-of select="$creatoriniNew"/>
									</xsl:element>
								</xsl:otherwise>
							</xsl:choose>
							<xsl:choose>
								<xsl:when test="@internextern='mpg'">
									<xsl:for-each select="../../../docaff/affiliation">
										<xsl:element name="e:organization">
											<xsl:element name="e:organization-name">
												<xsl:choose>
													<xsl:when test="mpgsunit">
														<xsl:value-of select="escidocFunctions:ou-name(mpgsunit)"/>
													</xsl:when>
													<xsl:otherwise>
														<xsl:value-of select="escidocFunctions:ou-name(mpgunit)"/>
													</xsl:otherwise>
												</xsl:choose>
											</xsl:element>
											<e:identifier>
												<xsl:choose>
													<xsl:when test="mpgsunit">
														<xsl:value-of select="escidocFunctions:ou-id(mpgsunit)"/>
													</xsl:when>
													<xsl:otherwise>
														<xsl:value-of select="escidocFunctions:ou-id(mpgunit)"/>
													</xsl:otherwise>
												</xsl:choose>
											</e:identifier>
										</xsl:element>
									</xsl:for-each>
									
									<xsl:variable name="collection" select="../../../docaff/collection"/>
									
									<xsl:if test="$collection-mapping/mapping[lower-case(edoc-collection) = lower-case($collection)] and not(../../../docaff/affiliation/*[lower-case(.) = lower-case($collection)])">
										<e:organization>
											<e:organization-name>
												<xsl:value-of select="escidocFunctions:ou-name($collection-mapping/mapping[lower-case(edoc-collection) = lower-case($collection)]/escidoc-ou)"/>
											</e:organization-name>
											<e:identifier>
												<xsl:value-of select="escidocFunctions:ou-id($collection-mapping/mapping[lower-case(edoc-collection) = lower-case($collection)]/escidoc-ou)"/>
											</e:identifier>
										</e:organization>
									</xsl:if>
								
								</xsl:when>
								<xsl:when test="@internextern='unknown' and not(../creator[@internextern = 'mpg']) and ../../../docaff/affiliation and not(../../../docaff_external)">
									<xsl:for-each select="../../../docaff/affiliation">
										<xsl:element name="e:organization">
											<xsl:element name="e:organization-name">
												<xsl:choose>
													<xsl:when test="mpgsunit">
														<xsl:value-of select="escidocFunctions:ou-name(mpgsunit)"/>
													</xsl:when>
													<xsl:otherwise>
														<xsl:value-of select="escidocFunctions:ou-name(mpgunit)"/>
													</xsl:otherwise>
												</xsl:choose>
											</xsl:element>
											<e:identifier>
												<xsl:choose>
													<xsl:when test="mpgsunit">
														<xsl:value-of select="escidocFunctions:ou-id(mpgsunit)"/>
													</xsl:when>
													<xsl:otherwise>
														<xsl:value-of select="escidocFunctions:ou-id(mpgunit)"/>
													</xsl:otherwise>
												</xsl:choose>
											</e:identifier>
										</xsl:element>
									</xsl:for-each>
									
									<xsl:variable name="collection" select="../../../docaff/collection"/>
									
									<xsl:if test="$collection-mapping/mapping[lower-case(edoc-collection) = lower-case($collection)] and not(../../../docaff/affiliation/*[lower-case(.) = lower-case($collection)])">
										<e:organization>
											<e:organization-name>
												<xsl:value-of select="escidocFunctions:ou-name($collection-mapping/mapping[lower-case(edoc-collection) = lower-case($collection)]/escidoc-ou)"/>
											</e:organization-name>
											<e:identifier>
												<xsl:value-of select="escidocFunctions:ou-id($collection-mapping/mapping[lower-case(edoc-collection) = lower-case($collection)]/escidoc-ou)"/>
											</e:identifier>
										</e:organization>
									</xsl:if>
								
								</xsl:when>
								<xsl:otherwise>
									<xsl:if test="../../../docaff/docaff_external">
										<e:organization>
											<e:organization-name>
												<xsl:value-of select="escidocFunctions:ou-name(../../../docaff/docaff_external)"/>
											</e:organization-name>
											<e:identifier>
												<xsl:value-of select="escidocFunctions:ou-id(../../../docaff/docaff_external)"/>
											</e:identifier>
										</e:organization>
									</xsl:if>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:element>
					</xsl:when>
					<xsl:otherwise>
						<e:person>
							<e:complete-name>
								<xsl:value-of select="$creatorngivenNew"/>
								<xsl:text> </xsl:text>
								<xsl:value-of select="$creatornfamily"/>
							</e:complete-name>
							<e:family-name>
								<xsl:value-of select="$creatornfamily"/>
							</e:family-name>
							<e:given-name>
								<xsl:value-of select="$creatorngivenNew"/>
							</e:given-name>
							
							<e:identifier xsi:type="eidt:CONE">
								<xsl:value-of select="$coneCreator/cone/rdf:RDF/rdf:Description[1]/@rdf:about"/>
							</e:identifier>

							<xsl:for-each select="$coneCreator/cone/rdf:RDF/rdf:Description/escidoc:position">
								<e:organization>
									<e:organization-name>
										<xsl:value-of select="rdf:Description/escidoc:organization"/>
									</e:organization-name>
									<e:identifier>
										<xsl:value-of select="rdf:Description/dc:identifier"/>
									</e:identifier>
								</e:organization>
							</xsl:for-each>

						</e:person>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<xsl:element name="e:organization">
					<xsl:element name="e:organization-name">
						<xsl:value-of select="creatornfamily"/>
					</xsl:element>
				</xsl:element>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template match="title">
		<xsl:element name="dc:title">
			<xsl:value-of select="title"/>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="journaltitle">
		<xsl:attribute name="type" select="'journal'"/>
	</xsl:template>
	<xsl:template match="booktitle">
		<xsl:attribute name="type" select="'book'"/>
	</xsl:template>
	<xsl:template match="issuetitle">
		<xsl:attribute name="type" select="'issue'"/>
	</xsl:template>
	<xsl:template match="titleofseries">
		<xsl:attribute name="type" select="'series'"/>
	</xsl:template>
	<xsl:template match="titleofproceedings">
		<xsl:attribute name="type" select="'proceedings'"/>
	</xsl:template>
	
		
	
	
	<!-- REVIEW-METHOD TEMPLATE -->
	<xsl:template match="refereed">
		<xsl:choose>
			<xsl:when test="../genre='Article' and exists(../journaltitle)">
				<xsl:element name="publ:review-method">
					<xsl:value-of select="'peer'"/>
				</xsl:element>
			</xsl:when>
			<xsl:when test="refereed='joureview'">
				<xsl:element name="publ:review-method">
					<xsl:value-of select="'peer'"/>
				</xsl:element>
			</xsl:when>
			<xsl:when test="refereed='notrev'">
				<xsl:element name="publ:review-method">
					<xsl:value-of select="'no review'"/>
				</xsl:element>
			</xsl:when>
			<xsl:when test="refereed='intrev'">
				<xsl:element name="publ:review-method">
					<xsl:value-of select="'internal'"/>
				</xsl:element>
			</xsl:when>
			<xsl:otherwise></xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	
	
	<xsl:template match="issuecontributorfn">
		<xsl:call-template name="parseContributor">
			<xsl:with-param name="role" select="'editor'"/>
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="proceedingscontributorfn">
		<xsl:call-template name="parseContributor">
			<xsl:with-param name="role" select="'editor'"/>
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="seriescontributorfn">
		<xsl:call-template name="parseContributor">
			<xsl:with-param name="role" select="'editor'"/>
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="bookcontributorfn">
		<xsl:call-template name="parseContributor">
			<xsl:with-param name="role" select="'editor'"/>
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="bookcreatorfn">
		<xsl:call-template name="parseContributor">
			<xsl:with-param name="role" select="'author'"/>
		</xsl:call-template>
	</xsl:template>
	<xsl:template name="parseContributor">
		<xsl:param name="role"/>
		
		<xsl:element name="creatorstring">
			<xsl:attribute name="role" select="$role"/>
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="journalabbreviation">
		<xsl:element name="dcterms:alternative">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<!-- EVENT TEMPLATE -->
	<xsl:template name="createEvent">
		<xsl:element name="publ:event">
			<xsl:element name="dc:title">
				<xsl:value-of select="nameofevent"/>
			</xsl:element>
			<xsl:element name="e:start-date">
				<xsl:value-of select="dateofevent"/>
			</xsl:element>
			<xsl:element name="e:end-date">
				<xsl:value-of select="enddateofevent"/>
			</xsl:element>
			<xsl:element name="e:place">
				<xsl:value-of select="placeofevent"/>
			</xsl:element>
			<xsl:apply-templates select="invitationStatus[.='invited']"/>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="invitationStatus[.='invited']">
		<xsl:element name="e:invitation-status">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="artnum">
		<e:sequence-number>
			<xsl:value-of select="."/>
		</e:sequence-number>
	</xsl:template>
	<xsl:template match="spage">
		<e:start-page>
			<xsl:value-of select="."/>
		</e:start-page>
	</xsl:template>
	<xsl:template match="epage">
		<e:end-page>
			<xsl:value-of select="."/>
		</e:end-page>
	</xsl:template>
	<xsl:template match="issuenr">
		<e:issue>
			<xsl:value-of select="."/>
		</e:issue>
	</xsl:template>
	<xsl:template match="toc">
		<xsl:element name="dcterms:tableOfContents">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="discipline">
		<xsl:element name="dc:subject">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="keywords">
		<xsl:element name="dc:subject">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="abstract">
		<xsl:element name="dcterms:abstract">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="datepublished">
		<xsl:element name="dcterms:issued">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="datemodified">
		<xsl:element name="dcterms:modified">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="dateaccepted">
		<xsl:element name="dcterms:dateAccepted">
			<xsl:value-of select="."/>
		</xsl:element>
		<xsl:if test="../genre='PhD-Thesis'">
			<xsl:element name="dcterms:issued">
				<xsl:value-of select="."/>
			</xsl:element>
		</xsl:if>
	</xsl:template>
	<xsl:template match="datesubmitted">
		<xsl:element name="dcterms:dateSubmitted">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="titlealt">
		<xsl:element name="dcterms:alternative">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="publisher">
		<xsl:element name="dc:publisher">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="language">
		<xsl:variable name="coneLanguage">
			<xsl:copy-of select="Util:queryCone('languages', .)"/>
		</xsl:variable>
		<xsl:element name="dc:language">
			<xsl:value-of select="$coneLanguage/cone/rdf:RDF/rdf:Description[1]/dc:identifier"/>
		</xsl:element>
	</xsl:template>
	
	<xsl:template name="validation">
		<xsl:variable name="collectionsWithoutOuMatch">
			<xsl:copy-of select="/edoc/record/docaff/collection[not($collection-mapping/mapping/edoc-collection = .)]"/>
		</xsl:variable>
		<xsl:variable name="recordsWithoutOuMatch">
			<xsl:value-of select="/edoc/record[docaff/collection[not($collection-mapping/mapping/edoc-collection = .)]]/@id"/>
		</xsl:variable>
		<xsl:if test="$collectionsWithoutOuMatch != ''">
			<xsl:value-of select="error(QName('http://www.escidoc.de', 'err:UnmatchedCollection' ), concat('Collections [', $collectionsWithoutOuMatch, '] do not match any eSciDoc ou. Records: ', $recordsWithoutOuMatch))"/>
		</xsl:if>
		<xsl:variable name="affiliationsWithoutOuMatch">
			<xsl:copy-of select="/edoc/record/docaff/affiliation/mpgsunit[not($collection-mapping/mapping/edoc-collection = .)]"/>
		</xsl:variable>
		<xsl:variable name="recordsWithoutOuMatch2">
			<xsl:value-of select="/edoc/record[docaff/affiliation/mpgsunit[not($collection-mapping/mapping/edoc-collection = .)]]/@id"/>
		</xsl:variable>
		<xsl:if test="$affiliationsWithoutOuMatch != ''">
			<xsl:value-of select="error(QName('http://www.escidoc.de', 'err:UnmatchedCollection' ), concat('Collections [', $affiliationsWithoutOuMatch, '] do not match any eSciDoc ou. Records: ', $recordsWithoutOuMatch2))"/>
		</xsl:if>
		<xsl:variable name="mappingWithoutOuMatch">
			<xsl:copy-of select="$collection-mapping/mapping/escidoc-ou[not(. = $organizational-units//ou/@name)]"/>
		</xsl:variable>
		<xsl:if test="$mappingWithoutOuMatch != ''">
			<xsl:value-of select="error(QName('http://www.escidoc.de', 'err:UnmatchedCollection' ), concat('OU mappings [', $mappingWithoutOuMatch, '] do not match any eSciDoc ou.'))"/>
		</xsl:if>
		<xsl:variable name="authorOuMappingWithoutOuMatch">
			<xsl:value-of select="$authors/authors/author/departments/department[not(. = $organizational-units//ou/@name)]"/>
		</xsl:variable>
		<xsl:if test="$authorOuMappingWithoutOuMatch != ''">
			<xsl:value-of select="error(QName('http://www.escidoc.de', 'err:UnmatchedAuthorOU' ), concat('OU mappings [', $authorOuMappingWithoutOuMatch, '] do not match any eSciDoc ou.'))"/>
		</xsl:if>
		<!-- Uncomment this to find out which authors are not mapped. -->		<!-- <xsl:variable name="authorsWithoutMatch">			<xsl:for-each select="//creator">				<xsl:sort select="creatornfamily"/>				<xsl:sort select="creatorngiven"/>				<xsl:variable name="creatornfamily" select="creatornfamily"/>				<xsl:variable name="creatorngiven" select="creatorngiven"/>				<xsl:if test="not($authors/authors/author[aliases/alias[lower-case(familyname) = lower-case($creatornfamily) and lower-case(givenname) = lower-case($creatorngiven)]])">					<xsl:value-of select="creatornfamily"/>, <xsl:value-of select="creatorngiven"/><xsl:text></xsl:text>				</xsl:if>			</xsl:for-each>		</xsl:variable>				<xsl:if test="$authorsWithoutMatch != ''">			<xsl:value-of select="error(QName('http://www.escidoc.de', 'err:UnmatchedAuthor' ), concat('Authors ', $authorsWithoutMatch, ' do not match any mapped author.'))"/>		</xsl:if> -->
	</xsl:template>
</xsl:stylesheet>
