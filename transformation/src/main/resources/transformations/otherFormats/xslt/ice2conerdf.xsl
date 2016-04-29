<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:srw="http://www.loc.gov/zing/srw/" xmlns:mdr="http://www.escidoc.de/schemas/metadatarecords/0.4" xmlns:srel="http://escidoc.de/core/01/structural-relations/" xmlns:mdou="http://purl.org/escidoc/metadata/profiles/0.1/organizationalunit" xmlns:search-result="http://www.escidoc.de/schemas/searchresult/0.7" xmlns:organizational-unit="http://www.escidoc.de/schemas/organizationalunit/0.7" xmlns:xs="http://www.w3.org/2001/XMLSchema-instance/" xmlns:fn="http://www.w3.org/2005/xpath-functions" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:foaf="http://xmlns.com/foaf/0.1/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:escidoc="http://purl.org/escidoc/metadata/terms/0.1/" xmlns:eprints="http://purl.org/eprint/terms/">
	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
	
	<xsl:param name="ou-url" select="'http://migration-coreservice.mpdl.mpg.de:8080'"/>
	<xsl:param name="ou-file-path" select="'file:/C:/tmp/test.xml'"/>
	
	<xsl:variable name="ou-list" select="document(concat($ou-url, '/srw/search/escidocou_all?query=(escidoc.objid=e*)&amp;maximumRecords=10000'))"/>
	<xsl:variable name="ou-file" select="document($ou-file-path)"/>
	
	<xsl:template match="/">
		<xsl:element name="rdf:RDF">
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="author">
		<xsl:element name="rdf:Description">
			<xsl:element name="dc:title">
				<xsl:value-of select="name_last"/>, <xsl:value-of select="name_first"/>
			</xsl:element>
			<xsl:element name="foaf:family_name">
				<xsl:value-of select="name_last"/>
			</xsl:element>
			<xsl:element name="foaf:givenname">
				<xsl:value-of select="name_first"/>
			</xsl:element>
			<xsl:element name="dc:identifier">
				<xsl:element name="rdf:Description">
					<xsl:element  name="xs:type">IRIS</xsl:element>
					<xsl:element name="rdf:value">
						<xsl:value-of select="iris_id"/>
					</xsl:element>
				</xsl:element>
			</xsl:element>
			<xsl:if test="exists(./groups/group)">
				<xsl:for-each select="./groups/group">
					<xsl:call-template name="position"/>
				</xsl:for-each>
			</xsl:if>
			<xsl:if test="not(exists(./groups/group))">
				<xsl:call-template name="position"/>
			</xsl:if>
		</xsl:element>
	</xsl:template>
	
	<xsl:template name="position">
		<xsl:variable name="ou-code" select="."/>
		<xsl:variable name="ou-name" select="normalize-space($ou-file/units/unit[code=$ou-code]/name_en)"/>
		<xsl:comment>XX<xsl:value-of select="$ou-name"/>XX</xsl:comment>
		<xsl:variable name="escidoc-ou">
			<xsl:if test="$ou-name != ''">
				<xsl:value-of select="$ou-list/srw:searchRetrieveResponse/srw:records/srw:record[normalize-space(srw:recordData/search-result:search-result-record/organizational-unit:organizational-unit/mdr:md-records/mdr:md-record/mdou:organizational-unit/dc:title) = $ou-name]/srw:recordData/search-result:search-result-record/organizational-unit:organizational-unit/@objid"/>
			</xsl:if>
			<xsl:if test="$ou-name = ''">
				<xsl:value-of select="$ou-list/srw:searchRetrieveResponse/srw:records/srw:record[normalize-space(srw:recordData/search-result:search-result-record/organizational-unit:organizational-unit/mdr:md-records/mdr:md-record/mdou:organizational-unit/dc:title) = 'MPI for Chemical Ecology']/srw:recordData/search-result:search-result-record/organizational-unit:organizational-unit/@objid"/>
			</xsl:if>
		</xsl:variable>
		<xsl:variable name="ou-path">
			<xsl:call-template name="get-ou-path">
				<xsl:with-param name="id" select="$escidoc-ou"/>
			</xsl:call-template>
		</xsl:variable>
		
		<xsl:element name="escidoc:position">
			<xsl:element name="rdf:Description">
				<xsl:if test="$escidoc-ou != ''">
					<xsl:element name="dc:identifier">
						<xsl:value-of select="$escidoc-ou"/>
					</xsl:element>
				</xsl:if>
				<xsl:element name="eprints:affiliatedInstitution">		
					<xsl:variable name="ou-code" select="name"/>
					<xsl:value-of select="$ou-path"/>
				</xsl:element>
				<xsl:if test="until != ''">
					<xsl:element name="escidoc:end-date">
						<xsl:value-of select="substring(until, 1, 4)"/>-<xsl:value-of select="substring(until, 5, 2)"/>-<xsl:value-of select="substring(until, 7, 2)"/>
					</xsl:element>
				</xsl:if>
			</xsl:element>
		</xsl:element>
	</xsl:template>
	
	<xsl:template name="get-ou-path">
		<xsl:param name="id"/>
		
		<xsl:variable name="ou" select="$ou-list/srw:searchRetrieveResponse/srw:records/srw:record/srw:recordData/search-result:search-result-record/organizational-unit:organizational-unit[@objid = $id]"/>
		
		<xsl:value-of select="normalize-space($ou/mdr:md-records/mdr:md-record/mdou:organizational-unit/dc:title)"/>
		
		<xsl:choose>
			<xsl:when test="exists($ou/organizational-unit:parents/srel:parent)">
				<xsl:text>, </xsl:text>
				<xsl:call-template name="get-ou-path">
					<xsl:with-param name="id" select="$ou/organizational-unit:parents/srel:parent[1]/@objid"/>
				</xsl:call-template>
			</xsl:when>
		</xsl:choose>
	</xsl:template>
	
</xsl:stylesheet>
