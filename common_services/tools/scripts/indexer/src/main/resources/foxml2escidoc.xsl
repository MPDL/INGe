<?xml version="1.0" encoding="UTF-8"?>
<!--
	Caution:
	- Only for latest versions
	- Only for items
	- Only for escidoc publication items
-->

<xsl:stylesheet version="2.0" xmlns:nsCR="http://www.escidoc.de/ontologies/mpdl-ontologies/content-relations/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:file="http://purl.org/escidoc/metadata/profiles/0.1/file" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#" xmlns:prop="http://escidoc.de/core/01/properties/" xmlns:version="http://escidoc.de/core/01/properties/version/" xmlns:release="http://escidoc.de/core/01/properties/release/" xmlns:srel="http://escidoc.de/core/01/structural-relations/" xmlns:origin="http://escidoc.de/core/01/structural-relations/origin/" xmlns:system="http://escidoc.de/core/01/system/" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:foxml="info:fedora/fedora-system:def/foxml#" xmlns:yearbook="http://purl.org/escidoc/metadata/profiles/0.1/yearbook">

	<xsl:output method="xml" indent="yes" encoding="UTF-8"/>

	<xsl:param name="version" select="'latest-release'"/>
	<xsl:param name="index-db"/>
	
	<xsl:variable name="database" select="document($index-db)"/>
	
	<xsl:template match="/">
	
		<xsl:if test="foxml:digitalObject/foxml:datastream[@ID='RELS-EXT']/foxml:datastreamVersion[1]/foxml:xmlContent/rdf:RDF/rdf:Description/rdf:type/@rdf:resource != 'http://escidoc.de/core/01/resources/Item'">
			<xsl:value-of select="error(QName('http://www.escidoc.de', 'err:noitem'), 'This is no item') "/>
		</xsl:if>
		
		<xsl:if test="starts-with(foxml:digitalObject/foxml:datastream[@ID='escidoc']/foxml:datastreamVersion[last()]/foxml:xmlContent/dc:title, 'Import Task')">
			<xsl:value-of select="error(QName('http://www.escidoc.de', 'err:noitem'), 'This is an import item') "/>
		</xsl:if>
		<xsl:if test="foxml:digitalObject/foxml:datastream[@ID='escidoc']/foxml:datastreamVersion[last()]/foxml:xmlContent/import-task != ''">
			<xsl:value-of select="error(QName('http://www.escidoc.de', 'err:noitem'), 'This is an import item') "/>
		</xsl:if>
		
		<xsl:if test="foxml:digitalObject/foxml:datastream[@ID='escidoc']/foxml:datastreamVersion[last()]/foxml:xmlContent/yearbook:yearbook/dc:title != ''">
			<xsl:value-of select="error(QName('http://www.escidoc.de', 'err:noitem'), 'This is an yearbook item') "/>
		</xsl:if>
		
		<xsl:variable name="PID" select="foxml:digitalObject/@PID"/>
		<xsl:variable name="first-dc-title" select="(//dc:title)[1]"/>
		<xsl:variable name="latest-version-rels-ext"><xsl:copy-of select="foxml:digitalObject/foxml:datastream[@ID='RELS-EXT']/foxml:datastreamVersion[last()]/foxml:xmlContent/rdf:RDF/rdf:Description/*"/></xsl:variable>
		<xsl:variable name="latest-release-rels-ext"><xsl:copy-of select="foxml:digitalObject/foxml:datastream[@ID='RELS-EXT']/foxml:datastreamVersion[foxml:xmlContent/rdf:RDF/rdf:Description/version:status = 'released'][last()]/foxml:xmlContent/rdf:RDF/rdf:Description/*"/></xsl:variable>
		<xsl:variable name="RELS-EXT">
			<xsl:choose>
				<xsl:when test="$version = 'latest-version'">
					<xsl:copy-of select="$latest-version-rels-ext"/>
				</xsl:when>
				<xsl:when test="$version = 'latest-release'">
					<xsl:copy-of select="$latest-release-rels-ext"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:copy-of select="foxml:digitalObject/foxml:datastream[@ID='RELS-EXT']/foxml:datastreamVersion[foxml:xmlContent/rdf:RDF/rdf:Description/version:number = $version][last()]/foxml:xmlContent/rdf:RDF/rdf:Description/*"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:if test="$RELS-EXT = ''">
			<xsl:value-of select="error(QName('http://www.escidoc.de', 'err:wrongStatus'), 'Item in wrong public status') "/>
		</xsl:if>
		
        <!--                                 
		<xsl:message>public-status <xsl:value-of select="$latest-version-rels-ext/prop:public-status"/></xsl:message>
		-->
		<xsl:variable name="status" select="$latest-version-rels-ext/prop:public-status" /> 
	
		<xsl:if test="$status = 'withdrawn' and $version = 'latest-release'">
			<xsl:value-of select="error(QName('http://www.escidoc.de', 'err:wrongStatus'), 'Item status is withdrawn') "/>
		</xsl:if>

		<xsl:variable name="last-modification-date" select="foxml:digitalObject/foxml:datastream[@ID='RELS-EXT']/foxml:datastreamVersion[last()]/foxml:xmlContent/rdf:RDF/rdf:Description/version:date"/>
		
		<xsl:variable name="creation-date" select="foxml:digitalObject/foxml:datastream[@ID='RELS-EXT']/foxml:datastreamVersion[1]/@CREATED"/>
		<escidocItem:item xmlns:relations="http://www.escidoc.de/schemas/relations/0.3" xmlns:escidocMetadataRecords="http://www.escidoc.de/schemas/metadatarecords/0.5" xmlns:escidocContentStreams="http://www.escidoc.de/schemas/contentstreams/0.7" xmlns:escidocComponents="http://www.escidoc.de/schemas/components/0.9" xmlns:version="http://escidoc.de/core/01/properties/version/" xmlns:release="http://escidoc.de/core/01/properties/release/" xmlns:escidocItem="http://www.escidoc.de/schemas/item/0.10" xmlns:prop="http://escidoc.de/core/01/properties/" xmlns:srel="http://escidoc.de/core/01/structural-relations/" xmlns:xlink="http://www.w3.org/1999/xlink" xml:base="http://coreservice.mpdl.mpg.de" xlink:type="simple" xlink:title="{$first-dc-title}" xlink:href="/ir/item/{$PID}" last-modification-date="{$last-modification-date}">
			<escidocItem:properties xlink:type="simple" xlink:title="Properties" xlink:href="/ir/item/{$PID}/properties">
				<prop:creation-date>
					<xsl:value-of select="$creation-date"/>
				</prop:creation-date>
				<srel:created-by xlink:type="simple" xlink:title="{$RELS-EXT/prop:created-by-title}" xlink:href="/aa/user-account/{replace($RELS-EXT/srel:created-by/@rdf:resource, 'info:fedora/', '')}" />
				<prop:public-status>
					<xsl:value-of select="$RELS-EXT/prop:public-status"/>
				</prop:public-status>
				<prop:public-status-comment>
					<xsl:value-of select="$RELS-EXT/prop:public-status-comment"/>
				</prop:public-status-comment>
				<srel:context xlink:type="simple" xlink:title="{$RELS-EXT/prop:context-title}" xlink:href="/ir/context/{replace($RELS-EXT/srel:context/@rdf:resource, 'info:fedora/', '')}" />
				<srel:content-model xlink:type="simple" xlink:title="{$RELS-EXT/prop:content-model-title}" xlink:href="/cmm/content-model/{replace($RELS-EXT/srel:content-model/@rdf:resource, 'info:fedora/', '')}" />
				<prop:lock-status>unlocked</prop:lock-status>
				<prop:pid>
					<xsl:value-of select="$RELS-EXT/prop:pid"/>
				</prop:pid>
				<prop:version xlink:type="simple" xlink:title="This Version" xlink:href="/ir/item/{$PID}:{$RELS-EXT/version:number}">
					<version:number>
						<xsl:value-of select="$RELS-EXT/version:number"/>
					</version:number>
					<version:date>
						<xsl:value-of select="$RELS-EXT/version:date"/>
					</version:date>
					<version:status>
						<xsl:value-of select="$RELS-EXT/version:status"/>
					</version:status>
					<srel:modified-by xlink:type="simple" xlink:title="{$RELS-EXT/prop:modified-by-title}" xlink:href="/aa/user-account/{replace($RELS-EXT/srel:modified-by/@rdf:resource, 'info:fedora/', '')}" />
					<version:comment>
						<xsl:value-of select="$RELS-EXT/version:comment"/>
					</version:comment>
					
					<xsl:variable name="version-pid" select="$RELS-EXT/version:pid" /> 
					
					<xsl:if test="$version-pid != ''">
						<version:pid>
							<xsl:value-of select="$RELS-EXT/version:pid"/>
						</version:pid>
					</xsl:if>
					
				</prop:version>
				<prop:latest-version xlink:type="simple" xlink:title="Latest Version" xlink:href="/ir/item/{$PID}:{$latest-version-rels-ext/version:number}">
					<version:number>
						<xsl:value-of select="$latest-version-rels-ext/version:number"/>
					</version:number>
					<version:date>
						<xsl:value-of select="$latest-version-rels-ext/version:date"/>
					</version:date>
				</prop:latest-version>
				
				<xsl:variable name="release-number" select="$RELS-EXT/release:number" /> 
	
				<xsl:if test="$release-number != ''">
					<prop:latest-release xlink:type="simple" xlink:title="Latest public version" xlink:href="/ir/item/{$PID}:{$RELS-EXT/release:number}">
						<release:number>
							<xsl:value-of select="$RELS-EXT/release:number"/>
						</release:number>
						<release:date>
							<xsl:value-of select="$RELS-EXT/release:date"/>
						</release:date>
						<release:pid>
							<xsl:value-of select="$RELS-EXT/release:pid"/>
						</release:pid>
					</prop:latest-release>
				</xsl:if>
				
				<xsl:copy-of select="foxml:digitalObject/foxml:datastream[@ID='content-model-specific']/foxml:datastreamVersion[last()]/foxml:xmlContent/*"/>
			</escidocItem:properties>
			<escidocMetadataRecords:md-records xlink:type="simple" xlink:title="Metadata Records of Item {$PID}" xlink:href="/ir/item/{$PID}/md-records">
				<escidocMetadataRecords:md-record name="escidoc" xlink:type="simple" xlink:title="escidoc" xlink:href="/ir/item/{$PID}/md-records/md-record/escidoc">
					<xsl:copy-of select="foxml:digitalObject/foxml:datastream[@ID='escidoc']/foxml:datastreamVersion[@CREATED &lt;= $RELS-EXT/version:date][last()]/foxml:xmlContent/*"/>
				</escidocMetadataRecords:md-record>
			</escidocMetadataRecords:md-records>
			<escidocComponents:components xlink:type="simple" xlink:title="Components of Item {$PID}" xlink:href="/ir/item/{$PID}/components">
				<xsl:for-each select="$RELS-EXT/srel:component">
					<xsl:variable name="component-id" select="replace(@rdf:resource, 'info:fedora/', '')"/>
					
					<xsl:variable name="component-data" select="document($database/index/object[@name = $component-id]/@path)"/>
					<xsl:variable name="component-metadata" select="$component-data/foxml:digitalObject/foxml:datastream[@ID = 'escidoc']/foxml:datastreamVersion[last()]/foxml:xmlContent"/>
					<xsl:variable name="component-rels-ext" select="$component-data/foxml:digitalObject/foxml:datastream[@ID = 'RELS-EXT']/foxml:datastreamVersion[last()]/foxml:xmlContent/rdf:RDF/rdf:Description"/>
					
					<!--  
					<xsl:comment>das ist component-rels-ext <xsl:value-of select="$component-rels-ext"/></xsl:comment>
				
					--> 
					<xsl:variable name="component-content" select="$component-data/foxml:digitalObject/foxml:datastream[@ID = 'content']/foxml:datastreamVersion[last()]"/>
					
					
					<escidocComponents:component xlink:type="simple" xlink:title="{$component-metadata/file:file/dc:title}" xlink:href="/ir/item/{$PID}/components/component/{$component-id}">
						<escidocComponents:properties xlink:type="simple" xlink:title="Properties" xlink:href="/ir/item/{$PID}/components/component/{$component-id}/properties">
							<prop:creation-date>
								<xsl:value-of select="$component-data/foxml:digitalObject/foxml:objectProperties/foxml:property[@NAME = 'info:fedora/fedora-system:def/model#createdDate']/@VALUE"/>
							</prop:creation-date>
							<srel:created-by xlink:type="simple" xlink:title="{$component-rels-ext/prop:created-by-title}" xlink:href="/aa/user-account/{replace($component-rels-ext/srel:created-by/@rdf:resource, 'info:fedora/', '')}" />
							<prop:description>
								<xsl:value-of select="$component-metadata/file:file/dc:description"/>
							</prop:description>
							<prop:valid-status>
								<xsl:value-of select="$component-rels-ext/prop:valid-status"/>
							</prop:valid-status>
							<prop:visibility>
								<xsl:value-of select="$component-rels-ext/prop:visibility"/>
							</prop:visibility>
							<xsl:if test="$component-rels-ext/prop:pid != ''">
								<prop:pid>
									<xsl:value-of select="$component-rels-ext/prop:pid"/>
								</prop:pid>
							</xsl:if>
							<prop:content-category>
								<xsl:value-of select="$component-rels-ext/prop:content-category"/>
							</prop:content-category>
							<prop:file-name>
								<xsl:value-of select="$component-metadata/file:file/dc:title"/>
							</prop:file-name>
							<prop:mime-type>
								<xsl:choose>
									<xsl:when test="$component-rels-ext/prop:mime-type != ''">
										<xsl:value-of select="$component-rels-ext/prop:mime-type"/>
									</xsl:when>
									<!--  xsl:otherwise>application/octet-stream</xsl:otherwise-->
								</xsl:choose>
							</prop:mime-type>
							<xsl:variable name="component-checksum" select="$component-content/foxml:contentDigest/@DIGEST"/>
							<xsl:if test="$component-checksum != ''">
								<prop:checksum>
									<xsl:value-of select="$component-content/foxml:contentDigest/@DIGEST"/>
								</prop:checksum>
								<prop:checksum-algorithm>
									<xsl:value-of select="$component-content/foxml:contentDigest/@TYPE"/>
								</prop:checksum-algorithm>
							</xsl:if>
						</escidocComponents:properties>
						<escidocComponents:content xlink:type="simple" xlink:title="{$component-metadata/file:file/dc:title}" xlink:href="/ir/item/{$PID}/components/component/{$component-id}/content">
						
							<xsl:message>content type <xsl:value-of select="$component-content/foxml:contentLocation/@TYPE"/></xsl:message>
							<xsl:message>component-content-ID <xsl:value-of select="$component-content/@ID"/></xsl:message>
							<xsl:message>component-content-MIMETYPE <xsl:value-of select="$component-content/@MIMETYPE"/></xsl:message>
							
							<xsl:choose>
							
								<xsl:when test="$component-content/foxml:contentLocation/@TYPE = 'INTERNAL_ID'">
									<xsl:attribute name="storage" select="'internal-managed'"/>
								</xsl:when>
								<xsl:when test="$component-content/foxml:contentLocation/@TYPE = 'URL'">
									<xsl:attribute name="storage" select="'external-url'"/>
								</xsl:when>
								<xsl:otherwise>
									ERROR!
								</xsl:otherwise>
							</xsl:choose>
						</escidocComponents:content>
						<escidocMetadataRecords:md-records xlink:type="simple" xlink:title="Metadata Records of Component {$component-id}" xlink:href="/ir/item/{$PID}/components/component/{$component-id}/md-records">
							<escidocMetadataRecords:md-record name="escidoc" xlink:type="simple" xlink:title="escidoc" xlink:href="/ir/item/{$PID}/components/component/{$component-id}/md-records/md-record/escidoc">
								<xsl:copy-of select="$component-metadata/file:file"/>
							</escidocMetadataRecords:md-record>
						</escidocMetadataRecords:md-records>
					</escidocComponents:component>
				</xsl:for-each>
			</escidocComponents:components>
			<relations:relations xlink:type="simple" xlink:title="Relations of Item" xlink:href="/ir/item/{$PID}/relations">
				<xsl:for-each select="$RELS-EXT/nsCR:isRevisionOf">
					<relations:relation predicate="http://www.escidoc.de/ontologies/mpdl-ontologies/content-relations#isRevisionOf" xlink:type="simple" xlink:title="Mut zur Lücke" xlink:href="/ir/item/{replace(@rdf:resource, 'info:fedora/', '')}"/>
				</xsl:for-each>
			</relations:relations>
			<escidocItem:resources xlink:type="simple" xlink:title="Resources" xlink:href="/ir/item/{$PID}/resources" />
		</escidocItem:item>
	</xsl:template>
</xsl:stylesheet>