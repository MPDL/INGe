<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:foaf="http://xmlns.com/foaf/0.1/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:escidoc="http://escidoc.mpg.de/" xmlns:eprints="http://purl.org/eprint/terms/">
	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
	
	<xsl:param name="ou-url" select="'http://migration-coreservice.mpdl.mpg.de:8080'"/>
	<xsl:param name="ou-file-path" select="'pm_units.xml'"/>
	
	<xsl:variable name="ou-list" select="document(concat($ou-url, '/srw/search/escidocou_all?query=(escidoc.objid=e*)&amp;maximumRecords=10000'))"/>
	<xsl:variable name="ou-file" select="document($ou-file-path)"/>
	
	<xsl:template match="/">
		<xsl:element name="rdf:RDF">
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="author">
		<xsl:element name="rdf:Descritpion">
				<xsl:element name="dcterms:creators">
						<xsl:element name="foaf:family_name">
							<xsl:value-of select="name_last"/>
						</xsl:element>
						<xsl:element name="dc:title">
							<xsl:value-of select="name_last"/>, <xsl:value-of select="name_first"/>
						</xsl:element>
						<xsl:element name="foaf:given_name">
							<xsl:value-of select="name_first"/>
						</xsl:element>
						<xsl:element name="escidoc:position">
							<xsl:call-template name="position"/>
						</xsl:element>
						<xsl:element name="dc:identifier">
							<xsl:attribute name="type" select="'iris'"/>
							<xsl:value-of select="iris_id"/>
						</xsl:element>
				</xsl:element>
		</xsl:element>
	</xsl:template>
	
	<xsl:template name="position">
		<xsl:variable name="ou-code" select="group"/>
		<xsl:variable name="ou-name" select="$ou-file/ice_units/unit[code= $ou-code]/name_en"/>
		
		<xsl:variable name="escidoc-ou">
			<xsl:value-of select="$ou-list/srw:searchRetrieveResponse/srw:records/srw:record[normalize-space(srw:recordData/search-result:search-result-record/organizational-unit:organizational-unit/mdr:md-records/mdr:md-record/mdou:organization-details/dc:title) = $ou-name]/srw:recordData/search-result:search-result-record/organizational-unit:organizational-unit/@objid"/>
		</xsl:variable>
	
		<xsl:variable name="ou-path">
			<xsl:call-template name="get-ou-path">
				<xsl:with-param name="id" select="$escidoc-ou"/>
			</xsl:call-template>
		</xsl:variable>
		
		<xsl:if test="$escidoc-ou != ''">
			<xsl:element name="dc:identifier">
				<xsl:value-of select="$escidoc-ou"/>
			</xsl:element>
		</xsl:if>
		
		<xsl:element name="eprints:affiliatedInstitution">
			<xsl:value-of select="$ou-path"/>
		</xsl:element>
	</xsl:template>
	
	<xsl:template name="get-ou-path">
		<xsl:param name="id"/>
		
		<xsl:variable name="ou" select="$ou-list/srw:searchRetrieveResponse/srw:records/srw:record/srw:recordData/search-result:search-result-record/organizational-unit:organizational-unit[@objid = $id]"/>
		
		<xsl:value-of select="normalize-space($ou/mdr:md-records/mdr:md-record/mdou:organization-details/dc:title)"/>
		
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
