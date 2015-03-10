<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:nsCR="http://www.escidoc.de/ontologies/mpdl-ontologies/content-relations/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:file="http://purl.org/escidoc/metadata/profiles/0.1/file" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#" xmlns:prop="http://escidoc.de/core/01/properties/" xmlns:version="http://escidoc.de/core/01/properties/version/" xmlns:release="http://escidoc.de/core/01/properties/release/" xmlns:srel="http://escidoc.de/core/01/structural-relations/" xmlns:origin="http://escidoc.de/core/01/structural-relations/origin/" xmlns:system="http://escidoc.de/core/01/system/" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:foxml="info:fedora/fedora-system:def/foxml#">

	<xsl:param name="attributes-file"/>

	<xsl:template match="*">
		<xsl:copy>
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates select="*"/>
		</xsl:copy>
	</xsl:template>
	
	<xsl:template match="xsl:include">
		<xsl:copy>
			<xsl:copy-of select="@*[name() != 'href']"/>
			<xsl:attribute name="href"><xsl:value-of select="$attributes-file"/></xsl:attribute>
			<xsl:apply-templates select="*"/>
		</xsl:copy>
	</xsl:template>
</xsl:stylesheet>