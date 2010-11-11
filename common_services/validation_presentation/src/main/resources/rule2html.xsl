<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:iso="http://purl.oclc.org/dsdl/schematron" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:r="http://www.escidoc.de/validation" xmlns:fn="http://www.escidoc.de/functions">

	<xsl:output encoding="UTF-8" indent="yes" method="xml"/>

	<xsl:param name="validation-schema"/>
	
	<xsl:variable name="max-counter" select="999"/>

	<xsl:variable name="validation-schema-content">
		<xsl:copy-of select="document($validation-schema)"/>
	</xsl:variable>

	<xsl:variable name="fields">
		<xsl:for-each select="/r:ruler/r:fields/r:root">
			<xsl:copy-of select="document(@schema)"/>
		</xsl:for-each>
	</xsl:variable>
		
	<xsl:template match="/">
	
		<xsl:for-each select="$fields/*">
			<xsl:apply-templates/>
		</xsl:for-each>
	
	</xsl:template>
	
	<xsl:template match="xs:element">
		<xsl:variable name="element" select="@name"/>
		<xsl:choose>
			<xsl:when test="@type != ''">
				<xsl:variable name="type" select="@type"/>
				<xsl:apply-templates select="../xs:complexType[@name=$type]"/>
			</xsl:when>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template match="xs:complexType">
		<xsl:apply-templates select=".//xs:element"/>
	</xsl:template>
	
	<xsl:template match="*"/>
	
</xsl:stylesheet>