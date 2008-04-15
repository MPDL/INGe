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
	
	
	Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
	für wissenschaftlich-technische Information mbH and Max-Planck-
	Gesellschaft zur Förderung der Wissenschaft e.V.
	All rights reserved. Use is subject to license terms.
-->
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:escidocAdminDescriptor="http://www.escidoc.de/schemas/admindescriptor/0.3"
	xmlns:escidocContext="http://www.escidoc.de/schemas/context/0.3"
	xmlns:escidocContextList="http://www.escidoc.de/schemas/contextlist/0.3"
	xmlns:escidocComponents="http://www.escidoc.de/schemas/components/0.3"
	xmlns:escidocItem="http://www.escidoc.de/schemas/item/0.3"
	xmlns:escidocItemList="http://www.escidoc.de/schemas/itemlist/0.3"
	xmlns:escidocMetadataRecords="http://www.escidoc.de/schemas/metadatarecords/0.3"
	xmlns:escidocRelations="http://www.escidoc.de/schemas/relations/0.3"
	xmlns:escidocResources="http://www.escidoc.de/schemas/resources/0.2"
	xmlns:escidocSearchResult="http://www.escidoc.de/schemas/searchresult/0.3"
	xmlns:xlink="http://www.w3.org/1999/xlink"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:oaipmh="http://www.openarchives.org/OAI/2.0/"
	xmlns:arxiv="http://arxiv.org/OAI/arXiv/"
	xsi:schemaLocation="http://www.escidoc.de/schemas/item/0.3 soap/item/0.3/item.xsd">

	<xsl:output method="xml" indent="yes" />

	<xsl:include href="msc.xsl"/>

	<xsl:template match="/">

		<escidocItem:item>
			<escidocItem:properties>
				<escidocItem:content-model objid="escidoc:persistent4" />
				<escidocItem:content-model-specific/>
			</escidocItem:properties>
			<escidocMetadataRecords:md-records>
				<escidocMetadataRecords:md-record name="escidoc">
					<escidocMetadataProfile:publication xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/"
						xmlns:eidt="http://escidoc.mpg.de/metadataprofile/schema/0.1/idtypes" xmlns:escidoc="http://escidoc.mpg.de/metadataprofile/schema/0.1/types"
						xmlns:escidocMetadataProfile="http://escidoc.mpg.de/metadataprofile/schema/0.1/"
						xmlns:publication="http://escidoc.mpg.de/metadataprofile/schema/0.1/publication" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" type="article">
						<xsl:for-each select="oaipmh:OAI-PMH/oaipmh:GetRecord/oaipmh:record/oaipmh:metadata/arxiv:arXiv/arxiv:authors/arxiv:author">
							<publication:creator role="author">
								<escidoc:person>
									<escidoc:family-name><xsl:value-of select="arxiv:keyname"/></escidoc:family-name>
									<escidoc:given-name><xsl:value-of select="arxiv:forenames"/></escidoc:given-name>
									<escidoc:organization>
										<escidoc:organization-name><xsl:value-of select="arxiv:affiliation"/></escidoc:organization-name>
										<escidoc:address />
									</escidoc:organization>
								</escidoc:person>
							</publication:creator>
						</xsl:for-each>
						<dc:title xml:lang="en"><xsl:value-of select="oaipmh:OAI-PMH/oaipmh:GetRecord/oaipmh:record/oaipmh:metadata/arxiv:arXiv/arxiv:title"/></dc:title>
						<dc:identifier xsi:type="eidt:OTHER"><xsl:value-of select="oaipmh:OAI-PMH/oaipmh:GetRecord/oaipmh:record/oaipmh:header/oaipmh:identifier"/></dc:identifier>
						<xsl:if test="oaipmh:OAI-PMH/oaipmh:GetRecord/oaipmh:record/oaipmh:metadata/arxiv:arXiv/arxiv:doi != ''">
							<dc:identifier xsi:type="eidt:DOI"><xsl:value-of select="oaipmh:OAI-PMH/oaipmh:GetRecord/oaipmh:record/oaipmh:metadata/arxiv:arXiv/arxiv:doi"/></dc:identifier>
						</xsl:if>
						<publication:publishing-info>
							<dc:publisher></dc:publisher>
							<escidoc:place></escidoc:place>
							<escidoc:edition></escidoc:edition>
						</publication:publishing-info>
						<dcterms:created xsi:type="dcterms:W3CDTF"></dcterms:created>
						<dcterms:modified xsi:type="dcterms:W3CDTF"></dcterms:modified>
						<dcterms:dateSubmitted xsi:type="dcterms:W3CDTF"></dcterms:dateSubmitted>
						<dcterms:dateAccepted xsi:type="dcterms:W3CDTF"></dcterms:dateAccepted>
						<!--  -->
						<publication:published-online xsi:type="dcterms:W3CDTF"><xsl:value-of select="OAI-PMH/GetRecord/record/datestamp"/></publication:published-online>
						<dcterms:issued xsi:type="dcterms:W3CDTF"></dcterms:issued>
						<publication:source>
							<xsl:choose>
								<!-- Journal-Ref -->
								<xsl:when test="oaipmh:OAI-PMH/oaipmh:GetRecord/oaipmh:record/oaipmh:metadata/arxiv:arXiv/arxiv:journal-ref != ''">
									<xsl:attribute name="type">JOURNAL</xsl:attribute>
									<dc:title xml:lang="en"><xsl:value-of select="oaipmh:OAI-PMH/oaipmh:GetRecord/oaipmh:record/oaipmh:metadata/arxiv:arXiv/arxiv:journal-ref"/></dc:title>
									<escidoc:creator role="author">
										<escidoc:person>
											<escidoc:family-name></escidoc:family-name>
											<escidoc:given-name></escidoc:given-name>
											<escidoc:organization>
												<escidoc:organization-name></escidoc:organization-name>
												<escidoc:address></escidoc:address>
											</escidoc:organization>
										</escidoc:person>
									</escidoc:creator>
									<escidoc:volume></escidoc:volume>
									<escidoc:issue></escidoc:issue>
									<escidoc:start-page></escidoc:start-page>
									<escidoc:end-page></escidoc:end-page>
									
									<!-- Report-No -->
									<escidoc:sequence-number><xsl:value-of select="oaipmh:OAI-PMH/oaipmh:GetRecord/oaipmh:record/oaipmh:metadata/arxiv:arXiv/arxiv:report-no"/></escidoc:sequence-number>
									<escidoc:publishing-info>
										<dc:publisher></dc:publisher>
										<escidoc:place></escidoc:place>
										<escidoc:edition></escidoc:edition>
									</escidoc:publishing-info>
								</xsl:when>
								<xsl:otherwise>
									<xsl:attribute name="type"></xsl:attribute>
									<dc:title xml:lang="en"></dc:title>
									<escidoc:creator role="author">
										<escidoc:person>
											<escidoc:family-name></escidoc:family-name>
											<escidoc:given-name></escidoc:given-name>
											<escidoc:organization>
												<escidoc:organization-name></escidoc:organization-name>
												<escidoc:address></escidoc:address>
											</escidoc:organization>
										</escidoc:person>
									</escidoc:creator>
									<escidoc:volume></escidoc:volume>
									<escidoc:issue></escidoc:issue>
									<escidoc:start-page></escidoc:start-page>
									<escidoc:end-page></escidoc:end-page>
									<escidoc:sequence-number></escidoc:sequence-number>
									<escidoc:publishing-info>
										<dc:publisher></dc:publisher>
										<escidoc:place></escidoc:place>
										<escidoc:edition></escidoc:edition>
									</escidoc:publishing-info>
								</xsl:otherwise>
							</xsl:choose>
						</publication:source>
						<publication:event>
							<dc:title xml:lang=""></dc:title>
							<escidoc:start-date xsi:type="dcterms:W3CDTF"></escidoc:start-date>
							<escidoc:end-date xsi:type="dcterms:W3CDTF"></escidoc:end-date>
							<escidoc:place></escidoc:place>
						</publication:event>
						<publication:total-number-of-pages><xsl:value-of select="oaipmh:OAI-PMH/oaipmh:GetRecord/oaipmh:record/oaipmh:metadata/arxiv:arXiv/arxiv:comments"/></publication:total-number-of-pages>
						<dcterms:abstract xml:lang="en"><xsl:value-of select="oaipmh:OAI-PMH/oaipmh:GetRecord/oaipmh:record/oaipmh:metadata/arxiv:arXiv/arxiv:abstract"/></dcterms:abstract>
						<xsl:if test="oaipmh:OAI-PMH/oaipmh:GetRecord/oaipmh:record/oaipmh:metadata/arxiv:arXiv/arxiv:msc-class != ''">
							<dc:subject xml:lang="en">
								<xsl:call-template name="msc">
									<xsl:with-param name="code" select="oaipmh:OAI-PMH/oaipmh:GetRecord/oaipmh:record/oaipmh:metadata/arxiv:arXiv/arxiv:msc-class"/>
								</xsl:call-template>
							</dc:subject>
						</xsl:if>
						<xsl:if test="oaipmh:OAI-PMH/oaipmh:GetRecord/oaipmh:record/oaipmh:metadata/arxiv:arXiv/arxiv:categories != ''">
							<dc:subject><xsl:value-of select="oaipmh:OAI-PMH/oaipmh:GetRecord/oaipmh:record/oaipmh:metadata/arxiv:arXiv/arxiv:categories"/></dc:subject>
						</xsl:if>
						<dcterms:tableOfContents xml:lang=""></dcterms:tableOfContents>
						<publication:location></publication:location>
					</escidocMetadataProfile:publication>
				</escidocMetadataRecords:md-record>
			</escidocMetadataRecords:md-records>
<!-- 	
			<escidocComponents:components>
				<escidocComponents:component>
					<escidocComponents:properties>
						<escidocComponents:description>This is a test</escidocComponents:description>
						<escidocComponents:valid-status>valid</escidocComponents:valid-status>
						<escidocComponents:visibility>public</escidocComponents:visibility>
						<escidocComponents:content-category>publisher-version</escidocComponents:content-category>
						<escidocComponents:file-name>dummy.txt</escidocComponents:file-name>
						<escidocComponents:mime-type>text/plain</escidocComponents:mime-type>
						<escidocComponents:file-size>249</escidocComponents:file-size>
					</escidocComponents:properties>
					<escidocComponents:content xlink:href="http://192.129.1.95:8080/st/staging-file/escidoctoken:1cf09ec0-0712-11dd-abd2-95ae64752efb" />
				</escidocComponents:component>
			</escidocComponents:components>
 -->
		</escidocItem:item>

	</xsl:template>

</xsl:stylesheet>