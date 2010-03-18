<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:foaf="http://xmlns.com/foaf/0.1/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:escidoc="http://escidoc.mpg.de/" xmlns:eprints="http://purl.org/eprint/terms/">
	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
		
	<xsl:variable name="ou-list" select="document('pm_units.xml')"/>
	
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
		<xsl:variable name="ou-name" select="$ou-list/ice_units/unit[code= $ou-code]/name_en"/>
		<xsl:element name="eprints:affiliatedInstitution">		
			<xsl:value-of select="$ou-name"/>
			<xsl:if test="$ou-name != ''">, </xsl:if>MPI for Chemical Ecology, Max Planck Society
		</xsl:element>
	</xsl:template>
	
</xsl:stylesheet>
