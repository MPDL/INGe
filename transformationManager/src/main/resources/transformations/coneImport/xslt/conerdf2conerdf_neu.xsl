<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:dc="http://purl.org/dc/elements/1.1/"
	xmlns:dcterms="http://purl.org/dc/terms/"
	xmlns:e="http://escidoc.mpg.de/metadataprofile/schema/0.1/types"
	xmlns:ei="http://www.escidoc.de/schemas/item/0.8" xmlns:eprint="http://purl.org/eprint/terms/"
	xmlns:eprints="http://purl.org/eprint/terms/"
	xmlns:escidoc="http://purl.org/escidoc/metadata/terms/0.1/"
	xmlns:escidocFunctions="urn:escidoc:functions"
	xmlns:excel="urn:schemas-microsoft-com:office:spreadsheet"
	xmlns:file="http://escidoc.mpg.de/metadataprofile/schema/0.1/file"
	xmlns:fn="http://www.w3.org/2005/xpath-functions" xmlns:foaf="http://xmlns.com/foaf/0.1/"
	xmlns:mdou="http://purl.org/escidoc/metadata/profiles/0.1/organizationalunit"
	xmlns:mdp="http://escidoc.mpg.de/metadataprofile/schema/0.1/"
	xmlns:mdr="http://www.escidoc.de/schemas/metadatarecords/0.5"
	xmlns:organizational-unit="http://www.escidoc.de/schemas/organizationalunit/0.8"
	xmlns:prop="http://escidoc.de/core/01/properties/"
	xmlns:publ="http://escidoc.mpg.de/metadataprofile/schema/0.1/publication"
	xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
	xmlns:release="http://escidoc.de/core/01/properties/release/"
	xmlns:search-result="http://www.escidoc.de/schemas/searchresult/0.8"
	xmlns:srel="http://escidoc.de/core/01/structural-relations/"
	xmlns:srw="http://www.loc.gov/zing/srw/"
	xmlns:version="http://escidoc.de/core/01/properties/version/"
	xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" encoding="UTF-8" indent="yes"></xsl:output>
	<xsl:param name="ou-url" select="'http://qa.pure.mpdl.mpg.de'"></xsl:param>
	<xsl:variable name="ou-list"
		select="document(concat($ou-url, '/rest/ous/xml?from=0&amp;size=5000'))"></xsl:variable>
	<xsl:template match="/">
		<rdf:RDF>
			<xsl:for-each select="rdf:RDF/rdf:Description">
				<xsl:variable name="about" select="@rdf:about"></xsl:variable>
				<xsl:if test="not(exists(preceding-sibling::*[@rdf:about = $about]))">
					<xsl:copy>
						<xsl:copy-of select="@*[name() != 'rdf:about']"></xsl:copy-of>
						<xsl:apply-templates></xsl:apply-templates>
					</xsl:copy>
				</xsl:if>
			</xsl:for-each>
		</rdf:RDF>
	</xsl:template>
	<xsl:template match="*">
		<xsl:copy>
			<xsl:copy-of select="@*"></xsl:copy-of>
			<xsl:apply-templates></xsl:apply-templates>
		</xsl:copy>
	</xsl:template>
	<xsl:template match="dcterms:contributor">
		<xsl:comment>removed: dcterms:contributor: <xsl:value-of select="."></xsl:value-of></xsl:comment>
	</xsl:template>
	<xsl:template match="dcterms:creator">
		<xsl:comment>removed: dcterms:creator: <xsl:value-of select="."></xsl:value-of></xsl:comment>
	</xsl:template>
	<xsl:template match="escidoc:position/rdf:Description/dc:identifier">
		<xsl:variable name="idToCompare">
			<xsl:value-of select="."></xsl:value-of>
		</xsl:variable>
		<xsl:variable name="ou">
			<xsl:choose>
				<xsl:when test="contains(../eprints:affiliatedInstitution, ',')">
					<xsl:value-of select="substring-before(../eprints:affiliatedInstitution, ',')"
					></xsl:value-of>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="../eprints:affiliatedInstitution"></xsl:value-of>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="ou_parent">
			<xsl:choose>
				<xsl:when test="contains(../eprints:affiliatedInstitution, ',')">
					<xsl:choose>
						<xsl:when
							test="contains(substring-after(../eprints:affiliatedInstitution, ','), ',')">
							<xsl:value-of
								select="normalize-space(substring-before(substring-after(../eprints:affiliatedInstitution, ','), ','))"
							></xsl:value-of>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of
								select="normalize-space(substring-after(../eprints:affiliatedInstitution, ','))"
							></xsl:value-of>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="''"></xsl:value-of>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:comment>ID for OU '<xsl:value-of select="$ou"></xsl:value-of>' and Parent '<xsl:value-of select="$ou_parent"></xsl:value-of>'&#xA;</xsl:comment>
		<xsl:if
			test="count($ou-list/root/records/record[normalize-space(name) = $ou and normalize-space(parent) = $ou_parent]) &gt; 1">
			<xsl:comment>ERROR with "<xsl:value-of select="$ou"></xsl:value-of>" (found more than one entry)&#xA;</xsl:comment>
		</xsl:if>
		<xsl:if
			test="not(exists($ou-list/root/records/record[normalize-space(name) = $ou and normalize-space(parent) = $ou_parent]/objectId))">
			<xsl:comment>ERROR with "<xsl:value-of select="$ou"></xsl:value-of>" (NO ID FOUND)&#xA;</xsl:comment>
		</xsl:if>
		<xsl:if
			test="$ou-list/root/records/record[normalize-space(name) = $ou and normalize-space(parent) = $ou_parent]/objectId = ''">
			<xsl:comment>ERROR with "<xsl:value-of select="$ou"></xsl:value-of>" (ID EMPTY)&#xA;</xsl:comment>
		</xsl:if>
		<dc:identifier>
			<xsl:value-of
				select="$ou-list/root/records/record[normalize-space(name) = $ou and normalize-space(parent) = $ou_parent]/objectId"
			></xsl:value-of>
		</dc:identifier>
	</xsl:template>
</xsl:stylesheet>
