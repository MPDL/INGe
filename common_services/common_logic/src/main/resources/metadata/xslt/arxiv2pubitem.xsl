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
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:escidocContext="${xsd.soap.context.context}" xmlns:escidocContextList="${xsd.soap.context.contextlist}"
	xmlns:escidocComponents="${xsd.soap.item.components}" xmlns:escidocItem="${xsd.soap.item.item}"
	xmlns:escidocItemList="${xsd.soap.item.itemlist}" xmlns:escidocMetadataRecords="${xsd.soap.common.mdrecords}"
	xmlns:escidocRelations="${xsd.soap.common.relations}"
	xmlns:escidocSearchResult="${xsd.soap.searchresult.searchresult}" xmlns:xlink="http://www.w3.org/1999/xlink"
	xmlns:prop="${xsd.soap.common.prop}"
	xmlns:srel="${xsd.soap.common.srel}"
	xmlns:version="${xsd.soap.common.version}"
	xmlns:release="${xsd.soap.common.release}"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:oaipmh="http://www.openarchives.org/OAI/2.0/" xmlns:arxiv="http://arxiv.org/OAI/arXiv/">

	<xsl:output method="xml" indent="yes" />

	<xsl:include href="msc.xsl" />

	<xsl:param name="external_organization_id" />

	<xsl:template match="/">

		<xsl:if test="oaipmh:OAI-PMH/oaipmh:error">
			<xsl:value-of select="error(QName('http://www.arxiv.org', oaipmh:OAI-PMH/oaipmh:error/@code ), oaipmh:OAI-PMH/oaipmh:error)"/>
		</xsl:if>

		<escidocItem:item>
			<escidocItem:properties>
				<srel:content-model objid="escidoc:persistent4" />
				<prop:content-model-specific />
			</escidocItem:properties>
			<escidocMetadataRecords:md-records>
				<escidocMetadataRecords:md-record name="escidoc">
					<escidocMetadataProfile:publication xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/"
						xmlns:eidt="http://escidoc.mpg.de/metadataprofile/schema/0.1/idtypes" xmlns:escidoc="http://escidoc.mpg.de/metadataprofile/schema/0.1/types"
						xmlns:escidocMetadataProfile="http://escidoc.mpg.de/metadataprofile/schema/0.1/"
						xmlns:publication="http://escidoc.mpg.de/metadataprofile/schema/0.1/publication" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
						>
						<xsl:attribute name="type">
							<xsl:choose>
								<xsl:when test="oaipmh:OAI-PMH/oaipmh:GetRecord/oaipmh:record/oaipmh:metadata/arxiv:arXiv/arxiv:journal-ref != ''">article</xsl:when>
								<xsl:otherwise>paper</xsl:otherwise>
							</xsl:choose>
						</xsl:attribute>
						<xsl:for-each select="oaipmh:OAI-PMH/oaipmh:GetRecord/oaipmh:record/oaipmh:metadata/arxiv:arXiv/arxiv:authors/arxiv:author">
							<publication:creator role="author">
								<escidoc:person>
									<escidoc:family-name>
										<xsl:value-of select="arxiv:keyname" />
									</escidoc:family-name>
									<escidoc:given-name>
										<xsl:value-of select="arxiv:forenames" />
									</escidoc:given-name>
									<escidoc:organization>
										<escidoc:organization-name>
											<xsl:value-of select="arxiv:affiliation" />
										</escidoc:organization-name>
										<escidoc:address />
										<escidoc:identifier>
											<xsl:value-of select="$external_organization_id" />
										</escidoc:identifier>
									</escidoc:organization>
								</escidoc:person>
							</publication:creator>
						</xsl:for-each>
						<dc:title>
							<xsl:value-of select="oaipmh:OAI-PMH/oaipmh:GetRecord/oaipmh:record/oaipmh:metadata/arxiv:arXiv/arxiv:title" />
						</dc:title>
						<dc:identifier xsi:type="eidt:URI">arXiv:<xsl:value-of select="oaipmh:OAI-PMH/oaipmh:GetRecord/oaipmh:record/oaipmh:metadata/arxiv:arXiv/arxiv:id" /></dc:identifier>
						<xsl:if test="oaipmh:OAI-PMH/oaipmh:GetRecord/oaipmh:record/oaipmh:metadata/arxiv:arXiv/arxiv:doi != ''">
							<dc:identifier xsi:type="eidt:DOI">
								<xsl:value-of select="oaipmh:OAI-PMH/oaipmh:GetRecord/oaipmh:record/oaipmh:metadata/arxiv:arXiv/arxiv:doi" />
							</dc:identifier>
						</xsl:if>
						<publication:publishing-info>
							<dc:publisher></dc:publisher>
							<escidoc:place></escidoc:place>
							<escidoc:edition></escidoc:edition>
						</publication:publishing-info>
						<dcterms:created xsi:type="dcterms:W3CDTF">
							<xsl:value-of select="oaipmh:OAI-PMH/oaipmh:GetRecord/oaipmh:record/oaipmh:metadata/arxiv:arXiv/created"/>
						</dcterms:created>
						<dcterms:modified xsi:type="dcterms:W3CDTF">
							<xsl:value-of select="oaipmh:OAI-PMH/oaipmh:GetRecord/oaipmh:record/oaipmh:metadata/arxiv:arXiv/updated"/>
						</dcterms:modified>
						<dcterms:dateSubmitted xsi:type="dcterms:W3CDTF"></dcterms:dateSubmitted>
						<dcterms:dateAccepted xsi:type="dcterms:W3CDTF"></dcterms:dateAccepted>
						<!--  -->
						<publication:published-online xsi:type="dcterms:W3CDTF">
							<xsl:value-of select="OAI-PMH/GetRecord/record/datestamp" />
						</publication:published-online>
						<dcterms:issued xsi:type="dcterms:W3CDTF"></dcterms:issued>
						<publication:source>
							<xsl:choose>
								<!-- Journal-Ref -->
								<xsl:when test="oaipmh:OAI-PMH/oaipmh:GetRecord/oaipmh:record/oaipmh:metadata/arxiv:arXiv/arxiv:journal-ref != ''">
									<xsl:attribute name="type">journal</xsl:attribute>
									<dc:title>
										<xsl:value-of select="oaipmh:OAI-PMH/oaipmh:GetRecord/oaipmh:record/oaipmh:metadata/arxiv:arXiv/arxiv:journal-ref" />
									</dc:title>
									<escidoc:creator>
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
									<escidoc:sequence-number>
										<xsl:value-of select="oaipmh:OAI-PMH/oaipmh:GetRecord/oaipmh:record/oaipmh:metadata/arxiv:arXiv/arxiv:report-no" />
									</escidoc:sequence-number>
									<escidoc:publishing-info>
										<dc:publisher></dc:publisher>
										<escidoc:place></escidoc:place>
										<escidoc:edition></escidoc:edition>
									</escidoc:publishing-info>
								</xsl:when>
								<xsl:otherwise>
									<dc:title></dc:title>
									<escidoc:creator>
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
							<dc:title></dc:title>
							<escidoc:start-date xsi:type="dcterms:W3CDTF"></escidoc:start-date>
							<escidoc:end-date xsi:type="dcterms:W3CDTF"></escidoc:end-date>
							<escidoc:place></escidoc:place>
						</publication:event>
						<publication:total-number-of-pages>
							<xsl:value-of select="oaipmh:OAI-PMH/oaipmh:GetRecord/oaipmh:record/oaipmh:metadata/arxiv:arXiv/arxiv:comments" />
						</publication:total-number-of-pages>
						<dcterms:abstract>
							<xsl:value-of select="oaipmh:OAI-PMH/oaipmh:GetRecord/oaipmh:record/oaipmh:metadata/arxiv:arXiv/arxiv:abstract" />
						</dcterms:abstract>
						<xsl:if test="oaipmh:OAI-PMH/oaipmh:GetRecord/oaipmh:record/oaipmh:metadata/arxiv:arXiv/arxiv:msc-class != ''">
							<dc:subject>
								<xsl:call-template name="msc">
									<xsl:with-param name="code"
										select="oaipmh:OAI-PMH/oaipmh:GetRecord/oaipmh:record/oaipmh:metadata/arxiv:arXiv/arxiv:msc-class" />
								</xsl:call-template>
							</dc:subject>
						</xsl:if>
						<xsl:if test="oaipmh:OAI-PMH/oaipmh:GetRecord/oaipmh:record/oaipmh:metadata/arxiv:arXiv/arxiv:categories != ''">
							<dc:subject>
								<xsl:value-of select="oaipmh:OAI-PMH/oaipmh:GetRecord/oaipmh:record/oaipmh:metadata/arxiv:arXiv/arxiv:categories" />
							</dc:subject>
						</xsl:if>
						<dcterms:tableOfContents></dcterms:tableOfContents>
						<publication:location></publication:location>
					</escidocMetadataProfile:publication>
				</escidocMetadataRecords:md-record>
			</escidocMetadataRecords:md-records>
			<escidocComponents:components>
				<escidocComponents:component>
					<escidocComponents:properties>
						<prop:valid-status>valid</prop:valid-status>
						<prop:visibility>public</prop:visibility>
						<prop:content-category></prop:content-category>
						<prop:file-name>0</prop:file-name>
					</escidocComponents:properties>
					<escidocComponents:content storage="internal-managed"/>
				</escidocComponents:component>
				<escidocComponents:component>
					<escidocComponents:properties>
						<prop:valid-status>valid</prop:valid-status>
						<prop:visibility>public</prop:visibility>
						<prop:content-category>supplementary-material</prop:content-category>
						<prop:file-name></prop:file-name>
					</escidocComponents:properties>
					<escidocComponents:content storage="external-url"/>
				</escidocComponents:component>
			</escidocComponents:components>
		</escidocItem:item>

	</xsl:template>

</xsl:stylesheet>