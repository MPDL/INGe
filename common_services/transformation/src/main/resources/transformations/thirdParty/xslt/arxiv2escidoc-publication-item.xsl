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
	
	
	Copyright 2006-2009 Fachinformationszentrum Karlsruhe Gesellschaft
	für wissenschaftlich-technische Information mbH and Max-Planck-
	Gesellschaft zur Förderung der Wissenschaft e.V.
	All rights reserved. Use is subject to license terms.
-->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:escidocContext="${resources.xsd.soap.context.context}" 
	xmlns:escidocContextList="${resources.xsd.soap.context.contextlist}"
	xmlns:escidocComponents="${xsd.soap.item.components}" 
	xmlns:escidocItem="${xsd.soap.item.item}"
	xmlns:escidocItemList="${resources.xsd.soap.item.itemlist}" 
	xmlns:escidocMetadataRecords="${xsd.soap.common.mdrecords}"
	xmlns:escidocRelations="${resources.xsd.soap.common.relations}"
	xmlns:escidocSearchResult="${resources.xsd.soap.searchresult.searchresult}" 
	xmlns:xlink="http://www.w3.org/1999/xlink"
	xmlns:prop="${xsd.soap.common.prop}" 
	xmlns:srel="${xsd.soap.common.srel}" 
	xmlns:version="${xsd.soap.common.version}" 
	xmlns:release="${xsd.soap.common.release}" 	 
	xmlns:organization="${xsd.metadata.organization}"
	xmlns:person="${xsd.metadata.person}"
	xmlns:source="${xsd.metadata.source}"
	xmlns:event="${xsd.metadata.event}"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
	xmlns:oaipmh="http://www.openarchives.org/OAI/2.0/" 
	xmlns:arxiv="http://arxiv.org/OAI/arXiv/"
	xmlns:dcterms="${xsd.metadata.dcterms}">

	<xsl:import href="../../vocabulary-mappings.xsl"/>

	<xsl:output method="xml" encoding="UTF-8" indent="yes" />

	<xsl:include href="msc.xsl" />
	<xsl:include href="arxiv_subjects.xsl" />

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
					<publication:publication xmlns:dc="${xsd.metadata.dc}" xmlns:dcterms="${xsd.metadata.dcterms}"
						xmlns:eterms="${xsd.metadata.terms}" 
						xmlns:publication="${xsd.metadata.publication}" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
						>
						<xsl:attribute name="type">
							<xsl:choose>
								<xsl:when test="oaipmh:OAI-PMH/oaipmh:GetRecord/oaipmh:record/oaipmh:metadata/arxiv:arXiv/arxiv:journal-ref != ''">
									<xsl:value-of select="$genre-ves/enum[.='article']/@uri"/>
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="$genre-ves/enum[.='paper']/@uri"/>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:attribute>
						<xsl:for-each select="oaipmh:OAI-PMH/oaipmh:GetRecord/oaipmh:record/oaipmh:metadata/arxiv:arXiv/arxiv:authors/arxiv:author">
							<xsl:element name="eterms:creator">
								<xsl:attribute name="role" select="$creator-ves/enum[.='author']/@uri"/>							
								<person:person>
									<eterms:family-name>
										<xsl:value-of select="arxiv:keyname" />
									</eterms:family-name>
									<eterms:given-name>
										<xsl:value-of select="arxiv:forenames" />
									</eterms:given-name>
									<organization:organization>
										<dc:title>
											<xsl:value-of select="arxiv:affiliation" />
										</dc:title>
										<eterms:address />
										<dc:identifier>
											<xsl:value-of select="$external_organization_id" />
										</dc:identifier>
									</organization:organization>
								</person:person>							
							</xsl:element>
						</xsl:for-each>
						<dc:title>
							<xsl:value-of select="oaipmh:OAI-PMH/oaipmh:GetRecord/oaipmh:record/oaipmh:metadata/arxiv:arXiv/arxiv:title" />
						</dc:title>
						<dc:identifier xsi:type="eidt:arxiv"><xsl:value-of select="oaipmh:OAI-PMH/oaipmh:GetRecord/oaipmh:record/oaipmh:metadata/arxiv:arXiv/arxiv:id" /></dc:identifier>
						<xsl:if test="oaipmh:OAI-PMH/oaipmh:GetRecord/oaipmh:record/oaipmh:metadata/arxiv:arXiv/arxiv:doi != ''">
							<dc:identifier xsi:type="eidt:DOI">
								<xsl:value-of select="oaipmh:OAI-PMH/oaipmh:GetRecord/oaipmh:record/oaipmh:metadata/arxiv:arXiv/arxiv:doi" />
							</dc:identifier>
						</xsl:if>
						<eterms:publishing-info>
							<dc:publisher></dc:publisher>
							<eterms:place></eterms:place>
							<eterms:edition></eterms:edition>
						</eterms:publishing-info>
						<dcterms:created xsi:type="dcterms:W3CDTF">
							<xsl:value-of select="oaipmh:OAI-PMH/oaipmh:GetRecord/oaipmh:record/oaipmh:metadata/arxiv:arXiv/arxiv:created"/>
						</dcterms:created>
						<dcterms:modified xsi:type="dcterms:W3CDTF">
							<xsl:value-of select="oaipmh:OAI-PMH/oaipmh:GetRecord/oaipmh:record/oaipmh:metadata/arxiv:arXiv/arxiv:updated"/>
						</dcterms:modified>
						<dcterms:dateSubmitted xsi:type="dcterms:W3CDTF"></dcterms:dateSubmitted>
						<dcterms:dateAccepted xsi:type="dcterms:W3CDTF"></dcterms:dateAccepted>
						<!--  -->
						<eterms:published-online xsi:type="dcterms:W3CDTF">
							<xsl:value-of select="OAI-PMH/GetRecord/record/datestamp" />
						</eterms:published-online>
						<dcterms:issued xsi:type="dcterms:W3CDTF"></dcterms:issued>
						<source:source>
							<xsl:choose>
								<!-- Journal-Ref -->
								<xsl:when test="oaipmh:OAI-PMH/oaipmh:GetRecord/oaipmh:record/oaipmh:metadata/arxiv:arXiv/arxiv:journal-ref != ''">
									<xsl:attribute name="type" select="$genre-ves/enum[.='journal']/@uri"/>
									<dc:title>
										<xsl:value-of select="oaipmh:OAI-PMH/oaipmh:GetRecord/oaipmh:record/oaipmh:metadata/arxiv:arXiv/arxiv:journal-ref" />
									</dc:title>
									<eterms:creator>
										<person:person>
											<eterms:family-name></eterms:family-name>
											<eterms:given-name></eterms:given-name>
											<organization:organization>
												<dc:title></dc:title>
												<eterms:address></eterms:address>
											</organization:organization>
										</person:person>
									</eterms:creator>
									<eterms:volume></eterms:volume>
									<eterms:issue></eterms:issue>
									<eterms:start-page></eterms:start-page>
									<eterms:end-page></eterms:end-page>

									<!-- Report-No -->
									<eterms:sequence-number>
										<xsl:value-of select="oaipmh:OAI-PMH/oaipmh:GetRecord/oaipmh:record/oaipmh:metadata/arxiv:arXiv/arxiv:report-no" />
									</eterms:sequence-number>
									<eterms:publishing-info>
										<dc:publisher></dc:publisher>
										<eterms:place></eterms:place>
										<eterms:edition></eterms:edition>
									</eterms:publishing-info>
								</xsl:when>
								<xsl:otherwise>
									<dc:title></dc:title>
									<eterms:creator>
										<person:person>
											<eterms:family-name></eterms:family-name>
											<eterms:given-name></eterms:given-name>
											<organization:organization>
												<dc:title></dc:title>
												<eterms:address></eterms:address>
											</organization:organization>
										</person:person>
									</eterms:creator>
									<eterms:volume></eterms:volume>
									<eterms:issue></eterms:issue>
									<eterms:start-page></eterms:start-page>
									<eterms:end-page></eterms:end-page>
									<eterms:sequence-number></eterms:sequence-number>
									<eterms:publishing-info>
										<dc:publisher></dc:publisher>
										<eterms:place></eterms:place>
										<eterms:edition></eterms:edition>
									</eterms:publishing-info>
								</xsl:otherwise>
							</xsl:choose>
						</source:source>
						<event:event>
							<dc:title></dc:title>
							<eterms:start-date xsi:type="dcterms:W3CDTF"></eterms:start-date>
							<eterms:end-date xsi:type="dcterms:W3CDTF"></eterms:end-date>
							<eterms:place></eterms:place>
						</event:event>
						<eterms:total-number-of-pages>
							<xsl:value-of select="oaipmh:OAI-PMH/oaipmh:GetRecord/oaipmh:record/oaipmh:metadata/arxiv:arXiv/arxiv:comments" />
						</eterms:total-number-of-pages>
						<dcterms:abstract>
							<xsl:value-of select="oaipmh:OAI-PMH/oaipmh:GetRecord/oaipmh:record/oaipmh:metadata/arxiv:arXiv/arxiv:abstract" />
						</dcterms:abstract>
						
						<xsl:if test="oaipmh:OAI-PMH/oaipmh:GetRecord/oaipmh:record/oaipmh:metadata/arxiv:arXiv/arxiv:categories != ''">
							<dcterms:subject> 
								<!-- <xsl:value-of select="oaipmh:OAI-PMH/oaipmh:GetRecord/oaipmh:record/oaipmh:metadata/arxiv:arXiv/arxiv:categories" />-->
								<xsl:call-template name="parseCategories">   
										<xsl:with-param name="string"
											select="oaipmh:OAI-PMH/oaipmh:GetRecord/oaipmh:record/oaipmh:metadata/arxiv:arXiv/arxiv:categories" />
								</xsl:call-template>	
								
								<xsl:if test="oaipmh:OAI-PMH/oaipmh:GetRecord/oaipmh:record/oaipmh:metadata/arxiv:arXiv/arxiv:msc-class != ''">
									<xsl:text>, </xsl:text>							
									<xsl:call-template name="msc">
										<xsl:with-param name="code"
											select="oaipmh:OAI-PMH/oaipmh:GetRecord/oaipmh:record/oaipmh:metadata/arxiv:arXiv/arxiv:msc-class" />
									</xsl:call-template>							
								</xsl:if>
							</dcterms:subject>
						</xsl:if>
						<dcterms:tableOfContents></dcterms:tableOfContents>
						<eterms:location></eterms:location>
					</publication:publication>
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
						<prop:content-category>http://purl.org/escidoc/metadata/ves/content-categories/supplementary-material</prop:content-category>
						<prop:file-name></prop:file-name>
					</escidocComponents:properties>
					<escidocComponents:content storage="external-url"/>
				</escidocComponents:component>
			</escidocComponents:components>
		</escidocItem:item>

	</xsl:template>
	
	<xsl:template name="parseCategories">
		<xsl:param name="string"/>

		<xsl:choose>
			<xsl:when test="substring-before($string,' ')=''">								
					<xsl:call-template name="arxiv_subjects"> 	
						<xsl:with-param name="subject" select="$string" />
					</xsl:call-template>				
			</xsl:when>
  			<xsl:when test="substring-before($string,' ')!=''">	
				<xsl:call-template name="arxiv_subjects">   
							<xsl:with-param name="subject"
									select="substring-before($string,' ')" />
				</xsl:call-template>
				<xsl:value-of select="','"/>
				<xsl:call-template name="parseCategories">
					<xsl:with-param name="string" select="substring-after($string,' ')"/>
				</xsl:call-template>				
			</xsl:when>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>